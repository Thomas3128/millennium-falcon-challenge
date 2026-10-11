package com.falcon.falcon.odds;

import static java.util.Comparator.comparingInt;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.falcon.falcon.config.BountyHunterSchedule;
import com.falcon.falcon.config.EmpirePlan;
import com.falcon.falcon.config.FalconConfig;
import com.falcon.falcon.routes.Galaxy;
import com.falcon.falcon.routes.Jump;
import com.falcon.falcon.utils.Require;

public class OddsCalculator {

    private record Node(State state, int encounters) {
    }

    private final Galaxy galaxy;
    private final int autonomy;
    private final String departure;
    private final String arrival;

    // Whether a route the tank can actually cover leads to the arrival. Checked once here,
    // since it depends only on the map and the autonomy: without it, an unreachable arrival
    // would make the search try every day of the countdown before giving up.
    private final boolean arrivalReachable;

    public OddsCalculator(Galaxy galaxy, FalconConfig config) {
        Require.that(galaxy != null, "galaxy is required");
        Require.that(config != null, "falcon configuration is required");

        this.galaxy = galaxy;
        this.autonomy = config.autonomy();
        this.departure = config.departure();
        this.arrival = config.arrival();
        this.arrivalReachable = isArrivalReachable(galaxy, autonomy, departure, arrival);
    }

    /**
     * Runs a Dijkstra over the travel states, where each move costs the number of bounty
     * hunter encounters it causes, and states past the countdown are never explored. The
     * cheapest path to the arrival gives the fewest possible encounters.
     *
     * @return the odds of success, from 0 when the arrival cannot be reached in time to 1
     */
    public double compute(EmpirePlan plan) {
        return itinerary(plan).odds();
    }

    /** Same search as {@link #compute}, but also returns the safest journey it found. */
    public Itinerary itinerary(EmpirePlan plan) {
        Require.that(plan != null, "empire plan is required");
        if (!arrivalReachable) {
            return Itinerary.unreachable();
        }

        BountyHunterSchedule schedule = new BountyHunterSchedule(plan.bountyHunters());
        int countdown = plan.countdown();

        State start = new State(departure, 0, autonomy);

        // Here we assume that the departure planet could be watched at day 0
        int startCost = schedule.isWatched(departure, 0) ? 1 : 0;

        Map<State, Integer> best = new HashMap<>();
        Map<State, State> previous = new HashMap<>(); // where each state was best reached from
        PriorityQueue<Node> queue = new PriorityQueue<>(comparingInt(Node::encounters)); // weight -> time of encountering hunters
        best.put(start, startCost);
        queue.add(new Node(start, startCost));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            State state = current.state();

            if (current.encounters() > best.getOrDefault(state, Integer.MAX_VALUE)) {
                continue; // don't consider a heavier path
            }

            if (state.planet().equals(arrival)) {
                return new Itinerary(SuccessOdds.afterEncounters(current.encounters()),
                        journeyTo(state, previous, schedule));
            }

            for (State next : successors(state, countdown)) {
                int weight = schedule.isWatched(next.planet(), next.day()) ? 1 : 0;
                int cost = current.encounters() + weight;

                if (cost < best.getOrDefault(next, Integer.MAX_VALUE)) {
                    best.put(next, cost);
                    previous.put(next, state);
                    queue.add(new Node(next, cost));
                }
            }
        }
        return Itinerary.unreachable();
    }

    /** Walks back from the arrival to the departure, then lists the states in travel order. */
    private static List<Step> journeyTo(State arrivalState, Map<State, State> previous,
            BountyHunterSchedule schedule) {
        List<Step> steps = new ArrayList<>();
        for (State state = arrivalState; state != null; state = previous.get(state)) {
            steps.add(0, new Step(state.planet(), state.day(), state.fuel(),
                    schedule.isWatched(state.planet(), state.day())));
        }
        return steps;
    }

    private List<State> successors(State state, int countdown) {
        List<Jump> neighbours = galaxy.neighboursOf(state.planet());
        List<State> validStates = new ArrayList<>();

        for (Jump jump : neighbours) {
            int arrivalDay = state.day() + jump.travelTime();
            if (arrivalDay <= countdown && jump.travelTime() <= state.fuel()) {
                validStates.add(new State(jump.planet(), arrivalDay, state.fuel() - jump.travelTime()));
            }
        }

        // Waiting can be a good strategy, take this opportunity to refill tank.
        if (state.day() + 1 <= countdown) {
            validStates.add(new State(state.planet(), state.day() + 1, autonomy));
        }

        return validStates;
    }


    private static boolean isArrivalReachable(Galaxy galaxy, int autonomy, String departure, String arrival) {
        Set<String> visited = new HashSet<>(List.of(departure));
        Deque<String> toVisit = new ArrayDeque<>(List.of(departure));

        while (!toVisit.isEmpty()) {
            String planet = toVisit.poll();
            if (planet.equals(arrival)) {
                return true;
            }
            for (Jump jump : galaxy.neighboursOf(planet)) {
                if (jump.travelTime() <= autonomy && visited.add(jump.planet())) {
                    toVisit.add(jump.planet());
                }
            }
        }
        return false;
    }
}
