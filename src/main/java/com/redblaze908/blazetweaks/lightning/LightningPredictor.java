package com.redblaze908.blazetweaks.lightning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class LightningPredictor {

    public static final class Prediction {
        public final int chunkIndex;
        public final int chunkX, chunkZ;
        public final int blockX, blockZ;
        public final int regionX, regionZ;
        public final double trapValue;
        public final boolean chargedCreeperEligible;
        public final boolean skeletonTrapEligible;

        public Prediction(int chunkIndex, int chunkX, int chunkZ, double trapValue) {
            this.chunkIndex = chunkIndex;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.blockX = chunkX * 16;
            this.blockZ = chunkZ * 16;
            this.regionX = chunkX >> 5;
            this.regionZ = chunkZ >> 5;
            this.trapValue = trapValue;
            this.chargedCreeperEligible = trapValue >= 0.0675D;
            this.skeletonTrapEligible = trapValue < 0.06D;
        }
    }

    private LightningPredictor() {
    }

    /**
     * @param liveRand
     * @param centerX
     * @param centerZ
     * @param radius
     *
     * @param maxResults
     */
    public static List<Prediction> predict(Random liveRand, int centerX, int centerZ, int radius, int maxResults) {
        Random rand = com.redblaze908.blazetweaks.utils.RandomCloner.clone(liveRand);
        List<int[]> candidates = buildCandidateChunks(centerX, centerZ, radius);

        List<Prediction> results = new ArrayList<>();

        int safetyLimitLoops = 500;
        int loop = 0;

        while (results.size() < maxResults && loop < safetyLimitLoops) {
            for (int chunkIdx = 0; chunkIdx < candidates.size(); chunkIdx++) {
                int lightningValue = rand.nextInt(100000);

                if (lightningValue == 0) {
                    rand.nextInt();
                    double trapValue = rand.nextDouble();

                    int[] chunk = candidates.get(chunkIdx);
                    results.add(new Prediction(chunkIdx, chunk[0], chunk[1], trapValue));

                    if (results.size() >= maxResults) {
                        break;
                    }
                }
            }
            loop++;
        }

        return results;
    }

    private static List<int[]> buildCandidateChunks(int centerX, int centerZ, int radius) {
        List<int[]> list = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double dx = x * 16 + 8 - 8;
                double dz = z * 16 + 8 - 8;

                if (dx * dx + dz * dz <= 128 * 128) {
                    list.add(new int[] { centerX + x, centerZ + z });
                }
            }
        }

        return list;
    }
}