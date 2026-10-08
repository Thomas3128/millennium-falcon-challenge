package com.falcon.falcon.routes;

import com.falcon.falcon.utils.Require;

public record Route(String origin, String destination, int travelTime) {

    public Route{
            Require.that(origin != null && !origin.isBlank(),"origin is required");
            Require.that(destination != null && !destination.isBlank(),"destination is required");
            Require.that(travelTime >0,"travel time must be strictly positive");
    }
}
