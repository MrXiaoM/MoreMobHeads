package com.github.joelgodofwar.mmh.util;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

import static com.github.joelgodofwar.mmh.MoreMobHeads.get;

public class Networks {
    static Metrics metrics = null;
    /**
     * update checker variables
     */
    static final int projectID = 73997; // https://spigotmc.org/resources/71236
    static final String githubURL = "https://github.com/JoelGodOfwar/MoreMobHeads/raw/master/versioncheck/1.15/versions.xml";
    static boolean updateAvailable = false;
    static String UCOldVer;
    static String UCNewVer;
    public static boolean checkUpdate;
    public static final String downloadLink = "https://www.spigotmc.org/resources/moremobheads.73997";
    public static boolean isUpdateAvailable() {
        return updateAvailable;
    }
    public static String getUpdateOldVersion() {
        return UCOldVer;
    }
    public static String getUpdateNewVersion() {
        return UCNewVer;
    }
    public static void checkUpdate(MoreMobHeads mmh) {
        if (!checkUpdate) return;
        mmh.loading("Checking for updates...");
        try {
            VersionChecker updater = new VersionChecker(mmh, projectID, githubURL);
            if (updater.checkForUpdates()) {
                /* Update available */
                updateAvailable = true; // TODO: Update Checker
                UCOldVer = updater.oldVersion();
                UCNewVer = updater.newVersion();

                mmh.loading("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
                mmh.loading("* " + get("mmh.version.message").replace("<MyPlugin>", MoreMobHeads.pluginDisplayName()));
                mmh.loading("* " + get("mmh.version.old_vers") + ChatColor.RED + UCOldVer);
                mmh.loading("* " + get("mmh.version.new_vers") + ChatColor.GREEN + UCNewVer);
                mmh.loading("*");
                mmh.loading("* " + get("mmh.version.please_update"));
                mmh.loading("*");
                mmh.loading("* " + get("mmh.version.download") + ": " + downloadLink + "/history");
                mmh.loading("* " + get("mmh.version.donate") + ": https://ko-fi.com/joelgodofwar");
                mmh.loading("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
            } else {
                /* Up to date */
                mmh.loading("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
                mmh.loading("* " + get("mmh.version.curvers"));
                mmh.loading("* " + get("mmh.version.donate") + ": https://ko-fi.com/joelgodofwar");
                mmh.loading("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
                updateAvailable = false;
            }
        } catch (Exception e) {
            /* Error */
            mmh.loading(get("mmh.version.update.error"));
            e.printStackTrace();
        }
    }
    public static void startMetrics(MoreMobHeads mmh) {
        if (metrics != null) return;
        metrics = new Metrics(mmh, 6128);
        // New chart here
        // myPlugins()
        metrics.addCustomChart(new Metrics.AdvancedPie("my_other_plugins", () -> {
            Map<String, Integer> valueMap = new HashMap<>();

            if (Bukkit.getPluginManager().getPlugin("DragonDropElytra") != null) {
                valueMap.put("DragonDropElytra", 1);
            }
            if (Bukkit.getPluginManager().getPlugin("NoEndermanGrief") != null) {
                valueMap.put("NoEndermanGrief", 1);
            }
            if (Bukkit.getPluginManager().getPlugin("PortalHelper") != null) {
                valueMap.put("PortalHelper", 1);
            }
            if (Bukkit.getPluginManager().getPlugin("ShulkerRespawner") != null) {
                valueMap.put("ShulkerRespawner", 1);
            }
            //if(Bukkit.getPluginManager().getPlugin("MoreMobHeads") != null){valueMap.put("MoreMobHeads", 1);}
            if (Bukkit.getPluginManager().getPlugin("SilenceMobs") != null) {
                valueMap.put("SilenceMobs", 1);
            }
            if (Bukkit.getPluginManager().getPlugin("SinglePlayerSleep") != null) {
                valueMap.put("SinglePlayerSleep", 1);
            }
            if (Bukkit.getPluginManager().getPlugin("VillagerWorkstationHighlights") != null) {
                valueMap.put("VillagerWorkstationHighlights", 1);
            }
            if (Bukkit.getPluginManager().getPlugin("RotationalWrench") != null) {
                valueMap.put("RotationalWrench", 1);
            }
            return valueMap;
        }));
        metrics.addCustomChart(new Metrics.AdvancedPie("vanilla_heads", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            //int varTotal = myPlugins();
            valueMap.put("CREEPER " + mmh.getConfig().getString("vanilla_heads.creeper", "").toUpperCase(), 1);
            valueMap.put("ENDER_DRAGON " + mmh.getConfig().getString("vanilla_heads.ender_dragon", "").toUpperCase(), 1);
            valueMap.put("SKELETON " + mmh.getConfig().getString("vanilla_heads.skeleton", "").toUpperCase(), 1);
            valueMap.put("WITHER_SKELETON " + mmh.getConfig().getString("vanilla_heads.wither_skeleton", "").toUpperCase(), 1);
            valueMap.put("ZOMBIE " + mmh.getConfig().getString("vanilla_heads.zombie", "").toUpperCase(), 1);
            return valueMap;
        }));
        metrics.addCustomChart(new Metrics.SimplePie("auto_update_check", () -> "" + mmh.getConfig().getString("auto_update_check", "").toUpperCase()));
        // add to site
        metrics.addCustomChart(new Metrics.SimplePie("var_debug", () -> "" + mmh.getConfig().getString("debug", "").toUpperCase()));
        metrics.addCustomChart(new Metrics.SimplePie("var_lang", () -> "" + mmh.getConfig().getString("lang", "").toUpperCase()));
        metrics.addCustomChart(new Metrics.SimplePie("whitelist.enforce", () -> "" + mmh.getConfig().getString("whitelist.enforce", "").toUpperCase()));
        metrics.addCustomChart(new Metrics.SimplePie("blacklist.enforce", () -> "" + mmh.getConfig().getString("blacklist.enforce", "").toUpperCase()));
        metrics.addCustomChart(new Metrics.SimplePie("custom_wandering_trader", () -> "" + mmh.getConfig().getString("wandering_trades.custom_wandering_trader", "").toUpperCase()));
        metrics.addCustomChart(new Metrics.SimplePie("player_heads", () -> "" + mmh.getConfig().getString("wandering_trades.player_heads.enabled", "").toUpperCase()));
        metrics.addCustomChart(new Metrics.SimplePie("block_heads", () -> "" + mmh.getConfig().getString("wandering_trades.block_heads.enabled", "").toUpperCase()));
        metrics.addCustomChart(new Metrics.SimplePie("custom_trades", () -> "" + mmh.getConfig().getString("wandering_trades.custom_trades.enabled", "").toUpperCase()));
        metrics.addCustomChart(new Metrics.SimplePie("apply_looting", () -> "" + mmh.getConfig().getString("apply_looting", "").toUpperCase()));
        metrics.addCustomChart(new Metrics.SimplePie("show_killer", () -> "" + mmh.getConfig().getString("lore.show_killer", "").toUpperCase()));
        metrics.addCustomChart(new Metrics.SimplePie("show_plugin_name", () -> "" + mmh.getConfig().getString("lore.show_plugin_name", "").toUpperCase()));
    }
}