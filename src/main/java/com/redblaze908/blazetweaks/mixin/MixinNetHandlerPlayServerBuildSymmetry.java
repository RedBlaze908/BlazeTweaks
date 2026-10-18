package com.redblaze908.blazetweaks.mixin;

import com.redblaze908.blazetweaks.BlazeTweaks;
import com.redblaze908.blazetweaks.symmetry.BuildSymmetryManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServerBuildSymmetry {
        @Shadow
        public EntityPlayerMP player;

        @Unique
        private BlockPos blazetweaks$symmetrySourcePos;
        @Unique
        private IBlockState blazetweaks$symmetryBeforeState;
        @Unique
        private WorldServer blazetweaks$symmetryWorld;

        @Inject(method = "processTryUseItemOnBlock", at = @At("HEAD"))
        private void blazetweaks$captureOriginalPlacement(CPacketPlayerTryUseItemOnBlock packet, CallbackInfo ci) {
                this.blazetweaks$symmetrySourcePos = null;
                this.blazetweaks$symmetryBeforeState = null;
                this.blazetweaks$symmetryWorld = null;

                EntityPlayerMP p = this.player;
                if (!BlazeTweaks.isBuildSymmetryEnabled || p == null || p.getServer() == null
                                || !p.getServer().isSinglePlayer() || !p.isCreative()
                                || packet == null || packet.getHand() != EnumHand.MAIN_HAND
                                || !BuildSymmetryManager.hasArea(p.world)) {
                        return;
                }

                if (p.getHeldItemMainhand().isEmpty()
                                || !(p.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBlock)) {
                        return;
                }

                BlockPos clickedPos = packet.getPos();
                if (!p.world.isBlockLoaded(clickedPos))
                        return;
                IBlockState clickedState = p.world.getBlockState(clickedPos);

                BlockPos target;
                if (BlazeTweaks.isAirPlacementEnabled && p.world.isAirBlock(clickedPos)) {
                        target = clickedPos;
                } else {
                        target = (!p.world.isAirBlock(clickedPos)
                                        && clickedState.getBlock().isReplaceable(p.world, clickedPos))
                                                        ? clickedPos
                                                        : clickedPos.offset(packet.getDirection());
                }
                if (!BuildSymmetryManager.contains(p.world, target) || !p.world.isBlockLoaded(target))
                        return;
                IBlockState before = p.world.getBlockState(target);
                if (!p.world.isAirBlock(target) && !before.getBlock().isReplaceable(p.world, target))
                        return;

                this.blazetweaks$symmetrySourcePos = target.toImmutable();
                this.blazetweaks$symmetryBeforeState = before;
                this.blazetweaks$symmetryWorld = (WorldServer) p.world;
        }

        @Inject(method = "processTryUseItemOnBlock", at = @At("TAIL"))
        private void blazetweaks$applyAuthoritativeMirror(CPacketPlayerTryUseItemOnBlock packet, CallbackInfo ci) {
                BlockPos sourcePos = this.blazetweaks$symmetrySourcePos;
                IBlockState before = this.blazetweaks$symmetryBeforeState;
                WorldServer expectedWorld = this.blazetweaks$symmetryWorld;
                this.blazetweaks$symmetrySourcePos = null;
                this.blazetweaks$symmetryBeforeState = null;
                this.blazetweaks$symmetryWorld = null;

                EntityPlayerMP p = this.player;
                if (sourcePos == null || before == null || expectedWorld == null || p == null
                                || p.world != expectedWorld || p.getServer() == null || !p.getServer().isSinglePlayer()
                                || !BlazeTweaks.isBuildSymmetryEnabled || !p.isCreative()
                                || !BuildSymmetryManager.hasArea(expectedWorld))
                        return;

                IBlockState placedState = expectedWorld.getBlockState(sourcePos);
                if (placedState.equals(before) || expectedWorld.isAirBlock(sourcePos))
                        return;

                BlockPos mirrorPos = BuildSymmetryManager.mirror(expectedWorld, sourcePos);
                if (mirrorPos == null || mirrorPos.equals(sourcePos)
                                || !BuildSymmetryManager.contains(expectedWorld, mirrorPos)
                                || !expectedWorld.isBlockLoaded(mirrorPos))
                        return;

                IBlockState existing = expectedWorld.getBlockState(mirrorPos);
                boolean replacingSolid = !expectedWorld.isAirBlock(mirrorPos)
                                && !existing.getBlock().isReplaceable(expectedWorld, mirrorPos);
                if (replacingSolid && !BlazeTweaks.isBlockReplaceEnabled)
                        return;

                Mirror mirror = BuildSymmetryManager.getAxis() == BuildSymmetryManager.Axis.X
                                ? Mirror.FRONT_BACK
                                : Mirror.LEFT_RIGHT;
                IBlockState mirroredState = placedState.withMirror(mirror);

                if (!replacingSolid && !mirroredState.getBlock().canPlaceBlockAt(expectedWorld, mirrorPos))
                        return;

                expectedWorld.setBlockState(mirrorPos, mirroredState, 3);
        }

        @Inject(method = "processPlayerDigging", at = @At("TAIL"))
        private void blazetweaks$mirrorServerBlockBreak(CPacketPlayerDigging packet, CallbackInfo ci) {
                EntityPlayerMP p = this.player;
                if (packet == null || p == null || p.getServer() == null
                                || !p.getServer().isSinglePlayer() || !BlazeTweaks.isBuildSymmetryEnabled
                                || !p.isCreative() || !BuildSymmetryManager.hasArea(p.world))
                        return;

                CPacketPlayerDigging.Action action = packet.getAction();
                if (action != CPacketPlayerDigging.Action.START_DESTROY_BLOCK
                                && action != CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)
                        return;

                WorldServer world = (WorldServer) p.world;
                BlockPos sourcePos = packet.getPosition();
                if (!BuildSymmetryManager.contains(world, sourcePos) || !world.isBlockLoaded(sourcePos)
                                || !world.isAirBlock(sourcePos))
                        return;

                BlockPos mirrorPos = BuildSymmetryManager.mirror(world, sourcePos);
                if (mirrorPos == null || mirrorPos.equals(sourcePos)
                                || !BuildSymmetryManager.contains(world, mirrorPos)
                                || !world.isBlockLoaded(mirrorPos) || world.isAirBlock(mirrorPos))
                        return;

                world.setBlockToAir(mirrorPos);
        }
}
