package com.falcon.falcon.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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

    @Test
    void resolves_a_relative_routes_db_next_to_the_config_file() {
        Path configPath = Path.of("../examples/example1/millennium-falcon.json");

        Path resolved = config(6, "Tatooine", "Endor", "universe.db").resolveRouteDbPath(configPath);

        assertThat(resolved).exists();
        assertThat(resolved.normalize())
                .isEqualTo(Path.of("../examples/example1/universe.db").toAbsolutePath().normalize());
    }

    @Test
    void keeps_an_absolute_routes_db_unchanged(@TempDir Path tmp) {
        Path absoluteDb = tmp.resolve("universe.db").toAbsolutePath();
        Path configPath = Path.of("../examples/example1/millennium-falcon.json");

        Path resolved = config(6, "Tatooine", "Endor", absoluteDb.toString()).resolveRouteDbPath(configPath);

        assertThat(resolved).isEqualTo(absoluteDb);
    }

    @Test
    void handles_a_config_file_without_a_parent_directory() {
        FalconConfig config = config(6, "Tatooine", "Endor", "universe.db");

        assertThatCode(() -> config.resolveRouteDbPath(Path.of("millennium-falcon.json")))
                .doesNotThrowAnyException();
    }
}
