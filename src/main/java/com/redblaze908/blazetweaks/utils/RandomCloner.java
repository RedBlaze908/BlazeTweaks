package com.redblaze908.blazetweaks.utils;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class RandomCloner {
    private static final Field SEED_FIELD;

    static {
        try {
            SEED_FIELD = Random.class.getDeclaredField("seed");
            SEED_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private RandomCloner() {
    }

    public static Random clone(Random source) {
        try {
            AtomicLong sourceSeed = (AtomicLong) SEED_FIELD.get(source);
            Random copy = new Random(0L);
            AtomicLong copySeedField = (AtomicLong) SEED_FIELD.get(copy);
            copySeedField.set(sourceSeed.get());
            return copy;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot clone Random via reflection", e);
        }
    }
}