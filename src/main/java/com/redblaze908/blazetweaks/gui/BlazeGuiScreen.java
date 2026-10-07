package com.redblaze908.blazetweaks.gui;

import java.io.IOException;

import javax.swing.text.html.parser.Entity;

import com.redblaze908.blazetweaks.BlazeTweaks;
import com.redblaze908.blazetweaks.KeyInputHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class BlazeGuiScreen extends GuiScreen {

    private static final int BUTTON_DUNGEON_TOGGLE_ID = 1;
    private static final int BUTTON_ENTITY_TICKING_TOGGLE_ID = 2;
    private static final int BUTTON_FALLING_BLOCK_TOGGLE_ID = 3;
    private static final int BUTTON_DEBUG_HUD_TOGGLE_ID = 4;

    @Override
    public void initGui() {
        this.buttonList.clear();

        int centerX = this.width / 2 - 100;
        int centerY = this.height / 2 - 35;

        this.buttonList.add(new GuiButton(
                BUTTON_DUNGEON_TOGGLE_ID,
                centerX, centerY, 200, 20, getDungeonToggleLabel()));

        this.buttonList.add(new GuiButton(
                BUTTON_ENTITY_TICKING_TOGGLE_ID,
                centerX, centerY + 25, 200, 20, getEntityTickingToggleLabel()));

        this.buttonList.add(new GuiButton(
                BUTTON_FALLING_BLOCK_TOGGLE_ID,
                centerX, centerY + 50, 200, 20, getFallingBlockToggleLabel()));

        this.buttonList.add(new GuiButton(
                BUTTON_DEBUG_HUD_TOGGLE_ID,
                centerX, centerY + 75, 200, 20, getDebugHudToggleLabel()));
    }

    private String getDungeonToggleLabel() {
        return "Dungeon Overlay: "
                + (BlazeTweaks.isDungeonOverlayEnabled
                        ? TextFormatting.GREEN + "ON"
                        : TextFormatting.RED + "OFF");
    }

    private String getEntityTickingToggleLabel() {
        if (!Minecraft.getMinecraft().isSingleplayer()) {
            return "Disable Entity Ticking: " + TextFormatting.DARK_GRAY + "N/A (Only singleplayer)";
        }

        return "Disable Entity Ticking: "
                + (BlazeTweaks.isEntityTickingDisabled
                        ? TextFormatting.GREEN + "ON"
                        : TextFormatting.RED + "OFF");
    }

    private String getFallingBlockToggleLabel() {
        if (!Minecraft.getMinecraft().isSingleplayer()) {
            return "Falling Block Ticking: " + TextFormatting.DARK_GRAY + "N/A";
        }
        return "Disable Falling Block Ticking: "
                + (BlazeTweaks.isFallingBlockTickingDisabled
                        ? TextFormatting.GREEN + "ON"
                        : TextFormatting.RED + "OFF");
    }

    private String getDebugHudToggleLabel() {
        return "Falling Block HUD Debug: "
                + (BlazeTweaks.isFallingBlockDebugEnabled
                        ? TextFormatting.GREEN + "ON"
                        : TextFormatting.RED + "OFF");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == BUTTON_DUNGEON_TOGGLE_ID) {
            BlazeTweaks.isDungeonOverlayEnabled = !BlazeTweaks.isDungeonOverlayEnabled;
            button.displayString = getDungeonToggleLabel();
        } else if (button.id == BUTTON_ENTITY_TICKING_TOGGLE_ID) {
            if (Minecraft.getMinecraft().isSingleplayer()) {
                BlazeTweaks.isEntityTickingDisabled = !BlazeTweaks.isEntityTickingDisabled;
                button.displayString = getEntityTickingToggleLabel();
            }
        } else if (button.id == BUTTON_FALLING_BLOCK_TOGGLE_ID) {
            if (Minecraft.getMinecraft().isSingleplayer()) {
                BlazeTweaks.isFallingBlockTickingDisabled = !BlazeTweaks.isFallingBlockTickingDisabled;
                button.displayString = getFallingBlockToggleLabel();
            }
        } else if (button.id == BUTTON_DEBUG_HUD_TOGGLE_ID) {
            BlazeTweaks.isFallingBlockDebugEnabled = !BlazeTweaks.isFallingBlockDebugEnabled;
            button.displayString = getDebugHudToggleLabel();
        }

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == KeyInputHandler.openGuiKey.getKeyCode() || keyCode == 1) {
            this.mc.displayGuiScreen((GuiScreen) null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}