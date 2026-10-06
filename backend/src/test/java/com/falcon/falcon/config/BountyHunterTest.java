package com.falcon.falcon.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class BountyHunterTest {

    @Test
    void accepts_day_zero_because_it_is_the_first_day_of_the_mission() {
        BountyHunter hunter = new BountyHunter("Tatooine", 0);

        assertThat(hunter.planet()).isEqualTo("Tatooine");
        assertThat(hunter.day()).isZero();
    }

    @Test
    void accepts_a_later_day() {
        assertThat(new BountyHunter("Hoth", 8).day()).isEqualTo(8);
    }

    @Test
    void rejects_a_negative_day() {
        assertThatThrownBy(() -> new BountyHunter("Hoth", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("day");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t" })
    void rejects_a_missing_or_blank_planet(String planet) {
        assertThatThrownBy(() -> new BountyHunter(planet, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("planet");
    }

}
