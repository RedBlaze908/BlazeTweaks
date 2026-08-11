package net.minecraft.block.state;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * "Bridge" class to publicly expose BlockPistonStructureHelper,
 * which in vanilla is package-private (accessible only from
 * net.minecraft.block.state).
 *
 * Must fit in this exact package to compile: package-private access
 * works based on the NAME of the package, not the jar/source the class comes
 * from.
 */
public class PistonHelperAccessor {
    private final BlockPistonStructureHelper helper;

    public PistonHelperAccessor(World world, BlockPos pos, EnumFacing facing, boolean extending) {
        this.helper = new BlockPistonStructureHelper(world, pos, facing, extending);
    }

    public boolean canMove() {
        return this.helper.canMove();
    }

    public List<BlockPos> getBlocksToMove() {
        return this.helper.getBlocksToMove();
    }

    public List<BlockPos> getBlocksToDestroy() {
        return this.helper.getBlocksToDestroy();
    }
}
