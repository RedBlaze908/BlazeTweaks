package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.events.LazyChunkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Suppresses the entity's own simulation tick in server-side lazy chunks while
 * leaving World.updateEntityWithOptionalForce's surrounding bookkeeping intact.
 *
 * IMPORTANT: updateEntityWithOptionalForce is declared on World, not
 * WorldServer. Targeting WorldServer means this redirect may never be applied.
 */
@Mixin(World.class)
public abstract class MixinWorldServerLazyChunk {
    @Redirect(
            method = "updateEntityWithOptionalForce(Lnet/minecraft/entity/Entity;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V")
    )
    private void blazetweaks$skipLazyEntityOnUpdate(Entity entity) {
        if (!blazetweaks$isServerLazy(entity)) {
            entity.onUpdate();
        }
    }

    @Redirect(
            method = "updateEntityWithOptionalForce(Lnet/minecraft/entity/Entity;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateRidden()V")
    )
    private void blazetweaks$skipLazyRiddenUpdate(Entity entity) {
        if (!blazetweaks$isServerLazy(entity)) {
            entity.updateRidden();
        }
    }

    private boolean blazetweaks$isServerLazy(Entity entity) {
        if (entity == null || entity instanceof EntityPlayer) return false;
        World world = (World) (Object) this;
        if (world.isRemote) return false;

        int chunkX = ((int) Math.floor(entity.posX)) >> 4;
        int chunkZ = ((int) Math.floor(entity.posZ)) >> 4;
        return LazyChunkManager.isLazy(world.provider.getDimension(), chunkX, chunkZ);
    }
}
