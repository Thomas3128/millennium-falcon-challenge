package com.falcon.falcon.utils;

public final class Require {

    private Require() {
    }

    public static void that(boolean condition, String message){
        if(!condition){
            throw new IllegalArgumentException(message);
        }
    }

}
