package com.redblaze908.blazetweaks.gui;

import com.redblaze908.blazetweaks.BlazeTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Displays ticks elapsed since the integrated server's last automatic save. */
public class AutosaveCounterHudOverlay extends Gui {

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (!BlazeTweaks.isAutosaveCounterEnabled || !mc.isSingleplayer()
                || mc.world == null || mc.player == null) {
            return;
        }

        int elapsedTicks = AutosaveCounterState.getElapsedTicks();
        int color;
        if (elapsedTicks >= 800) {
            color = 0xFF5555; // red from 800
        } else if (elapsedTicks >= 600) {
            color = 0xFFFF55; // yellow from 600
        } else {
            color = 0x55FF55; // green before 600
        }

        // Keep the counter beside the right edge of the hotbar, vertically centered on its slots.
        String text = Integer.toString(elapsedTicks);
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        int x = scaledWidth / 2 + 96;
        // Keep it inside the screen on narrower GUI resolutions.
        x = Math.min(x, scaledWidth - mc.fontRenderer.getStringWidth(text) - 4);
        int y = scaledHeight - 17;
        mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }
}
