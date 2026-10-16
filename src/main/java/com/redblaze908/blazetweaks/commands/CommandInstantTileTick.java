package com.redblaze908.blazetweaks.commands;

import com.redblaze908.blazetweaks.mixin.WorldInstantTileTickAccessor;
import net.minecraft.block.BlockFalling;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

import java.util.Locale;

public class CommandInstantTileTick extends CommandBase {
    public static volatile boolean ENABLED = false;

    @Override
    public String getName() {
        return "instanttile";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/instanttile <on|off|toggle|status>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + getUsage(sender)));
            return;
        }

        String action = args[0].toLowerCase(Locale.ROOT);
        switch (action) {
            case "on":
                setEnabled(server, true);
                break;
            case "off":
                setEnabled(server, false);
                break;
            case "toggle":
                setEnabled(server, !ENABLED);
                break;
            case "status":
                sendStatus(sender);
                return;
            default:
                sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + getUsage(sender)));
                return;
        }

        sendStatus(sender);
        if (ENABLED) {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW
                    + "ATTENTION: ITT is experimental; can cause loops, freezes or world corruption. Make a backup."));
        }
    }

    private static void setEnabled(MinecraftServer server, boolean enabled) {
        ENABLED = enabled;
        BlockFalling.fallInstantly = enabled;
        for (WorldServer world : server.worlds) {
            if (world != null) {
                ((WorldInstantTileTickAccessor) world).blazetweaks$setScheduledUpdatesAreImmediate(enabled);
            }
        }
    }

    private static void sendStatus(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Instant Tile Tick globale: "
                + (ENABLED ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF")));
    }
}
