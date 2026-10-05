package com.redblaze908.blazetweaks.commands;

import com.redblaze908.blazetweaks.utils.RNGHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandRNG extends CommandBase {
    @Override
    public String getName() {
        return "rngseed";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/rngseed";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        long worldSeed;
        if (args.length > 0) {
            worldSeed = Long.parseLong(args[0]);
        } else {
            worldSeed = Minecraft.getMinecraft().getIntegratedServer()
                    .getWorld(sender.getEntityWorld().provider.getDimension()).getSeed();
        }

        long[] seedArray = new long[] { RNGHelper.initialScramble(worldSeed) };
        int firstValue = RNGHelper.nextInt(seedArray, 100);

        sender.sendMessage(
                new TextComponentString(TextFormatting.GREEN + "World Seed: " + TextFormatting.WHITE + worldSeed));
        sender.sendMessage(new TextComponentString(
                TextFormatting.GREEN + "First int taken (0-99): " + TextFormatting.WHITE + firstValue));
    }

}
