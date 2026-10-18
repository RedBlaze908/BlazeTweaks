package com.redblaze908.blazetweaks.client;

import com.redblaze908.blazetweaks.symmetry.BuildSymmetryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BuildSymmetryRenderer {
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        World world = mc.world;
        if (player == null || world == null || !BuildSymmetryManager.hasFirstCorner(world)) return;

        BlockPos first = BuildSymmetryManager.getFirstCorner(world);
        BlockPos second = BuildSymmetryManager.getSecondCorner(world);
        if (first == null) return;
        int minX = second == null ? first.getX() : Math.min(first.getX(), second.getX());
        int minY = second == null ? first.getY() : Math.min(first.getY(), second.getY());
        int minZ = second == null ? first.getZ() : Math.min(first.getZ(), second.getZ());
        int maxX = second == null ? first.getX() : Math.max(first.getX(), second.getX());
        int maxY = second == null ? first.getY() : Math.max(first.getY(), second.getY());
        int maxZ = second == null ? first.getZ() : Math.max(first.getZ(), second.getZ());

        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-x, -y, -z);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.glLineWidth(2.0F);

        AxisAlignedBB box = new AxisAlignedBB(minX, minY, minZ, maxX + 1.0D, maxY + 1.0D, maxZ + 1.0D).grow(0.003D);
        if (second == null) {
            RenderGlobal.drawSelectionBoundingBox(box, 1.0F, 0.75F, 0.1F, 0.9F);
        } else {
            RenderGlobal.drawSelectionBoundingBox(box, 0.2F, 0.85F, 1.0F, 0.9F);
            // Draw the selected mirror plane in the center of the selected volume.
            if (BuildSymmetryManager.getAxis() == BuildSymmetryManager.Axis.X) {
                double centerX = (minX + maxX + 1.0D) * 0.5D;
                AxisAlignedBB plane = new AxisAlignedBB(centerX - 0.005D, minY, minZ, centerX + 0.005D, maxY + 1.0D, maxZ + 1.0D);
                RenderGlobal.drawSelectionBoundingBox(plane, 0.25F, 1.0F, 0.35F, 0.9F);
            } else {
                double centerZ = (minZ + maxZ + 1.0D) * 0.5D;
                AxisAlignedBB plane = new AxisAlignedBB(minX, minY, centerZ - 0.005D, maxX + 1.0D, maxY + 1.0D, centerZ + 0.005D);
                RenderGlobal.drawSelectionBoundingBox(plane, 0.25F, 1.0F, 0.35F, 0.9F);
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
