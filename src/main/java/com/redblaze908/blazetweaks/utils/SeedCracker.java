package com.redblaze908.blazetweaks.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class SeedCracker {

    private static List<Integer> possibleSeeds = new ArrayList<>();
    private static volatile boolean isCracking = false;
    private static volatile boolean shouldCancel = false;

    public static List<Integer> getPossibleSeeds() {
        return possibleSeeds;
    }

    public static boolean isCracking() {
        return isCracking;
    }

    public static void reset() {
        shouldCancel = true;
        possibleSeeds.clear();
        isCracking = false;
    }

    public static CompletableFuture<Void> crackSeedAsync(int[] levels, int[] clues, ItemStack stack) {
        if (stack.isEmpty())
            return CompletableFuture.completedFuture(null);

        final ItemStack safeStack = stack.copy();
        final int[] safeLevels = levels.clone();
        final int[] safeClues = clues.clone();

        isCracking = true;
        shouldCancel = false;

        return CompletableFuture.runAsync(() -> {
            try {
                int enchantability = safeStack.getItem().getItemEnchantability(safeStack);
                if (enchantability <= 0) {
                    isCracking = false;
                    return;
                }

                if (possibleSeeds.isEmpty()) {
                    List<Integer> matches = new ArrayList<>();
                    Random rand = new Random();

                    for (int i = 0; i < (1 << 24); i++) {
                        if (shouldCancel)
                            break;

                        int candidateSeed = (i << 8) | (i >>> 16);

                        if (checkSeedMatches(candidateSeed, safeLevels, safeClues, safeStack, enchantability, rand)) {
                            matches.add(candidateSeed);
                        }
                    }

                    if (!shouldCancel) {
                        possibleSeeds = matches;
                    }
                } else {
                    List<Integer> matches = new ArrayList<>();
                    Random rand = new Random();

                    for (int seed : possibleSeeds) {
                        if (shouldCancel)
                            break;

                        if (checkSeedMatches(seed, safeLevels, safeClues, safeStack, enchantability, rand)) {
                            matches.add(seed);
                        }
                    }

                    if (!shouldCancel) {
                        possibleSeeds = matches;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isCracking = false;
            }
        });
    }

    private static boolean checkSeedMatches(int seed, int[] levels, int[] clues, ItemStack stack, int enchantability,
            Random rand) {
        for (int slot = 0; slot < 3; slot++) {
            if (levels[slot] <= 0 || clues[slot] == -1)
                continue;

            rand.setSeed((long) seed + slot);

            int modifiedLevel = levels[slot] + 1 + rand.nextInt(enchantability / 4 + 1)
                    + rand.nextInt(enchantability / 4 + 1);
            float bonus = (rand.nextFloat() + rand.nextFloat() - 1.0F) * 0.15F;
            modifiedLevel = Math.max(1, Math.round(modifiedLevel + modifiedLevel * bonus));

            List<EnchantmentData> possible = getPossibleEnchantmentsFast(modifiedLevel, stack);
            if (possible.isEmpty())
                return false;

            EnchantmentData first = WeightedRandom.getRandomItem(rand, possible);
            if (first == null || Enchantment.getEnchantmentID(first.enchantment) != clues[slot]) {
                return false;
            }
        }
        return true;
    }

    private static List<EnchantmentData> getPossibleEnchantmentsFast(int level, ItemStack stack) {
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
}