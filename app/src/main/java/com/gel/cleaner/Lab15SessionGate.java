package com.gel.cleaner;

/** Pure Lab 15 callback gate. Pending work from a prior session is never current evidence. */
final class Lab15SessionGate {
    private Lab15SessionGate() {}

    static boolean mayContinue(int callbackGeneration, int currentGeneration,
                               boolean running, boolean finished, boolean activityFinishing) {
        return callbackGeneration == currentGeneration
                && running && !finished && !activityFinishing;
    }
}
