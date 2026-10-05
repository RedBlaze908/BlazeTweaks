package com.redblaze908.blazetweaks.commands;

import com.redblaze908.blazetweaks.utils.BackupManager;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandBackup extends CommandBase {

    @Override
    public String getName() {
        return "backup";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/backup <save|restore|list> [name_file]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Correct use: " + getUsage(sender)));
            return;
        }

        String action = args[0].toLowerCase();

        if (action.equals("save")) {
            BackupManager.createBackup();
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Backup started in the background!"));
        } else if (action.equals("list")) {
            File backupDir = new File(Minecraft.getMinecraft().mcDataDir, "backups");
            File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".zip"));
            if (files == null || files.length == 0) {
                sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "No backups found."));
                return;
            }
            sender.sendMessage(new TextComponentString(TextFormatting.AQUA + "--- Backups Available ---"));
            for (File f : files) {
                sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "- " + f.getName()));
            }
        } else if (action.equals("restore")) {
            if (args.length < 2) {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Specify the backup name!"));
                return;
            }

            String zipName = args[1];
            if (!zipName.endsWith(".zip"))
                zipName += ".zip";

            File backupDir = new File(Minecraft.getMinecraft().mcDataDir, "backups");
            File zipFile = new File(backupDir, zipName);

            if (!zipFile.exists()) {
                sender.sendMessage(
                        new TextComponentString(TextFormatting.RED + "Backup file not found: " + zipName));
                return;
            }

            sender.sendMessage(new TextComponentString(
                    TextFormatting.YELLOW + "Restoring... you will be reloaded soon."));
            BackupManager.restoreAndReload(zipFile);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
            @Nullable BlockPos targetPos) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.add("save");
            options.add("restore");
            options.add("list");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("restore")) {
            File backupDir = new File(Minecraft.getMinecraft().mcDataDir, "backups");
            File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".zip"));
            if (files != null) {
                for (File f : files)
                    options.add(f.getName());
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(lastArg)) {
                matches.add(option);
            }
        }
        return matches;
    }
}