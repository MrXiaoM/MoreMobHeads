package com.github.joelgodofwar.mmh;

import com.github.joelgodofwar.mmh.i18n.Translator;
import com.github.joelgodofwar.mmh.util.Networks;
import com.github.joelgodofwar.mmh.util.Utils;
import com.github.joelgodofwar.mmh.util.YmlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config {
    private static MoreMobHeads mmh;
    public static void reload(MoreMobHeads mmh) {
        Config.mmh = mmh;

        reloadMainConfig();

        if (!mmh.config.getBoolean("console.longpluginname", true)) {
            mmh.pluginName = "MMH";
        } else {
            mmh.pluginName = MoreMobHeads.THIS_NAME;
        }

        Networks.checkUpdate = mmh.config.getBoolean("auto_update_check");
        MoreMobHeads.debug = mmh.config.getBoolean("debug", false);
        MoreMobHeads.languageName = mmh.config.getString("lang", "zh_CN");

        mmh.world_whitelist = mmh.config.getString("world.whitelist", "");
        mmh.world_blacklist = mmh.config.getString("world.blacklist", "");
        mmh.mob_whitelist = mmh.config.getString("mob.whitelist", "");
        mmh.mob_blacklist = mmh.config.getString("mob.blacklist", "");
        mmh.colorful_console = mmh.config.getBoolean("console.colorful_console", true);

        reloadMessages();
        reloadChanceConfig();
        reloadMobNames();
        reloadOtherHeads();
        reloadCustomTrade();

        Translator.load(MoreMobHeads.languageName, mmh.getDataFolder(), mmh::getResource);

        if (mmh.handler != null) {
            mmh.handler.onReload();
        }
    }

    private static void reloadMainConfig() {
        File configFile = new File(mmh.getDataFolder(), "config.yml");
        File oldConfigFile = new File(mmh.getDataFolder(), "old_config.yml");
        YamlConfiguration oldConfig = new YamlConfiguration();
        mmh.log(Level.INFO, "Checking config file version...");
        try {
            oldConfig.load(configFile);
        } catch (Exception e2) {
            mmh.logWarn("Could not load config.yml");
            mmh.stacktraceInfo();
            e2.printStackTrace();
        }
        String configVersion = oldConfig.getString("version", "1.0.0");
        if (!configVersion.equalsIgnoreCase(BuildConstants.CONFIG_VERSION)) {
            try {
                Utils.copyFile_Java7(configFile, oldConfigFile);
            } catch (IOException e) {
                mmh.stacktraceInfo();
                e.printStackTrace();
            }
            mmh.saveResource("config.yml", true);

            try {
                mmh.config.load(configFile);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.logWarn("Could not load config.yml");
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }
            try {
                oldConfig.load(oldConfigFile);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.logWarn("Could not load old_config.yml");
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }
            mmh.config.set("auto_update_check", oldConfig.get("auto_update_check", true));
            mmh.config.set("debug", oldConfig.get("debug", false));
            mmh.config.set("lang", oldConfig.get("lang", "en_US"));
            mmh.config.set("console.colorful_console", oldConfig.get("colorful_console", true));
            mmh.config.set("vanilla_heads.creepers", oldConfig.get("vanilla_heads.creepers", false));
            mmh.config.set("vanilla_heads.ender_dragon", oldConfig.get("vanilla_heads.ender_dragon", false));
            mmh.config.set("vanilla_heads.skeleton", oldConfig.get("vanilla_heads.skeleton", false));
            mmh.config.set("vanilla_heads.wither_skeleton", oldConfig.get("vanilla_heads.wither_skeleton", false));
            mmh.config.set("vanilla_heads.zombie", oldConfig.get("vanilla_heads.zombie", false));
            mmh.config.set("world.whitelist", oldConfig.get("world.whitelist", ""));
            mmh.config.set("world.blacklist", oldConfig.get("world.blacklist", ""));
            mmh.config.set("mob.whitelist", oldConfig.get("mob.whitelist", ""));
            mmh.config.set("mob.blacklist", oldConfig.get("mob.blacklist", ""));
            mmh.config.set("mob.nametag", oldConfig.get("mob.nametag", false));
            mmh.config.set("lore.show_killer", oldConfig.get("lore.show_killer", true));
            mmh.config.set("lore.show_plugin_name", oldConfig.get("lore.show_plugin_name", true));
            mmh.config.set("wandering_trades.custom_wandering_trader", oldConfig.get("wandering_trades.custom_wandering_trader", true));
            mmh.config.set("wandering_trades.player_heads.enabled", oldConfig.get("wandering_trades.player_heads.enabled", true));
            mmh.config.set("wandering_trades.player_heads.min", oldConfig.get("wandering_trades.player_heads.min", 0));
            mmh.config.set("wandering_trades.player_heads.max", oldConfig.get("wandering_trades.player_heads.max", 5));
            mmh.config.set("wandering_trades.block_heads.enabled", oldConfig.get("wandering_trades.block_heads.enabled", true));
            mmh.config.set("wandering_trades.block_heads.pre_116.min", oldConfig.get("wandering_trader_min_block_heads", 0));
            mmh.config.set("wandering_trades.block_heads.pre_116.max", oldConfig.get("wandering_trader_max_block_heads", 5));
            mmh.config.set("wandering_trades.block_heads.is_116.min", oldConfig.get("wandering_trader_min_block_heads", 0));
            mmh.config.set("wandering_trades.block_heads.is_116.max", oldConfig.get("wandering_trader_max_block_heads", 5));
            mmh.config.set("wandering_trades.block_heads.is_117.min", oldConfig.get("wandering_trader_min_block_heads", 0));
            mmh.config.set("wandering_trades.block_heads.is_117.max", oldConfig.get("wandering_trader_max_block_heads", 5));

            mmh.config.set("wandering_trades.custom_trades.enabled", oldConfig.get("wandering_trades.custom_trades.enabled", false));
            mmh.config.set("wandering_trades.custom_trades.min", oldConfig.get("wandering_trades.custom_trades.min", 0));
            mmh.config.set("wandering_trades.custom_trades.max", oldConfig.get("wandering_trades.custom_trades.max", 5));
            mmh.config.set("apply_looting", oldConfig.get("apply_looting", true));
            mmh.config.set("whitelist.enforce", oldConfig.get("whitelist.enforce", true));
            mmh.config.set("whitelist.player_head_whitelist", oldConfig.get("whitelist.player_head_whitelist", "names_go_here"));
            mmh.config.set("blacklist.enforce", oldConfig.get("enforce_blacklist", true));
            mmh.config.set("blacklist.player_head_blacklist", oldConfig.get("blacklist.player_head_blacklist", "names_go_here"));

            try {
                mmh.config.save(configFile);
            } catch (IOException e) {
                mmh.logWarn("Could not save old settings to config.yml");
                mmh.stacktraceInfo();
                e.printStackTrace();
            }
            mmh.saveResource("chance_config.yml", true);
            mmh.log(Level.INFO, "config.yml Updated! old config saved as old_config.yml");
            mmh.log(Level.INFO, "chance_config.yml saved.");
        }
        mmh.log(Level.INFO, "Loading config file...");
        try {
            mmh.getConfig().load(configFile);
            mmh.config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            mmh.logWarn("Could not load config.yml");
            mmh.stacktraceInfo();
            e.printStackTrace();
        }
    }

    private static void reloadMessages() {
        File messageFile = new File(mmh.getDataFolder(), "message.yml");
        File oldMessageFile = new File(mmh.getDataFolder(), "old_message.yml");
        mmh.consoleLog("Loading messages file...");
        YamlConfiguration oldMessages = new YamlConfiguration();
        try {
            oldMessages.load(messageFile);
        } catch (Exception e) {
            mmh.logWarn("Could not load messages.yml");
            mmh.stacktraceInfo();
            e.printStackTrace();
        }

        String messageVersion = oldMessages.getString("version", "1.0.0");
        mmh.log("messages.yml, Expected version:[" + BuildConstants.MESSAGE_VERSION + "], Read version:[" + messageVersion + "]\nThese should be the same.");
        if (!messageVersion.equalsIgnoreCase(BuildConstants.MESSAGE_VERSION)) {
            try {
                Utils.copyFile_Java7(messageFile, oldMessageFile);
            } catch (IOException e) {
                mmh.stacktraceInfo();
                e.printStackTrace();
            }
            mmh.saveResource("messages.yml", true);

            try {
                mmh.beheadingMessages.load(messageFile);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.logWarn("Could not load messages.yml");
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }
            try {
                oldMessages.load(oldMessageFile);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }

            // Update messages
            ConfigurationSection oldMessagesSection = oldMessages.getConfigurationSection("messages");
            if(oldMessagesSection != null) {
                for (String messageKey : oldMessagesSection.getKeys(false)) {
                    String messageValue = oldMessagesSection.getString(messageKey, messageKey);
                    mmh.beheadingMessages.set("messages." + messageKey, messageValue.replace("<killerName>", "%killerName%")
                            .replace("<entityName>", "%entityName%")
                            .replace("<weaponName>", "%weaponName%"));
                }
            }

            try {
                mmh.beheadingMessages.save(messageFile);
            } catch (IOException e) {
                mmh.logWarn("Could not save old messages to messages.yml");
                mmh.stacktraceInfo();
                e.printStackTrace();
            }
            mmh.log(Level.INFO, "messages.yml Updated! Old messages saved as old_messages.yml");
        } else {
            try {
                mmh.beheadingMessages.load(messageFile);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.logWarn("Could not load messages.yml");
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }
        }
    }

    private static void reloadChanceConfig() {
        File chanceConfigFile = new File(mmh.getDataFolder(), "chance_config.yml");
        File oldChanceConfigFile = new File(mmh.getDataFolder(), "old_chance_config.yml");
        /* chanceConfig load */
        mmh.chanceFile = new File(mmh.getDataFolder(), "chance_config.yml");
        if (MoreMobHeads.debug) {
            mmh.logDebug("chanceFile=" + mmh.chanceFile.getPath());
        }
        if (!mmh.chanceFile.exists()) {
            mmh.saveResource("chance_config.yml", true);
            mmh.log(Level.INFO, "chance_config.yml not found! copied chance_config.yml to " + mmh.getDataFolder() + "");
        }
        mmh.consoleLog("Loading chance_config file...");
        mmh.chanceConfig = new YmlConfiguration();
        mmh.chanceFile = new File(mmh.getDataFolder(), "chance_config.yml");
        try {
            mmh.chanceConfig.load(mmh.chanceFile);
        } catch (IOException | InvalidConfigurationException e) {
            mmh.stacktraceInfo();
            e.printStackTrace();
        }
        /* chanceConfig update check */
        String chanceConfigVersion = mmh.chanceConfig.getString("version", "1.0.0");
        if (!chanceConfigVersion.equalsIgnoreCase(BuildConstants.CHANCE_CONFIG_VERSION)) {
            mmh.logDebug("Expected v: " + BuildConstants.CHANCE_CONFIG_VERSION + "got v: " + chanceConfigVersion);
            try {
                Utils.copyFile_Java7(chanceConfigFile, oldChanceConfigFile);
            } catch (IOException e) {
                mmh.stacktraceInfo();
                e.printStackTrace();
            }

            mmh.saveResource("chance_config.yml", true);
            copyChance(mmh, oldChanceConfigFile, mmh.chanceFile);
            mmh.log(Level.INFO, "chance_config.yml updated.");
        }
    }

    private static void reloadMobNames() {
        /* Mob names translation */
        mmh.mobNamesFile = new File(mmh.getDataFolder(), "lang/" + MoreMobHeads.languageName + "_mobnames.yml");
        if (MoreMobHeads.debug) {
            mmh.logDebug("langFilePath=" + mmh.mobNamesFile.getPath());
        }
        if (!mmh.mobNamesFile.exists()) {
            mmh.saveResource("lang/" + MoreMobHeads.languageName + "_mobnames.yml", true);
            mmh.log(Level.INFO, "lang_mobnames file not found! saved default " + MoreMobHeads.languageName + "_mobnames.yml to lang folder.");
        }
        mmh.consoleLog("Loading language based mobname file...");
        mmh.mobNames = new YamlConfiguration();
        try {
            mmh.mobNames.load(mmh.mobNamesFile);
        } catch (IOException | InvalidConfigurationException e) {
            mmh.stacktraceInfo();
            e.printStackTrace();
        }
        /* Mob Names update check */
        String mobNamesVersionChecker = mmh.mobNames.getString("vex.angry", "outdated");
        if (mobNamesVersionChecker.equalsIgnoreCase("outdated")) {
            mmh.log(Level.INFO, "lang_mobnames file outdated! Updating.");
            mmh.saveResource("lang/" + MoreMobHeads.languageName + "_mobnames.yml", true);
            mmh.log(Level.INFO, MoreMobHeads.languageName + "_mobnames.yml updated.");
            try {
                mmh.mobNames.load(mmh.mobNamesFile);
            } catch (IOException | InvalidConfigurationException e) {
                mmh.stacktraceInfo();
                e.printStackTrace();
            }
        }
        /* end Mob names translation */
    }

    private static void reloadOtherHeads() {
        /* Trader heads load */
        mmh.playerFile = new File(mmh.getDataFolder(), "player_heads.yml");
        if (MoreMobHeads.debug) {
            mmh.logDebug("player_heads=" + mmh.playerFile.getPath());
        }
        if (!mmh.playerFile.exists()) {
            mmh.saveResource("player_heads.yml", true);
            mmh.log(Level.INFO, "player_heads.yml not found! saved default player_heads.yml");
        }

        mmh.log(Level.INFO, "Loading player_heads file...");
        mmh.playerHeads = new YamlConfiguration();
        try {
            mmh.playerHeads.load(mmh.playerFile);
        } catch (IOException | InvalidConfigurationException e) {
            mmh.stacktraceInfo();
            e.printStackTrace();
        }
        mmh.log(Level.INFO, "" + mmh.playerHeads.getInt("players.number") + " player_heads Loaded...");
        mmh.log("MC Version=" + MoreMobHeads.getMCVersion());
        if (!MoreMobHeads.getMCVersion().startsWith("1.16") && !MoreMobHeads.getMCVersion().startsWith("1.17") && !MoreMobHeads.getMCVersion().startsWith("1.18")) {
            mmh.blockFile = new File(mmh.getDataFolder(), "block_heads.yml");
            if (MoreMobHeads.debug) {
                mmh.logDebug("block_heads=" + mmh.blockFile.getPath());
            }
            if (!mmh.blockFile.exists()) {
                mmh.saveResource("block_heads.yml", true);
                mmh.log(Level.INFO, "block_heads.yml not found! saved default block_heads.yml");
            }
        }
        /* Block heads load start */
        mmh.blockFile116 = new File(mmh.getDataFolder(), "block_heads_1_16.yml");
        mmh.blockFile1162 = new File(mmh.getDataFolder(), "block_heads_1_16_2.yml");
        mmh.blockFile117 = new File(mmh.getDataFolder(), "block_heads_1_17_3.yml");
        mmh.blockFile119 = new File(mmh.getDataFolder(), "block_heads_1_19.yml");

        if (MoreMobHeads.getMCVersion().startsWith("1.16")) {
            if (MoreMobHeads.debug) {
                mmh.logDebug("block_heads_1_16=" + mmh.blockFile116.getPath());
            }
            if (MoreMobHeads.debug) {
                mmh.logDebug("block_heads_1_16_2=" + mmh.blockFile1162.getPath());
            }
            if (!mmh.blockFile116.exists()) {
                mmh.saveResource("block_heads_1_16.yml", true);
                mmh.log(Level.INFO, "block_heads_1_16.yml not found! saved default block_heads_1_16.yml");
            }
            if (!mmh.blockFile1162.exists()) {
                mmh.saveResource("block_heads_1_16_2.yml", true);
                mmh.log(Level.INFO, "block_heads_1_16_2.yml not found! saved default block_heads_1_16_2.yml");
            }
            mmh.blockFile = new File(mmh.getDataFolder(), "block_heads_1_16.yml");
            mmh.log(Level.INFO, "Loading block_heads_1_16 files...");
        } else if (MoreMobHeads.getMCVersion().startsWith("1.17") || MoreMobHeads.getMCVersion().startsWith("1.18") || MoreMobHeads.getMCVersion().startsWith("1.19")) {
            if (MoreMobHeads.debug) {
                mmh.logDebug("block_heads_1_17=" + mmh.blockFile116.getPath());
            }
            if (MoreMobHeads.debug) {
                mmh.logDebug("block_heads_1_17_2=" + mmh.blockFile1162.getPath());
            }
            if (!mmh.blockFile116.exists()) {
                mmh.saveResource("block_heads_1_17.yml", true);
                mmh.log(Level.INFO, "block_heads_1_17.yml not found! saved default block_heads_1_17.yml");
            }
            if (!mmh.blockFile1162.exists()) {
                mmh.saveResource("block_heads_1_17_2.yml", true);
                mmh.log(Level.INFO, "block_heads_1_17_2.yml not found! saved default block_heads_1_17_2.yml");
            }
            if (!mmh.blockFile117.exists()) {
                mmh.saveResource("block_heads_1_17_3.yml", true);
                mmh.log(Level.INFO, "block_heads_1_17_3.yml not found! saved default block_heads_1_17_3.yml");
            }
            if (!mmh.blockFile119.exists()) {
                mmh.saveResource("block_heads_1_19.yml", true);
                mmh.log(Level.INFO, "block_heads_1_19.yml not found! saved default block_heads_1_19.yml");
            }
            mmh.blockFile = new File(mmh.getDataFolder(), "block_heads_1_17.yml");
            mmh.blockFile1162 = new File(mmh.getDataFolder(), "block_heads_1_17_2.yml");
            mmh.log(Level.INFO, "Loading block_heads_1_17 files...");
        }

        mmh.log(Level.INFO, "Loading block_heads file...");

        mmh.blockHeads = new YamlConfiguration();
        try {
            mmh.blockHeads.load(mmh.blockFile);
        } catch (IOException | InvalidConfigurationException e1) {
            mmh.stacktraceInfo();
            e1.printStackTrace();
        }

        mmh.blockHeads2 = new YamlConfiguration();
        try {
            mmh.blockHeads2.load(mmh.blockFile1162);
        } catch (IOException | InvalidConfigurationException e1) {
            mmh.stacktraceInfo();
            e1.printStackTrace();
        }
        if (Double.parseDouble(MoreMobHeads.getMCVersion().substring(0, 4)) >= 1.17) {
            mmh.blockHeads3 = new YamlConfiguration();
            try {
                mmh.blockHeads3.load(mmh.blockFile117);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }

            mmh.blockHeads4 = new YamlConfiguration();
            try {
                mmh.blockHeads4.load(mmh.blockFile119);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }
        }
        /* Block heads load end */
    }

    private static void reloadCustomTrade() {
        /* Custom Trades load */
        mmh.customFile = new File(mmh.getDataFolder(), "custom_trades.yml");
        if (MoreMobHeads.debug) {
            mmh.logDebug("customFile=" + mmh.customFile.getPath());
        }
        if (!mmh.customFile.exists()) {
            mmh.saveResource("custom_trades.yml", true);
            mmh.log(Level.INFO, "custom_trades.yml not found! saved default custom_trades.yml");
        }
        mmh.log(Level.INFO, "Loading custom_trades file...");
        mmh.traderCustom = new YamlConfiguration();
        try {
            mmh.traderCustom.load(mmh.customFile);
        } catch (IOException | InvalidConfigurationException e) {
            mmh.stacktraceInfo();
            e.printStackTrace();
        }
    }

    public static void copyChance(MoreMobHeads mmh, File file, File file2) {
        mmh.chanceConfig = new YmlConfiguration();
        YamlConfiguration oldChanceConfig = new YmlConfiguration();
        try {
            mmh.chanceConfig.load(file2);
            oldChanceConfig.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            mmh.stacktraceInfo();
            e.printStackTrace();
        }
        mmh.log(Level.INFO, "Copying values frome old_chance_config.yml to chance_config.yml");
        mmh.chanceConfig.set("chance_percent.player", oldChanceConfig.getDouble("chance_percent.player", 50.0));
        mmh.chanceConfig.set("chance_percent.named_mob", oldChanceConfig.getDouble("chance_percent.named_mob", 10.0));
        mmh.chanceConfig.set("chance_percent.allay", oldChanceConfig.getDouble("chance_percent.allay", 20.0));
        mmh.chanceConfig.set("chance_percent.axolotl.blue", oldChanceConfig.getDouble("chance_percent.axolotl.blue", 100.0));
        mmh.chanceConfig.set("chance_percent.axolotl.cyan", oldChanceConfig.getDouble("chance_percent.axolotl.cyan", 20.0));
        mmh.chanceConfig.set("chance_percent.axolotl.gold", oldChanceConfig.getDouble("chance_percent.axolotl.gold", 20.0));
        mmh.chanceConfig.set("chance_percent.axolotl.lucy", oldChanceConfig.getDouble("chance_percent.axolotl.lucy", 20.0));
        mmh.chanceConfig.set("chance_percent.axolotl.wild", oldChanceConfig.getDouble("chance_percent.axolotl.wild", 20.0));
        mmh.chanceConfig.set("chance_percent.bat", oldChanceConfig.getDouble("chance_percent.bat", 10.0));
        mmh.chanceConfig.set("chance_percent.bee.angry_pollinated", oldChanceConfig.getDouble("chance_percent.bee.angry_pollinated", 20.0));
        mmh.chanceConfig.set("chance_percent.bee.angry", oldChanceConfig.getDouble("chance_percent.bee.angry", 20.0));
        mmh.chanceConfig.set("chance_percent.bee.pollinated", oldChanceConfig.getDouble("chance_percent.bee.pollinated", 20.0));
        mmh.chanceConfig.set("chance_percent.bee.chance_percent", oldChanceConfig.getDouble("chance_percent.bee.normal", 20.0));
        mmh.chanceConfig.set("chance_percent.blaze", oldChanceConfig.getDouble("chance_percent.blaze", 0.5));
        mmh.chanceConfig.set("chance_percent.camel", oldChanceConfig.getDouble("chance_percent.camel", 27.0));
        mmh.chanceConfig.set("chance_percent.cat.all_black", oldChanceConfig.getDouble("chance_percent.cat.all_black", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.black", oldChanceConfig.getDouble("chance_percent.cat.black", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.british_shorthair", oldChanceConfig.getDouble("chance_percent.cat.british_shorthair", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.calico", oldChanceConfig.getDouble("chance_percent.cat.calico", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.jellie", oldChanceConfig.getDouble("chance_percent.cat.jellie", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.persian", oldChanceConfig.getDouble("chance_percent.cat.persian", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.ragdoll", oldChanceConfig.getDouble("chance_percent.cat.ragdoll", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.red", oldChanceConfig.getDouble("chance_percent.cat.red", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.siamese", oldChanceConfig.getDouble("chance_percent.cat.siamese", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.tabby", oldChanceConfig.getDouble("chance_percent.cat.tabby", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.white", oldChanceConfig.getDouble("chance_percent.cat.white", 33.0));

        mmh.chanceConfig.set("chance_percent.cave_spider", oldChanceConfig.getDouble("chance_percent.cave_spider", 0.5));
        mmh.chanceConfig.set("chance_percent.chicken", oldChanceConfig.getDouble("chance_percent.chicken", 1.0));
        mmh.chanceConfig.set("chance_percent.cod", oldChanceConfig.getDouble("chance_percent.cod", 10.0));
        mmh.chanceConfig.set("chance_percent.cow", oldChanceConfig.getDouble("chance_percent.cow", 1.0));
        mmh.chanceConfig.set("chance_percent.creeper", oldChanceConfig.getDouble("chance_percent.creeper", 50.0));
        mmh.chanceConfig.set("chance_percent.creeper_charged", oldChanceConfig.getDouble("chance_percent.creeper_charged", 100.0));
        mmh.chanceConfig.set("chance_percent.dolphin", oldChanceConfig.getDouble("chance_percent.dolphin", 33.0));
        mmh.chanceConfig.set("chance_percent.donkey", oldChanceConfig.getDouble("chance_percent.donkey", 20.0));
        mmh.chanceConfig.set("chance_percent.drowned", oldChanceConfig.getDouble("chance_percent.drowned", 5.0));
        mmh.chanceConfig.set("chance_percent.elder_guardian", oldChanceConfig.getDouble("chance_percent.elder_guardian", 100.0));
        mmh.chanceConfig.set("chance_percent.ender_dragon", oldChanceConfig.getDouble("chance_percent.ender_dragon", 100.0));
        mmh.chanceConfig.set("chance_percent.enderman", oldChanceConfig.getDouble("chance_percent.enderman", 0.5));
        mmh.chanceConfig.set("chance_percent.endermite", oldChanceConfig.getDouble("chance_percent.endermite", 10.0));
        mmh.chanceConfig.set("chance_percent.evoker", oldChanceConfig.getDouble("chance_percent.evoker", 25.0));
        mmh.chanceConfig.set("chance_percent.fox.red", oldChanceConfig.getDouble("chance_percent.fox.red", 10.0));
        mmh.chanceConfig.set("chance_percent.fox.snow", oldChanceConfig.getDouble("chance_percent.fox.snow", 20.0));
        mmh.chanceConfig.set("chance_percent.frog.cold", oldChanceConfig.getDouble("chance_percent.frog.cold", 20.0));
        mmh.chanceConfig.set("chance_percent.frog.temperate", oldChanceConfig.getDouble("chance_percent.frog.temperate", 20.0));
        mmh.chanceConfig.set("chance_percent.frog.warm", oldChanceConfig.getDouble("chance_percent.frog.warm", 20.0));
        mmh.chanceConfig.set("chance_percent.ghast", oldChanceConfig.getDouble("chance_percent.ghast", 6.25));
        mmh.chanceConfig.set("chance_percent.giant", oldChanceConfig.getDouble("chance_percent.giant", 2.5));
        mmh.chanceConfig.set("chance_percent.glow_squid", oldChanceConfig.getDouble("chance_percent.glow_squid", 5.0));
        mmh.chanceConfig.set("chance_percent.goat.mormal", oldChanceConfig.getDouble("chance_percent.goat.normal", 1.0));
        mmh.chanceConfig.set("chance_percent.goat.screaming", oldChanceConfig.getDouble("chance_percent.goat.screaming", 100.0));
        mmh.chanceConfig.set("chance_percent.guardian", oldChanceConfig.getDouble("chance_percent.guardian", 0.5));
        mmh.chanceConfig.set("chance_percent.hoglin", oldChanceConfig.getDouble("chance_percent.hoglin", 3.0));
        mmh.chanceConfig.set("chance_percent.horse.black", oldChanceConfig.getDouble("chance_percent.horse.black", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.brown", oldChanceConfig.getDouble("chance_percent.horse.brown", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.chestnut", oldChanceConfig.getDouble("chance_percent.horse.chestnut", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.creamy", oldChanceConfig.getDouble("chance_percent.horse.creamy", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.dark_brown", oldChanceConfig.getDouble("chance_percent.horse.dark_brown", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.gray", oldChanceConfig.getDouble("chance_percent.horse.gray", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.white", oldChanceConfig.getDouble("chance_percent.horse.white", 27.0));
        mmh.chanceConfig.set("chance_percent.husk", oldChanceConfig.getDouble("chance_percent.husk", 6.0));
        mmh.chanceConfig.set("chance_percent.illusioner", oldChanceConfig.getDouble("chance_percent.illusioner", 25.0));
        mmh.chanceConfig.set("chance_percent.iron_golem", oldChanceConfig.getDouble("chance_percent.iron_golem", 5.0));
        mmh.chanceConfig.set("chance_percent.llama.brown", oldChanceConfig.getDouble("chance_percent.llama.brown", 24.0));
        mmh.chanceConfig.set("chance_percent.llama.creamy", oldChanceConfig.getDouble("chance_percent.llama.creamy", 24.0));
        mmh.chanceConfig.set("chance_percent.llama.gray", oldChanceConfig.getDouble("chance_percent.llama.gray", 24.0));
        mmh.chanceConfig.set("chance_percent.llama.white", oldChanceConfig.getDouble("chance_percent.llama.white", 24.0));
        mmh.chanceConfig.set("chance_percent.magma_cube", oldChanceConfig.getDouble("chance_percent.magma_cube", 0.5));
        mmh.chanceConfig.set("chance_percent.mule", oldChanceConfig.getDouble("chance_percent.mule", 20.0));
        mmh.chanceConfig.set("chance_percent.mushroom_cow.red", oldChanceConfig.getDouble("chance_percent.mushroom_cow.red", 1.0));
        mmh.chanceConfig.set("chance_percent.mushroom_cow.brown", oldChanceConfig.getDouble("chance_percent.mushroom_cow.brown", 10.0));
        mmh.chanceConfig.set("chance_percent.ocelot", oldChanceConfig.getDouble("chance_percent.ocelot", 20.0));
        mmh.chanceConfig.set("chance_percent.panda.aggressive", oldChanceConfig.getDouble("chance_percent.panda.aggressive", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.brown", oldChanceConfig.getDouble("chance_percent.panda.brown", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.lazy", oldChanceConfig.getDouble("chance_percent.panda.lazy", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.normal", oldChanceConfig.getDouble("chance_percent.panda.normal", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.playful", oldChanceConfig.getDouble("chance_percent.panda.playful", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.weak", oldChanceConfig.getDouble("chance_percent.panda.weak", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.worried", oldChanceConfig.getDouble("chance_percent.panda.worried", 27.0));
        mmh.chanceConfig.set("chance_percent.parrot.blue", oldChanceConfig.getDouble("chance_percent.parrot.blue", 25.0));
        mmh.chanceConfig.set("chance_percent.parrot.cyan", oldChanceConfig.getDouble("chance_percent.parrot.cyan", 25.0));
        mmh.chanceConfig.set("chance_percent.parrot.gray", oldChanceConfig.getDouble("chance_percent.parrot.gray", 25.0));
        mmh.chanceConfig.set("chance_percent.parrot.green", oldChanceConfig.getDouble("chance_percent.parrot.green", 25.0));
        mmh.chanceConfig.set("chance_percent.parrot.red", oldChanceConfig.getDouble("chance_percent.parrot.red", 25.0));
        mmh.chanceConfig.set("chance_percent.phantom", oldChanceConfig.getDouble("chance_percent.phantom", 10.0));
        mmh.chanceConfig.set("chance_percent.pig", oldChanceConfig.getDouble("chance_percent.pig", 1.0));
        mmh.chanceConfig.set("chance_percent.piglin", oldChanceConfig.getDouble("chance_percent.piglin", 4.0));
        mmh.chanceConfig.set("chance_percent.pig_zombie", oldChanceConfig.getDouble("chance_percent.pig_zombie", 0.5));
        mmh.chanceConfig.set("chance_percent.pillager", oldChanceConfig.getDouble("chance_percent.pillager", 2.5));
        mmh.chanceConfig.set("chance_percent.polar_bear", oldChanceConfig.getDouble("chance_percent.polar_bear", 20.0));
        mmh.chanceConfig.set("chance_percent.pufferfish", oldChanceConfig.getDouble("chance_percent.pufferfish", 15.0));
        mmh.chanceConfig.set("chance_percent.rabbit.black", oldChanceConfig.getDouble("chance_percent.rabbit.black", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.black_and_white", oldChanceConfig.getDouble("chance_percent.rabbit.black_and_white", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.brown", oldChanceConfig.getDouble("chance_percent.rabbit.brown", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.gold", oldChanceConfig.getDouble("chance_percent.rabbit.gold", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.salt_and_pepper", oldChanceConfig.getDouble("chance_percent.rabbit.salt_and_pepper", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.the_killer_bunny", oldChanceConfig.getDouble("chance_percent.rabbit.the_killer_bunny", 100.0));
        mmh.chanceConfig.set("chance_percent.rabbit.toast", oldChanceConfig.getDouble("chance_percent.rabbit.toast", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.white", oldChanceConfig.getDouble("chance_percent.rabbit.white", 26.0));
        mmh.chanceConfig.set("chance_percent.ravager", oldChanceConfig.getDouble("chance_percent.ravager", 25.0));
        mmh.chanceConfig.set("chance_percent.salmon", oldChanceConfig.getDouble("chance_percent.salmon", 10.0));
        mmh.chanceConfig.set("chance_percent.sheep.black", oldChanceConfig.getDouble("chance_percent.sheep.black", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.blue", oldChanceConfig.getDouble("chance_percent.sheep.blue", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.brown", oldChanceConfig.getDouble("chance_percent.sheep.brown", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.cyan", oldChanceConfig.getDouble("chance_percent.sheep.cyan", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.gray", oldChanceConfig.getDouble("chance_percent.sheep.gray", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.green", oldChanceConfig.getDouble("chance_percent.sheep.green", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.jeb_", oldChanceConfig.getDouble("chance_percent.sheep.jeb_", 10.0));
        mmh.chanceConfig.set("chance_percent.sheep.light_blue", oldChanceConfig.getDouble("chance_percent.sheep.light_blue", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.light_gray", oldChanceConfig.getDouble("chance_percent.sheep.light_gray", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.lime", oldChanceConfig.getDouble("chance_percent.sheep.lime", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.magenta", oldChanceConfig.getDouble("chance_percent.sheep.magenta", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.orange", oldChanceConfig.getDouble("chance_percent.sheep.orange", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.pink", oldChanceConfig.getDouble("chance_percent.sheep.pink", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.purple", oldChanceConfig.getDouble("chance_percent.sheep.purple", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.red", oldChanceConfig.getDouble("chance_percent.sheep.red", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.white", oldChanceConfig.getDouble("chance_percent.sheep.white", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.yellow", oldChanceConfig.getDouble("chance_percent.sheep.yellow", 1.75));
        mmh.chanceConfig.set("chance_percent.shulker", oldChanceConfig.getDouble("chance_percent.shulker", 5.0));
        mmh.chanceConfig.set("chance_percent.silverfish", oldChanceConfig.getDouble("chance_percent.silverfish", 5.0));
        mmh.chanceConfig.set("chance_percent.skeleton", oldChanceConfig.getDouble("chance_percent.skeleton", 2.5));
        mmh.chanceConfig.set("chance_percent.skeleton_horse", oldChanceConfig.getDouble("chance_percent.skeleton_horse", 20.0));
        mmh.chanceConfig.set("chance_percent.slime", oldChanceConfig.getDouble("chance_percent.slime", 0.5));
        mmh.chanceConfig.set("chance_percent.sniffer", oldChanceConfig.getDouble("chance_percent.sniffer", 50.0));
        mmh.chanceConfig.set("chance_percent.snowman", oldChanceConfig.getDouble("chance_percent.snowman", 5.0));
        mmh.chanceConfig.set("chance_percent.spider", oldChanceConfig.getDouble("chance_percent.spider", 0.5));
        mmh.chanceConfig.set("chance_percent.squid", oldChanceConfig.getDouble("chance_percent.squid", 5.0));
        mmh.chanceConfig.set("chance_percent.stray", oldChanceConfig.getDouble("chance_percent.stray", 6.0));
        mmh.chanceConfig.set("chance_percent.strider", oldChanceConfig.getDouble("chance_percent.strider", 10.0));
        mmh.chanceConfig.set("chance_percent.tadpole", oldChanceConfig.getDouble("chance_percent.tadpole", 10.0));
        mmh.chanceConfig.set("chance_percent.trader_llama.brown", oldChanceConfig.getDouble("chance_percent.trader_llama.brown", 24.0));
        mmh.chanceConfig.set("chance_percent.trader_llama.creamy", oldChanceConfig.getDouble("chance_percent.trader_llama.creamy", 24.0));
        mmh.chanceConfig.set("chance_percent.trader_llama.gray", oldChanceConfig.getDouble("chance_percent.trader_llama.gray", 24.0));
        mmh.chanceConfig.set("chance_percent.trader_llama.white", oldChanceConfig.getDouble("chance_percent.trader_llama.white", 24.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.tropical_fish", oldChanceConfig.getDouble("chance_percent.tropical_fish.tropical_fish", 10.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.anemone", oldChanceConfig.getDouble("chance_percent.tropical_fish.anemone", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.black_tang", oldChanceConfig.getDouble("chance_percent.tropical_fish.black_tang", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.blue_tang", oldChanceConfig.getDouble("chance_percent.tropical_fish.blue_tang", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.butterflyfish", oldChanceConfig.getDouble("chance_percent.tropical_fish.butterflyfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.cichlid", oldChanceConfig.getDouble("chance_percent.tropical_fish.cichlid", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.clownfish", oldChanceConfig.getDouble("chance_percent.tropical_fish.clownfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.cotton_candy_betta", oldChanceConfig.getDouble("chance_percent.tropical_fish.cotton_candy_betta", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.dottyback", oldChanceConfig.getDouble("chance_percent.tropical_fish.dottyback", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.emperor_red_snapper", oldChanceConfig.getDouble("chance_percent.tropical_fish.emperor_red_snapper", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.goatfish", oldChanceConfig.getDouble("chance_percent.tropical_fish.goatfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.moorish_idol", oldChanceConfig.getDouble("chance_percent.tropical_fish.moorish_idol", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.ornate_butterflyfish", oldChanceConfig.getDouble("chance_percent.tropical_fish.ornate_butterflyfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.parrotfish", oldChanceConfig.getDouble("chance_percent.tropical_fish.parrotfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.queen_angelfish", oldChanceConfig.getDouble("chance_percent.tropical_fish.queen_angelfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.red_cichlid", oldChanceConfig.getDouble("chance_percent.tropical_fish.red_cichlid", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.red_lipped_blenny", oldChanceConfig.getDouble("chance_percent.tropical_fish.red_lipped_blenny", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.red_snapper", oldChanceConfig.getDouble("chance_percent.tropical_fish.red_snapper", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.threadfin", oldChanceConfig.getDouble("chance_percent.tropical_fish.threadfin", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.tomato_clownfish", oldChanceConfig.getDouble("chance_percent.tropical_fish.tomato_clownfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.triggerfish", oldChanceConfig.getDouble("chance_percent.tropical_fish.triggerfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.yellowtail_parrotfish", oldChanceConfig.getDouble("chance_percent.tropical_fish.yellow_parrotfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.yellow_tang", oldChanceConfig.getDouble("chance_percent.tropical_fish.yellow_tang", 50.0));

        mmh.chanceConfig.set("chance_percent.turtle", oldChanceConfig.getDouble("chance_percent.turtle", 10.0));
        mmh.chanceConfig.set("chance_percent.vex", oldChanceConfig.getDouble("chance_percent.vex", 10.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.armorer", oldChanceConfig.getDouble("chance_percent.villager.desert.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.butcher", oldChanceConfig.getDouble("chance_percent.villager.desert.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.cartographer", oldChanceConfig.getDouble("chance_percent.villager.desert.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.cleric", oldChanceConfig.getDouble("chance_percent.villager.desert.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.farmer", oldChanceConfig.getDouble("chance_percent.villager.desert.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.fisherman", oldChanceConfig.getDouble("chance_percent.villager.desert.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.fletcher", oldChanceConfig.getDouble("chance_percent.villager.desert.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.leatherworker", oldChanceConfig.getDouble("chance_percent.villager.desert.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.librarian", oldChanceConfig.getDouble("chance_percent.villager.desert.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.mason", oldChanceConfig.getDouble("chance_percent.villager.desert.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.nitwit", oldChanceConfig.getDouble("chance_percent.villager.desert.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.none", oldChanceConfig.getDouble("chance_percent.villager.desert.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.shepherd", oldChanceConfig.getDouble("chance_percent.villager.desert.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.toolsmith", oldChanceConfig.getDouble("chance_percent.villager.desert.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.weaponsmith", oldChanceConfig.getDouble("chance_percent.villager.desert.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.armorer", oldChanceConfig.getDouble("chance_percent.villager.jungle.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.butcher", oldChanceConfig.getDouble("chance_percent.villager.jungle.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.cartographer", oldChanceConfig.getDouble("chance_percent.villager.jungle.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.cleric", oldChanceConfig.getDouble("chance_percent.villager.jungle.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.farmer", oldChanceConfig.getDouble("chance_percent.villager.jungle.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.fisherman", oldChanceConfig.getDouble("chance_percent.villager.jungle.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.fletcher", oldChanceConfig.getDouble("chance_percent.villager.jungle.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.leatherworker", oldChanceConfig.getDouble("chance_percent.villager.jungle.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.librarian", oldChanceConfig.getDouble("chance_percent.villager.jungle.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.mason", oldChanceConfig.getDouble("chance_percent.villager.jungle.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.nitwit", oldChanceConfig.getDouble("chance_percent.villager.jungle.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.none", oldChanceConfig.getDouble("chance_percent.villager.jungle.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.shepherd", oldChanceConfig.getDouble("chance_percent.villager.jungle.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.toolsmith", oldChanceConfig.getDouble("chance_percent.villager.jungle.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.weaponsmith", oldChanceConfig.getDouble("chance_percent.villager.jungle.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.armorer", oldChanceConfig.getDouble("chance_percent.villager.plains.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.butcher", oldChanceConfig.getDouble("chance_percent.villager.plains.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.cartographer", oldChanceConfig.getDouble("chance_percent.villager.plains.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.cleric", oldChanceConfig.getDouble("chance_percent.villager.plains.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.farmer", oldChanceConfig.getDouble("chance_percent.villager.plains.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.fisherman", oldChanceConfig.getDouble("chance_percent.villager.plains.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.fletcher", oldChanceConfig.getDouble("chance_percent.villager.plains.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.leatherworker", oldChanceConfig.getDouble("chance_percent.villager.plains.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.librarian", oldChanceConfig.getDouble("chance_percent.villager.plains.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.mason", oldChanceConfig.getDouble("chance_percent.villager.plains.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.nitwit", oldChanceConfig.getDouble("chance_percent.villager.plains.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.none", oldChanceConfig.getDouble("chance_percent.villager.plains.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.shepherd", oldChanceConfig.getDouble("chance_percent.villager.plains.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.toolsmith", oldChanceConfig.getDouble("chance_percent.villager.plains.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.weaponsmith", oldChanceConfig.getDouble("chance_percent.villager.plains.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.armorer", oldChanceConfig.getDouble("chance_percent.villager.savanna.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.butcher", oldChanceConfig.getDouble("chance_percent.villager.savanna.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.cartographer", oldChanceConfig.getDouble("chance_percent.villager.savanna.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.cleric", oldChanceConfig.getDouble("chance_percent.villager.savanna.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.farmer", oldChanceConfig.getDouble("chance_percent.villager.savanna.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.fisherman", oldChanceConfig.getDouble("chance_percent.villager.savanna.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.fletcher", oldChanceConfig.getDouble("chance_percent.villager.savanna.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.leatherworker", oldChanceConfig.getDouble("chance_percent.villager.savanna.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.librarian", oldChanceConfig.getDouble("chance_percent.villager.savanna.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.mason", oldChanceConfig.getDouble("chance_percent.villager.savanna.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.nitwit", oldChanceConfig.getDouble("chance_percent.villager.savanna.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.none", oldChanceConfig.getDouble("chance_percent.villager.savanna.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.shepherd", oldChanceConfig.getDouble("chance_percent.villager.savanna.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.toolsmith", oldChanceConfig.getDouble("chance_percent.villager.savanna.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.weaponsmith", oldChanceConfig.getDouble("chance_percent.villager.savanna.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.armorer", oldChanceConfig.getDouble("chance_percent.villager.snow.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.butcher", oldChanceConfig.getDouble("chance_percent.villager.snow.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.cartographer", oldChanceConfig.getDouble("chance_percent.villager.snow.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.cleric", oldChanceConfig.getDouble("chance_percent.villager.snow.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.farmer", oldChanceConfig.getDouble("chance_percent.villager.snow.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.fisherman", oldChanceConfig.getDouble("chance_percent.villager.snow.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.fletcher", oldChanceConfig.getDouble("chance_percent.villager.snow.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.leatherworker", oldChanceConfig.getDouble("chance_percent.villager.snow.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.librarian", oldChanceConfig.getDouble("chance_percent.villager.snow.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.mason", oldChanceConfig.getDouble("chance_percent.villager.snow.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.nitwit", oldChanceConfig.getDouble("chance_percent.villager.snow.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.none", oldChanceConfig.getDouble("chance_percent.villager.snow.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.shepherd", oldChanceConfig.getDouble("chance_percent.villager.snow.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.toolsmith", oldChanceConfig.getDouble("chance_percent.villager.snow.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.weaponsmith", oldChanceConfig.getDouble("chance_percent.villager.snow.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.armorer", oldChanceConfig.getDouble("chance_percent.villager.swamp.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.butcher", oldChanceConfig.getDouble("chance_percent.villager.swamp.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.cartographer", oldChanceConfig.getDouble("chance_percent.villager.swamp.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.cleric", oldChanceConfig.getDouble("chance_percent.villager.swamp.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.farmer", oldChanceConfig.getDouble("chance_percent.villager.swamp.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.fisherman", oldChanceConfig.getDouble("chance_percent.villager.swamp.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.fletcher", oldChanceConfig.getDouble("chance_percent.villager.swamp.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.leatherworker", oldChanceConfig.getDouble("chance_percent.villager.swamp.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.librarian", oldChanceConfig.getDouble("chance_percent.villager.swamp.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.mason", oldChanceConfig.getDouble("chance_percent.villager.swamp.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.nitwit", oldChanceConfig.getDouble("chance_percent.villager.swamp.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.none", oldChanceConfig.getDouble("chance_percent.villager.swamp.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.shepherd", oldChanceConfig.getDouble("chance_percent.villager.swamp.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.toolsmith", oldChanceConfig.getDouble("chance_percent.villager.swamp.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.weaponsmith", oldChanceConfig.getDouble("chance_percent.villager.swamp.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.armorer", oldChanceConfig.getDouble("chance_percent.villager.taiga.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.butcher", oldChanceConfig.getDouble("chance_percent.villager.taiga.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.cartographer", oldChanceConfig.getDouble("chance_percent.villager.taiga.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.cleric", oldChanceConfig.getDouble("chance_percent.villager.taiga.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.farmer", oldChanceConfig.getDouble("chance_percent.villager.taiga.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.fisherman", oldChanceConfig.getDouble("chance_percent.villager.taiga.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.fletcher", oldChanceConfig.getDouble("chance_percent.villager.taiga.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.leatherworker", oldChanceConfig.getDouble("chance_percent.villager.taiga.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.librarian", oldChanceConfig.getDouble("chance_percent.villager.taiga.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.mason", oldChanceConfig.getDouble("chance_percent.villager.taiga.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.nitwit", oldChanceConfig.getDouble("chance_percent.villager.taiga.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.none", oldChanceConfig.getDouble("chance_percent.villager.taiga.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.shepherd", oldChanceConfig.getDouble("chance_percent.villager.taiga.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.toolsmith", oldChanceConfig.getDouble("chance_percent.villager.taiga.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.weaponsmith", oldChanceConfig.getDouble("chance_percent.villager.taiga.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.vindicator", oldChanceConfig.getDouble("chance_percent.vindicator", 5.0));
        mmh.chanceConfig.set("chance_percent.wandering_trader", oldChanceConfig.getDouble("chance_percent.wandering_trader", 100.0));
        mmh.chanceConfig.set("chance_percent.warden", oldChanceConfig.getDouble("chance_percent.warden", 100.0));
        mmh.chanceConfig.set("chance_percent.witch", oldChanceConfig.getDouble("chance_percent.witch", 0.5));
        mmh.chanceConfig.set("chance_percent.wither", oldChanceConfig.getDouble("chance_percent.wither", 100.0));
        mmh.chanceConfig.set("chance_percent.wither_skeleton", oldChanceConfig.getDouble("chance_percent.wither_skeleton", 2.5));
        mmh.chanceConfig.set("chance_percent.wolf", oldChanceConfig.getDouble("chance_percent.wolf", 20.0));
        mmh.chanceConfig.set("chance_percent.zoglin", oldChanceConfig.getDouble("chance_percent.zoglin", 20.0));
        mmh.chanceConfig.set("chance_percent.zombie", oldChanceConfig.getDouble("chance_percent.zombie", 2.5));
        mmh.chanceConfig.set("chance_percent.zombie_horse", oldChanceConfig.getDouble("chance_percent.zombie_horse", 100.0));
        mmh.chanceConfig.set("chance_percent.zombie_pigman", oldChanceConfig.getDouble("chance_percent.zombie_pigman", 0.5));
        mmh.chanceConfig.set("chance_percent.zombified_piglin", oldChanceConfig.getDouble("chance_percent.zombified_piglin", 0.5));
        mmh.chanceConfig.set("chance_percent.zombie_villager", oldChanceConfig.getDouble("chance_percent.zombie_villager", 50.0));
        try {
            mmh.chanceConfig.save(file2);
        } catch (IOException e) {
            mmh.stacktraceInfo();
            e.printStackTrace();
        }
        mmh.log(Level.INFO, "chance_config.yml has been updated!");
    }
}
