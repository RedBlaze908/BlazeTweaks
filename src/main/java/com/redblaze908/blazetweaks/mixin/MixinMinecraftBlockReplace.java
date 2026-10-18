package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.BlazeTweaks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftBlockReplace {

    @Shadow
    public RayTraceResult objectMouseOver;
    @Shadow
    public PlayerControllerMP playerController;
    @Shadow
    private int rightClickDelayTimer;

    @org.spongepowered.asm.mixin.Unique
    private BlockPos blazetweaks$pendingSupportPos;
    @org.spongepowered.asm.mixin.Unique
    private EnumFacing blazetweaks$pendingSupportSide;
    @org.spongepowered.asm.mixin.Unique
    private Vec3d blazetweaks$pendingHitVec;
    @org.spongepowered.asm.mixin.Unique
    private int blazetweaks$pendingPlacementTicks;

    @Inject(method = "rightClickMouse", at = @At("HEAD"), cancellable = true)
    private void blazetweaks$replaceTargetBlock(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        EntityPlayerSP player = mc.player;
        WorldClient world = mc.world;

        if (!BlazeTweaks.isBlockReplaceEnabled || player == null || world == null
                || !player.capabilities.isCreativeMode
                || objectMouseOver == null
                || objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK
                || objectMouseOver.getBlockPos() == null) {
            return;
        }

        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty() || !(held.getItem() instanceof ItemBlock)) {
            return;
        }

        final BlockPos targetPos = objectMouseOver.getBlockPos();
        final EnumFacing aimedSide = objectMouseOver.sideHit;
        if (!world.isBlockLoaded(targetPos) || world.isAirBlock(targetPos)) {
            return;
        }

        EnumFacing[] directions = new EnumFacing[] {
                aimedSide, aimedSide.getOpposite(), EnumFacing.UP,
                EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH,
                EnumFacing.WEST, EnumFacing.EAST
        };
        BlockPos supportPos = null;
        EnumFacing supportSide = null;
        for (EnumFacing direction : directions) {
            BlockPos candidate = targetPos.offset(direction);
            EnumFacing faceTowardTarget = direction.getOpposite();
            if (!world.isBlockLoaded(candidate)) {
                continue;
            }
            if (world.isAirBlock(candidate)) {
                continue;
            }
            IBlockState state = world.getBlockState(candidate);
            if (state.getBlock().isSideSolid(state, world, candidate, faceTowardTarget)) {
                supportPos = candidate;
                supportSide = faceTowardTarget;
                break;
            }
        }

        boolean isolatedTarget = supportPos == null;
        if (isolatedTarget) {
            supportPos = targetPos;
            supportSide = aimedSide;
        }

        ci.cancel();
        this.rightClickDelayTimer = 4;
        if (supportPos == null || supportSide == null) {
            return;
        }

        Vec3d hitVec;
        if (isolatedTarget) {
            hitVec = new Vec3d(targetPos.getX() + 0.5D,
                    targetPos.getY() + 0.5D, targetPos.getZ() + 0.5D);
        } else {
            hitVec = new Vec3d(
                    supportPos.getX() + 0.5D + supportSide.getFrontOffsetX() * 0.5D,
                    supportPos.getY() + 0.5D + supportSide.getFrontOffsetY() * 0.5D,
                    supportPos.getZ() + 0.5D + supportSide.getFrontOffsetZ() * 0.5D);
        }

        player.connection.sendPacket(new CPacketPlayerDigging(
                CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetPos, aimedSide));
        boolean destroyed = playerController.onPlayerDestroyBlock(targetPos);
        if (!destroyed) {
            return;
        }

        this.blazetweaks$pendingSupportPos = supportPos;
        this.blazetweaks$pendingSupportSide = supportSide;
        this.blazetweaks$pendingHitVec = hitVec;
        this.blazetweaks$pendingPlacementTicks = 2;
    }

    @Inject(method = "runTick", at = @At("TAIL"))
    private void blazetweaks$finishPendingReplacement(CallbackInfo ci) {
        if (this.blazetweaks$pendingPlacementTicks <= 0) {
            return;
        }
        this.blazetweaks$pendingPlacementTicks--;
        if (this.blazetweaks$pendingPlacementTicks > 0) {
            return;
        }

        Minecraft mc = (Minecraft) (Object) this;
        EntityPlayerSP player = mc.player;
        WorldClient world = mc.world;
        BlockPos supportPos = this.blazetweaks$pendingSupportPos;
        EnumFacing supportSide = this.blazetweaks$pendingSupportSide;
        Vec3d hitVec = this.blazetweaks$pendingHitVec;

        this.blazetweaks$pendingSupportPos = null;
        this.blazetweaks$pendingSupportSide = null;
        this.blazetweaks$pendingHitVec = null;
        this.blazetweaks$pendingPlacementTicks = 0;

        if (!BlazeTweaks.isBlockReplaceEnabled || player == null || world == null
                || !player.capabilities.isCreativeMode || supportPos == null
                || supportSide == null || hitVec == null) {
            return;
        }
        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty() || !(held.getItem() instanceof ItemBlock)) {
            return;
        }
        if (!world.isBlockLoaded(supportPos)) {
            return;
        }

        playerController.processRightClickBlock(player, world, supportPos,
                supportSide, hitVec, EnumHand.MAIN_HAND);
    }
}
