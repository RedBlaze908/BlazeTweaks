package com.redblaze908.blazetweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import com.redblaze908.blazetweaks.gui.BlazeGuiScreen;

public class KeyInputHandler {

    public static KeyBinding openGuiKey = new KeyBinding(
            "Open Gui",
            Keyboard.KEY_RMENU,
            "BlazeTweaks");

    // Piston overlay key
    public static final KeyBinding TOGGLE_KEY = new KeyBinding(
            "Toggle Pistorder Overlay",
            Keyboard.KEY_O,
            "BlazeTweaks");

    private static boolean enabled = true;

    public static void init() {
        ClientRegistry.registerKeyBinding(TOGGLE_KEY);
        ClientRegistry.registerKeyBinding(openGuiKey);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (TOGGLE_KEY.isPressed()) {
            enabled = !enabled;

            // If disabled, we clean the on-screen overlays
            if (!enabled) {
                PistonOverlayManager.clear();
            }

            if (Minecraft.getMinecraft().player != null) {
                String status = enabled
                        ? TextFormatting.GREEN + "ACTIVATED"
                        : TextFormatting.RED + "DEACTIVATED";

                Minecraft.getMinecraft().player.sendMessage(
                        new TextComponentString(
                                TextFormatting.GOLD + "[PistonOrderOverlay] " + TextFormatting.WHITE + "Mod "
                                        + status));
            }
        } else if (openGuiKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new BlazeGuiScreen());
        }
    }

}