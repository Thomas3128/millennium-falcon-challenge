package com.falcon.falcon.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FalconConfig(int autonomy, String departure, String arrival, 
    @JsonProperty("routes_db") String routesDb) {

}
