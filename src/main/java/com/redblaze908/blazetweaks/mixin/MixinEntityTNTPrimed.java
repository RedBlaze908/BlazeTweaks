package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.events.TickControlHandler;
import net.minecraft.entity.item.EntityTNTPrimed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTNTPrimed.class)
public class MixinEntityTNTPrimed {

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void blazetweaks$freezeTnt(CallbackInfo ci) {
        if (TickControlHandler.isFrozen()
                && TickControlHandler.getRemainingStepTicks() <= 0) {
            ci.cancel();
        }
    }
}