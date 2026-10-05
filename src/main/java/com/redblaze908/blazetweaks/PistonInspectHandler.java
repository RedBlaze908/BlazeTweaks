package com.redblaze908.blazetweaks;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PistonInspectHandler {

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!KeyInputHandler.isEnabled()) {
            return;
        }

        if (event.getHand() != EnumHand.MAIN_HAND) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();
        BlockPos pos = event.getPos();

        if (!world.isRemote) {
            return;
        }

        if (player.isSneaking()) {
            return;
        }

        if (!player.getHeldItemMainhand().isEmpty()) {
            return;
        }

        IBlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof BlockPistonBase)) {
            return;
        }

        event.setCanceled(true);

        EnumFacing facing = state.getValue(BlockPistonBase.FACING);
        boolean extended = state.getValue(BlockPistonBase.EXTENDED);
        boolean isSticky = state.getBlock() == Blocks.STICKY_PISTON;

        // PistonOverlayManager.toggleUnsupported(player, pos, facing);

        PistonOverlayManager.toggle(
                player,
                world,
                pos,
                facing,
                extended,
                isSticky);
    }
}