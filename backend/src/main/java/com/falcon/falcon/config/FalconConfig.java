package com.falcon.falcon.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FalconConfig(int autonomy, String departure, String arrival, 
    @JsonProperty("routes_db") String routesDb) {
        
        public FalconConfig {
            Require.that(autonomy > 0,"autonomy must be strictly positive");
            Require.that(departure != null && !departure.isBlank(),"departure is required");
            Require.that(arrival != null && !arrival.isBlank(),"arrival is required");
            Require.that(routesDb != null && !routesDb.isBlank(),"routes_db is required");
        }
}
