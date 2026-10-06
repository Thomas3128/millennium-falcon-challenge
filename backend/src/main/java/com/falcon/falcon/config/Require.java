package com.falcon.falcon.config;

public final class Require {
    
    private Require(){}

    static void that(boolean condition, String message){
        if(!condition){
            throw new IllegalArgumentException(message);
        }
    }

}
