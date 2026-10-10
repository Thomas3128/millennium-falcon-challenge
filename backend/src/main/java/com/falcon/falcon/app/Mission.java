package com.falcon.falcon.app;

import java.nio.file.Path;

import com.falcon.falcon.config.ConfigLoader;
import com.falcon.falcon.config.EmpirePlan;
import com.falcon.falcon.config.FalconConfig;
import com.falcon.falcon.odds.OddsCalculator;
import com.falcon.falcon.routes.Galaxy;
import com.falcon.falcon.routes.RoutesReader;

/**
 * Loads the configuration and the routes once, then answers any number of empire plans.
 * Both the CLI and the web API go through it.
 */
public class Mission {

    private final ConfigLoader configLoader = new ConfigLoader();
    private final OddsCalculator calculator;

    public Mission(Path falconConfigPath) {
        FalconConfig falconConfig = configLoader.loadFalconConfig(falconConfigPath);
        Path routesDbPath = falconConfig.resolveRouteDbPath(falconConfigPath);
        Galaxy galaxy = new Galaxy(new RoutesReader(routesDbPath).findAll());
        this.calculator = new OddsCalculator(galaxy, falconConfig);
    }

    /** Used by the web API, which receives the plan in a request body. */
    public double odds(EmpirePlan plan) {
        return calculator.compute(plan);
    }

    /** Used by the command line interface, which receives a file path. */
    public double odds(Path empirePlanPath) {
        return odds(configLoader.loadEmpirePlan(empirePlanPath));
    }
}
