package com.falcon.falcon.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


class MissionTest {

    private static final double PRECISION = 1e-9;

    private static Path example(int number, String fileName) {
        return Path.of("../examples/example" + number, fileName);
    }

    @ParameterizedTest(name = "example {0} gives {1}")
    @CsvSource({
            "1, 0.0",
            "2, 0.81",
            "3, 0.9",
            "4, 1.0"
    })
    void reproduces_the_examples_from_their_own_files(int number, double expected) {
        Mission mission = new Mission(example(number, "millennium-falcon.json"));

        double odds = mission.getOdds(example(number, "empire.json"));

        assertThat(odds).isCloseTo(expected, within(PRECISION));
    }

    @Test
    void resolves_the_database_relatively_to_the_configuration_file() {
        // The config says only "universe.db": finding it proves the path was resolved
        // against the folder of millennium-falcon.json, not the working directory.
        assertThatThrownBy(() -> new Mission(Path.of("universe.db")))
                .isInstanceOf(RuntimeException.class);

        assertThat(new Mission(example(2, "millennium-falcon.json"))).isNotNull();
    }

    @Test
    void fails_when_the_configuration_file_does_not_exist() {
        assertThatThrownBy(() -> new Mission(Path.of("no_such_config.json")))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void fails_when_the_empire_plan_does_not_exist() {
        Mission mission = new Mission(example(2, "millennium-falcon.json"));

        assertThatThrownBy(() -> mission.getOdds(Path.of("no_such_plan.json")))
                .isInstanceOf(RuntimeException.class);
    }
}
