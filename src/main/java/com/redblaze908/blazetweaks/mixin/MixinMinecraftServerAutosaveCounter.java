package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.gui.AutosaveCounterState;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Hooks the automatic save call made from MinecraftServer.tick(), not manual saves. */
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServerAutosaveCounter {

    private static final Logger BLAZETWEAKS_AUTOSAVE_LOGGER = LogManager.getLogger("BlazeTweaks-AutosaveDiagnostic");

    @Inject(
            method = "tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;saveAllWorlds(Z)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            require = 1
    )
    private void blazetweaks$onAutomaticSave(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        // Temporary diagnostic: this line should appear only when this exact hook fires.
        BLAZETWEAKS_AUTOSAVE_LOGGER.info("AUTOSAVE HOOK FIRED: saveAllWorlds(Z)V inside MinecraftServer.tick(); serverTick={}", server.getTickCounter());
        AutosaveCounterState.onAutosaveTriggered();
    }

    @Inject(method = "tick()V", at = @At("TAIL"), require = 1)
    private void blazetweaks$advanceAutosaveCounter(CallbackInfo ci) {
        AutosaveCounterState.onServerTickEnd();
    }
}
