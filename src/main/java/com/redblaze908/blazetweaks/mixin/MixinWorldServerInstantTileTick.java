package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.commands.CommandInstantTileTick;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class MixinWorldServerInstantTileTick {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void blazetweaks$applyGlobalInstantTileTick(CallbackInfo ci) {
        ((WorldInstantTileTickAccessor) this).blazetweaks$setScheduledUpdatesAreImmediate(
                CommandInstantTileTick.ENABLED);
    }
}
