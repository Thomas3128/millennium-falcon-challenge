package com.falcon.falcon.odds;

import com.falcon.falcon.utils.Require;

public final class SuccessOdds {

    private SuccessOdds(){
    }

    public static double afterEncounters(int encounters){
        Require.that(encounters>=0, "number of encounters must be positive or null");
        return Math.pow(0.9, encounters);
    }

    /**
     * Converts odds into the whole percentage shown to the user, rounded to the nearest
     * integer but never down to 0 when the target is reachable: the statement reserves
     * 0% for a target that cannot be reached at all.
     */
    public static long toPercentage(double odds){
        Require.that(odds >= 0 && odds <= 1, "odds must be between 0 and 1, was " + odds);
        long percentage = Math.round(odds * 100);
        return odds > 0 ? Math.max(1, percentage) : 0;
    }
}
