package com.falcon.falcon.api;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
