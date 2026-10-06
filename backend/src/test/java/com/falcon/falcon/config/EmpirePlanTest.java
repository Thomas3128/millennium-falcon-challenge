package com.falcon.falcon.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class EmpirePlanTest {

    @Test
    void accepts_a_valid_plan() {
        EmpirePlan plan = new EmpirePlan(8, List.of(new BountyHunter("Hoth", 6)));

        assertThat(plan.countdown()).isEqualTo(8);
        assertThat(plan.bountyHunters()).containsExactly(new BountyHunter("Hoth", 6));
    }

    @Test
    void accepts_an_empty_bounty_hunter_list_because_it_means_no_danger() {
        EmpirePlan plan = new EmpirePlan(8, List.of());

        assertThat(plan.bountyHunters()).isEmpty();
    }

    @Test
    void accepts_a_countdown_of_zero() {
        assertThat(new EmpirePlan(0, List.of()).countdown()).isZero();
    }

    @Test
    void rejects_a_negative_countdown() {
        assertThatThrownBy(() -> new EmpirePlan(-1, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("countdown");
    }

    @Test
    void rejects_a_missing_bounty_hunter_list() {
        assertThatThrownBy(() -> new EmpirePlan(8, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bounty_hunters");
    }
}
