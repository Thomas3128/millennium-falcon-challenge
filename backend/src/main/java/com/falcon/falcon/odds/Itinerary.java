package com.falcon.falcon.odds;

import java.util.List;

/** The odds of success, and the safest journey that achieves them: empty when none does. */
public record Itinerary(double odds, List<Step> steps) {

    static Itinerary unreachable() {
        return new Itinerary(0.0, List.of());
    }
}
