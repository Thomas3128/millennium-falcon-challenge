package com.falcon.falcon.app;

import java.nio.file.Path;
import java.util.List;

import com.falcon.falcon.config.ConfigLoader;
import com.falcon.falcon.config.EmpirePlan;
import com.falcon.falcon.config.FalconConfig;
import com.falcon.falcon.odds.Itinerary;
import com.falcon.falcon.odds.OddsCalculator;
import com.falcon.falcon.routes.Galaxy;
import com.falcon.falcon.routes.Route;
import com.falcon.falcon.routes.RoutesReader;

/**
 * Loads the configuration and the routes once, then answers any number of empire plans.
 * Both the CLI and the web API go through it.
 */
public class Mission {

    private final ConfigLoader configLoader = new ConfigLoader();
    private final FalconConfig falconConfig;
    private final List<Route> routes;
    private final OddsCalculator calculator;

    public Mission(Path falconConfigPath) {
        this.falconConfig = configLoader.loadFalconConfig(falconConfigPath);
        Path routesDbPath = falconConfig.resolveRouteDbPath(falconConfigPath);
        this.routes = List.copyOf(new RoutesReader(routesDbPath).findAll());
        this.calculator = new OddsCalculator(new Galaxy(routes), falconConfig);
    }

    /** Used by the web API, which receives the plan in a request body. */
    public double odds(EmpirePlan plan) {
        return calculator.compute(plan);
    }

    /** Used by the command line interface, which receives a file path. */
    public double odds(Path empirePlanPath) {
        return odds(configLoader.loadEmpirePlan(empirePlanPath));
    }

    /** The odds together with the safest journey, for the web page to draw. */
    public Itinerary itinerary(EmpirePlan plan) {
        return calculator.itinerary(plan);
    }

    public FalconConfig falconConfig() {
        return falconConfig;
    }

    public List<Route> routes() {
        return routes;
    }
}
