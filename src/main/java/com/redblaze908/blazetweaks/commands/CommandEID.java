package com.redblaze908.blazetweaks.commands;

import com.redblaze908.blazetweaks.accessor.EntityIdAccessor;
import com.redblaze908.blazetweaks.eid.EntityIdFixState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.Locale;

public class CommandEID extends CommandBase {
    @Override
    public String getName() {
        return "eid";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/eid <get|set <value>|increment [value]|decrement [value]|entity <selector>|collide <selector>|fix>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);

        if ("entity".equals(subcommand) || "collide".equals(subcommand)) {
            if (args.length != 2) {
                throw new CommandException("Uso: /eid " + subcommand + " <selector>");
            }

            Entity target = CommandBase.getEntity(server, sender, args[1]);
            if ("entity".equals(subcommand)) {
                sender.sendMessage(new TextComponentString(
                        "Entity runtime ID: " + target.getEntityId()
                                + " (type: " + target.getClass().getSimpleName() + ")"));
            } else {
                Entity commandEntity = sender.getCommandSenderEntity();
                if (!(commandEntity instanceof EntityIdAccessor)) {
                    throw new CommandException("Execute this command from a player.");
                }
                ((EntityIdAccessor) commandEntity).blazetweaks$setNextEntityID(target.getEntityId());
                sender.sendMessage(new TextComponentString(
                        "Next Entity ID set to " + target.getEntityId()
                                + ", same as the ID of " + target.getName()
                                + ". Now generate another entity to test the collision."));
            }
            return;
        }

        if ("fix".equals(subcommand)) {
            if (args.length != 1) {
                throw new CommandException("Uso: /eid fix");
            }
            boolean enabled = EntityIdFixState.toggle();
            sender.sendMessage(new TextComponentString(
                    "Singleplayer Entity ID fix " + (enabled ? "ACTIVATED" : "DEACTIVATED") + "."));
            return;
        }

        Entity entity = sender.getCommandSenderEntity();
        if (!(entity instanceof EntityIdAccessor)) {
            throw new CommandException("Run this command from a player.");
        }
        EntityIdAccessor accessor = (EntityIdAccessor) entity;
        int current = accessor.blazetweaks$getNextEntityID();

        switch (subcommand) {
            case "get":
                if (args.length != 1) {
                    throw new CommandException("Uso: /eid get");
                }
                sender.sendMessage(new TextComponentString("Current Entity ID: " + current
                        + " (client increments avoided: " + EntityIdFixState.getSuppressed() + ")"));
                break;

            case "set":
                if (args.length != 2) {
                    throw new CommandException("Uso: /eid set <value>");
                }
                int setValue = parseInt(args[1]);
                accessor.blazetweaks$setNextEntityID(setValue);
                sender.sendMessage(new TextComponentString("Current Entity ID set to: " + setValue));
                break;

            case "increment":
                if (args.length > 2) {
                    throw new CommandException("Uso: /eid increment [value]");
                }
                int increment = args.length == 2 ? parseInt(args[1]) : 1;
                try {
                    accessor.blazetweaks$setNextEntityID(Math.addExact(current, increment));
                } catch (ArithmeticException ex) {
                    throw new CommandException("Value outside integer limits.");
                }
                sender.sendMessage(new TextComponentString(
                        "Current Entity ID: " + accessor.blazetweaks$getNextEntityID()));
                break;

            case "decrement":
                if (args.length > 2) {
                    throw new CommandException("Uso: /eid decrement [value]");
                }
                int decrement = args.length == 2 ? parseInt(args[1]) : 1;
                try {
                    accessor.blazetweaks$setNextEntityID(Math.subtractExact(current, decrement));
                } catch (ArithmeticException ex) {
                    throw new CommandException("Value outside integer limits.");
                }
                sender.sendMessage(new TextComponentString(
                        "Current Entity ID: " + accessor.blazetweaks$getNextEntityID()));
                break;

            default:
                sender.sendMessage(new TextComponentString(getUsage(sender)));
        }
    }
}