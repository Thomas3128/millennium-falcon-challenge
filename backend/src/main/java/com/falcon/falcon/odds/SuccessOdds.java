package com.falcon.falcon.odds;

import com.falcon.falcon.utils.Require;

public class SuccessOdds {

    private SuccessOdds(){
    }

    public static double afterEncounters(int k){
        Require.that(k>=0, "number of time hunters encounters must be positive or null");
        return Math.pow(0.9, k);
    }
}
