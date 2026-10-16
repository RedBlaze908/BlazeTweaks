package com.redblaze908.blazetweaks.mixin;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(World.class)
public interface WorldInstantTileTickAccessor {
    @Accessor("scheduledUpdatesAreImmediate")
    void blazetweaks$setScheduledUpdatesAreImmediate(boolean enabled);
}
