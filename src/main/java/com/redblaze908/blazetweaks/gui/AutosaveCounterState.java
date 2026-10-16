package com.redblaze908.blazetweaks.gui;

/**
 * Shared client/integrated-server state for the real autosave cadence.
 * This is advanced by the integrated server's tick method and reset only at
 * the saveAllWorlds call made from MinecraftServer.tick(). Manual saves do not
 * hit that injection point.
 */
public final class AutosaveCounterState {
    private static volatile int elapsedTicks;
    private static volatile boolean autosaveWasTriggeredThisTick;

    private AutosaveCounterState() {
    }

    /** Called at the autosave invocation inside MinecraftServer.tick(). */
    public static void onAutosaveTriggered() {
        elapsedTicks = 0;
        autosaveWasTriggeredThisTick = true;
    }

    /** Called at the tail of a completed integrated-server tick. */
    public static void onServerTickEnd() {
        if (autosaveWasTriggeredThisTick) {
            // Keep the counter at 0 for the tick in which the save actually ran.
            autosaveWasTriggeredThisTick = false;
            return;
        }
        if (elapsedTicks < Integer.MAX_VALUE) {
            elapsedTicks++;
        }
    }

    public static int getElapsedTicks() {
        return elapsedTicks;
    }

    public static void reset() {
        elapsedTicks = 0;
        autosaveWasTriggeredThisTick = false;
    }
}
