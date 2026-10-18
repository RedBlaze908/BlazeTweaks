package com.redblaze908.blazetweaks.gui;

import java.io.IOException;

import com.redblaze908.blazetweaks.BlazeTweaks;
import com.redblaze908.blazetweaks.KeyInputHandler;
import com.redblaze908.blazetweaks.symmetry.BuildSymmetryManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

/** BlazeTweaks settings screen split into Building and Technical tabs. */
public class BlazeGuiScreen extends GuiScreen {
    private static final int TAB_BUILDING_ID = 100;
    private static final int TAB_TECHNICAL_ID = 101;

    private static final int BUTTON_DUNGEON_TOGGLE_ID = 1;
    private static final int BUTTON_ENTITY_TICKING_TOGGLE_ID = 2;
    private static final int BUTTON_FALLING_BLOCK_TOGGLE_ID = 3;
    private static final int BUTTON_DEBUG_HUD_TOGGLE_ID = 4;
    private static final int BUTTON_AUTOSAVE_COUNTER_ID = 5;
    private static final int BUTTON_AIR_PLACEMENT_ID = 6;
    private static final int BUTTON_BLOCK_REPLACE_ID = 7;
    private static final int BUTTON_BUILD_SYMMETRY_ID = 8;
    private static final int BUTTON_SYMMETRY_AXIS_ID = 9;
    private static final int BUTTON_SYMMETRY_CLEAR_ID = 10;

    private enum Tab { BUILDING, TECHNICAL }
    private Tab activeTab = Tab.TECHNICAL;

    @Override
    public void initGui() {
        this.buttonList.clear();
        int left = this.width / 2 - 101;
        int top = Math.max(12, this.height / 2 - 73);
        int tabWidth = 99;
        this.buttonList.add(new GuiButton(TAB_BUILDING_ID, left, top, tabWidth, 20,
                (activeTab == Tab.BUILDING ? TextFormatting.GREEN + "[ " : "") + "Building"
                        + (activeTab == Tab.BUILDING ? TextFormatting.GREEN + " ]" : "")));
        this.buttonList.add(new GuiButton(TAB_TECHNICAL_ID, left + 103, top, tabWidth, 20,
                (activeTab == Tab.TECHNICAL ? TextFormatting.GREEN + "[ " : "") + "Technical / Redstone"
                        + (activeTab == Tab.TECHNICAL ? TextFormatting.GREEN + " ]" : "")));

        int buttonTop = top + 29;
        int rowGap = 23;
        if (activeTab == Tab.BUILDING) {
            addButton(BUTTON_AIR_PLACEMENT_ID, left, buttonTop, 202, getAirPlacementLabel());
            addButton(BUTTON_BLOCK_REPLACE_ID, left, buttonTop + rowGap, 202, getBlockReplaceLabel());
            addButton(BUTTON_BUILD_SYMMETRY_ID, left, buttonTop + rowGap * 2, 202, getBuildSymmetryLabel());
            addButton(BUTTON_SYMMETRY_AXIS_ID, left, buttonTop + rowGap * 3, 202, getSymmetryAxisLabel());
            addButton(BUTTON_SYMMETRY_CLEAR_ID, left, buttonTop + rowGap * 4, 202, "Clear Symmetry Area");
        } else {
            addButton(BUTTON_DUNGEON_TOGGLE_ID, left, buttonTop, 202, getDungeonToggleLabel());
            addButton(BUTTON_ENTITY_TICKING_TOGGLE_ID, left, buttonTop + rowGap, 202, getEntityTickingToggleLabel());
            addButton(BUTTON_FALLING_BLOCK_TOGGLE_ID, left, buttonTop + rowGap * 2, 202, getFallingBlockToggleLabel());
            addButton(BUTTON_DEBUG_HUD_TOGGLE_ID, left, buttonTop + rowGap * 3, 202, getDebugHudToggleLabel());
            addButton(BUTTON_AUTOSAVE_COUNTER_ID, left, buttonTop + rowGap * 4, 202, getAutosaveCounterLabel());
        }
    }

    private void addButton(int id, int x, int y, int width, String label) {
        this.buttonList.add(new GuiButton(id, x, y, width, 20, label));
    }

    private String getDungeonToggleLabel() {
        return "Dungeon Overlay: " + (BlazeTweaks.isDungeonOverlayEnabled
                ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF");
    }

    private String getEntityTickingToggleLabel() {
        if (!Minecraft.getMinecraft().isSingleplayer())
            return "Entity Ticking: " + TextFormatting.DARK_GRAY + "N/A (Singleplayer)";
        return "Disable Entity Ticking: " + (BlazeTweaks.isEntityTickingDisabled
                ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF");
    }

    private String getFallingBlockToggleLabel() {
        if (!Minecraft.getMinecraft().isSingleplayer())
            return "Falling Block Ticking: " + TextFormatting.DARK_GRAY + "N/A";
        return "Disable Falling Block Ticking: " + (BlazeTweaks.isFallingBlockTickingDisabled
                ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF");
    }

    private String getDebugHudToggleLabel() {
        return "Falling Block HUD Debug: " + (BlazeTweaks.isFallingBlockDebugEnabled
                ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF");
    }

    private String getAutosaveCounterLabel() {
        if (!Minecraft.getMinecraft().isSingleplayer())
            return "Autosave Counter: " + TextFormatting.DARK_GRAY + "N/A (Singleplayer)";
        return "Autosave Counter: " + (BlazeTweaks.isAutosaveCounterEnabled
                ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF");
    }

    private String getAirPlacementLabel() {
        return "Place Blocks in Air: " + (BlazeTweaks.isAirPlacementEnabled
                ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF")
                + TextFormatting.GRAY + " (Creative)";
    }

    private String getBuildSymmetryLabel() {
        return "Build Symmetry: " + (BlazeTweaks.isBuildSymmetryEnabled
                ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF")
                + TextFormatting.GRAY + " (Creative)";
    }

    private String getSymmetryAxisLabel() {
        return "Symmetry Axis: " + BuildSymmetryManager.getAxis().name();
    }

    private String getBlockReplaceLabel() {
        return "Replace Target Block: " + (BlazeTweaks.isBlockReplaceEnabled
                ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF")
                + TextFormatting.GRAY + " (Creative)";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        String title = activeTab == Tab.BUILDING ? "BlazeTweaks - Building" : "BlazeTweaks - Technical / Redstone";
        this.drawCenteredString(this.fontRenderer, title, this.width / 2, Math.max(2, this.height / 2 - 91), 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == TAB_BUILDING_ID) {
            activeTab = Tab.BUILDING;
            initGui();
            return;
        }
        if (button.id == TAB_TECHNICAL_ID) {
            activeTab = Tab.TECHNICAL;
            initGui();
            return;
        }

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
        } else if (button.id == BUTTON_AUTOSAVE_COUNTER_ID) {
            if (Minecraft.getMinecraft().isSingleplayer()) {
                BlazeTweaks.isAutosaveCounterEnabled = !BlazeTweaks.isAutosaveCounterEnabled;
                button.displayString = getAutosaveCounterLabel();
            }
        } else if (button.id == BUTTON_AIR_PLACEMENT_ID) {
            BlazeTweaks.isAirPlacementEnabled = !BlazeTweaks.isAirPlacementEnabled;
            button.displayString = getAirPlacementLabel();
        } else if (button.id == BUTTON_BLOCK_REPLACE_ID) {
            BlazeTweaks.isBlockReplaceEnabled = !BlazeTweaks.isBlockReplaceEnabled;
            button.displayString = getBlockReplaceLabel();
        } else if (button.id == BUTTON_BUILD_SYMMETRY_ID) {
            BlazeTweaks.isBuildSymmetryEnabled = !BlazeTweaks.isBuildSymmetryEnabled;
            button.displayString = getBuildSymmetryLabel();
        } else if (button.id == BUTTON_SYMMETRY_AXIS_ID) {
            BuildSymmetryManager.toggleAxis();
            button.displayString = getSymmetryAxisLabel();
        } else if (button.id == BUTTON_SYMMETRY_CLEAR_ID) {
            BuildSymmetryManager.clearSelection();
            if (Minecraft.getMinecraft().player != null) {
                Minecraft.getMinecraft().player.sendMessage(new net.minecraft.util.text.TextComponentString(
                        TextFormatting.GOLD + "[BlazeTweaks] Symmetry area cleared."));
            }
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
    public boolean doesGuiPauseGame() { return false; }
}
