package com.falcon.falcon.odds;

import static java.util.Comparator.comparingInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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

    public OddsCalculator(Galaxy galaxy, FalconConfig config) {
        Require.that(galaxy != null, "galaxy is required");
        Require.that(config != null, "falcon configuration is required");

        this.galaxy = galaxy;
        this.autonomy = config.autonomy();
        this.departure = config.departure();
        this.arrival = config.arrival();
    }

    /**
     * The goal is to do a dijkstra where the weight is the number of time the
     * falcon encounters the hunters,
     * and to drop the paths leading to a countdown overlap.
     */
    public double compute(EmpirePlan plan) {
        Require.that(plan != null, "empire plan is required");

        BountyHunterSchedule schedule = new BountyHunterSchedule(plan.bountyHunters());
        int countdown = plan.countdown();

        State start = new State(departure, 0, autonomy);

        // Here we assume that the departure planet could be watched at day 0
        int startCost = schedule.isWatched(departure, 0) ? 1 : 0;

        Map<State, Integer> best = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(comparingInt(Node::encounters)); // weight -> time of encounterinig hunters
        best.put(start, startCost);
        queue.add(new Node(start, startCost));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            State state = current.state();

            if (current.encounters() > best.getOrDefault(state, Integer.MAX_VALUE)) {
                continue; // don't consider an heavier path
            }

            if (state.planet().equals(arrival)) {
                return SuccessOdds.afterEncounters(current.encounters());
            }

            for (State next : successors(state, countdown)) {
                int weight = schedule.isWatched(next.planet(), next.day()) ? 1 : 0;
                int cost = current.encounters() + weight;

                if (cost < best.getOrDefault(next, Integer.MAX_VALUE)) {
                    best.put(next, cost);
                    queue.add(new Node(next, cost));
                }
            }
        }
        return 0.0;
    }

    private List<State> successors(State state, int countdown) {
        List<Jump> neighbours = galaxy.neighboursOf(state.planet());
        List<State> validStates = new ArrayList<>();

        for (Jump jump : neighbours) {
            if (state.day() + jump.travelTime() <= countdown && jump.travelTime() <= state.fuel()) {
                validStates.add(
                        new State(jump.planet(), state.day() + jump.travelTime(), state.fuel() - jump.travelTime()));
            }
        }

        // Waiting can be a good strategy, take this opportunity to refill tank.
        if (state.day() + 1 <= countdown) {
            validStates.add(new State(state.planet(), state.day() + 1, autonomy));
        }

        return validStates;
    }
}
