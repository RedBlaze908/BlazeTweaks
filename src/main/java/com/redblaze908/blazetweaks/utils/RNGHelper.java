package com.redblaze908.blazetweaks.utils;

import java.util.ArrayList;
import java.util.List;

public class RNGHelper {
    // constant of LCG from java.util.random 48bit
    public static final long MULTIPLIER = 0x5DEECE66DL;
    public static final long ADDEND = 0xBL;
    public static final long MASK = (1L << 48) - 1;

    public static final long INVERSE_MULTIPLIER = 0xDFE05BCB1365L;

    // Advance 1 seed
    public static long nextSeed(long currentSeed) {
        return (currentSeed * MULTIPLIER + ADDEND) & MASK;
    }

    public static long previusSeed(long currentSeed) {
        return ((currentSeed - ADDEND) * INVERSE_MULTIPLIER) & MASK;
    }

    // Simulation of random.nextInt(bound) advance 1 seed
    public static int nextInt(long[] currentSeed, int bound) {
        currentSeed[0] = nextSeed(currentSeed[0]);
        // it takes the 31 bit
        int bits = (int) (currentSeed[0] >>> 17);

        int val = bits % bound;
        while (bits - val + (bound - 1) < 0) {
            currentSeed[0] = nextSeed(currentSeed[0]);
            bits = (int) (currentSeed[0] >>> 17);
            val = bits % bound;
        }

        return val;
    }

    // Simulation of random.nextFloat()
    public static float nextFloat(long[] currentSeed) {
        currentSeed[0] = nextSeed(currentSeed[0]);
        int bits = (int) (currentSeed[0] >>> 24);
        return bits / (float) (1 << 24);
    }

    // Initialize the RNG from a starting seed
    public static long initialScramble(long seed) {
        return (seed ^ MULTIPLIER);
    }

    public static List<Long> crackSeedFromInts(int val1, int val2, int bound) {
        List<Long> possibleSeeds = new ArrayList<>();
        long internal1 = ((long) val1) << 17;

        for (int i = 0; i < 65536; ++i) {
            long candidateSeed = internal1 | i;
            long next = nextSeed(candidateSeed);
            int testVal2 = (int) ((next >>> 17) % bound);

            if (testVal2 == val2) {
                possibleSeeds.add(candidateSeed);
            }
        }
        return possibleSeeds;
    }

    // === Lightning & Weather ===
    public static int getNextClearThunderDuration(long[] seedArray) {
        return nextInt(seedArray, 168000) + 12000;
    }

    public static int getNextThunderDuration(long[] seedArray) {
        return nextInt(seedArray, 12000) + 3600;
    }

}
