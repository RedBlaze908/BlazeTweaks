package com.redblaze908.blazetweaks.utils;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PotentialDungeonFinder {

    public static class SpawnerAttempt {
        public final BlockPos pos;
        public final int attemptNumber;
        public final ChunkPos chunkPos;
        public final boolean isRealSpawner;

        public SpawnerAttempt(BlockPos pos, int attemptNumber, ChunkPos chunkPos, boolean isRealSpawner) {
            this.pos = pos;
            this.attemptNumber = attemptNumber;
            this.chunkPos = chunkPos;
            this.isRealSpawner = isRealSpawner;
        }
    }

    public static List<SpawnerAttempt> getPotentialSpawnersForChunk(World world, ChunkPos chunkPos) {
        List<SpawnerAttempt> attempts = new ArrayList<>();

        long worldSeed = world.getSeed();
        Random rand = new Random(worldSeed);

        long xSeed = (rand.nextLong() / 2L) * 2L + 1L;
        long zSeed = (rand.nextLong() / 2L) * 2L + 1L;
        rand.setSeed((long) chunkPos.x * xSeed + (long) chunkPos.z * zSeed ^ worldSeed);

        for (int i = 0; i < 8; ++i) {
            int randX = rand.nextInt(16) + 8;
            int randY = rand.nextInt(256);
            int randZ = rand.nextInt(16) + 8;

            int blockX = (chunkPos.x << 4) + randX;
            int blockY = randY;
            int blockZ = (chunkPos.z << 4) + randZ;

            BlockPos calculatedPos = new BlockPos(blockX, blockY, blockZ);

            rand.nextInt(2);
            rand.nextInt(2);

            BlockPos finalPos = calculatedPos;
            boolean foundReal = false;

            if (world.isBlockLoaded(calculatedPos)) {
                for (BlockPos.MutableBlockPos mpos : BlockPos.getAllInBoxMutable(
                        calculatedPos.add(-3, -4, -3),
                        calculatedPos.add(3, 4, 3))) {

                    if (world.getBlockState(mpos).getBlock() == Blocks.MOB_SPAWNER) {
                        finalPos = mpos.toImmutable();
                        foundReal = true;
                        break;
                    }
                }
            }

            attempts.add(new SpawnerAttempt(finalPos, i, chunkPos, foundReal));
        }

        return attempts;
    }
}