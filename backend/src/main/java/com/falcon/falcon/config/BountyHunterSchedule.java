package com.falcon.falcon.config;

import java.util.List;
import java.util.Set;

public class BountyHunterSchedule {

    private final Set<BountyHunter> presences;

    public BountyHunterSchedule(List<BountyHunter> hunters) {
        this.presences = Set.copyOf(hunters);
    }

    public boolean isWatched(String planet, int day) {
        return presences.contains(new BountyHunter(planet, day));
    }
}
