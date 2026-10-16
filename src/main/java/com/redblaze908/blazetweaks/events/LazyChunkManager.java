package com.redblaze908.blazetweaks.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class LazyChunkManager {
    private static final Set<ChunkKey> LAZY_CHUNKS = Collections.newSetFromMap(new ConcurrentHashMap<ChunkKey, Boolean>());
    private static volatile boolean autoRestore = false;

    public LazyChunkManager() {}

    public static boolean isLazy(int dimension, int chunkX, int chunkZ) {
        return LAZY_CHUNKS.contains(new ChunkKey(dimension, chunkX, chunkZ));
    }

    public static boolean add(int dimension, int chunkX, int chunkZ) {
        return LAZY_CHUNKS.add(new ChunkKey(dimension, chunkX, chunkZ));
    }

    public static boolean remove(int dimension, int chunkX, int chunkZ) {
        return LAZY_CHUNKS.remove(new ChunkKey(dimension, chunkX, chunkZ));
    }

    public static Set<ChunkKey> getLazyChunks() {
        return Collections.unmodifiableSet(LAZY_CHUNKS);
    }

    public static boolean isAutoRestoreEnabled() {
        return autoRestore;
    }

    public static void setAutoRestoreEnabled(boolean enabled) {
        autoRestore = enabled;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !autoRestore) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.isSingleplayer() || mc.player == null || mc.world == null || mc.gameSettings == null) return;

        int dimension = mc.player.dimension;
        int playerChunkX = mc.player.chunkCoordX;
        int playerChunkZ = mc.player.chunkCoordZ;
        int renderDistance = Math.max(2, mc.gameSettings.renderDistanceChunks);

        LAZY_CHUNKS.removeIf(key -> key.dimension == dimension
                && (Math.abs(key.x - playerChunkX) > renderDistance
                || Math.abs(key.z - playerChunkZ) > renderDistance));
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            LAZY_CHUNKS.clear();
            autoRestore = false;
        }
    }

    public static final class ChunkKey {
        public final int dimension;
        public final int x;
        public final int z;

        public ChunkKey(int dimension, int x, int z) {
            this.dimension = dimension;
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ChunkKey)) return false;
            ChunkKey other = (ChunkKey) obj;
            return dimension == other.dimension && x == other.x && z == other.z;
        }

        @Override
        public int hashCode() {
            int result = dimension;
            result = 31 * result + x;
            result = 31 * result + z;
            return result;
        }

        @Override
        public String toString() {
            return "dim=" + dimension + ":" + x + "," + z;
        }
    }
}
