package com.redblaze908.blazetweaks.commands;

import com.redblaze908.blazetweaks.lightning.LightningPredictor;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

import java.util.List;

public class CommandLightningFinder extends CommandBase {

    private static final int DEFAULT_RADIUS = 12;
    private static final int MAX_ALLOWED_RESULTS = 100;

    @Override
    public String getName() {
        return "lightningfinder";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/lightningfinder <count> [radius] [centerChunkX centerChunkZ]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException(getUsage(sender));
        }

        int count;
        try {
            count = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new WrongUsageException(getUsage(sender));
        }

        if (count <= 0 || count > MAX_ALLOWED_RESULTS) {
            sender.sendMessage(new TextComponentString(
                    TextFormatting.RED + "count must be between 1 and " + MAX_ALLOWED_RESULTS));
            return;
        }

        int radius = DEFAULT_RADIUS;
        if (args.length >= 2) {
            try {
                radius = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new WrongUsageException(getUsage(sender));
            }
        }

        int centerChunkX;
        int centerChunkZ;

        if (args.length >= 4) {
            try {
                centerChunkX = Integer.parseInt(args[2]);
                centerChunkZ = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                throw new WrongUsageException(getUsage(sender));
            }
        } else {
            centerChunkX = ((int) Math.floor(sender.getPositionVector().x)) >> 4;
            centerChunkZ = ((int) Math.floor(sender.getPositionVector().z)) >> 4;
        }

        int dimensionId = sender.getEntityWorld().provider.getDimension();
        WorldServer world = server.getWorld(dimensionId);

        int randomTickSpeed = world.getGameRules().getInt("randomTickSpeed");
        if (randomTickSpeed != 0) {
            sender.sendMessage(new TextComponentString(
                    TextFormatting.YELLOW + "Warning: randomTickSpeed is not 0 (" + randomTickSpeed
                            + "). Results may not be reliable."));
        }

        sender.sendMessage(new TextComponentString(
                TextFormatting.GOLD + "Looking for " + count + " lightning within a radius of " + radius
                        + " chunk around [" + centerChunkX + ", " + centerChunkZ + "]..."));

        List<LightningPredictor.Prediction> results = LightningPredictor.predict(world.rand, centerChunkX, centerChunkZ,
                radius, count);

        if (results.isEmpty()) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "No lightning found."));
            return;
        }

        for (LightningPredictor.Prediction p : results) {
            String tag = "";
            if (p.chargedCreeperEligible) {
                tag = TextFormatting.LIGHT_PURPLE + " [charged creeper eligible]";
            } else if (p.skeletonTrapEligible) {
                tag = TextFormatting.DARK_PURPLE + " [skeleton trap eligible]";
            }

            sender.sendMessage(new TextComponentString(
                    TextFormatting.AQUA + "Chunk [" + p.chunkX + ", " + p.chunkZ + "] "
                            + TextFormatting.WHITE + "-> block (" + p.blockX + ", " + p.blockZ + ") at (" + p.blockX
                            + "..+15, " + p.blockZ + "..+15) "
                            + TextFormatting.GRAY + "| region r." + p.regionX + "." + p.regionZ + ".mca "
                            + "(index " + p.chunkIndex + ", trapValue=" + String.format("%.4f", p.trapValue) + ")"
                            + tag));
        }
    }
}