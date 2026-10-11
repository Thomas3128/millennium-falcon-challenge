package com.falcon.falcon.api;

import java.util.List;

import com.falcon.falcon.odds.Step;

/**
 * The shape of the answer, mirroring the answer.json files of the statement: a fraction
 * between 0 and 1, left to the front end to render as a percentage. The path lists the
 * safest journey found, and is empty when the arrival cannot be reached in time.
 */
public record OddsResponse(double odds, List<Step> path) {
}
