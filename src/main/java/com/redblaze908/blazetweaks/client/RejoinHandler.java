package com.redblaze908.blazetweaks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RejoinHandler {

    private static final int REJOIN_BUTTON_ID = 908;
    private static ServerData lastServerData;

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiIngameMenu) {

            if (!Minecraft.getMinecraft().isSingleplayer()) {
                lastServerData = Minecraft.getMinecraft().getCurrentServerData();
            }

            int buttonWidth = 200;
            int buttonHeight = 20;

            int x = event.getGui().width / 2 - 100;
            int y = event.getGui().height / 4 - 14;

            String buttonText = "";
            if (Minecraft.getMinecraft().isSingleplayer()) {
                buttonText = "Save & ReJoin";
            } else {
                buttonText = "ReJoin";
            }

            event.getButtonList().add(new GuiButton(
                    REJOIN_BUTTON_ID,
                    x,
                    y,
                    buttonWidth,
                    buttonHeight,
                    TextFormatting.GREEN + buttonText));
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.getGui() instanceof GuiIngameMenu && event.getButton().id == REJOIN_BUTTON_ID) {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.isSingleplayer()) {
                // Singleplayer Logic + Lan Logic
                String worldName = mc.getIntegratedServer().getFolderName();
                String worldTitle = mc.getIntegratedServer().getWorldName();

                if (mc.getIntegratedServer() != null) {
                    mc.getIntegratedServer().initiateShutdown();
                }

                mc.world.sendQuittingDisconnectingPacket();
                mc.loadWorld(null);

                mc.displayGuiScreen(new GuiMainMenu());

                mc.addScheduledTask(() -> {
                    mc.launchIntegratedServer(worldName, worldTitle, null);
                });

            } else {
                // Multiplayer logic
                if (lastServerData != null) {
                    mc.world.sendQuittingDisconnectingPacket();
                    mc.loadWorld(null);
                    mc.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(new GuiMainMenu()), mc, lastServerData));
                }
            }
        }
    }
}