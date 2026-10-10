package com.falcon.falcon.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest(properties = "falcon.config-file=../examples/example2/millennium-falcon.json")
@AutoConfigureMockMvc
class OddsControllerTest {

    @Autowired
    private MockMvcTester mvc;

    private MockMvcTester.MockMvcRequestBuilder postPlan(String json) {
        return mvc.post().uri("/api/odds").contentType(APPLICATION_JSON).content(json);
    }

    @Test
    void answers_the_odds_of_the_second_example() throws Exception {
        String plan = Files.readString(Path.of("../examples/example2/empire.json"));

        assertThat(postPlan(plan)).hasStatusOk().bodyJson().extractingPath("$.odds").isEqualTo(0.81);
    }

    @Test
    void answers_zero_when_the_countdown_is_too_short() throws Exception {
        String plan = Files.readString(Path.of("../examples/example1/empire.json"));

        assertThat(postPlan(plan)).hasStatusOk().bodyJson().extractingPath("$.odds").isEqualTo(0.0);
    }

    @Test
    void rejects_a_plan_that_breaks_a_validation_rule() {
        assertThat(postPlan("{\"countdown\": -1, \"bounty_hunters\": []}")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void rejects_a_body_that_is_not_json() {
        assertThat(postPlan("not json")).hasStatus(HttpStatus.BAD_REQUEST);
    }
}
