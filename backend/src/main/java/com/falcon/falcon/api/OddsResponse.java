package com.falcon.falcon.api;

/**
 * The shape of the answer, mirroring the answer.json files of the statement:
 * a fraction between 0 and 1, left to the front end to render as a percentage.
 */
public record OddsResponse(double odds) {
}
