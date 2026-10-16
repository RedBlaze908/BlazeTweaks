package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.commands.CommandInstantTileTick;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class MixinWorldInstantTileTick {
    @Redirect(method = "scheduleUpdate(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getTickRandomly()Z"), require = 0)
    private boolean blazetweaks$instantTileTick(Block block) {
        return CommandInstantTileTick.ENABLED || block.getTickRandomly();
    }
}
