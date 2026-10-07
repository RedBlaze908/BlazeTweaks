package com.redblaze908.blazetweaks.gui;

import com.redblaze908.blazetweaks.BlazeTweaks;
import com.redblaze908.blazetweaks.events.EntityTickingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FallingBlockHudOverlay extends Gui {

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT)
            return;
        if (!BlazeTweaks.isFallingBlockDebugEnabled)
            return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null)
            return;

        List<EntityFallingBlock> fallingBlocks = new ArrayList<>();
        for (Object entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityFallingBlock) {
                fallingBlocks.add((EntityFallingBlock) entity);
            }
        }

        if (fallingBlocks.isEmpty())
            return;

        fallingBlocks.sort(Comparator.comparingDouble(b -> b.getDistanceSq(mc.player)));

        FontRenderer font = mc.fontRenderer;
        int screenHeight = event.getResolution().getScaledHeight();

        int x = 10;
        int y = screenHeight - 20;

        int maxToDisplay = Math.min(fallingBlocks.size(), 5);
        for (int i = 0; i < maxToDisplay; i++) {
            EntityFallingBlock block = fallingBlocks.get(i);

            Vec3d savedMotion = EntityTickingHandler.SAVED_MOTIONS.get(block.getUniqueID());

            double motX = (savedMotion != null) ? savedMotion.x : block.motionX;
            double motY = (savedMotion != null) ? savedMotion.y : block.motionY;
            double motZ = (savedMotion != null) ? savedMotion.z : block.motionZ;

            double speed = Math.sqrt(motX * motX + motY * motY + motZ * motZ);

            String line1 = String.format("%s#%d [%s] %sPos: %.2f, %.2f, %.2f",
                    TextFormatting.YELLOW, (i + 1),
                    block.getBlock() != null ? block.getBlock().getBlock().getLocalizedName() : "Block",
                    TextFormatting.WHITE,
                    block.posX, block.posY, block.posZ);

            String line2 = String.format("   %sFallTime: %s%dt | %sFrozen: %s%b | %sSpeed: %s%.3f b/t",
                    TextFormatting.GRAY, TextFormatting.AQUA, block.fallTime,
                    TextFormatting.GRAY, block.updateBlocked ? TextFormatting.GREEN : TextFormatting.RED,
                    block.updateBlocked,
                    TextFormatting.GRAY, TextFormatting.LIGHT_PURPLE, speed);

            String line3 = String.format("   %sSaved Motion: %sX:%.3f Y:%.3f Z:%.3f",
                    TextFormatting.GRAY, TextFormatting.GREEN,
                    motX, motY, motZ);

            int blockOffsetY = y - ((maxToDisplay - i) * 32);
            font.drawStringWithShadow(line1, x, blockOffsetY, 0xFFFFFF);
            font.drawStringWithShadow(line2, x, blockOffsetY + 10, 0xFFFFFF);
            font.drawStringWithShadow(line3, x, blockOffsetY + 20, 0xFFFFFF);
        }
    }
}