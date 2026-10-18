package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.BlazeTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftAirPlacement {

    @Shadow
    public RayTraceResult objectMouseOver;
    @Shadow
    public PlayerControllerMP playerController;
    @Shadow
    private int rightClickDelayTimer;

    @Inject(method = "rightClickMouse", at = @At("HEAD"), cancellable = true)
    private void blazetweaks$placeBlockInAir(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        EntityPlayerSP player = mc.player;
        WorldClient world = mc.world;

        if (!BlazeTweaks.isAirPlacementEnabled || player == null || world == null
                || !player.capabilities.isCreativeMode
                || objectMouseOver == null
                || objectMouseOver.typeOfHit != RayTraceResult.Type.MISS) {
            return;
        }

        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty() || !(held.getItem() instanceof ItemBlock)) {
            return;
        }

        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        double reach = playerController.getBlockReachDistance();
        BlockPos target = null;

        for (double distance = 1.5D; distance <= reach; distance += 0.25D) {
            Vec3d point = eyes.add(look.scale(distance));
            BlockPos candidate = new BlockPos(point.x, point.y, point.z);
            if (!world.isBlockLoaded(candidate)) {
                break;
            }
            if (world.isAirBlock(candidate)
                    && !player.getEntityBoundingBox().intersects(new AxisAlignedBB(candidate))) {
                target = candidate;
                break;
            }
        }

        if (target == null) {
            return;
        }

        Vec3d hitVec = new Vec3d(target.getX() + 0.5D,
                target.getY() + 0.5D, target.getZ() + 0.5D);
        playerController.processRightClickBlock(player, world, target,
                EnumFacing.DOWN, hitVec, EnumHand.MAIN_HAND);

        this.rightClickDelayTimer = 4;
        ci.cancel();
    }
}
