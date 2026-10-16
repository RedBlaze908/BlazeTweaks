package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.events.LazyChunkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Keeps server-authoritative piston pushes visible while client-side entity ticking
 * is suppressed in lazy chunks. The normal interpolation path depends on entity
 * ticks, so lazy entities can otherwise appear to snap back and forth.
 */
@Mixin(Entity.class)
public abstract class MixinEntityLazyChunkPositionSync {
    @Inject(
            method = "setPositionAndRotationDirect(DDDFFIZ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void blazetweaks$syncPositionForLazyChunk(
            double x, double y, double z,
            float yaw, float pitch,
            int posRotationIncrements, boolean teleport,
            CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity.world == null || !entity.world.isRemote || entity instanceof EntityPlayer) {
            return;
        }

        int dimension = entity.world.provider.getDimension();
        int currentChunkX = ((int) Math.floor(entity.posX)) >> 4;
        int currentChunkZ = ((int) Math.floor(entity.posZ)) >> 4;
        int targetChunkX = ((int) Math.floor(x)) >> 4;
        int targetChunkZ = ((int) Math.floor(z)) >> 4;

        boolean currentChunkLazy = LazyChunkManager.isLazy(dimension, currentChunkX, currentChunkZ);
        boolean targetChunkLazy = LazyChunkManager.isLazy(dimension, targetChunkX, targetChunkZ);
        if (!currentChunkLazy && !targetChunkLazy) {
            return;
        }

        // Apply the server's position immediately and align render interpolation
        // history so it cannot blend back toward a stale position.
        entity.setPositionAndRotation(x, y, z, yaw, pitch);
        entity.prevPosX = x;
        entity.prevPosY = y;
        entity.prevPosZ = z;
        entity.lastTickPosX = x;
        entity.lastTickPosY = y;
        entity.lastTickPosZ = z;
        entity.prevRotationYaw = yaw;
        entity.prevRotationPitch = pitch;
        ci.cancel();
    }
}
