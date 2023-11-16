package com.github.joelgodofwar.mmh.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {

    @SuppressWarnings({"unchecked"})
    public static <T extends ItemMeta> T getItemMeta(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return (T)(meta != null ? meta : Bukkit.getItemFactory().getItemMeta(item.getType()));
    }

    public static void sendJson(CommandSender player, String string) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw \"" + player.getName() + "\" " + string);
    }

    public static void sendJson(Player player, String string) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw \"" + player.getName() + "\" " + string);
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
