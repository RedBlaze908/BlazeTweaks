package com.redblaze908.blazetweaks.symmetry;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Client-side selection and axis state for Build Symmetry. */
public final class BuildSymmetryManager {
    public enum Axis { X, Z }

    private static BlockPos firstCorner;
    private static BlockPos secondCorner;
    private static int selectedDimension = Integer.MIN_VALUE;
    private static Axis axis = Axis.X;

    private BuildSymmetryManager() { }

    public static synchronized boolean selectCorner(World world, BlockPos pos) {
        if (world == null || pos == null) return false;
        if (selectedDimension != world.provider.getDimension() || firstCorner == null || secondCorner != null) {
            selectedDimension = world.provider.getDimension();
            firstCorner = pos.toImmutable();
            secondCorner = null;
            return false;
        }
        secondCorner = pos.toImmutable();
        return true;
    }

    public static synchronized void clearSelection() {
        firstCorner = null;
        secondCorner = null;
        selectedDimension = Integer.MIN_VALUE;
    }

    public static synchronized boolean hasFirstCorner(World world) {
        ensureWorld(world);
        return firstCorner != null;
    }

    public static synchronized boolean hasArea(World world) {
        ensureWorld(world);
        return firstCorner != null && secondCorner != null;
    }

    public static synchronized BlockPos getFirstCorner(World world) {
        ensureWorld(world);
        return firstCorner;
    }

    public static synchronized BlockPos getSecondCorner(World world) {
        ensureWorld(world);
        return secondCorner;
    }

    public static synchronized Axis getAxis() { return axis; }

    public static synchronized void toggleAxis() {
        axis = axis == Axis.X ? Axis.Z : Axis.X;
    }

    public static synchronized boolean contains(World world, BlockPos pos) {
        ensureWorld(world);
        if (firstCorner == null || secondCorner == null || pos == null) return false;
        return pos.getX() >= minX() && pos.getX() <= maxX()
                && pos.getY() >= minY() && pos.getY() <= maxY()
                && pos.getZ() >= minZ() && pos.getZ() <= maxZ();
    }

    public static synchronized BlockPos mirror(World world, BlockPos pos) {
        ensureWorld(world);
        if (firstCorner == null || secondCorner == null || pos == null) return null;
        if (axis == Axis.X) {
            return new BlockPos(minX() + maxX() - pos.getX(), pos.getY(), pos.getZ());
        }
        return new BlockPos(pos.getX(), pos.getY(), minZ() + maxZ() - pos.getZ());
    }

    public static synchronized int minX() { return Math.min(firstCorner.getX(), secondCorner.getX()); }
    public static synchronized int maxX() { return Math.max(firstCorner.getX(), secondCorner.getX()); }
    public static synchronized int minY() { return Math.min(firstCorner.getY(), secondCorner.getY()); }
    public static synchronized int maxY() { return Math.max(firstCorner.getY(), secondCorner.getY()); }
    public static synchronized int minZ() { return Math.min(firstCorner.getZ(), secondCorner.getZ()); }
    public static synchronized int maxZ() { return Math.max(firstCorner.getZ(), secondCorner.getZ()); }

    private static void ensureWorld(World world) {
        if (world == null || (selectedDimension != Integer.MIN_VALUE
                && selectedDimension != world.provider.getDimension())) {
            firstCorner = null;
            secondCorner = null;
            selectedDimension = Integer.MIN_VALUE;
        }
    }
}
