package com.falcon.falcon.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.ValueInstantiationException;

/**
 * Covers the JSON mapping concerns: field naming, unreadable or malformed
 * input, and the fact that a validation failure is not swallowed on the way.
 * The validation rules themselves are tested on the records.
 */
class ConfigLoaderTest {

    private static final Path EXAMPLE1 = Path.of("../examples/example1");

    private final ConfigLoader configLoader = new ConfigLoader();

    @Test
    void maps_every_falcon_config_field_including_the_snake_case_one() {
        FalconConfig falconConfig = configLoader.loadFalconConfig(EXAMPLE1.resolve("millennium-falcon.json"));

        assertThat(falconConfig.autonomy()).isEqualTo(6);
        assertThat(falconConfig.departure()).isEqualTo("Tatooine");
        assertThat(falconConfig.arrival()).isEqualTo("Endor");
        assertThat(falconConfig.routesDb()).isEqualTo("universe.db");
    }

    @Test
    void maps_the_nested_bounty_hunter_list() {
        EmpirePlan empirePlan = configLoader.loadEmpirePlan(EXAMPLE1.resolve("empire.json"));

        assertThat(empirePlan.countdown()).isEqualTo(7);
        assertThat(empirePlan.bountyHunters()).containsExactly(
                new BountyHunter("Hoth", 6),
                new BountyHunter("Hoth", 7),
                new BountyHunter("Hoth", 8));
    }

    @Test
    void accepts_an_empty_bounty_hunter_array(@TempDir Path tmp) throws IOException {
        // Documented decision: an empty array means "no danger", unlike a missing key.
        Path file = tmp.resolve("empire.json");
        Files.writeString(file, "{\"countdown\": 7, \"bounty_hunters\": []}");

        EmpirePlan empirePlan = configLoader.loadEmpirePlan(file);

        assertThat(empirePlan.countdown()).isEqualTo(7);
        assertThat(empirePlan.bountyHunters()).isEmpty();
    }

    @Test
    void fails_when_the_file_does_not_exist() {
        assertThatThrownBy(() -> configLoader.loadFalconConfig(Path.of("this_file_does_not_exist.json")))
                .isInstanceOf(JacksonIOException.class);
    }

    @Test
    void fails_when_the_file_holds_a_different_shape() {
        assertThatThrownBy(() -> configLoader.loadFalconConfig(EXAMPLE1.resolve("empire.json")))
                .isInstanceOf(MismatchedInputException.class);
    }

    @Test
    void fails_when_a_primitive_field_is_missing(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("millennium-falcon.json");
        Files.writeString(file, "{}");

        assertThatThrownBy(() -> configLoader.loadFalconConfig(file))
                .isInstanceOf(MismatchedInputException.class);
    }

    @Test
    void surfaces_a_validation_failure_instead_of_swallowing_it(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("millennium-falcon.json");
        Files.writeString(file, "{\"autonomy\": 7}");

        assertThatThrownBy(() -> configLoader.loadFalconConfig(file))
                .isInstanceOf(ValueInstantiationException.class)
                .hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void resolve_relative_routes_db_next_to_the_config_file(){
        Path falconConfigPath = EXAMPLE1.resolve("millennium-falcon.json");
        FalconConfig falconConfig = configLoader.loadFalconConfig(falconConfigPath);

        Path resolved = falconConfig.resolveRouteDbPath(falconConfigPath);

        assertThat(resolved).exists();
        assertThat(resolved.normalize()).isEqualTo(EXAMPLE1.resolve("universe.db").toAbsolutePath().normalize());
    }

    @Test
    void keep_an_absolute_path_unchanged(@TempDir Path tmp){
        Path tmpPath = tmp.resolve("universe.db").toAbsolutePath();
        FalconConfig falconConfig = new FalconConfig(6, "Tatooine", "Endor", tmpPath.toString());

        Path resolved = falconConfig.resolveRouteDbPath(tmpPath);

        assertThat(resolved).isEqualTo(tmpPath);
    }

    @Test
        void handle_config_file_without_parent_directory(){
            FalconConfig falconConfig = new FalconConfig(6, "Tatooine", "Endor", "universe.db");
            
            assertThatCode(() -> falconConfig.resolveRouteDbPath(Path.of("millenium-falcon.json"))).doesNotThrowAnyException();
        }

}
