package com.redblaze908.blazetweaks.eid;

import java.util.concurrent.atomic.AtomicInteger;

public final class EntityIdFixState {
    private static volatile boolean enabled = false;

    private static final AtomicInteger suppressed = new AtomicInteger();

    private EntityIdFixState() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public static void recordSuppressed() {
        suppressed.incrementAndGet();
    }

    public static int getSuppressed() {
        return suppressed.get();
    }
}