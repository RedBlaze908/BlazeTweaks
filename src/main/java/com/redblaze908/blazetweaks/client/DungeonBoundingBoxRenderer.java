package com.redblaze908.blazetweaks.client;

import com.redblaze908.blazetweaks.BlazeTweaks;
import com.redblaze908.blazetweaks.events.ChunkGeneratorTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class DungeonBoundingBoxRenderer {

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!BlazeTweaks.isDungeonOverlayEnabled)
            return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        World world = mc.world;

        if (player == null || world == null)
            return;

        double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
        double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
        double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.glLineWidth(2.5F);

        Set<BlockPos> realSpawners = new HashSet<>();

        for (TileEntity tile : world.loadedTileEntityList) {
            if (tile instanceof TileEntityMobSpawner) {
                BlockPos pos = tile.getPos();
                realSpawners.add(pos);

                AxisAlignedBB spawnerBox = new AxisAlignedBB(pos).grow(0.002D);
                RenderGlobal.drawSelectionBoundingBox(spawnerBox, 0.0F, 1.0F, 1.0F, 1.0F);

                AxisAlignedBB roomBox = new AxisAlignedBB(
                        pos.getX() - 3, pos.getY() - 1, pos.getZ() - 3,
                        pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4);
                RenderGlobal.drawSelectionBoundingBox(roomBox, 1.0F, 1.0F, 0.0F, 0.8F);
            }
        }

        int playerChunkX = player.chunkCoordX;
        int playerChunkZ = player.chunkCoordZ;

        for (int cx = playerChunkX - 3; cx <= playerChunkX + 3; cx++) {
            for (int cz = playerChunkZ - 3; cz <= playerChunkZ + 3; cz++) {
                ChunkPos cpos = new ChunkPos(cx, cz);

                List<BlockPos> attempts = ChunkGeneratorTracker.getOrComputeAttempts(world, cpos);

                for (BlockPos pos : attempts) {
                    if (!realSpawners.contains(pos)) {
                        AxisAlignedBB box = new AxisAlignedBB(pos).grow(0.002D);
                        RenderGlobal.drawSelectionBoundingBox(box, 1.0F, 0.0F, 0.0F, 0.4F);
                    }
                }
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}