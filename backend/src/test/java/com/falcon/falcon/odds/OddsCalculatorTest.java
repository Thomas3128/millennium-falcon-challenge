package com.falcon.falcon.odds;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.falcon.falcon.config.BountyHunter;
import com.falcon.falcon.config.EmpirePlan;
import com.falcon.falcon.config.FalconConfig;
import com.falcon.falcon.routes.Galaxy;
import com.falcon.falcon.routes.Route;

class OddsCalculatorTest {

    private static final double PRECISION = 1e-9;

    /** The universe of the four examples of the statement. */
    private static final Galaxy GALAXY = new Galaxy(List.of(
            new Route("Tatooine", "Dagobah", 6),
            new Route("Dagobah", "Endor", 4),
            new Route("Dagobah", "Hoth", 1),
            new Route("Hoth", "Endor", 1),
            new Route("Tatooine", "Hoth", 6)));

    private static final FalconConfig FALCON = new FalconConfig(6, "Tatooine", "Endor", "universe.db");

    /** The intercepted plan shared by the four examples: Hoth, days 6 to 8. */
    private static final List<BountyHunter> HUNTERS_ON_HOTH = List.of(
            new BountyHunter("Hoth", 6),
            new BountyHunter("Hoth", 7),
            new BountyHunter("Hoth", 8));

    private final OddsCalculator calculator = new OddsCalculator(GALAXY, FALCON);

    @ParameterizedTest(name = "example with countdown {0} gives {1}")
    @CsvSource({
            "7,  0.0",   // example 1: Endor is out of reach, refuelling costs one day too many
            "8,  0.81",  // example 2: via Hoth, caught twice on the way
            "9,  0.9",   // example 3: via Dagobah, Hoth crossed once while watched
            "10, 1.0"    // example 4: one idle day on a full tank dodges every hunter
    })
    void matches_the_four_examples_of_the_statement(int countdown, double expected) {
        double odds = calculator.compute(new EmpirePlan(countdown, HUNTERS_ON_HOTH));

        assertThat(odds).isCloseTo(expected, within(PRECISION));
    }

    @Test
    void succeeds_without_moving_when_the_falcon_already_sits_on_the_target() {
        OddsCalculator alreadyThere = new OddsCalculator(GALAXY,
                new FalconConfig(6, "Endor", "Endor", "universe.db"));

        assertThat(alreadyThere.compute(new EmpirePlan(0, List.of()))).isEqualTo(1.0);
    }

    @Test
    void crosses_safely_when_the_rebels_intercepted_no_plan() {
        assertThat(calculator.compute(new EmpirePlan(10, List.of()))).isEqualTo(1.0);
    }

    @Test
    void counts_hunters_already_waiting_on_the_departure_planet_on_day_zero() {
        // Documented assumption: the crew is on Tatooine that day, so the watch counts.
        // It cannot be dodged, hence one unavoidable encounter and no more.
        EmpirePlan ambushAtHome = new EmpirePlan(10, List.of(new BountyHunter("Tatooine", 0)));

        assertThat(calculator.compute(ambushAtHome)).isCloseTo(0.9, within(PRECISION));
    }

    @Test
    void finds_the_safest_path_rather_than_the_shortest_one() {
        // Hoth is the fast way out of Tatooine but is watched on arrival day 6;
        // Dagobah costs the same six days and is clear, so the detour wins.
        EmpirePlan hothIsWatched = new EmpirePlan(9, List.of(new BountyHunter("Hoth", 6)));

        assertThat(calculator.compute(hothIsWatched)).isEqualTo(1.0);
    }

    @Test
    void gives_up_at_once_when_no_route_leads_to_the_target() {
        // Without a reachability check, the search would try every day of the countdown
        // before concluding that Coruscant cannot be reached.
        OddsCalculator towardsNowhere = new OddsCalculator(GALAXY,
                new FalconConfig(6, "Tatooine", "Coruscant", "universe.db"));
        EmpirePlan endlessCountdown = new EmpirePlan(1_000_000, List.of());

        double odds = assertTimeout(Duration.ofSeconds(1), () -> towardsNowhere.compute(endlessCountdown));

        assertThat(odds).isEqualTo(0.0);
    }

    @Test
    void gives_up_at_once_when_the_only_route_is_longer_than_the_autonomy() {
        // Endor is on the map, but a 10-day jump never fits in a 6-day tank.
        Galaxy galaxy = new Galaxy(List.of(
                new Route("Tatooine", "Dagobah", 1),
                new Route("Dagobah", "Hoth", 1),
                new Route("Hoth", "Endor", 10)));
        OddsCalculator calculator = new OddsCalculator(galaxy, FALCON);
        EmpirePlan endlessCountdown = new EmpirePlan(1_000_000, List.of());

        double odds = assertTimeout(Duration.ofSeconds(1), () -> calculator.compute(endlessCountdown));

        assertThat(odds).isEqualTo(0.0);
    }

    @Test
    void returns_the_only_journey_of_the_second_example() {
        // Within 8 days the only way is: Tatooine to Hoth, refuel on Hoth, then Endor.
        Itinerary itinerary = calculator.itinerary(new EmpirePlan(8, HUNTERS_ON_HOTH));

        assertThat(itinerary.steps()).containsExactly(
                new Step("Tatooine", 0, 6, false),
                new Step("Hoth", 6, 0, true),
                new Step("Hoth", 7, 6, true),
                new Step("Endor", 8, 5, false));
    }

    @Test
    void returns_a_journey_without_any_watched_step_when_the_odds_are_certain() {
        Itinerary itinerary = calculator.itinerary(new EmpirePlan(10, HUNTERS_ON_HOTH));

        assertThat(itinerary.odds()).isEqualTo(1.0);
        assertThat(itinerary.steps()).first().isEqualTo(new Step("Tatooine", 0, 6, false));
        assertThat(itinerary.steps()).last().extracting(Step::planet).isEqualTo("Endor");
        assertThat(itinerary.steps()).noneMatch(Step::watched);
    }

    @Test
    void returns_no_journey_when_the_arrival_is_out_of_reach() {
        Itinerary itinerary = calculator.itinerary(new EmpirePlan(7, HUNTERS_ON_HOTH));

        assertThat(itinerary.odds()).isEqualTo(0.0);
        assertThat(itinerary.steps()).isEmpty();
    }
}
