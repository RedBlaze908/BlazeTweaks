package com.example.pistonorderoverlay;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.PistonHelperAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Status of the piston overlay.
 */
public final class PistonOverlayManager {
    private static final Map<BlockPos, OverlayData> OVERLAYS = new HashMap<>();

    private PistonOverlayManager() {
    }

    /**
     * Show/hide the overlay relating to the clicked piston.
     */
    public static void toggle(
            EntityPlayer player,
            World world,
            BlockPos pistonPos,
            EnumFacing facing,
            boolean extended,
            boolean sticky) {
        BlockPos key = pistonPos.toImmutable();

        if (OVERLAYS.containsKey(key)) {
            OVERLAYS.remove(key);
            return;
        }

        boolean canMove = false;
        List<BlockPos> blocksToMove = new ArrayList<>();
        List<BlockPos> blocksToDestroy = new ArrayList<>();

        if (!extended) {
            PistonHelperAccessor helper = new PistonHelperAccessor(world, pistonPos, facing, true);
            canMove = helper.canMove();
            if (canMove) {
                blocksToMove.addAll(helper.getBlocksToMove());
                blocksToDestroy.addAll(helper.getBlocksToDestroy());
            }

            if (blocksToMove.isEmpty() && blocksToDestroy.isEmpty()) {
                canMove = false;
            }
        } else if (sticky) {
            BlockPos headPos = pistonPos.offset(facing);
            BlockPos startPos = headPos.offset(facing);

            if (world.isAirBlock(startPos)) {
                canMove = true;
            } else {
                java.util.Queue<BlockPos> queue = new java.util.LinkedList<>();
                java.util.Set<BlockPos> visited = new java.util.HashSet<>();

                queue.add(startPos);
                visited.add(startPos);
                visited.add(headPos);

                boolean blocked = false;

                while (!queue.isEmpty() && visited.size() <= 12) {
                    BlockPos current = queue.poll();
                    IBlockState state = world.getBlockState(current);

                    if (state.getMobilityFlag() == net.minecraft.block.material.EnumPushReaction.BLOCK) {
                        blocked = true;
                        break;
                    }

                    blocksToMove.add(current);

                    if (state.getBlock() == Blocks.SLIME_BLOCK) {
                        for (EnumFacing dir : EnumFacing.values()) {
                            BlockPos neighbor = current.offset(dir);
                            if (!visited.contains(neighbor) && !world.isAirBlock(neighbor)) {
                                visited.add(neighbor);
                                queue.add(neighbor);
                            }
                        }
                    }
                }

                canMove = !blocked && blocksToMove.size() <= 12;
            }
        }

        OVERLAYS.put(
                key,
                new OverlayData(
                        world,
                        pistonPos,
                        facing,
                        !extended,
                        sticky,
                        canMove,
                        blocksToMove,
                        blocksToDestroy));
    }

    /**
     * Overlay used for an already extended regular piston.
     */
    public static void toggleUnsupported(
            EntityPlayer player,
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