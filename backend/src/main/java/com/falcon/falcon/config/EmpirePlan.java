package com.falcon.falcon.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmpirePlan(int countdown, 
    @JsonProperty("bounty_hunters") List<BountyHunter> bountyHunters) {

}
