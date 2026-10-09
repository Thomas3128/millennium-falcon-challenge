package com.falcon.falcon.cli;

import java.nio.file.Path;

import com.falcon.falcon.app.Mission;

/**
 * Prints the odds of success as a percentage on standard output.
 */
public class GiveMeTheOdds {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: give-me-the-odds <millennium-falcon.json> <empire.json>");
            System.exit(1);
        }

        try {
            Mission mission = new Mission(Path.of(args[0]));
            System.out.println(Math.round(mission.getOdds(Path.of(args[1])) * 100));

        } catch (RuntimeException failure) {
            // Report the message rather than a stack trace, the type when there is none.
            String message = failure.getMessage() != null ? failure.getMessage() : failure.toString();
            System.err.println(message);
            System.exit(2);
        }
    }
}
