package com.redblaze908.blazetweaks.events;

public final class TickControlHandler {

    private static boolean frozen = false;
    private static int stepTicks = 0;

    private TickControlHandler() {
    }

    public static boolean isFrozen() {
        return frozen;
    }

    public static int getRemainingStepTicks() {
        return stepTicks;
    }

    public static void freeze() {
        frozen = true;
        stepTicks = 0;
    }

    public static void unfreeze() {
        frozen = false;
        stepTicks = 0;
    }

    public static void step(int amount) {
        if (amount <= 0) {
            return;
        }

        frozen = true;
        stepTicks += amount;
    }

    public static boolean shouldRunWorldTick() {
        if (!frozen) {
            return true;
        }

        if (stepTicks > 0) {
            stepTicks--;
            return true;
        }

        return false;
    }
}