package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.eid.EntityIdFixState;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class MixinEntityIdFix {

    @Shadow
    private static int nextEntityID;

    /*
     * In 1.12.2 the Entity(World) constructor contains:
     * this.entityId = nextEntityID++;
     */
    @Redirect(method = "<init>(Lnet/minecraft/world/World;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;nextEntityID:I", opcode = Opcodes.PUTSTATIC), require = 1)
    private void blazetweaks$fixSingleplayerEntityId(int value, World worldIn) {
        if (EntityIdFixState.isEnabled()
                && worldIn != null
                && worldIn.isRemote) {
            EntityIdFixState.recordSuppressed();
            return;
        }

        nextEntityID = value;
    }
}
