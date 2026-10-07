package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.events.TickControlHandler;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    static {
        System.out.println("========================================");
        System.out.println("[BlazeTweaks] MIXIN MINECRAFTSERVER LOADED");
        System.out.println("========================================");
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At("HEAD"), cancellable = true)
    private void blazetweaks$controlWorldTick(CallbackInfo ci) {

        System.out.println(
                "[BlazeTweaks] updateTimeLightAndEntities() called - frozen="
                        + TickControlHandler.isFrozen()
                        + " steps="
                        + TickControlHandler.getRemainingStepTicks());

        if (!TickControlHandler.shouldRunWorldTick()) {
            System.out.println("[BlazeTweaks] >>> WORLD TICK CANCELLED <<<");
            ci.cancel();
        }
    }
}