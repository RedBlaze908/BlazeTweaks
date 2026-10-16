package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.accessor.EntityIdAccessor;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntityIdAccessor implements EntityIdAccessor {
    @Shadow private static int nextEntityID;

    @Override
    public int blazetweaks$getNextEntityID() {
        return nextEntityID;
    }

    @Override
    public void blazetweaks$setNextEntityID(int id) {
        nextEntityID = id;
    }
}
