package com.falcon.falcon.config;

import com.falcon.falcon.utils.Require;

public record BountyHunter(String planet, int day) {

    public BountyHunter{
        Require.that(planet != null && !planet.isBlank(), "a planet is required for a hunter");
        Require.that(day >= 0, "day of presence must not be negative for a hunter");
    }

}
