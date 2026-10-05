package com.redblaze908.blazetweaks.client;

import com.redblaze908.blazetweaks.utils.EnchantmentPredictor;
import com.redblaze908.blazetweaks.utils.SeedCracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class EnchantmentGuiHandler {

    @SubscribeEvent
    public void onGuiDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.getGui() instanceof GuiEnchantment))
            return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null)
            return;

        GuiEnchantment gui = (GuiEnchantment) event.getGui();
        FontRenderer font = mc.fontRenderer;

        int xpSeed = 0;
        int[] actualLevels = new int[] { 0, 0, 0 };
        int[] enchantClue = new int[] { -1, -1, -1 };
        ItemStack targetItem = ItemStack.EMPTY;

        if (gui.inventorySlots instanceof ContainerEnchantment) {
            ContainerEnchantment container = (ContainerEnchantment) gui.inventorySlots;

            try {
                int[] levels = ReflectionHelper.getPrivateValue(ContainerEnchantment.class, container, "enchantLevels",
                        "field_75154_e");
                int[] clues = ReflectionHelper.getPrivateValue(ContainerEnchantment.class, container, "enchantClue",
                        "field_178156_e");

                if (levels != null && levels.length >= 3) {
                    actualLevels[0] = levels[0];
                    actualLevels[1] = levels[1];
                    actualLevels[2] = levels[2];
                }
                if (clues != null) {
                    enchantClue = clues;
                }
            } catch (Exception ignored) {
            }

            targetItem = container.getSlot(0).getStack();
        }

        int x = (gui.width / 2) + 90;
        int y = (gui.height / 2) - 75;

        // ==========================================
        // SINGLEPLAYER (IL TUO CODICE)
        // ==========================================
        if (mc.isSingleplayer() && mc.getIntegratedServer() != null) {
            try {
                EntityPlayerMP serverPlayer = mc.getIntegratedServer()
                        .getPlayerList()
                        .getPlayerByUUID(mc.player.getUniqueID());

                if (serverPlayer != null) {
                    xpSeed = ReflectionHelper.getPrivateValue(EntityPlayer.class, serverPlayer, "xpSeed",
                            "field_71068_ca");
                }
            } catch (Exception ignored) {
            }

            font.drawStringWithShadow(TextFormatting.GREEN + "=== ENCHANT RNG ===", x, y, 0xFFFFFF);
            font.drawStringWithShadow(TextFormatting.DARK_GREEN + "Server XP Seed: " + TextFormatting.WHITE + xpSeed, x,
                    y + 10, 0xFFFFFF);

            renderEnchantList(font, xpSeed, targetItem, actualLevels, x, y + 25);
            return;
        }

        // ==========================================
        // MULTIPLAYER (SEED CRACKING)
        // ==========================================
        font.drawStringWithShadow(TextFormatting.GOLD + "=== MULTIPLAYER RNG ===", x, y, 0xFFFFFF);

        List<Integer> possible = SeedCracker.getPossibleSeeds();

        if (SeedCracker.isCracking()) {
            font.drawStringWithShadow(TextFormatting.YELLOW + "Seed search in progress...", x, y + 12, 0xFFFFFF);
        } else if (possible.size() == 1) {
            xpSeed = possible.get(0);
            font.drawStringWithShadow(TextFormatting.GREEN + "Seed Found: " + TextFormatting.WHITE + xpSeed, x,
                    y + 12, 0xFFFFFF);
            renderEnchantList(font, xpSeed, targetItem, actualLevels, x, y + 25);
        } else {
            font.drawStringWithShadow(TextFormatting.RED + "Seeds found: " + TextFormatting.WHITE + possible.size(), x,
                    y + 12, 0xFFFFFF);
            font.drawStringWithShadow(TextFormatting.GRAY + "Press [C] to scan", x, y + 24, 0xFFFFFF);
            font.drawStringWithShadow(TextFormatting.GRAY + "Press [R] to reset", x, y + 34, 0xFFFFFF);
        }
    }

    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!(event.getGui() instanceof GuiEnchantment))
            return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.isSingleplayer())
            return;

        if (Keyboard.getEventKeyState()) {
            int key = Keyboard.getEventKey();

            if (key == Keyboard.KEY_C && !SeedCracker.isCracking()) {
                GuiEnchantment gui = (GuiEnchantment) event.getGui();
                if (gui.inventorySlots instanceof ContainerEnchantment) {
                    ContainerEnchantment container = (ContainerEnchantment) gui.inventorySlots;
                    try {
                        int[] levels = ReflectionHelper.getPrivateValue(ContainerEnchantment.class, container,
                                "enchantLevels", "field_75154_e");
                        int[] clues = ReflectionHelper.getPrivateValue(ContainerEnchantment.class, container,
                                "enchantClue", "field_178156_e");
                        ItemStack item = container.getSlot(0).getStack();

                        if (!item.isEmpty() && levels != null && levels[0] > 0) {
                            SeedCracker.crackSeedAsync(levels, clues, item);
                        }
                    } catch (Exception ignored) {
                    }
                }
            } else if (key == Keyboard.KEY_R) {
                SeedCracker.reset();
            }
        }
    }

    private void renderEnchantList(FontRenderer font, int xpSeed, ItemStack targetItem, int[] actualLevels, int x,
            int startY) {
        if (!targetItem.isEmpty() && actualLevels[0] > 0) {
            int lineY = startY;

            for (int slot = 0; slot < 3; slot++) {
                font.drawStringWithShadow(
                        TextFormatting.YELLOW + "Slot " + (slot + 1) + " (" + actualLevels[slot] + " lvl):", x, lineY,
                        0xFFFFFF);
                lineY += 10;

                List<EnchantmentData> enchants = EnchantmentPredictor.predictSingleplayer(xpSeed, targetItem, slot,
                        actualLevels[slot]);

                if (enchants.isEmpty()) {
                    font.drawStringWithShadow(TextFormatting.RED + "  - No Enchants", x, lineY, 0xFFFFFF);
                    lineY += 10;
                } else {
                    for (EnchantmentData data : enchants) {
                        String enchantName = data.enchantment.getTranslatedName(data.enchantmentLevel);
                        font.drawStringWithShadow(TextFormatting.AQUA + "  > " + TextFormatting.WHITE + enchantName, x,
                                lineY, 0xFFFFFF);
                        lineY += 10;
                    }
                }
                lineY += 2;
            }
        } else {
            font.drawStringWithShadow(TextFormatting.GRAY + "Place an object to see", x, startY + 5, 0xFFFFFF);
        }
    }
}