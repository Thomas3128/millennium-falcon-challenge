package com.falcon.falcon.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class FalconConfigTest {

    private static FalconConfig config(int autonomy, String departure, String arrival, String routesDb) {
        return new FalconConfig(autonomy, departure, arrival, routesDb);
    }

    @Test
    void accepts_a_complete_configuration() {
        FalconConfig config = config(6, "Tatooine", "Endor", "universe.db");

        assertThat(config.autonomy()).isEqualTo(6);
        assertThat(config.departure()).isEqualTo("Tatooine");
        assertThat(config.arrival()).isEqualTo("Endor");
        assertThat(config.routesDb()).isEqualTo("universe.db");
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1 })
    void rejects_a_non_positive_autonomy(int autonomy) {
        assertThatThrownBy(() -> config(autonomy, "Tatooine", "Endor", "universe.db"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("autonomy");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t" })
    void rejects_a_missing_or_blank_departure(String departure) {
        assertThatThrownBy(() -> config(6, departure, "Endor", "universe.db"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("departure");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t" })
    void rejects_a_missing_or_blank_arrival(String arrival) {
        assertThatThrownBy(() -> config(6, "Tatooine", arrival, "universe.db"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("arrival");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t" })
    void rejects_a_missing_or_blank_routes_db(String routesDb) {
        assertThatThrownBy(() -> config(6, "Tatooine", "Endor", routesDb))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("routes_db");
    }
}
