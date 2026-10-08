package com.falcon.falcon.config;

import java.util.List;

import com.falcon.falcon.utils.Require;
import com.fasterxml.jackson.annotation.JsonProperty;

public record EmpirePlan(int countdown, 
    @JsonProperty("bounty_hunters") List<BountyHunter> bountyHunters) {

        public EmpirePlan{
            Require.that(countdown >= 0, "initial countdown must not be negative");
            Require.that(bountyHunters != null, "bounty_hunters is required, but can be empty");
        }

}
