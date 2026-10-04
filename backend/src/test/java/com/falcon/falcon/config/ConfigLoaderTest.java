package com.falcon.falcon.config;


import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class ConfigLoaderTest {

    private final ConfigLoader configLoader = new ConfigLoader();

    @Test
    void loadFalconConfig(){
        FalconConfig falconConfig = configLoader.loadFalconConfig(
            Path.of("../examples/example1/millennium-falcon.json")
        );

        assertThat(falconConfig.autonomy()).isEqualTo(6);
        assertThat(falconConfig.departure()).isEqualTo("Tatooine");
        assertThat(falconConfig.arrival()).isEqualTo("Endor");
        assertThat(falconConfig.routesDb()).isEqualTo("universe.db");
    }

    @Test
    void loadEmpirePlan(){
        EmpirePlan empirePlan = configLoader.loadEmpirePlan(
            Path.of("../examples/example1/empire.json")
        );

        assertThat(empirePlan.countdown()).isEqualTo(7);

        assertThat(empirePlan.bountyHunters()).containsExactly(
            new BountyHunter("Hoth",6),
            new BountyHunter("Hoth",7),
            new BountyHunter("Hoth",8)
        );
    }

    @Test
    void loadFakeConfig(){
        assertThatThrownBy(() -> configLoader.loadFalconConfig(Path.of("this_file_does_not_exist.json"))).isInstanceOf(JacksonIOException.class);
    }

    @Test
    void loadWrongConfig(){
            assertThatThrownBy(() -> configLoader.loadFalconConfig(Path.of("../examples/example1/empire.json"))).isInstanceOf(MismatchedInputException.class);
    }




}
