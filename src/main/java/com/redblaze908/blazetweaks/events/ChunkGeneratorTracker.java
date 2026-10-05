package com.redblaze908.blazetweaks.events;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ChunkGeneratorTracker {
    public static final Map<ChunkPos, List<BlockPos>> ATTEMPT_MAP = new HashMap<>();

    @SubscribeEvent
    public void onChunkPopulate(PopulateChunkEvent.Populate event) {
        if (event.getType() != PopulateChunkEvent.Populate.EventType.DUNGEON) {
            return;
        }

        ChunkPos chunkPos = new ChunkPos(
                event.getChunkX(),
                event.getChunkZ());

        Random vanillaRandom = event.getRand();

        try {
            Random clonedRandom = cloneRandom(vanillaRandom);

            List<BlockPos> attempts = calculateDungeonPositions(
                    clonedRandom,
                    chunkPos);

            ATTEMPT_MAP.put(
                    chunkPos,
                    Collections.unmodifiableList(attempts));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<BlockPos> getOrComputeAttempts(
            World world,
            ChunkPos chunkPos) {
        List<BlockPos> cached = ATTEMPT_MAP.get(chunkPos);

        if (cached != null) {
            return cached;
        }

        try {
            Random rand = createChunkPopulationRandom(
                    world.getSeed(),
                    chunkPos);

            List<BlockPos> attempts = calculateDungeonPositions(
                    rand,
                    chunkPos);

            List<BlockPos> immutable = Collections.unmodifiableList(attempts);

            ATTEMPT_MAP.put(chunkPos, immutable);

            return immutable;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static List<BlockPos> calculateDungeonPositions(
            Random rand,
            ChunkPos chunkPos) {
        List<BlockPos> attempts = new ArrayList<>(8);

        int chunkStartX = chunkPos.x << 4;
        int chunkStartZ = chunkPos.z << 4;

        for (int i = 0; i < 8; ++i) {
            int x = chunkStartX + rand.nextInt(16) + 8;
            int y = rand.nextInt(256);
            int z = chunkStartZ + rand.nextInt(16) + 8;

            attempts.add(new BlockPos(x, y, z));

            rand.nextInt(2);
            rand.nextInt(2);
        }

        return attempts;
    }

    private static Random createChunkPopulationRandom(
            long worldSeed,
            ChunkPos chunkPos) {
        Random rand = new Random(worldSeed);

        long xSeed = (rand.nextLong() / 2L) * 2L + 1L;

        long zSeed = (rand.nextLong() / 2L) * 2L + 1L;

        rand.setSeed(
                (long) chunkPos.x * xSeed
                        + (long) chunkPos.z * zSeed
                        ^ worldSeed);

        return rand;
    }

    private static Random cloneRandom(Random original)
            throws Exception {
        Field seedField = Random.class.getDeclaredField("seed");

        seedField.setAccessible(true);

        AtomicLong originalSeed = (AtomicLong) seedField.get(original);

        Random clone = new Random();

        AtomicLong cloneSeed = (AtomicLong) seedField.get(clone);

        cloneSeed.set(
                originalSeed.get());

        return clone;
    }

    public static void clear() {
        ATTEMPT_MAP.clear();
    }

    public static void remove(ChunkPos chunkPos) {
        ATTEMPT_MAP.remove(chunkPos);
    }
}