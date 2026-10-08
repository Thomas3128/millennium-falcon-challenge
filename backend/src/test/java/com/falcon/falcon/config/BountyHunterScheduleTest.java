package com.falcon.falcon.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class BountyHunterScheduleTest {

    /** The intercepted plan of the examples*/
    private final BountyHunterSchedule schedule = new BountyHunterSchedule(List.of(
            new BountyHunter("Hoth", 6),
            new BountyHunter("Hoth", 7),
            new BountyHunter("Hoth", 8)));

    @Test
    void reports_a_watched_planet_on_a_watched_day() {
        assertThat(schedule.isWatched("Hoth", 6)).isTrue();
        assertThat(schedule.isWatched("Hoth", 7)).isTrue();
        assertThat(schedule.isWatched("Hoth", 8)).isTrue();
    }

    @Test
    void reports_nothing_on_the_same_planet_another_day() {
        assertThat(schedule.isWatched("Hoth", 5)).isFalse();
        assertThat(schedule.isWatched("Hoth", 9)).isFalse();
    }

    @Test
    void reports_nothing_on_another_planet_the_same_day() {
        assertThat(schedule.isWatched("Tatooine", 6)).isFalse();
        assertThat(schedule.isWatched("Endor", 6)).isFalse();
    }

    @Test
    void watches_the_very_first_day_of_the_mission() {
        BountyHunterSchedule fromDayZero = new BountyHunterSchedule(
                List.of(new BountyHunter("Tatooine", 0)));

        assertThat(fromDayZero.isWatched("Tatooine", 0)).isTrue();
    }

    @Test
    void counts_a_duplicated_presence_only_once() {
        BountyHunterSchedule withDuplicate = new BountyHunterSchedule(List.of(
                new BountyHunter("Hoth", 6),
                new BountyHunter("Hoth", 6)));

        assertThat(withDuplicate.isWatched("Hoth", 6)).isTrue();
    }

    @Test
    void watches_nothing_when_the_rebels_intercepted_no_plan() {
        BountyHunterSchedule empty = new BountyHunterSchedule(List.of());

        assertThat(empty.isWatched("Hoth", 6)).isFalse();
        assertThat(empty.isWatched("Tatooine", 0)).isFalse();
    }

    @Test
    void rejects_a_missing_hunter_list() {
        // EmpirePlan already refuses a missing list, so reaching this is a programming
        // error rather than bad user input: the NullPointerException is acceptable.
        assertThatThrownBy(() -> new BountyHunterSchedule(null))
                .isInstanceOf(NullPointerException.class);
    }
}
