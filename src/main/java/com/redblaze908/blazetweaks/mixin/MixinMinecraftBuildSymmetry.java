package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.BlazeTweaks;
import com.redblaze908.blazetweaks.symmetry.BuildSymmetryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftBuildSymmetry {
        @Shadow
        public RayTraceResult objectMouseOver;
        @Shadow
        public PlayerControllerMP playerController;

        @Unique
        private BlockPos blazetweaks$pendingMirrorPos;
        @Unique
        private BlockPos blazetweaks$pendingSupportPos;
        @Unique
        private EnumFacing blazetweaks$pendingSupportSide;
        @Unique
        private Vec3d blazetweaks$pendingHitVec;
        @Unique
        private WorldClient blazetweaks$pendingWorld;
        @Unique
        private boolean blazetweaks$pendingDirectPlacement;

        @Inject(method = "rightClickMouse", at = @At("HEAD"))
        private void blazetweaks$queueMirrorPlacement(CallbackInfo ci) {
                Minecraft mc = (Minecraft) (Object) this;
                EntityPlayerSP player = mc.player;
                WorldClient world = mc.world;
                if (!BlazeTweaks.isBuildSymmetryEnabled || player == null || world == null
                                || !player.capabilities.isCreativeMode
                                || BlazeTweaks.isBlockReplaceEnabled
                                || objectMouseOver == null
                                || objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK
                                || objectMouseOver.getBlockPos() == null) {
                        return;
                }

                ItemStack held = player.getHeldItemMainhand();
                if (held.isEmpty() || !(held.getItem() instanceof ItemBlock))
                        return;

                BlockPos clickedPos = objectMouseOver.getBlockPos();
                EnumFacing clickedSide = objectMouseOver.sideHit;
                if (clickedSide == null)
                        return;
                BlockPos sourcePos = clickedPos.offset(clickedSide);
                if (!BuildSymmetryManager.hasArea(world) || !BuildSymmetryManager.contains(world, sourcePos))
                        return;
                if (!world.isBlockLoaded(sourcePos)
                                || (!world.isAirBlock(sourcePos)
                                                && !world.getBlockState(sourcePos).getBlock().isReplaceable(world,
                                                                sourcePos)))
                        return;

                BlockPos mirrorPos = BuildSymmetryManager.mirror(world, sourcePos);
                if (mirrorPos == null || mirrorPos.equals(sourcePos)
                                || !BuildSymmetryManager.contains(world, mirrorPos)
                                || !world.isBlockLoaded(mirrorPos))
                        return;
                if (!world.isAirBlock(mirrorPos)
                                && !world.getBlockState(mirrorPos).getBlock().isReplaceable(world, mirrorPos))
                        return;

                this.blazetweaks$pendingMirrorPos = mirrorPos.toImmutable();
                this.blazetweaks$pendingWorld = world;
                this.blazetweaks$pendingSupportPos = null;
                this.blazetweaks$pendingSupportSide = null;
                this.blazetweaks$pendingHitVec = null;
                this.blazetweaks$pendingDirectPlacement = true;

                for (EnumFacing direction : EnumFacing.values()) {
                        BlockPos supportPos = mirrorPos.offset(direction);
                        if (!world.isBlockLoaded(supportPos) || world.isAirBlock(supportPos))
                                continue;
                        EnumFacing faceTowardTarget = direction.getOpposite();
                        if (!world.getBlockState(supportPos).isSideSolid(world, supportPos, faceTowardTarget))
                                continue;

                        this.blazetweaks$pendingSupportPos = supportPos.toImmutable();
                        this.blazetweaks$pendingSupportSide = faceTowardTarget;
                        this.blazetweaks$pendingHitVec = new Vec3d(
                                        supportPos.getX() + 0.5D + faceTowardTarget.getFrontOffsetX() * 0.5D,
                                        supportPos.getY() + 0.5D + faceTowardTarget.getFrontOffsetY() * 0.5D,
                                        supportPos.getZ() + 0.5D + faceTowardTarget.getFrontOffsetZ() * 0.5D);
                        this.blazetweaks$pendingDirectPlacement = false;
                        break;
                }
        }

        @Inject(method = "runTick", at = @At("TAIL"))
        private void blazetweaks$performQueuedMirrorPlacement(CallbackInfo ci) {
                BlockPos mirrorPos = this.blazetweaks$pendingMirrorPos;
                if (mirrorPos == null)
                        return;

                WorldClient expectedWorld = this.blazetweaks$pendingWorld;
                BlockPos supportPos = this.blazetweaks$pendingSupportPos;
                EnumFacing supportSide = this.blazetweaks$pendingSupportSide;
                Vec3d hitVec = this.blazetweaks$pendingHitVec;
                boolean directPlacement = this.blazetweaks$pendingDirectPlacement;

                this.blazetweaks$pendingMirrorPos = null;
                this.blazetweaks$pendingSupportPos = null;
                this.blazetweaks$pendingSupportSide = null;
                this.blazetweaks$pendingHitVec = null;
                this.blazetweaks$pendingWorld = null;
                this.blazetweaks$pendingDirectPlacement = false;

                Minecraft mc = (Minecraft) (Object) this;
                EntityPlayerSP player = mc.player;
                WorldClient world = mc.world;
                if (!BlazeTweaks.isBuildSymmetryEnabled || player == null || world == null
                                || world != expectedWorld || !player.capabilities.isCreativeMode
                                || BlazeTweaks.isBlockReplaceEnabled || !world.isBlockLoaded(mirrorPos))
                        return;

                ItemStack held = player.getHeldItemMainhand();
                if (held.isEmpty() || !(held.getItem() instanceof ItemBlock))
                        return;
                if (!world.isAirBlock(mirrorPos)
                                && !world.getBlockState(mirrorPos).getBlock().isReplaceable(world, mirrorPos))
                        return;

                if (!directPlacement && supportPos != null && supportSide != null && hitVec != null
                                && world.isBlockLoaded(supportPos)
                                && !world.isAirBlock(supportPos)) {
                        playerController.processRightClickBlock(player, world, supportPos,
                                        supportSide, hitVec, EnumHand.MAIN_HAND);
                } else {
                        Vec3d targetHitVec = new Vec3d(
                                        mirrorPos.getX() + 0.5D,
                                        mirrorPos.getY() + 0.5D,
                                        mirrorPos.getZ() + 0.5D);
                        playerController.processRightClickBlock(player, world, mirrorPos,
                                        EnumFacing.DOWN, targetHitVec, EnumHand.MAIN_HAND);
                }
        }
}
