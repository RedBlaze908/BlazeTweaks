package com.redblaze908.blazetweaks.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupManager {

    public static void createBackup() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.isIntegratedServerRunning() || mc.getIntegratedServer() == null)
            return;

        try {
            mc.getIntegratedServer().saveAllWorlds(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String folderName = mc.getIntegratedServer().getFolderName();
        File worldFolder = new File(mc.mcDataDir, "saves/" + folderName);
        File backupDir = new File(mc.mcDataDir, "backups");
        if (!backupDir.exists())
            backupDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File zipFile = new File(backupDir, folderName + "_" + timeStamp + ".zip");

        CompletableFuture.runAsync(() -> {
            try (FileOutputStream fos = new FileOutputStream(zipFile);
                    ZipOutputStream zos = new ZipOutputStream(fos)) {
                zipDirectory(worldFolder, worldFolder.getName(), zos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void restoreAndReload(File zipBackup) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.isIntegratedServerRunning() || mc.getIntegratedServer() == null)
            return;

        String folderName = mc.getIntegratedServer().getFolderName();
        String worldName = mc.getIntegratedServer().getWorldName();

        if (mc.world != null) {
            mc.world.sendQuittingDisconnectingPacket();
        }
        mc.loadWorld(null);
        mc.displayGuiScreen(new GuiMainMenu());

        new Thread(() -> {
            try {
                while (mc.isIntegratedServerRunning()) {
                    Thread.sleep(100);
                }

                Thread.sleep(1000);

                File savesDir = new File(mc.mcDataDir, "saves");
                File currentWorldDir = new File(savesDir, folderName);
                File tempOldDir = new File(savesDir, folderName + "_old_" + System.currentTimeMillis());

                if (currentWorldDir.exists()) {
                    currentWorldDir.renameTo(tempOldDir);
                }

                unzip(zipBackup, savesDir);
                deleteDirectory(tempOldDir);

                mc.addScheduledTask(() -> {
                    mc.launchIntegratedServer(folderName, worldName, null);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void zipDirectory(File folderToZip, String parentFolder, ZipOutputStream zos) throws IOException {
        File[] files = folderToZip.listFiles();
        if (files == null)
            return;
        byte[] buffer = new byte[1024];
        for (File file : files) {
            if (file.getName().endsWith(".lock"))
                continue;
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }
            FileInputStream fis = new FileInputStream(file);
            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
            int len;
            while ((len = fis.read(buffer)) > 0)
                zos.write(buffer, 0, len);
            zos.closeEntry();
            fis.close();
        }
    }

    private static void unzip(File zipFile, File destDir) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0)
                            fos.write(buffer, 0, len);
                    }
                }
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        }
    }

    private static boolean deleteDirectory(File dir) {
        if (!dir.exists())
            return true;
        File[] allFiles = dir.listFiles();
        if (allFiles != null) {
            for (File file : allFiles) {
                deleteDirectory(file);
            }
        }
        boolean deleted = dir.delete();
        if (!deleted) {
            dir.deleteOnExit();
        }
        return deleted;
    }
}