package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.events.TickControlHandler;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 2000)
public class MixinMinecraftServer {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void blazetweaks$controlServerTick(CallbackInfo ci) {

        System.out.println(
                "[BlazeTweaks] SERVER TICK - frozen="
                        + TickControlHandler.isFrozen()
                        + " steps="
                        + TickControlHandler.getRemainingStepTicks());

        if (!TickControlHandler.shouldRunWorldTick()) {
            System.out.println("[BlazeTweaks] >>> SERVER TICK CANCELLED <<<");
            ci.cancel();
        }
    }
}