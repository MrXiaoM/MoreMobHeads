package com.github.joelgodofwar.mmh.util;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.authlib.GameProfile;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Utils {

    @SuppressWarnings({"unchecked"})
    public static <T extends ItemMeta> T getItemMeta(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return (T)(meta != null ? meta : Bukkit.getItemFactory().getItemMeta(item.getType()));
    }

    @CanIgnoreReturnValue
    public static boolean createFileIfNotExists(File file) {
        if (file.exists()) return false;
        try {
            return file.createNewFile();
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    public static void sendJson(Player player, String string) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw \"" + player.getName() + "\" " + string);
    }

    public static void copyFile_Java7(File origin, File destination) throws IOException {
        Path FROM = origin.toPath();
        Path TO = destination.toPath();
        //overwrite the destination file if it exists, and copy
        // the file attributes, including the rwx permissions
        CopyOption[] options = new CopyOption[]{
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
        };
        Files.copy(FROM, TO, options);
    }

    public static String timeToString(long startTime) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;
        long milliseconds = elapsedTime % 1000;

        if (minutes > 0) {
            return String.format("%d min %d s %d ms.", minutes, seconds, milliseconds);
        } else if (seconds > 0) {
            return String.format("%d s %d ms.", seconds, milliseconds);
        } else {
            return String.format("%d ms.", elapsedTime);
        }
    }

    public static int randomBetween(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private static Method methodProfileItem;

    public static void setGameProfile(SkullMeta meta, GameProfile profile) {
        try {
            if (methodProfileItem == null) {
                methodProfileItem = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            }
            methodProfileItem.setAccessible(true);
            methodProfileItem.invoke(meta, profile);
        } catch (ReflectiveOperationException e) {
            MoreMobHeads.stacktraceInfoStatic();
            e.printStackTrace();
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public String parsePAPI(String string) {
        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(null, string);
        } else {
            return string;
        }
    }

    public String parsePAPI(Player player, String string) {
        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, string);
        } else {
            return string;
        }
    }

    public String parsePAPI(OfflinePlayer player, String string) {
        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, string);
        } else {
            return string;
        }
    }

}
