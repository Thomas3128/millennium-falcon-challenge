package com.falcon.falcon.api;

import java.util.List;

import com.falcon.falcon.routes.Route;

/** The map the server loaded at startup, for the web page to draw. */
public record GalaxyResponse(String departure, String arrival, int autonomy, List<Route> routes) {
}
