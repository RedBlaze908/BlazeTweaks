package com.example.pistonorderoverlay;

import net.minecraft.block.state.PistonHelperAccessor; // IMPORT FONDAMENTALE
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Status of the piston overlay.
 *
 * This class does not render directly: it only preserves
 * the results of the analysis, which are then drawn by
 * PistonOverlayRenderer.
 */
public final class PistonOverlayManager {
    private static final Map<BlockPos, OverlayData> OVERLAYS = new HashMap<>();

    private PistonOverlayManager() {
    }

    /**
     * Show/hide the overlay relating to the clicked piston.
     */
    public static void toggle(
            net.minecraft.entity.player.EntityPlayer player,
            World world,
            BlockPos pistonPos,
            EnumFacing facing,
            boolean extending,
            boolean sticky) {
        BlockPos key = pistonPos.toImmutable();

        if (OVERLAYS.containsKey(key)) {
            OVERLAYS.remove(key);
            return;
        }

        PistonHelperAccessor helper = new PistonHelperAccessor(world, pistonPos, facing, extending);

        boolean canMove = helper.canMove();

        List<BlockPos> blocksToMove = new ArrayList<>();
        List<BlockPos> blocksToDestroy = new ArrayList<>();

        if (canMove) {
            blocksToMove.addAll(helper.getBlocksToMove());
            blocksToDestroy.addAll(helper.getBlocksToDestroy());
        }

        OVERLAYS.put(
                key,
                new OverlayData(
                        world,
                        pistonPos,
                        facing,
                        extending,
                        sticky,
                        canMove,
                        blocksToMove,
                        blocksToDestroy));
    }

    /**
     * Overlay used for an already extended regular piston.
     * A retract is not simulated because the normal piston is not sticky.
     */
    public static void toggleUnsupported(
            net.minecraft.entity.player.EntityPlayer player,
            BlockPos pistonPos,
            EnumFacing facing) {
        BlockPos key = pistonPos.toImmutable();

        if (OVERLAYS.containsKey(key)) {
            OVERLAYS.remove(key);
            return;
        }

        OVERLAYS.put(
                key,
                new OverlayData(
                        player.world,
                        pistonPos,
                        facing,
                        false,
                        false,
                        false,
                        Collections.<BlockPos>emptyList(),
                        Collections.<BlockPos>emptyList()));
    }

    public static List<OverlayData> getOverlays() {
        return new ArrayList<>(OVERLAYS.values());
    }

    public static void clear() {
        OVERLAYS.clear();
    }

    /**
     * Removes overlays that belong to another world.
     * Useful when changing dimensions/world.
     */
    public static void cleanup(World currentWorld) {
        List<BlockPos> toRemove = new ArrayList<>();

        for (Map.Entry<BlockPos, OverlayData> entry : OVERLAYS.entrySet()) {
            if (entry.getValue().world != currentWorld) {
                toRemove.add(entry.getKey());
            }
        }

        for (BlockPos pos : toRemove) {
            OVERLAYS.remove(pos);
        }
    }

    public static final class OverlayData {
        private final World world;
        private final BlockPos pistonPos;
        private final EnumFacing facing;
        private final boolean extending;
        private final boolean sticky;
        private final boolean canMove;
        private final List<BlockPos> blocksToMove;
        private final List<BlockPos> blocksToDestroy;

        private OverlayData(
                World world,
                BlockPos pistonPos,
                EnumFacing facing,
                boolean extending,
                boolean sticky,
                boolean canMove,
                List<BlockPos> blocksToMove,
                List<BlockPos> blocksToDestroy) {
            this.world = world;
            this.pistonPos = pistonPos.toImmutable();
            this.facing = facing;
            this.extending = extending;
            this.sticky = sticky;
            this.canMove = canMove;
            this.blocksToMove = Collections.unmodifiableList(new ArrayList<>(blocksToMove));
            this.blocksToDestroy = Collections.unmodifiableList(new ArrayList<>(blocksToDestroy));
        }

        public World getWorld() {
            return world;
        }

        public BlockPos getPistonPos() {
            return pistonPos;
        }

        public EnumFacing getFacing() {
            return facing;
        }

        public boolean isExtending() {
            return extending;
        }

        public boolean isSticky() {
            return sticky;
        }

        public boolean canMove() {
            return canMove;
        }

        public List<BlockPos> getBlocksToMove() {
            return blocksToMove;
        }

        public List<BlockPos> getBlocksToDestroy() {
            return blocksToDestroy;
        }
    }
}