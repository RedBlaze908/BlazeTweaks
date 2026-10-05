package com.redblaze908.blazetweaks.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EnchantmentPredictor {

    public static List<EnchantmentData> predictSingleplayer(int xpSeed, ItemStack stack, int slot, int level) {
        if (stack.isEmpty() || level <= 0) {
            return new ArrayList<>();
        }

        Random rand = new Random();
        rand.setSeed((long) xpSeed + slot);

        int enchantability = stack.getItem().getItemEnchantability(stack);
        if (enchantability <= 0)
            return new ArrayList<>();

        int modifiedLevel = level + 1 + rand.nextInt(enchantability / 4 + 1) + rand.nextInt(enchantability / 4 + 1);
        float bonus = (rand.nextFloat() + rand.nextFloat() - 1.0F) * 0.15F;
        modifiedLevel = Math.max(1, Math.round(modifiedLevel + modifiedLevel * bonus));

        List<EnchantmentData> possibleEnchants = getPossibleEnchantments(modifiedLevel, stack);
        if (possibleEnchants.isEmpty())
            return new ArrayList<>();

        List<EnchantmentData> result = new ArrayList<>();
        EnchantmentData first = WeightedRandom.getRandomItem(rand, possibleEnchants);

        if (first != null) {
            result.add(first);

            while (rand.nextInt(50) <= modifiedLevel) {
                filterIncompatible(possibleEnchants, first);
                if (possibleEnchants.isEmpty())
                    break;

                EnchantmentData extra = WeightedRandom.getRandomItem(rand, possibleEnchants);
                if (extra != null) {
                    result.add(extra);
                }
                modifiedLevel /= 2;
            }
        }

        return result;
    }

    private static List<EnchantmentData> getPossibleEnchantments(int level, ItemStack stack) {
        List<EnchantmentData> list = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.REGISTRY) {
            if (!enchantment.isTreasureEnchantment() && enchantment.canApplyAtEnchantingTable(stack)) {
                for (int lvl = enchantment.getMaxLevel(); lvl >= enchantment.getMinLevel(); --lvl) {
                    if (level >= enchantment.getMinEnchantability(lvl)
                            && level <= enchantment.getMaxEnchantability(lvl)) {
                        list.add(new EnchantmentData(enchantment, lvl));
                        break;
                    }
                }
            }
        }
        return list;
    }

    private static void filterIncompatible(List<EnchantmentData> list, EnchantmentData target) {
        Iterator<EnchantmentData> iterator = list.iterator();
        while (iterator.hasNext()) {
            EnchantmentData data = iterator.next();
            if (!target.enchantment.isCompatibleWith(data.enchantment)) {
                iterator.remove();
            }
        }
    }
}