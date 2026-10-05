package com.redblaze908.blazetweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Pistorder overlay client-side renderer.
 *
 * Draw:
 * - 1, 2, 3... on the blocks that will be moved;
 * - X on the blocks that will be destroyed;
 * - X on the piston when the operation is not possible;
 * - PUSH / RETRACT over the piston.
 */
public class PistonOverlayRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.world == null || mc.player == null) {
            return;
        }

        PistonOverlayManager.cleanup(mc.world);

        List<PistonOverlayManager.OverlayData> overlays = PistonOverlayManager.getOverlays();

        if (overlays.isEmpty()) {
            return;
        }

        EntityPlayer player = mc.player;

        double viewerX = mc.getRenderManager().viewerPosX;
        double viewerY = mc.getRenderManager().viewerPosY;
        double viewerZ = mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();

        GlStateManager.translate(
                -viewerX,
                -viewerY,
                -viewerZ);

        // Overlay always visible through blocks.
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ZERO);

        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();

        for (PistonOverlayManager.OverlayData overlay : overlays) {
            if (overlay.getWorld() != mc.world) {
                continue;
            }

            renderOverlay(overlay, player);
        }

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }

    private void renderOverlay(
            PistonOverlayManager.OverlayData overlay,
            EntityPlayer player) {
        List<BlockPos> move = overlay.getBlocksToMove();
        List<BlockPos> destroy = overlay.getBlocksToDestroy();

        /*
         * First the blocks to move.
         * The index is exactly the one returned by
         * BlockPistonStructureHelper.
         */
        for (int i = 0; i < move.size(); i++) {
            drawBlockMarker(
                    move.get(i),
                    Integer.toString(i + 1),
                    0.0F,
                    1.0F,
                    0.0F,
                    player);
        }

        /*
         * Blocks to be destroyed are displayed with X.
         */
        for (BlockPos pos : destroy) {
            drawBlockMarker(
                    pos,
                    "X",
                    1.0F,
                    0.15F,
                    0.15F,
                    player);
        }

        /*
         * Piston state.
         */
        String action;

        if (!overlay.canMove()) {
            action = "X";
        } else if (overlay.isExtending()) {
            action = "PUSH";
        } else {
            action = "RETRACT";
        }

        drawBlockMarker(
                overlay.getPistonPos(),
                action,
                overlay.canMove() ? 1.0F : 1.0F,
                overlay.canMove() ? 1.0F : 0.15F,
                overlay.canMove() ? 1.0F : 0.15F,
                player);
    }

    private void drawBlockMarker(
            BlockPos pos,
            String text,
            float red,
            float green,
            float blue,
            EntityPlayer player) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.05D;
        double z = pos.getZ() + 0.5D;

        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);

        /*
         * The text must face the camera.
         */
        GlStateManager.rotate(
                -player.rotationYaw,
                0.0F,
                1.0F,
                0.0F);

        GlStateManager.rotate(
                player.rotationPitch,
                1.0F,
                0.0F,
                0.0F);

        GlStateManager.scale(
                -0.025F,
                -0.025F,
                0.025F);

        FontRenderer font = mc.fontRenderer;

        int width = font.getStringWidth(text);

        /*
         * Small semi-transparent background to make it readable
         * the number also above very light blocks.
         */
        GlStateManager.enableTexture2D();

        GlStateManager.disableLighting();

        drawBackground(
                -width / 2 - 2,
                -2,
                width + 4,
                font.FONT_HEIGHT + 4);

        font.drawString(
                text,
                -width / 2,
                0,
                getColor(red, green, blue),
                false);

        GlStateManager.disableTexture2D();

        GlStateManager.popMatrix();
    }

    private void drawBackground(
            int left,
            int top,
            int width,
            int height) {
        GlStateManager.disableTexture2D();

        GL11.glBegin(GL11.GL_QUADS);

        GL11.glColor4f(
                0.0F,
                0.0F,
                0.0F,
                0.55F);

        GL11.glVertex3f(left, top, 0.0F);
        GL11.glVertex3f(left, top + height, 0.0F);
        GL11.glVertex3f(left + width, top + height, 0.0F);
        GL11.glVertex3f(left + width, top, 0.0F);

        GL11.glEnd();

        GlStateManager.enableTexture2D();
    }

    private int getColor(float red, float green, float blue) {
        int r = (int) (red * 255.0F) & 255;
        int g = (int) (green * 255.0F) & 255;
        int b = (int) (blue * 255.0F) & 255;

        return (r << 16) | (g << 8) | b;
    }
}
