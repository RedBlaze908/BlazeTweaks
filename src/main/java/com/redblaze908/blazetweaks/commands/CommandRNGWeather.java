package com.redblaze908.blazetweaks.commands;

import com.redblaze908.blazetweaks.utils.RNGHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.WorldInfo;

public class CommandRNGWeather extends CommandBase {

    @Override
    public String getName() {
        return "rngweather";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/rngweather";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        WorldInfo info = Minecraft.getMinecraft().getIntegratedServer()
                .getWorld(sender.getEntityWorld().provider.getDimension()).getWorldInfo();

        boolean isThundering = info.isThundering();
        int currentThunderTime = info.getThunderTime();

        int secondsLeft = currentThunderTime / 20;
        int minutesLeft = secondsLeft / 60;

        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "=== WEATHER & LIGHTNING PREDICTION ==="));

        if (isThundering) {
            sender.sendMessage(new TextComponentString(
                    TextFormatting.RED + "Actual State:" + TextFormatting.WHITE + "Thunderstorm in progress!"));
            sender.sendMessage(new TextComponentString(
                    TextFormatting.YELLOW + "End time in: " + TextFormatting.WHITE + secondsLeft + "s (" + minutesLeft
                            + "m) [" + currentThunderTime + " tick]"));
        } else {
            sender.sendMessage(new TextComponentString(
                    TextFormatting.GREEN + "Current status: " + TextFormatting.GREEN + "Serene Sky"));
            sender.sendMessage(new TextComponentString(
                    TextFormatting.GREEN + "Next storm in: " + TextFormatting.WHITE + secondsLeft + "s ("
                            + minutesLeft + "m) [" + currentThunderTime + " tick]"));
        }
    }
}