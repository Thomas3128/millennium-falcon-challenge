package com.falcon.falcon.app;

import java.nio.file.Path;

import com.falcon.falcon.config.ConfigLoader;
import com.falcon.falcon.config.FalconConfig;
import com.falcon.falcon.odds.OddsCalculator;
import com.falcon.falcon.routes.Galaxy;
import com.falcon.falcon.routes.RoutesReader;

public class Mission {

    private final ConfigLoader configLoader = new ConfigLoader();
    private final OddsCalculator calculator;

    public Mission(Path falconConfigPath){
        FalconConfig falconConfig = configLoader.loadFalconConfig(falconConfigPath);
        Path routesDbPath = falconConfig.resolveRouteDbPath(falconConfigPath);
        Galaxy galaxy = new Galaxy(new RoutesReader(routesDbPath).findAll());
        this.calculator =  new OddsCalculator(galaxy, falconConfig);
    }

    public double getOdds(Path empirePlantPath){
        return calculator.compute(configLoader.loadEmpirePlan(empirePlantPath));
    }
}
