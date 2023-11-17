package com.github.joelgodofwar.mmh;

import com.github.joelgodofwar.mmh.handlers.*;
import com.github.joelgodofwar.mmh.i18n.Translator;
import com.github.joelgodofwar.mmh.util.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import de.tr7zw.nbtapi.NBTListCompound;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoreMobHeads extends JavaPlugin {

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
    public YmlConfiguration beheadingMessages = new YmlConfiguration();
    public YmlConfiguration config = new YmlConfiguration();
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
    EventHandlerCommon commonHandler;
    public final HashMap<String, String> namedTropicalFish = new HashMap<>() {{
        put("STRIPEY-ORANGE-GRAY", "ANEMONE");
        put("FLOPPER-GRAY-GRAY", "BLACK_TANG");
        put("FLOPPER-GRAY-BLUE", "BLUE_TANG");
        put("CLAYFISH-WHITE-GRAY", "BUTTERFLYFISH");
        put("SUNSTREAK-BLUE-GRAY", "CICHLID");
        put("KOB-ORANGE-WHITE", "CLOWNFISH");
        put("SPOTTY-PINK-LIGHT_BLUE", "COTTON_CANDY_BETTA");
        put("BLOCKFISH-PURPLE-YELLOW", "DOTTYBACK");
        put("CLAYFISH-WHITE-RED", "EMPEROR_RED_SNAPPER");
        put("SPOTTY-WHITE-YELLOW", "GOATFISH");
        put("GLITTER-WHITE-GRAY", "MOORISH_IDOL");
        put("CLAYFISH-WHITE-ORANGE", "ORNATE_BUTTERFLYFISH");
        put("DASHER-CYAN-PINK", "PARROTFISH");
        put("BRINELY-LIME-LIGHT_BLUE", "QUEEN_ANGELFISH");
        put("BETTY-RED-WHITE", "RED_CICHLID");
        put("SNOOPER-GRAY-RED", "RED_LIPPED_BLENNY");
        put("BLOCKFISH-RED-WHITE", "RED_SNAPPER");
        put("KOB-RED-WHITE", "TOMATO_CLOWNFISH");
        put("FLOPPER-WHITE-YELLOW", "THREADFIN");
        put("SUNSTREAK-GRAY-WHITE", "TRIGGERFISH");
        put("DASHER-CYAN-YELLOW", "YELLOWTAIL_PARROTFISH");
        put("FLOPPER-YELLOW-YELLOW", "YELLOW_TANG");
    }};
    public final Map<Player, Random> chanceRandoms = new HashMap<>();
    Reloadable handler = null;
    @Override // TODO: onEnable
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        Heads.onEnable(this);

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

        Utils.createFileIfNotExists(debugFile = new File(getDataFolder(), "logs/mmh_debug.log"));

        /* DEV check **/
        File jarfile = this.getFile().getAbsoluteFile();
        if (jarfile.toString().contains("-DEV")) {
            debug = true;
            logDebug("Jar file contains -DEV, debug set to true");
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

        /* Check for config */
        try {
            if (!getDataFolder().exists()) {
                log(Level.INFO, "Data Folder doesn't exist");
                log(Level.INFO, "Creating Data Folder");
                if (getDataFolder().mkdirs()) {
                    log(Level.INFO, "Data Folder Created at " + getDataFolder());
                }
            }
            File file = new File(getDataFolder(), "config.yml");
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
        commonHandler = new EventHandlerCommon(this);
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

        Networks.checkUpdate(this);

        consoleInfo("Enabled - Loading took " + Utils.timeToString(startTime));
        if (getConfig().getBoolean("metrics", false)) Networks.startMetrics(this);
    }

    @Override
    public void onDisable() {
        consoleInfo("Disabled");
    }

    public EventHandlerCommon getCommonHandler() {
        return commonHandler;
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

    public ItemStack makeSkull(String textureCode, String headName, Player killer) {// TODO: makeSkull
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        if (textureCode == null) {
            return item;
        }
        SkullMeta meta = Utils.getItemMeta(item);

        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(textureCode.getBytes()), textureCode);
        profile.getProperties().put("textures", new Property("textures", textureCode));
        profile.getProperties().put("display", new Property("Name", headName));

        Utils.setGameProfile(meta, profile);
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
        Utils.setGameProfile(meta, profile);
        ArrayList<String> lore = new ArrayList<>();

        if (getConfig().getBoolean("lore.show_plugin_name", true)) {
            lore.add(ChatColor.AQUA + "MoreMobHeads");
        }
        meta.setLore(lore);

        meta.setDisplayName(headName);
        item.setItemMeta(meta);
        return item;
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
