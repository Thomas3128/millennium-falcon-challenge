package com.falcon.falcon.api;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.falcon.falcon.app.Mission;
import com.falcon.falcon.config.EmpirePlan;
import com.falcon.falcon.config.FalconConfig;
import com.falcon.falcon.odds.Itinerary;

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
        Itinerary itinerary = mission.itinerary(plan);
        return new OddsResponse(itinerary.odds(), itinerary.steps());
    }

    @GetMapping("/api/galaxy")
    GalaxyResponse galaxy() {
        FalconConfig config = mission.falconConfig();
        return new GalaxyResponse(config.departure(), config.arrival(), config.autonomy(), mission.routes());
    }

    /**
     * Spring raises this when the request body cannot become an EmpirePlan. When one of
     * our validation rules is the cause, its message tells the user what to fix.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> invalidPlan(HttpMessageNotReadableException failure) {
        Throwable root = failure.getRootCause();
        String reason = root instanceof IllegalArgumentException
                ? root.getMessage()
                : "malformed JSON, or missing or unknown fields";
        return ResponseEntity.badRequest().body(new ApiError(reason));
    }
}
