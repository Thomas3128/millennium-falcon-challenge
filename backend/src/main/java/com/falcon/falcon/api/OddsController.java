package com.falcon.falcon.api;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.falcon.falcon.app.Mission;
import com.falcon.falcon.config.EmpirePlan;

/**
 * The onboard computer, reachable over HTTP. Spring creates a single instance when the
 * server starts, so the galaxy is read once and shared by every request.
 */
@RestController
public class OddsController {

    private final Mission mission;

    OddsController(@Value("${falcon.config-file}") String falconConfigFile) {
        this.mission = new Mission(Path.of(falconConfigFile));
    }

    @PostMapping("/api/odds")
    OddsResponse odds(@RequestBody EmpirePlan plan) {
        return new OddsResponse(mission.odds(plan));
    }
}
