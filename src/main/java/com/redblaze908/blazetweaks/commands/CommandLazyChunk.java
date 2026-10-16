package com.redblaze908.blazetweaks.commands;

import com.redblaze908.blazetweaks.events.LazyChunkManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommandLazyChunk extends CommandBase {
    @Override
    public String getName() { return "lazychunk"; }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/lazychunk [chunkX chunkZ] | off [chunkX chunkZ] | list | auto <on|off>";
    }

    @Override
    public int getRequiredPermissionLevel() { return 0; }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (server.isDedicatedServer()) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "/lazychunk è disponibile solo in singleplayer."));
            return;
        }
        if (!(sender.getCommandSenderEntity() instanceof net.minecraft.entity.player.EntityPlayer)) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Devi essere un giocatore per usare questo comando."));
            return;
        }

        net.minecraft.entity.player.EntityPlayer player = (net.minecraft.entity.player.EntityPlayer) sender.getCommandSenderEntity();
        int dimension = player.dimension;

        if (args.length == 0) {
            setLazy(server, sender, dimension, player.chunkCoordX, player.chunkCoordZ, true);
            return;
        }

        String action = args[0].toLowerCase(Locale.ROOT);
        if (action.equals("list") && args.length == 1) {
            int count = 0;
            sender.sendMessage(new TextComponentString(TextFormatting.AQUA + "Chunk lazy (dimensione " + dimension + "):"));
            for (LazyChunkManager.ChunkKey key : LazyChunkManager.getLazyChunks()) {
                if (key.dimension == dimension) {
                    sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "- " + key.x + "," + key.z));
                    count++;
                }
            }
            if (count == 0) sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Nessun chunk lazy in questa dimensione."));
            return;
        }
        if (action.equals("auto") && args.length == 2) {
            if (args[1].equalsIgnoreCase("on")) {
                LazyChunkManager.setAutoRestoreEnabled(true);
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Ripristino automatico attivo: i chunk lazy verranno normalizzati quando escono dalla render distance."));
            } else if (args[1].equalsIgnoreCase("off")) {
                LazyChunkManager.setAutoRestoreEnabled(false);
                sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Ripristino automatico disattivato."));
            } else {
                usage(sender);
            }
            return;
        }
        if (action.equals("off")) {
            if (args.length == 1) {
                setLazy(server, sender, dimension, player.chunkCoordX, player.chunkCoordZ, false);
            } else if (args.length == 3) {
                setLazy(server, sender, dimension, parseChunkCoordinate(args[1]), parseChunkCoordinate(args[2]), false);
            } else {
                usage(sender);
            }
            return;
        }
        if (args.length == 2) {
            setLazy(server, sender, dimension, parseChunkCoordinate(args[0]), parseChunkCoordinate(args[1]), true);
            return;
        }
        usage(sender);
    }

    private int parseChunkCoordinate(String value) throws CommandException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new CommandException("Coordinate chunk non valida: " + value);
        }
    }

    private void setLazy(MinecraftServer server, ICommandSender sender, int dimension, int x, int z, boolean lazy) {
        WorldServer world = server.getWorld(dimension);
        if (lazy && (world == null || world.getChunkProvider().getLoadedChunk(x, z) == null)) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED
                    + "Il chunk " + x + "," + z + " non è caricato. Puoi rendere lazy solo chunk già caricati."));
            return;
        }
        boolean changed = lazy ? LazyChunkManager.add(dimension, x, z) : LazyChunkManager.remove(dimension, x, z);
        if (lazy) {
            sender.sendMessage(new TextComponentString((changed ? TextFormatting.GREEN : TextFormatting.YELLOW)
                    + (changed ? "Chunk " + x + "," + z + " reso lazy." : "Il chunk " + x + "," + z + " era già lazy.")));
        } else {
            sender.sendMessage(new TextComponentString((changed ? TextFormatting.GREEN : TextFormatting.YELLOW)
                    + (changed ? "Chunk " + x + "," + z + " ripristinato al ticking normale." : "Il chunk " + x + "," + z + " non era nella lista lazy.")));
        }
    }

    private void usage(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + getUsage(sender)));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
            @Nullable BlockPos targetPos) {
        List<String> options = new ArrayList<>();
        if (server.isDedicatedServer()) return options;
        net.minecraft.entity.Entity entity = sender.getCommandSenderEntity();
        if (!(entity instanceof net.minecraft.entity.player.EntityPlayer)) return options;
        net.minecraft.entity.player.EntityPlayer player = (net.minecraft.entity.player.EntityPlayer) entity;
        if (args.length == 1) {
            options.add(Integer.toString(player.chunkCoordX));
            options.add("off");
            options.add("list");
            options.add("auto");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("auto")) {
                options.add("on");
                options.add("off");
            } else if (args[0].equalsIgnoreCase("off")) {
                for (LazyChunkManager.ChunkKey key : LazyChunkManager.getLazyChunks()) {
                    if (key.dimension == player.dimension) options.add(Integer.toString(key.x));
                }
                options.add(Integer.toString(player.chunkCoordX));
            } else {
                options.add(Integer.toString(player.chunkCoordZ));
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("off")) {
            for (LazyChunkManager.ChunkKey key : LazyChunkManager.getLazyChunks()) {
                if (key.dimension == player.dimension && Integer.toString(key.x).equals(args[1])) {
                    options.add(Integer.toString(key.z));
                }
            }
            options.add(Integer.toString(player.chunkCoordZ));
        }
        return getListOfStringsMatchingLastWord(args, options);
    }
}
