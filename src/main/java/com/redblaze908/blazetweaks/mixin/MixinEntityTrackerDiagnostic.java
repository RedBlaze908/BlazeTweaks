package com.redblaze908.blazetweaks.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.util.IntHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTracker.class)
public abstract class MixinEntityTrackerDiagnostic {
    private static final Logger BLAZETWEAKS_LOGGER = LogManager.getLogger("BlazeTweaks-EID");

    @Shadow
    private IntHashMap<EntityTrackerEntry> trackedEntityHashTable;

    @Inject(method = "track(Lnet/minecraft/entity/Entity;IIZ)V", at = @At("HEAD"), require = 1)
    private void blazetweaks$logDuplicateRuntimeId(
            Entity entityIn, int trackingRange, int updateFrequency,
            boolean sendVelocityUpdates, CallbackInfo ci) {
        int id = entityIn.getEntityId();
        if (this.trackedEntityHashTable != null
                && this.trackedEntityHashTable.containsItem(id)) {
            BLAZETWEAKS_LOGGER.warn(
                    "[EID-DIAG] EntityTracker collision: runtimeId={}, incomingType={}, incomingName='{}', worldRemote={}. Vanilla tracking will handle the collision.",
                    id, entityIn.getClass().getName(), entityIn.getName(),
                    entityIn.world != null && entityIn.world.isRemote);
        }
    }
}
