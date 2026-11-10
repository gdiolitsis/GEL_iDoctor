package com.gel.cleaner;

/** Suppresses stale sensor results across pause, restart and repeated runs. */
public final class SensorRunGate {
    private long generation = 0L;
    private boolean active = false;

    public synchronized long begin() {
        if (active) return -1L;
        active = true;
        return ++generation;
    }

    public synchronized void cancel() {
        active = false;
        generation++;
    }

    public synchronized boolean isActive() { return active; }

    public synchronized boolean complete(long token) {
        if (!active || token != generation) return false;
        active = false;
        return true;
    }
}
