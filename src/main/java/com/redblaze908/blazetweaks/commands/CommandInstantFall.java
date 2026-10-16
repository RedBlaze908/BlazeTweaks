package com.redblaze908.blazetweaks.commands;

import net.minecraft.block.BlockFalling;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandInstantFall extends CommandBase {

    @Override
    public String getName() {
        return "instantfall";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/instantfall <on|off|toggle|status>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException {
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + getUsage(sender)));
            return;
        }

        String action = args[0].toLowerCase(java.util.Locale.ROOT);
        switch (action) {
            case "on":
                BlockFalling.fallInstantly = true;
                break;
            case "off":
                BlockFalling.fallInstantly = false;
                break;
            case "toggle":
                BlockFalling.fallInstantly = !BlockFalling.fallInstantly;
                break;
            case "status":
                sendStatus(sender);
                return;
            default:
                sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + getUsage(sender)));
                return;
        }

        sendStatus(sender);
    }

    private static void sendStatus(ICommandSender sender) {
        boolean enabled = BlockFalling.fallInstantly;
        sender.sendMessage(new TextComponentString(
                (enabled ? TextFormatting.GREEN : TextFormatting.RED)
                        + "Instant fall: " + (enabled ? "ON" : "OFF")));
    }
}
