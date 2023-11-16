package com.github.joelgodofwar.mmh;

import com.github.joelgodofwar.mmh.handlers.*;
import com.github.joelgodofwar.mmh.i18n.Translator;
import com.github.joelgodofwar.mmh.util.*;
import com.github.joelgodofwar.mmh.util.datatypes.JsonDataType;
import com.github.joelgodofwar.mmh.util.mob.NameTag;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import de.tr7zw.nbtapi.NBTListCompound;
import io.papermc.lib.PaperLib;
import io.papermc.lib.features.blockstatesnapshot.BlockStateSnapshotResult;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.TileState;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"deprecation"})
public class MoreMobHeads extends JavaPlugin implements Listener {

    public final static Logger logger = Logger.getLogger("Minecraft");
    public static String pluginDisplayName() {
        return THIS_NAME;
    }
    static String THIS_NAME;
    static String THIS_VERSION;

    public boolean isDev = false;
    public static boolean debug = false;
    public static String languageName;
    File mobNamesFile;
    public FileConfiguration mobNames;
    public File playerFile;
    public FileConfiguration playerHeads;
    File blockFile;
    File blockFile116;
    File blockFile1162;
    File blockFile117;
    File blockFile119;
    File blockFile120;
    public FileConfiguration blockHeads = new YamlConfiguration();
    public FileConfiguration blockHeads2 = new YamlConfiguration();
    public FileConfiguration blockHeads3 = new YamlConfiguration();
    public FileConfiguration blockHeads4 = new YamlConfiguration();
    public FileConfiguration blockHeads5 = new YamlConfiguration();
    public File customFile;
    public FileConfiguration traderCustom;
    File chanceFile;
    public YmlConfiguration chanceConfig;
    public YmlConfiguration oldChanceConfig;
    public YmlConfiguration beheadingMessages = new YmlConfiguration();
    public YamlConfiguration oldMessages;
    public YmlConfiguration config = new YmlConfiguration();
    YamlConfiguration oldConfig = new YamlConfiguration();
    public String world_whitelist;
    public String world_blacklist;
    public String mob_whitelist;
    public String mob_blacklist;
    boolean colorful_console;
    boolean silent_console;
    public final NamespacedKey NAMETAG_KEY = new NamespacedKey(this, "name_tag");
    public final NamespacedKey SHIVERING_KEY = new NamespacedKey(this, "shivering_tag");
    public Map<UUID, ItemStack> playerWeapons = new HashMap<>();
    public Map<UUID, UUID> endCrystals = new HashMap<>();
    File debugFile;
    Random random = new Random();
    String pluginName = THIS_NAME;
    private final Set<String> triggeredPlayers = new HashSet<>();
    private final HashMap<String, String> namedTropicalFish = new HashMap<>();
    private final Map<Player, Random> chanceRandoms = new HashMap<>();
    Reloadable handler = null;
    @Override // TODO: onEnable
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        Networks.checkUpdate = getConfig().getBoolean("auto_update_check");
        debug = getConfig().getBoolean("debug", false);
        languageName = getConfig().getString("lang", "zh_CN");
        oldConfig = new YamlConfiguration();
        oldMessages = new YamlConfiguration();
        THIS_NAME = this.getDescription().getName();
        THIS_VERSION = this.getDescription().getVersion();
        if (!getConfig().getBoolean("console.longpluginname", true)) {
            pluginName = "MMH";
        } else {
            pluginName = THIS_NAME;
        }

        colorful_console = getConfig().getBoolean("console.colorful_console", true);
        silent_console = getConfig().getBoolean("console.silent_console", false);

        loading(Ansi.GREEN + "**************************************" + Ansi.RESET);
        loading(Ansi.YELLOW + THIS_NAME + " v" + THIS_VERSION + Ansi.RESET + " Loading...");

        debugFile = new File(this.getDataFolder() + File.separator + "logs" + File.separator + "mmh_debug.log");
        if (!debugFile.exists()) try {
            debugFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* DEV check **/
        File jarfile = this.getFile().getAbsoluteFile();
        if (jarfile.toString().contains("-DEV")) {
            debug = true;
            logDebug("Jar file contains -DEV, debug set to true");
            //log("jarfile contains dev, debug set to true.");
        }

        /* Version Check */
        if (!(Double.parseDouble(getMCVersion().substring(0, 4)) >= 1.14)) {
            // !getMCVersion().startsWith("1.14")&&!getMCVersion().startsWith("1.15")&&!getMCVersion().startsWith("1.16")&&!getMCVersion().startsWith("1.17")
            logger.info(Ansi.RED + "WARNING! *!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + Ansi.RESET);
            logger.info(Ansi.RED + "WARNING! " + get("mmh.message.server_not_version") + Ansi.RESET);
            logger.info(Ansi.RED + "WARNING! " + THIS_NAME + " v" + THIS_VERSION + " disabling." + Ansi.RESET);
            logger.info(Ansi.RED + "WARNING! *!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + Ansi.RESET);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        /*    Check for config */
        try {
            if (!getDataFolder().exists()) {
                log(Level.INFO, "Data Folder doesn't exist");
                log(Level.INFO, "Creating Data Folder");
                if (getDataFolder().mkdirs()) {
                    log(Level.INFO, "Data Folder Created at " + getDataFolder());
                }
            }
            File file = new File(getDataFolder(), "config.yml");
            if (debug) {
                logDebug("" + file);
            }
            if (!file.exists()) {
                log(Level.INFO, "config.yml not found, creating!");
                saveResource("config.yml", true);
                saveResource("chance_config.yml", true);
            }
        } catch (Exception e) {
            stacktraceInfo();
            e.printStackTrace();
        }

        Config.reload(this);

        world_whitelist = config.getString("world.whitelist", "");
        world_blacklist = config.getString("world.blacklist", "");
        mob_whitelist = config.getString("mob.whitelist", "");
        mob_blacklist = config.getString("mob.blacklist", "");

        getServer().getPluginManager().registerEvents(this, this);

        String jarFileName = this.getFile().getAbsoluteFile().toString();
        loading(Ansi.GREEN + " (  " + Ansi.YELLOW + "-<[ PLEASE INCLUDE THIS WITH ANY ISSUE REPORTS ]>-" + Ansi.RESET);
        loading(Ansi.GREEN + "  ) " + Ansi.WHITE + "This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")" + Ansi.RESET);
        loading(Ansi.GREEN + " (  " + Ansi.WHITE + "vardebug=" + debug + " debug=" + getConfig().get("debug", "error") + " in " + this.getDataFolder() + File.separatorChar + "config.yml" + Ansi.RESET);
        loading(Ansi.GREEN + "  ) " + Ansi.WHITE + "jarfilename=" + StrUtils.Right(jarFileName, jarFileName.length() - jarFileName.lastIndexOf(File.separatorChar)) + Ansi.RESET);
        loading(Ansi.GREEN + " (  " + Ansi.YELLOW + "-<[ PLEASE INCLUDE THIS WITH ANY ISSUE REPORTS ]>-" + Ansi.RESET);

        //Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "version");
        if (getConfig().getBoolean("debug") && !jarfile.toString().contains("-DEV")) {
            logDebug("Config.yml DUMP - INCLUDE THIS WITH ANY ISSUE REPORT VVV");
            dumpConfig(getConfig());
            logDebug("Config.yml DUMP - INCLUDE THIS WITH ANY ISSUE REPORT ^^^");
        }

        /* Register EventHandler */
        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 2);
        if (debug) {
            logDebug("version=" + version);
        }
        Listener listener;
        if (version.contains("1_16_R") || version.contains("1_15_R") || version.contains("1_14_R")) {
            listener = (Listener) (handler = new EventHandler_1_16(this));

        } else if (version.contains("1_17_R") || version.contains("1_18_R")) {
            listener = (Listener) (handler = new EventHandler_1_17(this));

        } else if (version.contains("1_19_R")) {
            listener = (Listener) (handler = new EventHandler_1_19(this));

        } else if (version.contains("1_20_R")) {
            listener = (Listener) (handler = new EventHandler_1_20(this));

        } else {
            logWarn("Not compatible with this version of Minecraft:" + version);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(listener, this);

        namedTropicalFish.put("STRIPEY-ORANGE-GRAY", "ANEMONE");
        namedTropicalFish.put("FLOPPER-GRAY-GRAY", "BLACK_TANG");
        namedTropicalFish.put("FLOPPER-GRAY-BLUE", "BLUE_TANG");
        namedTropicalFish.put("CLAYFISH-WHITE-GRAY", "BUTTERFLYFISH");
        namedTropicalFish.put("SUNSTREAK-BLUE-GRAY", "CICHLID");
        namedTropicalFish.put("KOB-ORANGE-WHITE", "CLOWNFISH");
        namedTropicalFish.put("SPOTTY-PINK-LIGHT_BLUE", "COTTON_CANDY_BETTA");
        namedTropicalFish.put("BLOCKFISH-PURPLE-YELLOW", "DOTTYBACK");
        namedTropicalFish.put("CLAYFISH-WHITE-RED", "EMPEROR_RED_SNAPPER");
        namedTropicalFish.put("SPOTTY-WHITE-YELLOW", "GOATFISH");
        namedTropicalFish.put("GLITTER-WHITE-GRAY", "MOORISH_IDOL");
        namedTropicalFish.put("CLAYFISH-WHITE-ORANGE", "ORNATE_BUTTERFLYFISH");
        namedTropicalFish.put("DASHER-CYAN-PINK", "PARROTFISH");
        namedTropicalFish.put("BRINELY-LIME-LIGHT_BLUE", "QUEEN_ANGELFISH");
        namedTropicalFish.put("BETTY-RED-WHITE", "RED_CICHLID");
        namedTropicalFish.put("SNOOPER-GRAY-RED", "RED_LIPPED_BLENNY");
        namedTropicalFish.put("BLOCKFISH-RED-WHITE", "RED_SNAPPER");
        namedTropicalFish.put("KOB-RED-WHITE", "TOMATO_CLOWNFISH");
        namedTropicalFish.put("FLOPPER-WHITE-YELLOW", "THREADFIN");
        namedTropicalFish.put("SUNSTREAK-GRAY-WHITE", "TRIGGERFISH");
        namedTropicalFish.put("DASHER-CYAN-YELLOW", "YELLOWTAIL_PARROTFISH");
        namedTropicalFish.put("FLOPPER-YELLOW-YELLOW", "YELLOW_TANG");

        Networks.checkUpdate(this);

        consoleInfo("Enabled - Loading took " + Utils.LoadTime(startTime));
        if (getConfig().getBoolean("metrics", false)) Networks.checkUpdate(this);
    }

    @Override
    public void onDisable() {
        consoleInfo("Disabled");
    }

    public void consoleInfo(String state) {
        loading(Ansi.GREEN + "**************************************" + Ansi.RESET);
        loading(Ansi.YELLOW + THIS_NAME + " v" + THIS_VERSION + Ansi.RESET + " is " + state);
        loading(Ansi.GREEN + "**************************************" + Ansi.RESET);
    }

    public void loading(String string) {
        if (!colorful_console) {
            string = Ansi.stripAnsi(string);
        }
        logger.info(string);
    }

    public void log(String string) {
        if (!colorful_console) {
            string = Ansi.stripAnsi(string);
        }
        log(Level.INFO, string);
    }

    public void log(Level level, String string) {// TODO: log
        if (!colorful_console) {
            string = Ansi.stripAnsi(string);
        }
        logger.log(level, ChatColor.YELLOW + THIS_NAME + " v" + THIS_VERSION + ChatColor.RESET + " " + string);
    }

    public void logDebug(String log) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); //"dd/MM HH:mm:ss"
        String date = dateFormat.format(new Date());
        String message = "[" + date + "] [v" + THIS_VERSION + "] [DEBUG]: " + ChatColor.stripColor(log);
        try {
            FileWriter writer = new FileWriter(debugFile.toString(), true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.append(message).append("\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log(Level.INFO, Ansi.RESET + "[" + Ansi.LIGHT_BLUE + "DEBUG" + Ansi.RESET + "]" + log + Ansi.RESET);
    }

    public void logWarn(String log) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
        String date = dateFormat.format(new Date());
        String message = "[" + date + "] [WARN]: " + log;
        logger.warning(message + "\n");
        try {
            FileWriter writer = new FileWriter(debugFile, true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.append(message);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log(Level.WARNING, Ansi.RESET + "[" + Ansi.LIGHT_YELLOW + "WARNING" + Ansi.RESET + "] " + log + Ansi.RESET);
    }

    public static String getMCVersion() {
        String strVersion = Bukkit.getVersion();
        strVersion = strVersion.substring(strVersion.indexOf("MC: "));
        strVersion = strVersion.replace("MC: ", "").replace(")", "");
        return strVersion;
    }

    public void giveMobHead(LivingEntity mob, String name) {
        ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = Utils.getItemMeta(helmet);
        meta.setDisplayName(name + "'s Head");
        meta.setOwner(name);
        helmet.setItemMeta(meta);
        if (mob.getEquipment() != null) mob.getEquipment().setHelmet(helmet);

        if (getServer().getPluginManager().getPlugin("WildStacker") != null) {
            @Nonnull
            PersistentDataContainer pdc = mob.getPersistentDataContainer();
            pdc.set(NAMETAG_KEY, PersistentDataType.STRING, "nametag");
        }
    }

    public void givePlayerHead(Player player, String playerName) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = Utils.getItemMeta(playerHead);
        meta.setDisplayName(playerName + "'s Head");
        meta.setOwner(playerName); //.setOwner(name);
        ArrayList<String> lore = new ArrayList<>();
        if (getConfig().getBoolean("lore.show_plugin_name", true)) {
            lore.add(ChatColor.AQUA + "" + THIS_NAME);
        }
        meta.setLore(lore);
        playerHead.setItemMeta(meta);

        playerHead.setItemMeta(meta);

        player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(playerHead, EntityType.PLAYER));
    }

    public void giveBlockHead(Player player, String blockName) {
        if (debug) {
            logDebug("giveBlockHead START");
        }
        ItemStack blockStack = null;
        int isBlock = isBlockHeadName(blockName);
        int isBlock2 = isBlockHeadName2(blockName);
        int isBlock3 = isBlockHeadName3(blockName);
        int isBlock4 = isBlockHeadName4(blockName);
        int isBlock5 = isBlockHeadName5(blockName);
        if (isBlock != -1) {
            if (debug) {
                logDebug("GBH isBlock=" + isBlock);
            }
            blockStack = blockHeads.getItemStack("blocks.block_" + isBlock + ".itemstack", new ItemStack(Material.AIR));
        } else if (isBlock2 != -1) {
            if (debug) {
                logDebug("GBH isBlock2=" + isBlock2);
            }
            blockStack = blockHeads2.getItemStack("blocks.block_" + isBlock2 + ".itemstack", new ItemStack(Material.AIR));
        } else if (isBlock3 != -1) {
            if (debug) {
                logDebug("GBH isBlock3=" + isBlock3);
            }
            blockStack = blockHeads3.getItemStack("blocks.block_" + isBlock3 + ".itemstack", new ItemStack(Material.AIR));
        } else if (isBlock4 != -1) {
            if (debug) {
                logDebug("GBH isBlock4=" + isBlock4);
            }
            blockStack = blockHeads4.getItemStack("blocks.block_" + isBlock4 + ".itemstack", new ItemStack(Material.AIR));
        } else if (isBlock5 != -1) {
            if (debug) {
                logDebug("GBH isBlock5=" + isBlock5);
            }
            blockStack = blockHeads5.getItemStack("blocks.block_" + isBlock5 + ".itemstack", new ItemStack(Material.AIR));
        } else {
            player.sendMessage(get("mmh.command.give.blockhead.notfound")
                    .replace("<plugin>", THIS_NAME).replace("<version>", THIS_VERSION)
                    .replace("<block>", blockName));
        }
        if ((blockStack != null) && (blockStack.getType() != Material.AIR)) {
            player.getWorld().dropItemNaturally(player.getLocation(), blockStack);
            if (debug) {
                logDebug("GBH BlockHead given to " + player.getName());
            }
        }
        if (debug) {
            logDebug("giveBlockHead END");
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEntityEvent event) {// TODO: PlayerInteractEntityEvent
        try {
            Player player = event.getPlayer();
            if (player.hasPermission("moremobheads.nametag")) {
                if (debug) {
                    log("moremobheads.nametag=true");
                }
                if (getConfig().getBoolean("mob.nametag", false)) {
                    if (debug) {
                        log("mob.nametag=true");
                    }
                    PlayerInventory inv = player.getInventory();
                    Material material = inv.getItemInMainHand().getType();
                    Material material2 = inv.getItemInOffHand().getType();
                    String name = "";
                    if (material.equals(Material.NAME_TAG)) {
                        name = Utils.getItemMeta(inv.getItemInMainHand()).getDisplayName();
                        if (debug) {
                            logDebug("PIEE" + player.getDisplayName() + " Main hand name=" + name);
                        }
                    }
                    if (material2.equals(Material.NAME_TAG)) {
                        name = Utils.getItemMeta(inv.getItemInOffHand()).getDisplayName();
                        if (debug) {
                            logDebug("PIEE " + player.getDisplayName() + " Off hand name=" + name);
                        }
                    }

                    if (material.equals(Material.NAME_TAG) || material2.equals(Material.NAME_TAG)) {
                        if (getServer().getPluginManager().getPlugin("SilenceMobs") != null) {
                            if (name.toLowerCase().contains("silenceme") || name.toLowerCase().contains("silence me")) {
                                return;
                            }
                        }
                        LivingEntity mob = (LivingEntity) event.getRightClicked();
                        if (debug) {
                            log("canwearhead=" + NameTag.canWearHead(mob));
                        }
                        if (NameTag.canWearHead(mob)) {
                            boolean enforcewhitelist = getConfig().getBoolean("whitelist.enforce", false);
                            boolean enforceblacklist = getConfig().getBoolean("blacklist.enforce", false);
                            boolean onwhitelist = getConfig().getString("whitelist.player_head_whitelist", "").toLowerCase().contains(name.toLowerCase());
                            boolean onblacklist = getConfig().getString("blacklist.player_head_blacklist", "").toLowerCase().contains(name.toLowerCase());
                            if (enforcewhitelist && enforceblacklist) {
                                if (onwhitelist && !(onblacklist)) {
                                    giveMobHead(mob, name);
                                } else {
                                    event.setCancelled(true); // return;
                                    if (debug) {
                                        log(Level.INFO, "PIE - Name Error 1");
                                    }
                                }
                            } else if (enforcewhitelist) {
                                if (onwhitelist) {
                                    giveMobHead(mob, name);
                                } else {
                                    event.setCancelled(true); // return;
                                    if (debug) {
                                        log(Level.INFO, "PIE - Name not on whitelist.");
                                    }
                                }
                            } else if (enforceblacklist) {
                                if (!onblacklist) {
                                    giveMobHead(mob, name);
                                } else {
                                    event.setCancelled(true); // return;
                                    if (debug) {
                                        log(Level.INFO, "PIE - Name is on blacklist.");
                                    }
                                }
                            } else {
                                giveMobHead(mob, name);
                            }
                        }
                    }
                } else if (debug) {
                    log("mob.nametag=false");
                }
            } else
                if (debug) {
                    log("moremobheads.nametag=false");
                }
        } catch (Exception e) {
            stacktraceInfo();
            e.printStackTrace();
        }

    }

    public ItemStack dropMobHead(Entity entity, String name, Player killer) {// TODO: dropMobHead
        ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = Utils.getItemMeta(helmet);
        meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(name)); //.setOwner(name);
        meta.setDisplayName(name + "'s Head");
        ArrayList<String> lore = new ArrayList<>();
        if (getConfig().getBoolean("lore.show_killer", true)) {
            String killed_by = ChatColorUtils.setColors(mobNames.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>"));
            lore.add(ChatColor.RESET + killed_by.replace("<player>", "" + killer.getDisplayName()));
        }
        if (getConfig().getBoolean("lore.show_plugin_name", true)) {
            lore.add(ChatColor.AQUA + "" + THIS_NAME);
        }
        meta.setLore(lore);
        meta.setLore(lore);
        helmet.setItemMeta(meta);//																	 e2d4c388-42d5-4a96-b4c9-623df7f5e026
        helmet.setItemMeta(meta);
        entity.getWorld().dropItemNaturally(entity.getLocation(), helmet);
        return helmet;
    }

    public boolean DropIt(EntityDeathEvent event, double chancepercent) {// TODO: DropIt
        Player player = event.getEntity().getKiller();
        if (player == null) return false;
        ItemStack itemstack = player.getInventory().getItemInMainHand();
        if (debug) {
            logDebug("DI itemstack=" + itemstack.getType());
        }
        int enchantmentlevel = 0;
        if (getConfig().getBoolean("apply_looting", true)) {
            enchantmentlevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }
        if (chancepercent == 0) {
            enchantmentlevel = 0;
        }
        if (debug) {
            logDebug("DI enchantmentlevel=" + enchantmentlevel);
        }
        double enchantmentlevelpercent = enchantmentlevel;
        if (debug) {
            logDebug("DI enchantmentlevelpercent=" + enchantmentlevelpercent);
        }
        Random chanceRandom = chanceRandoms.computeIfAbsent(player, p -> new Random(p.getUniqueId().hashCode()));
        double chance = chanceRandom.nextDouble() * 100;
        if (debug) {
            logDebug("DI chance=" + chance);
        }
        if (debug) {
            logDebug("DI chancepercent=" + chancepercent);
        }
        chancepercent = chancepercent + enchantmentlevelpercent;
        if (debug) {
            logDebug("DI chancepercent2=" + chancepercent);
        }
        return (chancepercent >= chance) || isDev;
    }

    public int randomBetween(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) // TODO: OnPlayerJoin
    {
        Player player = event.getPlayer();
        //if(p.isOp() && UpdateCheck||p.hasPermission("moremobheads.showUpdateAvailable")){
        /* Notify Ops */
        if (Networks.isUpdateAvailable() && (player.isOp() || player.hasPermission("moremobheads.showUpdateAvailable"))) {
            String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>\"}}]";
            links = links.replace("<DownloadLink>", Networks.downloadLink).replace("<Download>", get("mmh.version.download"))
                    .replace("<Donate>", get("mmh.version.donate")).replace("<please_update>", get("mmh.version.please_update"))
                    .replace("<Donate_msg>", get("mmh.version.donate.message")).replace("<Notes>", get("mmh.version.notes"))
                    .replace("<Notes_msg>", get("mmh.version.notes.message"));
            String versions = "" + ChatColor.GRAY + get("mmh.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + get("mmh.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
            player.sendMessage("" + ChatColor.GRAY + get("mmh.version.message").replace("<MyPlugin>", ChatColor.GOLD + THIS_NAME + ChatColor.GRAY));
            Utils.sendJson(player, links);
            player.sendMessage(versions.replace("{nVers}", Networks.getUpdateNewVersion()).replace("{oVers}", Networks.getUpdateOldVersion()));
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd");
        LocalDate localDate = LocalDate.now();
        String daDay = dtf.format(localDate);

        if (daDay.equals("04/16")) {
            String playerId = player.getUniqueId().toString();
            if (!triggeredPlayers.contains(playerId)) {
                if (Utils.isPluginRequired(THIS_NAME)) {
                    player.sendTitle("Happy Birthday Mom", "I miss you - 4/16/1954-12/23/2022", 10, 70, 20);
                }
                triggeredPlayers.add(playerId);
            }
        }
        if (player.getDisplayName().equals("JoelYahwehOfWar") || player.getDisplayName().equals("JoelGodOfWar")) {
            player.sendMessage(THIS_NAME + " " + THIS_VERSION + " Hello father!");
        }
    }
    public ItemStack makeSkull(String textureCode, String headName, Player killer) {// TODO: makeSkull
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        if (textureCode == null) {
            return item;
        }
        SkullMeta meta = Utils.getItemMeta(item);

        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(textureCode.getBytes()), textureCode);
        profile.getProperties().put("textures", new Property("textures", textureCode));
        profile.getProperties().put("display", new Property("Name", headName));

        setGameProfile(meta, profile);
        ArrayList<String> lore = new ArrayList<>();

        if (getConfig().getBoolean("lore.show_killer", true)) {
            lore.add(ChatColor.RESET + ChatColorUtils.setColors(mobNames.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>").replace("<player>", killer.getName())));
        }
        if (getConfig().getBoolean("lore.show_plugin_name", true)) {
            lore.add(ChatColor.AQUA + "MoreMobHeads");
        }
        meta.setLore(lore);

        meta.setDisplayName(headName);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack makeSkulls(String textureCode, String headName, int amount) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, amount);
        if (textureCode == null) {
            return item;
        }
        SkullMeta meta = Utils.getItemMeta(item);

        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(textureCode.getBytes()), textureCode);
        profile.getProperties().put("textures", new Property("textures", textureCode));
        profile.getProperties().put("display", new Property("Name", headName));
        setGameProfile(meta, profile);
        ArrayList<String> lore = new ArrayList<>();

        if (getConfig().getBoolean("lore.show_plugin_name", true)) {
            lore.add(ChatColor.AQUA + "MoreMobHeads");
        }
        meta.setLore(lore);

        meta.setDisplayName(headName);
        item.setItemMeta(meta);
        return item;
    }

    private static Field fieldProfileItem;

    public static void setGameProfile(SkullMeta meta, GameProfile profile) {
        try {

            if (fieldProfileItem == null) {
                fieldProfileItem = meta.getClass().getDeclaredField("profile");
            }
            fieldProfileItem.setAccessible(true);
            fieldProfileItem.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
            stacktraceInfoStatic();
            e.printStackTrace();
        }
    }


    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public String isPlayerHead(String string) {
        try {
            playerFile = new File(getDataFolder() + "" + File.separatorChar + "player_heads.yml");//\
            if (!playerFile.exists()) {                                                                    // checks if the yaml does not exist
                return null;
            }
            int numOfCustomTrades = playerHeads.getInt("players.number", 0) + 1;
            if (debug) {
                logDebug("iPH string=" + string);
            }
            for (int randomPlayerHead = 1; randomPlayerHead < numOfCustomTrades; randomPlayerHead++) {
                ItemStack itemstack = playerHeads.getItemStack("players.player_" + randomPlayerHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = Utils.getItemMeta(itemstack);
                if (skullmeta != null) {
                    if (skullmeta.getOwner() != null) {
                        if (skullmeta.getOwner().contains(string)) {
                            return skullmeta.getDisplayName();
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        //playerHeads
        return null;
    }

    public String isBlockHead(String string) { // TODO: isBlockHead
        try {
            if (!(Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16)) {
                blockFile = new File(getDataFolder() + "" + File.separatorChar + "block_heads.yml");//\
                if (!blockFile.exists()) {                                                                    // checks if the yaml does not exist
                    return null;
                }
            }
            blockFile116 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
            if (Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16) {
                if (!blockFile116.exists()) {
                    return null;
                }
            }
            int numOfCustomTrades = blockHeads.getInt("blocks.number", 0) + 1;
            if (debug) {
                logDebug("iBH string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equals(string)) {
                        return itemstack.getItemMeta().getDisplayName();
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        //blockHeads
        return null;
    }

    public String isBlockHead2(String string) {
        try {
            if (!(Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16)) {                                                                // checks if the yaml does not exist
                return null;
            }
            blockFile1162 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16_2.yml");
            if (getMCVersion().startsWith("1.16") || getMCVersion().startsWith("1.17")) {
                if (!blockFile1162.exists()) {
                    return null;
                }

            }
            int numOfCustomTrades = blockHeads2.getInt("blocks.number", 0) + 1;
            if (debug) {
                logDebug("iBH2 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = Utils.getItemMeta(itemstack);
                if (skullmeta != null) {
                    if (skullmeta.getOwner() != null) {
                        if (skullmeta.getOwner().contains(string)) {
                            return skullmeta.getDisplayName();
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        //blockHeads
        return null;
    }

    public String isBlockHead3(String string) {
        try {
            if (!(Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16)) {                                                                // checks if the yaml does not exist
                return null;
            }
            blockFile117 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_17.yml");
            if (getMCVersion().startsWith("1.16") || getMCVersion().startsWith("1.17")) {
                if (!blockFile117.exists()) {
                    return null;
                }

            }
            int numOfCustomTrades = blockHeads3.getInt("blocks.number", 0) + 1;
            if (debug) {
                logDebug("iBH3 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = blockHeads3.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = Utils.getItemMeta(itemstack);
                if (skullmeta != null) {
                    if (skullmeta.getOwner() != null) {
                        if (skullmeta.getOwner().contains(string)) {
                            return skullmeta.getDisplayName();
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        //blockHeads
        return null;
    }


    public int isBlockHeadName(String string) { // TODO: isBlockHeadName
        if (debug) {
            logDebug("iBHN START");
        }
        try {
            double mcVer = Double.parseDouble(StrUtils.Left(getMCVersion(), 4));
            if (!(mcVer >= 1.16)) {
                blockFile = new File(getDataFolder() + "" + File.separatorChar + "block_heads.yml");//\
                if (!blockFile.exists()) {                                                                    // checks if the yaml does not exist
                    return -1;
                }
            } else if (mcVer == 1.16) {
                blockFile = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
            } else if (mcVer >= 1.17) {
                blockFile = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_17.yml");
            }

            if (debug) {
                logDebug("iBH blockFile=" + blockFile.toString());
            }
            if (blockHeads.getInt("blocks.number", 0) == 0) {
                try {
                    blockHeads.load(blockFile);
                } catch (IOException | InvalidConfigurationException e1) {
                    stacktraceInfo();
                    e1.printStackTrace();
                }
            }
            int numOfCustomTrades = blockHeads.getInt("blocks.number", 0) + 1;
            if (debug) {
                logDebug("iBH number=" + numOfCustomTrades);
            }
            if (debug) {
                logDebug("iBH string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equalsIgnoreCase(string)) {
                        if (debug) {
                            logDebug("iBHN END Sucess!");
                        }
                        return randomBlockHead; //itemstack.getItemMeta().getDisplayName();
                    }
                }
            }
        } catch (Exception e) {
            //stacktraceInfo();
            e.printStackTrace();
            if (debug) {
                logDebug("iBHN END Failure=Exception");
            }
            return -1;
        }
        //blockHeads
        if (debug) {
            logDebug("iBHN END Failure!");
        }
        return -1;
    }

    public int isBlockHeadName2(String string) {
        if (debug) {
            logDebug("iBHN2 START");
        }
        try {
            double mcVer = Double.parseDouble(StrUtils.Left(getMCVersion(), 4));
            if (!(mcVer >= 1.16)) {                                                                // checks if the yaml does not exist
                return -1;
            } else if (mcVer == 1.16) {
                blockFile1162 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16_2.yml");
            } else if (mcVer >= 1.17) {
                blockFile1162 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_17_2.yml");
            }

            if (getMCVersion().startsWith("1.16") || getMCVersion().startsWith("1.17")) {
                if (!blockFile1162.exists()) {
                    return -1;
                }

            }
            if (debug) {
                logDebug("iBH blockFile1162=" + blockFile1162.toString());
            }
            if (blockHeads2.getInt("blocks.number", 0) == 0) {
                try {
                    blockHeads2.load(blockFile1162);
                } catch (IOException | InvalidConfigurationException e1) {
                    stacktraceInfo();
                    e1.printStackTrace();
                }
            }
            int numOfCustomTrades = blockHeads2.getInt("blocks.number", 0) + 1;
            if (debug) {
                logDebug("iBH2 number=" + numOfCustomTrades);
            }
            if (debug) {
                logDebug("iBH2 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equalsIgnoreCase(string)) {
                        if (debug) {
                            logDebug("iBHN END Sucess!");
                        }
                        return randomBlockHead; //itemstack.getItemMeta().getDisplayName();
                    }
                }
            }
        } catch (Exception e) {
            if (debug) {
                logDebug("iBHN END Failure=Exception");
            }
            return -1;
        }
        //blockHeads
        if (debug) {
            logDebug("iBHN2 END Failure!");
        }
        return -1;
    }

    public int isBlockHeadName3(String string) {
        if (debug) {
            logDebug("iBHN3 START");
        }
        try {
            double mcVer = Double.parseDouble(StrUtils.Left(getMCVersion(), 4));
            if (!(mcVer >= 1.16)) {                                                                // checks if the yaml does not exist
                return -1;
            } else if (mcVer == 1.16) {
                return -1;
            } else if (mcVer >= 1.17) {
                blockFile117 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_17_3.yml");
            }

            if (getMCVersion().startsWith("1.16") || getMCVersion().startsWith("1.17")) {
                if (!blockFile117.exists()) {
                    return -1;
                }

            }
            if (debug) {
                logDebug("iBHN3 blockFile117=" + blockFile117.toString());
            }
            if (blockHeads3.getInt("blocks.number", 0) == 0) {
                try {
                    blockHeads3.load(blockFile117);
                } catch (IOException | InvalidConfigurationException e1) {
                    stacktraceInfo();
                    e1.printStackTrace();
                }
            }
            int numOfCustomTrades = blockHeads3.getInt("blocks.number", 0) + 1;
            if (debug) {
                logDebug("iBH3 number=" + numOfCustomTrades);
            }
            if (debug) {
                logDebug("iBH3 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = blockHeads3.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equalsIgnoreCase(string)) {
                        if (debug) {
                            logDebug("iBHN END Sucess!");
                        }
                        return randomBlockHead; //itemstack.getItemMeta().getDisplayName();

                    }
                }
            }
        } catch (Exception e) {
            if (debug) {
                logDebug("iBHN3 END Failure=Exception");
            }
            return -1;
        }
        //blockHeads
        if (debug) {
            logDebug("iBHN3 END Failure!");
        }
        return -1;
    }

    public int isBlockHeadName4(String string) {
        if (debug) {
            logDebug("iBHN4 START");
        }
        try {
            double mcVer = Double.parseDouble(StrUtils.Left(getMCVersion(), 4));
            if (!(mcVer >= 1.16)) {                                                                // checks if the yaml does not exist
                return -1;
            } else if (mcVer == 1.16) {
                return -1;
            } else if (mcVer == 1.19) {
                blockFile119 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_19.yml");
            }

            if (getMCVersion().startsWith("1.19")) {
                if (!blockFile119.exists()) {
                    return -1;
                }

            }
            if (debug) {
                logDebug("iBHN4 blockFile119=" + blockFile119.toString());
            }
            if (blockHeads4.getInt("blocks.number", 0) == 0) {
                try {
                    blockHeads4.load(blockFile119);
                } catch (IOException | InvalidConfigurationException e1) {
                    stacktraceInfo();
                    e1.printStackTrace();
                }
            }
            int numOfCustomTrades = blockHeads4.getInt("blocks.number", 0) + 1;
            if (debug) {
                logDebug("iBH4 number=" + numOfCustomTrades);
            }
            if (debug) {
                logDebug("iBH4 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = blockHeads4.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equalsIgnoreCase(string)) {
                        if (debug) {
                            logDebug("iBHN4 END Sucess!");
                        }
                        return randomBlockHead; //itemstack.getItemMeta().getDisplayName();
                    }
                }
            }
        } catch (Exception e) {
            if (debug) {
                logDebug("iBHN4 END Failure=Exception");
            }
            return -1;
        }
        //blockHeads
        if (debug) {
            logDebug("iBHN4 END Failure!");
        }
        return -1;
    }

    public int isBlockHeadName5(String string) {
        if (debug) {
            logDebug("iBHN5 START");
        }
        try {
            double mcVer = Double.parseDouble(StrUtils.Left(getMCVersion(), 4));
            if (!(mcVer >= 1.16)) {                                                                // checks if the yaml does not exist
                return -1;
            } else if (mcVer == 1.16) {
                return -1;
            } else if (mcVer == 1.19) {
                return -1;
            } else if (mcVer == 1.20) {
                blockFile120 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_20.yml");
            }

            if (getMCVersion().startsWith("1.20")) {
                if (!blockFile120.exists()) {
                    return -1;
                }

            }
            if (debug) {
                logDebug("iBHN5 blockFile120=" + blockFile120.toString());
            }
            if (blockHeads5.getInt("blocks.number", 0) == 0) {
                try {
                    blockHeads5.load(blockFile120);
                } catch (IOException | InvalidConfigurationException e1) {
                    stacktraceInfo();
                    e1.printStackTrace();
                }
            }
            int numOfCustomTrades = blockHeads5.getInt("blocks.number", 0) + 1;
            if (debug) {
                logDebug("iBH5 number=" + numOfCustomTrades);
            }
            if (debug) {
                logDebug("iBH5 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = blockHeads5.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equalsIgnoreCase(string)) {
                        if (debug) {
                            logDebug("iBHN5 END Sucess!");
                        }
                        return randomBlockHead; //itemstack.getItemMeta().getDisplayName();
                    }
                }
            }
        } catch (Exception e) {
            if (debug) {
                logDebug("iBHN5 END Failure=Exception");
            }
            return -1;
        }
        //blockHeads
        if (debug) {
            logDebug("iBHN5 END Failure!");
        }
        return -1;
    }

    public void stacktraceInfo() {
        logger.info(THIS_NAME + " v" + THIS_VERSION + " Include this with the stacktrace when reporting issues.");
        logger.info(THIS_NAME + " v" + THIS_VERSION + " This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")");
        logger.info(THIS_NAME + " v" + THIS_VERSION + " vardebug=" + debug + " debug=" + getConfig().get("debug", "error") + " in " + this.getDataFolder() + "/config.yml");
        logger.info(THIS_NAME + " v" + THIS_VERSION + " jarfile name=" + this.getFile().getAbsoluteFile());
        debug = true;
        logger.info(THIS_NAME + " v" + THIS_VERSION + " DEBUG has been set as true until plugin reload or /mmh td, or /mmh reload.");
    }

    public static void stacktraceInfoStatic() {
        logger.info(THIS_NAME + " v" + THIS_VERSION + " Include this with the stacktrace when reporting issues.");
        logger.info(THIS_NAME + " v" + THIS_VERSION + " This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")");
        logger.info(THIS_NAME + " v" + THIS_VERSION + " vardebug=" + debug);
        debug = true;
        logger.info(THIS_NAME + " v" + THIS_VERSION + " DEBUG has been set as true until plugin reload or /mmh td, or /mmh reload.");
    }

    // Persistent Heads
    private final NamespacedKey NAME_KEY = new NamespacedKey(this, "head_name");
    private final NamespacedKey LORE_KEY = new NamespacedKey(this, "head_lore");
    private final PersistentDataType<String, String[]> LORE_PDT = new JsonDataType<>(String[].class);

    // TODO: Persistent Heads
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        @Nonnull ItemStack headItem = event.getItemInHand();
        if (headItem.getType() != Material.PLAYER_HEAD) {
            return;
        }
        ItemMeta meta = headItem.getItemMeta();
        if (meta == null) {
            return;
        }
        @Nonnull String name = meta.getDisplayName();
        @Nullable List<String> lore = meta.getLore();
        @Nonnull Block block = event.getBlockPlaced();
        // NOTE: Not using snapshots is broken: https://github.com/PaperMC/Paper/issues/3913
        BlockStateSnapshotResult blockStateSnapshotResult = PaperLib.getBlockState(block, true);
        TileState skullState = (TileState) blockStateSnapshotResult.getState();
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
        if (lore != null) {
            skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0]));
        }
        if (blockStateSnapshotResult.isSnapshot()) {
            skullState.update();
        }
        String strLore = "no lore";
        if (lore != null) {
            strLore = lore.toString();
        }
        if (debug) {
            log(Level.INFO, "Player " + event.getPlayer().getName() + " placed a head named \"" + name + "\" with lore='" + strLore + "' at " + event.getBlockPlaced().getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockDropItemEvent(BlockDropItemEvent event) {
        @Nonnull BlockState blockState = event.getBlockState();
        Material blockType = blockState.getType();
        if ((blockType != Material.PLAYER_HEAD) && (blockType != Material.PLAYER_WALL_HEAD)) {
            return;
        }
        TileState skullState = (TileState) blockState;
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        @Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
        @Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
        if (name == null) {
            return;
        }
        for (Item item : event.getItems()) { // Ideally should only be one...
            @Nonnull ItemStack itemstack = item.getItemStack();
            if (itemstack.getType() == Material.PLAYER_HEAD) {
                @Nullable ItemMeta meta = itemstack.getItemMeta();
                if (meta == null) {
                    continue; // This shouldn't happen
                }
                meta.setDisplayName(name);
                if (lore != null) {
                    meta.setLore(Arrays.asList(lore));
                }
                itemstack.setItemMeta(meta);
            }
        }
        if (debug) {
            log(Level.INFO, "BDIE - Persistent head completed.");
        }
    }

    /**
     * Prevents player from removing player-head NBT by water logging them
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        handleBlock(event.getBlock(), event, false);
    }

    /**
     * Prevents player from removing player-head NBT using running water
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLiquidFlow(BlockFromToEvent event) {
        handleBlock(event.getToBlock(), event, true);
    }

    /*
     * Prevents explosion from removing player-head NBT using an explosion
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplosion(BlockExplodeEvent event) {
        handleExplosionEvent(event.blockList(), event.getYield());
    }

    /*
     * Prevents entity from removing player-head NBT using an explosion
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplosion(EntityExplodeEvent event) {
        handleExplosionEvent(event.blockList(), event.getYield());
    }

    /*
     * Prevents piston extending from removing NBT data.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPistonExtendEvent(BlockPistonExtendEvent event) {
        if (!config.getBoolean("event.piston_extend", true)) {
            return;
        }
        List<Block> blocks = event.getBlocks();
        Iterator<Block> iter = blocks.iterator();
        try {
            while (iter.hasNext()) {
                Block block = iter.next();
                if (block.getState() instanceof Skull) { //if (block.getState() instanceof Skull && random.nextFloat() <= explosionYield)
                    handleBlock(block, null, false);
                    iter.remove();
                }
            }
        } catch (Exception ignored) {

        }
    }

    @SuppressWarnings("unused")
    private void handleExplosionEvent(@Nonnull final List<Block> blocksExploded, final float explosionYield) {
        final Random random = ThreadLocalRandom.current();
        Iterator<Block> iter = blocksExploded.iterator();
        try {
            while (iter.hasNext()) {
                Block block = iter.next();
                if (block.getState() instanceof Skull) { //if (block.getState() instanceof Skull && random.nextFloat() <= explosionYield)
                    handleBlock(block, null, false);
                    iter.remove();
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void handleBlock(Block block, Cancellable event, boolean cancelEvent) {
        @Nonnull BlockState blockState = block.getState();
        if ((blockState.getType() != Material.PLAYER_HEAD) && (blockState.getType() != Material.PLAYER_WALL_HEAD)) {
            return;
        }
        Skull skullState = (Skull) blockState;
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        @Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
        @Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
        if (name == null) {
            return;
        }
        @Nonnull Optional<ItemStack> skullStack = block.getDrops().stream().filter(is -> is.getType() == Material.PLAYER_HEAD).findAny();
        if (skullStack.isPresent()) {
            if (updateDrop(block, name, lore, skullStack.get())) {
                return; // This shouldn't happen
            }
            if (cancelEvent) {
                event.setCancelled(true);
            }
        }

        BlockState blockState1 = block.getWorld().getBlockAt(block.getLocation()).getState();
        blockState1.update(true, true);
        if (debug) {
            log(Level.INFO, "HB - Persistent head completed.");
        }
    }

    @SuppressWarnings("unused")
    private void handleEvent(Supplier<Block> blockSupplier, Cancellable event, boolean cancelEvent) {
        Block block = blockSupplier.get();
        @Nonnull BlockState blockState = block.getState();
        if ((blockState.getType() != Material.PLAYER_HEAD) && (blockState.getType() != Material.PLAYER_WALL_HEAD)) {
            return;
        }
        Skull skullState = (Skull) blockState;
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        @Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
        @Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
        if (name == null) {
            return;
        }
        @Nonnull Optional<ItemStack> skullStack = block.getDrops().stream().filter(is -> is.getType() == Material.PLAYER_HEAD).findAny();
        if (skullStack.isPresent()) {
            if (updateDrop(block, name, lore, skullStack.get())) {
                return; // This shouldn't happen
            }
            if (cancelEvent) {
                event.setCancelled(true);
            }
        }

        BlockState blockState1 = block.getWorld().getBlockAt(block.getLocation()).getState();
        blockState1.update(true, true);
        if (debug) {
            log(Level.INFO, "HE - Persistent head completed.");
        }
    }

    private boolean updateDrop(Block block, @Nullable String name, @Nullable String[] lore, @Nonnull ItemStack itemstack) {
        @Nullable ItemMeta meta = itemstack.getItemMeta();
        if (meta == null) {
            return true;
        }
        meta.setDisplayName(name);
        if (lore != null) {
            meta.setLore(Arrays.asList(lore));
        }
        itemstack.setItemMeta(meta);

        block.getWorld().dropItemNaturally(block.getLocation(), itemstack);
        block.getDrops().clear();
        block.setType(Material.AIR);
        if (debug) {
            log(Level.INFO, "UD - Persistent head completed.");
        }
        return false;
    }

    @SuppressWarnings("unused")
    public ItemStack fixHeadStack(ItemStack offHand, ItemStack mainHand) {
        NBTItem nbti = new NBTItem(offHand);
        Set<String> SkullKeys = nbti.getKeys();
        int damage = nbti.getInteger("Damage");
        NBTCompound display = nbti.getCompound("display");
        NBTCompound SkullOwner = nbti.getCompound("SkullOwner");
        if (debug) {
            logDebug("FHS Offhand damage=" + damage);
        }
        if (debug) {
            logDebug("FHS Offhand display=" + display.toString());
        }
        if (debug) {
            logDebug("FHS Offhand SkullOwner=" + SkullOwner.toString());
        }

        NBTItem nbti2 = new NBTItem(mainHand);
        Set<String> SkullKeys2 = nbti2.getKeys();
        int damage2 = nbti2.getInteger("Damage");
        NBTCompound display2 = nbti2.getCompound("display");
        NBTCompound SkullOwner2 = nbti2.getCompound("SkullOwner");
        if (debug) {
            logDebug("FHS Mainhand damage=" + damage2);
        }
        if (debug) {
            logDebug("FHS Mainhand display=" + display2.toString());
        }
        if (debug) {
            logDebug("FHS Mainhand SkullOwner=" + SkullOwner2.toString());
        }

        if (display.equals(display2) && SkullOwner.equals(SkullOwner2) && (damage != damage2)) {
            ItemStack is = new ItemStack(offHand);
            is.setAmount(mainHand.getAmount());
            if (debug) {
                logDebug("FHS d=d2, so=so2, d!=D2 - return offhand");
            }
            return is;
        } else if (!display.equals(display2) && SkullOwner.equals(SkullOwner2)) {
            ItemStack is = new ItemStack(offHand);
            is.setAmount(mainHand.getAmount());
            if (debug) {
                logDebug("FHS d!=d2, so=so2, d ignored - return offhand");
            }
            return is;
        } else if (display.equals(display2) && SkullOwner.equals(SkullOwner2) && (damage == damage2)) {
            if (debug) {
                logDebug("FHS d=d2, so=so2, d=d2 - return mainhand");
            }
            return mainHand;
        }
        return null;
    }

    public ItemStack fixHeadNBT(String textureValue, String displayName, ArrayList<String> lore) {
        //String textureValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY1MjQxNjZmN2NlODhhNTM3MTU4NzY2YTFjNTExZTMyMmE5M2E1ZTExZGJmMzBmYTZlODVlNzhkYTg2MWQ4In19fQ=="; // Pulled from the head link, scroll to the bottom and the "Other Value" field has this texture id.

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1); // Creating the ItemStack, your input may vary.
        NBTItem nbti = new NBTItem(head); // Creating the wrapper.

        NBTCompound disp = nbti.addCompound("display");
        disp.setString("Name", displayName); // Setting the name of the Item
        if (lore.isEmpty()) {
            if (getConfig().getBoolean("lore.show_plugin_name", true)) {
                lore.add(ChatColor.AQUA + "MoreMobHeads");
            }
        }
        if (!lore.isEmpty()) {
            NBTList<String> l = disp.getStringList("Lore");
            l.addAll(lore); // Adding a bit of lore.
        }

        NBTCompound skull = nbti.addCompound("SkullOwner"); // Getting the compound, that way we can set the skin information
        skull.setString("Name", displayName); // Owner's name
        //skull.setString("Id", uuid);
        // The UUID, note that skulls with the same UUID but different textures will misbehave and only one texture will load
        // (They'll share it), if skulls have different UUIDs and same textures they won't stack. See UUID.randomUUID();

        NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
        texture.setString("Value", textureValue);

        head = nbti.getItem(); // Refresh the ItemStack
        return head;
    }

    public boolean chance25oftrue() {
        //For 25% chance of true
        return random.nextInt(4) == 0;
    }

    public void consoleLog(String string) {
        if (!silent_console) {
            loading(string);
        }
    }

    public static String get(String key) {
        return Translator.get(key, null);
    }
    public static String get(String key, String defaultValue) {
        return Translator.get(key, defaultValue);
    }

    public void dumpConfig(FileConfiguration config) {
        for (String key : config.getKeys(true)) {
            Object value = config.get(key);
            if ((value != null) && value.getClass().isArray()) {
                value = Arrays.asList((Object[]) value);
            }
            log(key + "=" + value);
        }
    }

    public String getNamedTropicalFishName(Pattern pattern, DyeColor color1, DyeColor color2) {
        String key = pattern.name() + "-" + color1.name() + "-" + color2.name();
        log("key=" + key);
        log("namedTropicalFish=" + namedTropicalFish.isEmpty());
        return namedTropicalFish.getOrDefault(key, "TROPICAL_FISH");
    }

}
