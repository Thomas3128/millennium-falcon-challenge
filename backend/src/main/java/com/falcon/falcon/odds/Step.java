package com.falcon.falcon.odds;

/** One state of the journey, and whether bounty hunters wait there on that day. */
public record Step(String planet, int day, int fuel, boolean watched) {
}
