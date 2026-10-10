package com.falcon.falcon.api;

/** The body of an error answer: a reason the user can act on. */
public record ApiError(String error) {
}
