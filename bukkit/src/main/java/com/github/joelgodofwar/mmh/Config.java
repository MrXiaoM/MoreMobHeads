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

        Networks.checkUpdate = mmh.getConfig().getBoolean("auto_update_check");
        MoreMobHeads.debug = mmh.getConfig().getBoolean("debug", false);
        MoreMobHeads.languageName = mmh.getConfig().getString("lang", "zh_CN");

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
        mmh.oldConfig = new YamlConfiguration();
        mmh.log(Level.INFO, "Checking config file version...");
        try {
            mmh.oldConfig.load(configFile);
        } catch (Exception e2) {
            mmh.logWarn("Could not load config.yml");
            mmh.stacktraceInfo();
            e2.printStackTrace();
        }
        String configVersion = mmh.oldConfig.getString("version", "1.0.0");
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
                mmh.oldConfig.load(oldConfigFile);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.logWarn("Could not load old_config.yml");
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }
            mmh.config.set("auto_update_check", mmh.oldConfig.get("auto_update_check", true));
            mmh.config.set("debug", mmh.oldConfig.get("debug", false));
            mmh.config.set("lang", mmh.oldConfig.get("lang", "en_US"));
            mmh.config.set("console.colorful_console", mmh.oldConfig.get("colorful_console", true));
            mmh.config.set("vanilla_heads.creepers", mmh.oldConfig.get("vanilla_heads.creepers", false));
            mmh.config.set("vanilla_heads.ender_dragon", mmh.oldConfig.get("vanilla_heads.ender_dragon", false));
            mmh.config.set("vanilla_heads.skeleton", mmh.oldConfig.get("vanilla_heads.skeleton", false));
            mmh.config.set("vanilla_heads.wither_skeleton", mmh.oldConfig.get("vanilla_heads.wither_skeleton", false));
            mmh.config.set("vanilla_heads.zombie", mmh.oldConfig.get("vanilla_heads.zombie", false));
            mmh.config.set("world.whitelist", mmh.oldConfig.get("world.whitelist", ""));
            mmh.config.set("world.blacklist", mmh.oldConfig.get("world.blacklist", ""));
            mmh.config.set("mob.whitelist", mmh.oldConfig.get("mob.whitelist", ""));
            mmh.config.set("mob.blacklist", mmh.oldConfig.get("mob.blacklist", ""));
            mmh.config.set("mob.nametag", mmh.oldConfig.get("mob.nametag", false));
            mmh.config.set("lore.show_killer", mmh.oldConfig.get("lore.show_killer", true));
            mmh.config.set("lore.show_plugin_name", mmh.oldConfig.get("lore.show_plugin_name", true));
            mmh.config.set("wandering_trades.custom_wandering_trader", mmh.oldConfig.get("wandering_trades.custom_wandering_trader", true));
            mmh.config.set("wandering_trades.player_heads.enabled", mmh.oldConfig.get("wandering_trades.player_heads.enabled", true));
            mmh.config.set("wandering_trades.player_heads.min", mmh.oldConfig.get("wandering_trades.player_heads.min", 0));
            mmh.config.set("wandering_trades.player_heads.max", mmh.oldConfig.get("wandering_trades.player_heads.max", 5));
            mmh.config.set("wandering_trades.block_heads.enabled", mmh.oldConfig.get("wandering_trades.block_heads.enabled", true));
            mmh.config.set("wandering_trades.block_heads.pre_116.min", mmh.oldConfig.get("wandering_trader_min_block_heads", 0));
            mmh.config.set("wandering_trades.block_heads.pre_116.max", mmh.oldConfig.get("wandering_trader_max_block_heads", 5));
            mmh.config.set("wandering_trades.block_heads.is_116.min", mmh.oldConfig.get("wandering_trader_min_block_heads", 0));
            mmh.config.set("wandering_trades.block_heads.is_116.max", mmh.oldConfig.get("wandering_trader_max_block_heads", 5));
            mmh.config.set("wandering_trades.block_heads.is_117.min", mmh.oldConfig.get("wandering_trader_min_block_heads", 0));
            mmh.config.set("wandering_trades.block_heads.is_117.max", mmh.oldConfig.get("wandering_trader_max_block_heads", 5));

            mmh.config.set("wandering_trades.custom_trades.enabled", mmh.oldConfig.get("wandering_trades.custom_trades.enabled", false));
            mmh.config.set("wandering_trades.custom_trades.min", mmh.oldConfig.get("wandering_trades.custom_trades.min", 0));
            mmh.config.set("wandering_trades.custom_trades.max", mmh.oldConfig.get("wandering_trades.custom_trades.max", 5));
            mmh.config.set("apply_looting", mmh.oldConfig.get("apply_looting", true));
            mmh.config.set("whitelist.enforce", mmh.oldConfig.get("whitelist.enforce", true));
            mmh.config.set("whitelist.player_head_whitelist", mmh.oldConfig.get("whitelist.player_head_whitelist", "names_go_here"));
            mmh.config.set("blacklist.enforce", mmh.oldConfig.get("enforce_blacklist", true));
            mmh.config.set("blacklist.player_head_blacklist", mmh.oldConfig.get("blacklist.player_head_blacklist", "names_go_here"));

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
        mmh.oldConfig = null;
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
        try {
            mmh.oldMessages.load(messageFile);
        } catch (Exception e) {
            mmh.logWarn("Could not load messages.yml");
            mmh.stacktraceInfo();
            e.printStackTrace();
        }

        String messageVersion = mmh.oldMessages.getString("version", "1.0.0");
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
                mmh.oldMessages.load(oldMessageFile);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }

            // Update messages
            ConfigurationSection oldMessagesSection = mmh.oldMessages.getConfigurationSection("messages");
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
        mmh.oldMessages = null;

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
        mmh.oldChanceConfig = new YmlConfiguration();
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
        mmh.oldChanceConfig = new YmlConfiguration();
        try {
            mmh.chanceConfig.load(file2);
            mmh.oldChanceConfig.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            mmh.stacktraceInfo();
            e.printStackTrace();
        }
        mmh.log(Level.INFO, "Copying values frome old_chance_config.yml to chance_config.yml");
        mmh.chanceConfig.set("chance_percent.player", mmh.oldChanceConfig.getDouble("chance_percent.player", 50.0));
        mmh.chanceConfig.set("chance_percent.named_mob", mmh.oldChanceConfig.getDouble("chance_percent.named_mob", 10.0));
        mmh.chanceConfig.set("chance_percent.allay", mmh.oldChanceConfig.getDouble("chance_percent.allay", 20.0));
        mmh.chanceConfig.set("chance_percent.axolotl.blue", mmh.oldChanceConfig.getDouble("chance_percent.axolotl.blue", 100.0));
        mmh.chanceConfig.set("chance_percent.axolotl.cyan", mmh.oldChanceConfig.getDouble("chance_percent.axolotl.cyan", 20.0));
        mmh.chanceConfig.set("chance_percent.axolotl.gold", mmh.oldChanceConfig.getDouble("chance_percent.axolotl.gold", 20.0));
        mmh.chanceConfig.set("chance_percent.axolotl.lucy", mmh.oldChanceConfig.getDouble("chance_percent.axolotl.lucy", 20.0));
        mmh.chanceConfig.set("chance_percent.axolotl.wild", mmh.oldChanceConfig.getDouble("chance_percent.axolotl.wild", 20.0));
        mmh.chanceConfig.set("chance_percent.bat", mmh.oldChanceConfig.getDouble("chance_percent.bat", 10.0));
        mmh.chanceConfig.set("chance_percent.bee.angry_pollinated", mmh.oldChanceConfig.getDouble("chance_percent.bee.angry_pollinated", 20.0));
        mmh.chanceConfig.set("chance_percent.bee.angry", mmh.oldChanceConfig.getDouble("chance_percent.bee.angry", 20.0));
        mmh.chanceConfig.set("chance_percent.bee.pollinated", mmh.oldChanceConfig.getDouble("chance_percent.bee.pollinated", 20.0));
        mmh.chanceConfig.set("chance_percent.bee.chance_percent", mmh.oldChanceConfig.getDouble("chance_percent.bee.normal", 20.0));
        mmh.chanceConfig.set("chance_percent.blaze", mmh.oldChanceConfig.getDouble("chance_percent.blaze", 0.5));
        mmh.chanceConfig.set("chance_percent.camel", mmh.oldChanceConfig.getDouble("chance_percent.camel", 27.0));
        mmh.chanceConfig.set("chance_percent.cat.all_black", mmh.oldChanceConfig.getDouble("chance_percent.cat.all_black", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.black", mmh.oldChanceConfig.getDouble("chance_percent.cat.black", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.british_shorthair", mmh.oldChanceConfig.getDouble("chance_percent.cat.british_shorthair", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.calico", mmh.oldChanceConfig.getDouble("chance_percent.cat.calico", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.jellie", mmh.oldChanceConfig.getDouble("chance_percent.cat.jellie", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.persian", mmh.oldChanceConfig.getDouble("chance_percent.cat.persian", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.ragdoll", mmh.oldChanceConfig.getDouble("chance_percent.cat.ragdoll", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.red", mmh.oldChanceConfig.getDouble("chance_percent.cat.red", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.siamese", mmh.oldChanceConfig.getDouble("chance_percent.cat.siamese", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.tabby", mmh.oldChanceConfig.getDouble("chance_percent.cat.tabby", 33.0));
        mmh.chanceConfig.set("chance_percent.cat.white", mmh.oldChanceConfig.getDouble("chance_percent.cat.white", 33.0));

        mmh.chanceConfig.set("chance_percent.cave_spider", mmh.oldChanceConfig.getDouble("chance_percent.cave_spider", 0.5));
        mmh.chanceConfig.set("chance_percent.chicken", mmh.oldChanceConfig.getDouble("chance_percent.chicken", 1.0));
        mmh.chanceConfig.set("chance_percent.cod", mmh.oldChanceConfig.getDouble("chance_percent.cod", 10.0));
        mmh.chanceConfig.set("chance_percent.cow", mmh.oldChanceConfig.getDouble("chance_percent.cow", 1.0));
        mmh.chanceConfig.set("chance_percent.creeper", mmh.oldChanceConfig.getDouble("chance_percent.creeper", 50.0));
        mmh.chanceConfig.set("chance_percent.creeper_charged", mmh.oldChanceConfig.getDouble("chance_percent.creeper_charged", 100.0));
        mmh.chanceConfig.set("chance_percent.dolphin", mmh.oldChanceConfig.getDouble("chance_percent.dolphin", 33.0));
        mmh.chanceConfig.set("chance_percent.donkey", mmh.oldChanceConfig.getDouble("chance_percent.donkey", 20.0));
        mmh.chanceConfig.set("chance_percent.drowned", mmh.oldChanceConfig.getDouble("chance_percent.drowned", 5.0));
        mmh.chanceConfig.set("chance_percent.elder_guardian", mmh.oldChanceConfig.getDouble("chance_percent.elder_guardian", 100.0));
        mmh.chanceConfig.set("chance_percent.ender_dragon", mmh.oldChanceConfig.getDouble("chance_percent.ender_dragon", 100.0));
        mmh.chanceConfig.set("chance_percent.enderman", mmh.oldChanceConfig.getDouble("chance_percent.enderman", 0.5));
        mmh.chanceConfig.set("chance_percent.endermite", mmh.oldChanceConfig.getDouble("chance_percent.endermite", 10.0));
        mmh.chanceConfig.set("chance_percent.evoker", mmh.oldChanceConfig.getDouble("chance_percent.evoker", 25.0));
        mmh.chanceConfig.set("chance_percent.fox.red", mmh.oldChanceConfig.getDouble("chance_percent.fox.red", 10.0));
        mmh.chanceConfig.set("chance_percent.fox.snow", mmh.oldChanceConfig.getDouble("chance_percent.fox.snow", 20.0));
        mmh.chanceConfig.set("chance_percent.frog.cold", mmh.oldChanceConfig.getDouble("chance_percent.frog.cold", 20.0));
        mmh.chanceConfig.set("chance_percent.frog.temperate", mmh.oldChanceConfig.getDouble("chance_percent.frog.temperate", 20.0));
        mmh.chanceConfig.set("chance_percent.frog.warm", mmh.oldChanceConfig.getDouble("chance_percent.frog.warm", 20.0));
        mmh.chanceConfig.set("chance_percent.ghast", mmh.oldChanceConfig.getDouble("chance_percent.ghast", 6.25));
        mmh.chanceConfig.set("chance_percent.giant", mmh.oldChanceConfig.getDouble("chance_percent.giant", 2.5));
        mmh.chanceConfig.set("chance_percent.glow_squid", mmh.oldChanceConfig.getDouble("chance_percent.glow_squid", 5.0));
        mmh.chanceConfig.set("chance_percent.goat.mormal", mmh.oldChanceConfig.getDouble("chance_percent.goat.normal", 1.0));
        mmh.chanceConfig.set("chance_percent.goat.screaming", mmh.oldChanceConfig.getDouble("chance_percent.goat.screaming", 100.0));
        mmh.chanceConfig.set("chance_percent.guardian", mmh.oldChanceConfig.getDouble("chance_percent.guardian", 0.5));
        mmh.chanceConfig.set("chance_percent.hoglin", mmh.oldChanceConfig.getDouble("chance_percent.hoglin", 3.0));
        mmh.chanceConfig.set("chance_percent.horse.black", mmh.oldChanceConfig.getDouble("chance_percent.horse.black", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.brown", mmh.oldChanceConfig.getDouble("chance_percent.horse.brown", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.chestnut", mmh.oldChanceConfig.getDouble("chance_percent.horse.chestnut", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.creamy", mmh.oldChanceConfig.getDouble("chance_percent.horse.creamy", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.dark_brown", mmh.oldChanceConfig.getDouble("chance_percent.horse.dark_brown", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.gray", mmh.oldChanceConfig.getDouble("chance_percent.horse.gray", 27.0));
        mmh.chanceConfig.set("chance_percent.horse.white", mmh.oldChanceConfig.getDouble("chance_percent.horse.white", 27.0));
        mmh.chanceConfig.set("chance_percent.husk", mmh.oldChanceConfig.getDouble("chance_percent.husk", 6.0));
        mmh.chanceConfig.set("chance_percent.illusioner", mmh.oldChanceConfig.getDouble("chance_percent.illusioner", 25.0));
        mmh.chanceConfig.set("chance_percent.iron_golem", mmh.oldChanceConfig.getDouble("chance_percent.iron_golem", 5.0));
        mmh.chanceConfig.set("chance_percent.llama.brown", mmh.oldChanceConfig.getDouble("chance_percent.llama.brown", 24.0));
        mmh.chanceConfig.set("chance_percent.llama.creamy", mmh.oldChanceConfig.getDouble("chance_percent.llama.creamy", 24.0));
        mmh.chanceConfig.set("chance_percent.llama.gray", mmh.oldChanceConfig.getDouble("chance_percent.llama.gray", 24.0));
        mmh.chanceConfig.set("chance_percent.llama.white", mmh.oldChanceConfig.getDouble("chance_percent.llama.white", 24.0));
        mmh.chanceConfig.set("chance_percent.magma_cube", mmh.oldChanceConfig.getDouble("chance_percent.magma_cube", 0.5));
        mmh.chanceConfig.set("chance_percent.mule", mmh.oldChanceConfig.getDouble("chance_percent.mule", 20.0));
        mmh.chanceConfig.set("chance_percent.mushroom_cow.red", mmh.oldChanceConfig.getDouble("chance_percent.mushroom_cow.red", 1.0));
        mmh.chanceConfig.set("chance_percent.mushroom_cow.brown", mmh.oldChanceConfig.getDouble("chance_percent.mushroom_cow.brown", 10.0));
        mmh.chanceConfig.set("chance_percent.ocelot", mmh.oldChanceConfig.getDouble("chance_percent.ocelot", 20.0));
        mmh.chanceConfig.set("chance_percent.panda.aggressive", mmh.oldChanceConfig.getDouble("chance_percent.panda.aggressive", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.brown", mmh.oldChanceConfig.getDouble("chance_percent.panda.brown", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.lazy", mmh.oldChanceConfig.getDouble("chance_percent.panda.lazy", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.normal", mmh.oldChanceConfig.getDouble("chance_percent.panda.normal", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.playful", mmh.oldChanceConfig.getDouble("chance_percent.panda.playful", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.weak", mmh.oldChanceConfig.getDouble("chance_percent.panda.weak", 27.0));
        mmh.chanceConfig.set("chance_percent.panda.worried", mmh.oldChanceConfig.getDouble("chance_percent.panda.worried", 27.0));
        mmh.chanceConfig.set("chance_percent.parrot.blue", mmh.oldChanceConfig.getDouble("chance_percent.parrot.blue", 25.0));
        mmh.chanceConfig.set("chance_percent.parrot.cyan", mmh.oldChanceConfig.getDouble("chance_percent.parrot.cyan", 25.0));
        mmh.chanceConfig.set("chance_percent.parrot.gray", mmh.oldChanceConfig.getDouble("chance_percent.parrot.gray", 25.0));
        mmh.chanceConfig.set("chance_percent.parrot.green", mmh.oldChanceConfig.getDouble("chance_percent.parrot.green", 25.0));
        mmh.chanceConfig.set("chance_percent.parrot.red", mmh.oldChanceConfig.getDouble("chance_percent.parrot.red", 25.0));
        mmh.chanceConfig.set("chance_percent.phantom", mmh.oldChanceConfig.getDouble("chance_percent.phantom", 10.0));
        mmh.chanceConfig.set("chance_percent.pig", mmh.oldChanceConfig.getDouble("chance_percent.pig", 1.0));
        mmh.chanceConfig.set("chance_percent.piglin", mmh.oldChanceConfig.getDouble("chance_percent.piglin", 4.0));
        mmh.chanceConfig.set("chance_percent.pig_zombie", mmh.oldChanceConfig.getDouble("chance_percent.pig_zombie", 0.5));
        mmh.chanceConfig.set("chance_percent.pillager", mmh.oldChanceConfig.getDouble("chance_percent.pillager", 2.5));
        mmh.chanceConfig.set("chance_percent.polar_bear", mmh.oldChanceConfig.getDouble("chance_percent.polar_bear", 20.0));
        mmh.chanceConfig.set("chance_percent.pufferfish", mmh.oldChanceConfig.getDouble("chance_percent.pufferfish", 15.0));
        mmh.chanceConfig.set("chance_percent.rabbit.black", mmh.oldChanceConfig.getDouble("chance_percent.rabbit.black", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.black_and_white", mmh.oldChanceConfig.getDouble("chance_percent.rabbit.black_and_white", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.brown", mmh.oldChanceConfig.getDouble("chance_percent.rabbit.brown", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.gold", mmh.oldChanceConfig.getDouble("chance_percent.rabbit.gold", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.salt_and_pepper", mmh.oldChanceConfig.getDouble("chance_percent.rabbit.salt_and_pepper", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.the_killer_bunny", mmh.oldChanceConfig.getDouble("chance_percent.rabbit.the_killer_bunny", 100.0));
        mmh.chanceConfig.set("chance_percent.rabbit.toast", mmh.oldChanceConfig.getDouble("chance_percent.rabbit.toast", 26.0));
        mmh.chanceConfig.set("chance_percent.rabbit.white", mmh.oldChanceConfig.getDouble("chance_percent.rabbit.white", 26.0));
        mmh.chanceConfig.set("chance_percent.ravager", mmh.oldChanceConfig.getDouble("chance_percent.ravager", 25.0));
        mmh.chanceConfig.set("chance_percent.salmon", mmh.oldChanceConfig.getDouble("chance_percent.salmon", 10.0));
        mmh.chanceConfig.set("chance_percent.sheep.black", mmh.oldChanceConfig.getDouble("chance_percent.sheep.black", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.blue", mmh.oldChanceConfig.getDouble("chance_percent.sheep.blue", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.brown", mmh.oldChanceConfig.getDouble("chance_percent.sheep.brown", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.cyan", mmh.oldChanceConfig.getDouble("chance_percent.sheep.cyan", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.gray", mmh.oldChanceConfig.getDouble("chance_percent.sheep.gray", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.green", mmh.oldChanceConfig.getDouble("chance_percent.sheep.green", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.jeb_", mmh.oldChanceConfig.getDouble("chance_percent.sheep.jeb_", 10.0));
        mmh.chanceConfig.set("chance_percent.sheep.light_blue", mmh.oldChanceConfig.getDouble("chance_percent.sheep.light_blue", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.light_gray", mmh.oldChanceConfig.getDouble("chance_percent.sheep.light_gray", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.lime", mmh.oldChanceConfig.getDouble("chance_percent.sheep.lime", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.magenta", mmh.oldChanceConfig.getDouble("chance_percent.sheep.magenta", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.orange", mmh.oldChanceConfig.getDouble("chance_percent.sheep.orange", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.pink", mmh.oldChanceConfig.getDouble("chance_percent.sheep.pink", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.purple", mmh.oldChanceConfig.getDouble("chance_percent.sheep.purple", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.red", mmh.oldChanceConfig.getDouble("chance_percent.sheep.red", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.white", mmh.oldChanceConfig.getDouble("chance_percent.sheep.white", 1.75));
        mmh.chanceConfig.set("chance_percent.sheep.yellow", mmh.oldChanceConfig.getDouble("chance_percent.sheep.yellow", 1.75));
        mmh.chanceConfig.set("chance_percent.shulker", mmh.oldChanceConfig.getDouble("chance_percent.shulker", 5.0));
        mmh.chanceConfig.set("chance_percent.silverfish", mmh.oldChanceConfig.getDouble("chance_percent.silverfish", 5.0));
        mmh.chanceConfig.set("chance_percent.skeleton", mmh.oldChanceConfig.getDouble("chance_percent.skeleton", 2.5));
        mmh.chanceConfig.set("chance_percent.skeleton_horse", mmh.oldChanceConfig.getDouble("chance_percent.skeleton_horse", 20.0));
        mmh.chanceConfig.set("chance_percent.slime", mmh.oldChanceConfig.getDouble("chance_percent.slime", 0.5));
        mmh.chanceConfig.set("chance_percent.sniffer", mmh.oldChanceConfig.getDouble("chance_percent.sniffer", 50.0));
        mmh.chanceConfig.set("chance_percent.snowman", mmh.oldChanceConfig.getDouble("chance_percent.snowman", 5.0));
        mmh.chanceConfig.set("chance_percent.spider", mmh.oldChanceConfig.getDouble("chance_percent.spider", 0.5));
        mmh.chanceConfig.set("chance_percent.squid", mmh.oldChanceConfig.getDouble("chance_percent.squid", 5.0));
        mmh.chanceConfig.set("chance_percent.stray", mmh.oldChanceConfig.getDouble("chance_percent.stray", 6.0));
        mmh.chanceConfig.set("chance_percent.strider", mmh.oldChanceConfig.getDouble("chance_percent.strider", 10.0));
        mmh.chanceConfig.set("chance_percent.tadpole", mmh.oldChanceConfig.getDouble("chance_percent.tadpole", 10.0));
        mmh.chanceConfig.set("chance_percent.trader_llama.brown", mmh.oldChanceConfig.getDouble("chance_percent.trader_llama.brown", 24.0));
        mmh.chanceConfig.set("chance_percent.trader_llama.creamy", mmh.oldChanceConfig.getDouble("chance_percent.trader_llama.creamy", 24.0));
        mmh.chanceConfig.set("chance_percent.trader_llama.gray", mmh.oldChanceConfig.getDouble("chance_percent.trader_llama.gray", 24.0));
        mmh.chanceConfig.set("chance_percent.trader_llama.white", mmh.oldChanceConfig.getDouble("chance_percent.trader_llama.white", 24.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.tropical_fish", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.tropical_fish", 10.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.anemone", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.anemone", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.black_tang", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.black_tang", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.blue_tang", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.blue_tang", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.butterflyfish", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.butterflyfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.cichlid", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.cichlid", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.clownfish", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.clownfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.cotton_candy_betta", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.cotton_candy_betta", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.dottyback", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.dottyback", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.emperor_red_snapper", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.emperor_red_snapper", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.goatfish", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.goatfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.moorish_idol", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.moorish_idol", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.ornate_butterflyfish", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.ornate_butterflyfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.parrotfish", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.parrotfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.queen_angelfish", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.queen_angelfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.red_cichlid", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.red_cichlid", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.red_lipped_blenny", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.red_lipped_blenny", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.red_snapper", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.red_snapper", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.threadfin", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.threadfin", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.tomato_clownfish", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.tomato_clownfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.triggerfish", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.triggerfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.yellowtail_parrotfish", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.yellow_parrotfish", 50.0));
        mmh.chanceConfig.set("chance_percent.tropical_fish.yellow_tang", mmh.oldChanceConfig.getDouble("chance_percent.tropical_fish.yellow_tang", 50.0));

        mmh.chanceConfig.set("chance_percent.turtle", mmh.oldChanceConfig.getDouble("chance_percent.turtle", 10.0));
        mmh.chanceConfig.set("chance_percent.vex", mmh.oldChanceConfig.getDouble("chance_percent.vex", 10.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.armorer", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.butcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.cartographer", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.cleric", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.farmer", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.fisherman", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.fletcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.leatherworker", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.librarian", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.mason", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.nitwit", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.none", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.shepherd", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.toolsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.desert.weaponsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.desert.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.armorer", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.butcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.cartographer", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.cleric", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.farmer", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.fisherman", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.fletcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.leatherworker", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.librarian", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.mason", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.nitwit", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.none", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.shepherd", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.toolsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.jungle.weaponsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.jungle.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.armorer", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.butcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.cartographer", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.cleric", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.farmer", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.fisherman", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.fletcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.leatherworker", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.librarian", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.mason", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.nitwit", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.none", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.shepherd", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.toolsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.plains.weaponsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.plains.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.armorer", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.butcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.cartographer", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.cleric", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.farmer", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.fisherman", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.fletcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.leatherworker", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.librarian", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.mason", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.nitwit", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.none", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.shepherd", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.toolsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.savanna.weaponsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.savanna.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.armorer", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.butcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.cartographer", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.cleric", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.farmer", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.fisherman", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.fletcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.leatherworker", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.librarian", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.mason", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.nitwit", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.none", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.shepherd", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.toolsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.snow.weaponsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.snow.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.armorer", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.butcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.cartographer", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.cleric", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.farmer", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.fisherman", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.fletcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.leatherworker", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.librarian", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.mason", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.nitwit", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.none", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.shepherd", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.toolsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.swamp.weaponsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.swamp.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.armorer", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.armorer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.butcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.butcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.cartographer", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.cartographer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.cleric", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.cleric", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.farmer", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.farmer", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.fisherman", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.fisherman", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.fletcher", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.fletcher", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.leatherworker", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.leatherworker", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.librarian", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.librarian", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.mason", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.mason", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.nitwit", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.nitwit", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.none", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.none", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.shepherd", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.shepherd", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.toolsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.toolsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.villager.taiga.weaponsmith", mmh.oldChanceConfig.getDouble("chance_percent.villager.taiga.weaponsmith", 100.0));
        mmh.chanceConfig.set("chance_percent.vindicator", mmh.oldChanceConfig.getDouble("chance_percent.vindicator", 5.0));
        mmh.chanceConfig.set("chance_percent.wandering_trader", mmh.oldChanceConfig.getDouble("chance_percent.wandering_trader", 100.0));
        mmh.chanceConfig.set("chance_percent.warden", mmh.oldChanceConfig.getDouble("chance_percent.warden", 100.0));
        mmh.chanceConfig.set("chance_percent.witch", mmh.oldChanceConfig.getDouble("chance_percent.witch", 0.5));
        mmh.chanceConfig.set("chance_percent.wither", mmh.oldChanceConfig.getDouble("chance_percent.wither", 100.0));
        mmh.chanceConfig.set("chance_percent.wither_skeleton", mmh.oldChanceConfig.getDouble("chance_percent.wither_skeleton", 2.5));
        mmh.chanceConfig.set("chance_percent.wolf", mmh.oldChanceConfig.getDouble("chance_percent.wolf", 20.0));
        mmh.chanceConfig.set("chance_percent.zoglin", mmh.oldChanceConfig.getDouble("chance_percent.zoglin", 20.0));
        mmh.chanceConfig.set("chance_percent.zombie", mmh.oldChanceConfig.getDouble("chance_percent.zombie", 2.5));
        mmh.chanceConfig.set("chance_percent.zombie_horse", mmh.oldChanceConfig.getDouble("chance_percent.zombie_horse", 100.0));
        mmh.chanceConfig.set("chance_percent.zombie_pigman", mmh.oldChanceConfig.getDouble("chance_percent.zombie_pigman", 0.5));
        mmh.chanceConfig.set("chance_percent.zombified_piglin", mmh.oldChanceConfig.getDouble("chance_percent.zombified_piglin", 0.5));
        mmh.chanceConfig.set("chance_percent.zombie_villager", mmh.oldChanceConfig.getDouble("chance_percent.zombie_villager", 50.0));
        try {
            mmh.chanceConfig.save(file2);
        } catch (IOException e) {
            mmh.stacktraceInfo();
            e.printStackTrace();
        }
        mmh.log(Level.INFO, "chance_config.yml has been updated!");
        mmh.oldChanceConfig = null;
    }
}
