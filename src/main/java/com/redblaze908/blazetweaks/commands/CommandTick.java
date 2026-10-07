package com.redblaze908.blazetweaks.commands;

import com.redblaze908.blazetweaks.events.TickControlHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandTick extends CommandBase {

    @Override
    public String getName() {
        return "ticks";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ticks <freeze|unfreeze|step <N>>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Utilizzabile da qualsiasi giocatore client
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + getUsage(sender)));
            return;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("freeze")) {
            TickControlHandler.freeze();
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "[BlazeTweaks] Game frozen."));
        } else if (subCommand.equals("unfreeze")) {
            TickControlHandler.unfreeze();
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "[BlazeTweaks] Game unfrozen."));
        } else if (subCommand.equals("step")) {
            int ticks = 1;
            if (args.length > 1) {
                try {
                    ticks = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid tick number!"));
                    return;
                }
            }
            TickControlHandler.step(ticks);
            sender.sendMessage(
                    new TextComponentString(TextFormatting.GREEN + "[BlazeTweaks] Stepping " + ticks + " tick(s)..."));
        } else {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + getUsage(sender)));
        }
    }
}