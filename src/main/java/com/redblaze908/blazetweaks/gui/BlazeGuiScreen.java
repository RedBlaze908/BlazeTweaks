package com.redblaze908.blazetweaks.gui;

import java.io.IOException;

import com.redblaze908.blazetweaks.BlazeTweaks;
import com.redblaze908.blazetweaks.KeyInputHandler;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class BlazeGuiScreen extends GuiScreen {

    private static final int BUTTON_DUNGEON_TOGGLE_ID = 1;

    @Override
    public void initGui() {
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(
                BUTTON_DUNGEON_TOGGLE_ID,
                this.width / 2 - 100,
                this.height / 2 - 10,
                200,
                20,
                getDungeonToggleLabel()));
    }

    private String getDungeonToggleLabel() {
        return "Dungeon Overlay: "
                + (BlazeTweaks.isDungeonOverlayEnabled
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