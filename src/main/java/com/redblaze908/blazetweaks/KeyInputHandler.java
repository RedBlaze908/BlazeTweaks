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

    public static final KeyBinding toggleEntityTicking = new KeyBinding(
            "Toggle Entity Ticking",
            Keyboard.KEY_NONE,
            "BlazeTweaks");

    public static final KeyBinding toggleFallingBlockKey = new KeyBinding(
            "Toggle Falling Block Ticking",
            Keyboard.KEY_BACKSLASH,
            "BlazeTweaks");

    private static boolean enabled = true;

    public static void init() {
        ClientRegistry.registerKeyBinding(TOGGLE_KEY);
        ClientRegistry.registerKeyBinding(openGuiKey);
        ClientRegistry.registerKeyBinding(toggleEntityTicking);
        ClientRegistry.registerKeyBinding(toggleFallingBlockKey);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // piston overlay
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

            // custom gui
        } else if (openGuiKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new BlazeGuiScreen());

            // entity ticking
        } else if (toggleEntityTicking.isPressed()) {
            if (Minecraft.getMinecraft().isSingleplayer()) {
                BlazeTweaks.isEntityTickingDisabled = !BlazeTweaks.isEntityTickingDisabled;

                if (Minecraft.getMinecraft().player != null) {
                    String status = BlazeTweaks.isEntityTickingDisabled
                            ? TextFormatting.GREEN + "Disabled (Frozen)"
                            : TextFormatting.RED + "Enabled (Normal)";
                    Minecraft.getMinecraft().player
                            .sendStatusMessage(new TextComponentString("Entity Ticking: " + status), true);
                }
            }
        } else if (toggleFallingBlockKey.isPressed() && Minecraft.getMinecraft().isSingleplayer()) {
            BlazeTweaks.isFallingBlockTickingDisabled = !BlazeTweaks.isFallingBlockTickingDisabled;
            if (Minecraft.getMinecraft().player != null) {
                String status = BlazeTweaks.isFallingBlockTickingDisabled
                        ? TextFormatting.GREEN + "Disabled (Falling Block Frozen)"
                        : TextFormatting.RED + "Enabled (Normal)";
                Minecraft.getMinecraft().player
                        .sendStatusMessage(new TextComponentString("Falling Block Ticking: " + status), true);
            }
        }
    }

}