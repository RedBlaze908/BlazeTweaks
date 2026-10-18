package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.BlazeTweaks;
import com.redblaze908.blazetweaks.symmetry.BuildSymmetryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerBuildSymmetry {
    @Unique
    private boolean blazetweaks$breakingMirror;

    @Inject(method = "onPlayerDestroyBlock", at = @At("TAIL"))
    private void blazetweaks$mirrorDestroyedBlock(BlockPos pos,
            CallbackInfoReturnable<Boolean> cir) {
        if (this.blazetweaks$breakingMirror || !cir.getReturnValue())
            return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        WorldClient world = mc.world;
        if (!mc.isSingleplayer() || !BlazeTweaks.isBuildSymmetryEnabled || player == null || world == null
                || !player.capabilities.isCreativeMode || !BuildSymmetryManager.hasArea(world)
                || pos == null || !BuildSymmetryManager.contains(world, pos))
            return;

        BlockPos mirrorPos = BuildSymmetryManager.mirror(world, pos);
        if (mirrorPos == null || mirrorPos.equals(pos)
                || !BuildSymmetryManager.contains(world, mirrorPos)
                || !world.isBlockLoaded(mirrorPos) || world.isAirBlock(mirrorPos))
            return;

        this.blazetweaks$breakingMirror = true;
        try {
            ((PlayerControllerMP) (Object) this).onPlayerDestroyBlock(mirrorPos);
        } finally {
            this.blazetweaks$breakingMirror = false;
        }
    }
}
