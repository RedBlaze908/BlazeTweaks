package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.events.LazyChunkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class MixinWorldLazyChunkClient {
    @Redirect(
            method = "updateEntityWithOptionalForce(Lnet/minecraft/entity/Entity;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V"),
            require = 1
    )
    private void blazetweaks$skipLazyEntityOnUpdate(Entity entity) {
        if (!blazetweaks$isLazy(entity)) {
            entity.onUpdate();
        }
    }

    @Redirect(
            method = "updateEntityWithOptionalForce(Lnet/minecraft/entity/Entity;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateRidden()V"),
            require = 1
    )
    private void blazetweaks$skipLazyRiddenUpdate(Entity entity) {
        if (!blazetweaks$isLazy(entity)) {
            entity.updateRidden();
        }
    }

    private boolean blazetweaks$isLazy(Entity entity) {
        if (entity == null || entity instanceof EntityPlayer || entity.world == null) return false;
        World world = (World) (Object) this;
        int chunkX = ((int) Math.floor(entity.posX)) >> 4;
        int chunkZ = ((int) Math.floor(entity.posZ)) >> 4;
        return LazyChunkManager.isLazy(world.provider.getDimension(), chunkX, chunkZ);
    }
}
