package com.github.joelgodofwar.mmh.handlers;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.commands.MMHCommand;
import com.github.joelgodofwar.mmh.enums.*;
import com.github.joelgodofwar.mmh.util.StrUtils;
import com.github.joelgodofwar.mmh.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.StriderTemperatureChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import static com.github.joelgodofwar.mmh.MoreMobHeads.debug;

/**
 * 1.8		1_8_R1		1.8.3	1_8_R2
 * 1.8.8 	1_8_R3
 * 1.9		1_9_R1		1.9.4	1_9_R2
 * 1.10	1_10_R1
 * 1.11	1_11_R1
 * 1.12	1_12_R1
 * 1.13	1_13_R1		1.13.1	1_13_R2
 * 1.14	1_14_R1
 * 1.15	1_15_R1
 * 1.16.1	1_16_R1		1.16.2	1_16_R2
 * 1.17	1_17_R1
 */

public class EventHandler_1_16 implements Listener, Reloadable {
    /**
     * Variables
     */
    MoreMobHeads mmh;
    double defpercent = 0.013;
    String world_whitelist;
    String world_blacklist;
    String mob_whitelist;
    String mob_blacklist;
    File blockFile;
    File blockFile116;
    File blockFile1162;
    public FileConfiguration blockHeads = new YamlConfiguration();
    public FileConfiguration blockHeads2 = new YamlConfiguration();
    public FileConfiguration blockHeads3 = new YamlConfiguration();

    //List<MerchantRecipe> cached_recipes = new ArrayList<MerchantRecipe>();
    List<MerchantRecipe> playerhead_recipes = new ArrayList<>();
    List<MerchantRecipe> blockhead_recipes = new ArrayList<>();
    List<MerchantRecipe> custometrade_recipes = new ArrayList<>();
    int BHNum, BHNum2;

    MMHCommand command;

    public EventHandler_1_16(final MoreMobHeads plugin) {
        /* Set variables */
        mmh = plugin;
        mmh.log(Level.INFO, "Loading 1.15-16 EventHandler...");
        command = new MMHCommand(plugin);
        onReload();
    }

    @Override
    public void onReload() {
        long startTime = System.currentTimeMillis();
        world_whitelist = mmh.world_whitelist;
        world_blacklist = mmh.mob_blacklist;
        mob_whitelist = mmh.mob_whitelist;
        mob_blacklist = mmh.mob_blacklist;
        blockFile116 = new File(mmh.getDataFolder(), "block_heads_1_16.yml");
        blockFile1162 = new File(mmh.getDataFolder(), "block_heads_1_16_2.yml");
        if (mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)) {
            if (!mmh.getMCVersion().startsWith("1.16") && !mmh.getMCVersion().startsWith("1.17")) {
                blockFile = new File(mmh.getDataFolder(), "block_heads.yml");
                if (debug) {
                    logDebug("block_heads=" + blockFile.getPath());
                }
                if (!blockFile.exists()) {                                                                    // checks if the yaml does not exist
                    mmh.saveResource("block_heads.yml", true);
                    log(Level.INFO, "block_heads.yml not found! Creating in " + mmh.getDataFolder() + "");
                    //ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
                }
            }
            if (Double.parseDouble(StrUtils.Left(mmh.getMCVersion(), 4)) == 1.16) {
                if (debug) {
                    logDebug("block_heads_1_16=" + blockFile116.getPath());
                }
                if (debug) {
                    logDebug("block_heads_1_16_2=" + blockFile1162.getPath());
                }
                if (!blockFile116.exists()) {
                    mmh.saveResource("block_heads_1_16.yml", true);
                    log(Level.INFO, "block_heads_1_16.yml not found! Creating in " + mmh.getDataFolder() + "");
                }
                if (!blockFile1162.exists()) {
                    mmh.saveResource("block_heads_1_16_2.yml", true);
                    log(Level.INFO, "block_heads_1_16_2.yml not found! Creating in " + mmh.getDataFolder() + "");
                }
                blockFile = new File(mmh.getDataFolder(), "block_heads_1_16.yml");
                log(Level.INFO, "Loading block_heads_1_16 files...");

            } else {
                log(Level.INFO, "Loading block_heads file...");
            }

            blockHeads = new YamlConfiguration();
            try {
                blockHeads.load(blockFile);
                log(Level.INFO, "Loading " + blockFile + "...");
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }
            if (blockHeads.get("blocks.block_79.price_2.amount", null) != null) {
                log(Level.INFO, "block_heads files outdated, updating...");
                blockHeads.set("blocks.block_79.price_2.amount", "");
                try {
                    blockHeads.save(blockFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            blockHeads2 = new YamlConfiguration();
            try {
                blockHeads2.load(blockFile1162);
                log(Level.INFO, "Loading " + blockFile1162 + "...");
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }

            boolean showlore = mmh.getConfig().getBoolean("lore.show_plugin_name", true);
            ArrayList<String> headlore = new ArrayList();
            headlore.add(ChatColor.AQUA + "" + mmh.getName());

            mmh.log(Level.INFO, "Loading PlayerHead Recipes...");
            for (int i = 1; i < mmh.playerHeads.getInt("players.number") + 1; i++) {
                ItemStack price1 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_1", new ItemStack(Material.AIR));
                ItemStack price2 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_2", new ItemStack(Material.AIR));
                ItemStack itemstack = mmh.playerHeads.getItemStack("players.player_" + i + ".itemstack", new ItemStack(Material.AIR));
                if (showlore) {
                    SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
                    meta.setLore(headlore);
                    itemstack.setItemMeta(meta);
                    itemstack.setItemMeta(meta);
                }
                MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.playerHeads.getInt("players.player_" + i + ".quantity", 3));
                recipe.addIngredient(price1);
                recipe.addIngredient(price2);
                playerhead_recipes.add(recipe);
            }
            mmh.log(Level.INFO, playerhead_recipes.size() + " PlayerHead Recipes ADDED...");
            mmh.log(Level.INFO, "Loading BlockHead Recipes...");
            BHNum = blockHeads.getInt("blocks.number");
            // BlockHeads
            mmh.log(Level.INFO, "BlockHeads=" + BHNum);
            for (int i = 1; i < BHNum + 1; i++) {
                ItemStack price1 = blockHeads.getItemStack("blocks.block_" + i + ".price_1", new ItemStack(Material.AIR));
                ItemStack price2 = blockHeads.getItemStack("blocks.block_" + i + ".price_2", new ItemStack(Material.AIR));
                ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + i + ".itemstack", new ItemStack(Material.AIR));
                if (showlore) {
                    SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
                    meta.setLore(headlore);
                    itemstack.setItemMeta(meta);
                    itemstack.setItemMeta(meta);
                }
                MerchantRecipe recipe = new MerchantRecipe(itemstack, blockHeads.getInt("blocks.block_" + i + ".quantity", 1));
                recipe.setExperienceReward(true);
                recipe.addIngredient(price1);
                recipe.addIngredient(price2);
                blockhead_recipes.add(recipe);
            }
            BHNum2 = blockHeads2.getInt("blocks.number");
            // blockHeads 2
            mmh.log(Level.INFO, "BlockHeads2=" + BHNum2);
            for (int i = 1; i < BHNum2 + 1; i++) {
                ItemStack price1 = blockHeads2.getItemStack("blocks.block_" + i + ".price_1", new ItemStack(Material.AIR));
                ItemStack price2 = blockHeads2.getItemStack("blocks.block_" + i + ".price_2", new ItemStack(Material.AIR));
                ItemStack itemstack = blockHeads2.getItemStack("blocks.block_" + i + ".itemstack", new ItemStack(Material.AIR));
                if (showlore) {
                    SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
                    meta.setLore(headlore);
                    itemstack.setItemMeta(meta);
                    itemstack.setItemMeta(meta);
                }
                MerchantRecipe recipe = new MerchantRecipe(itemstack, blockHeads2.getInt("blocks.block_" + i + ".quantity", 1));
                recipe.setExperienceReward(true);
                recipe.addIngredient(price1);
                recipe.addIngredient(price2);
                blockhead_recipes.add(recipe);
            }

            mmh.log(Level.INFO, blockhead_recipes.size() + " BlockHead Recipes ADDED...");
            mmh.log(Level.INFO, "Loading CustomTrades Recipes...");
            for (int i = 1; i < mmh.traderCustom.getInt("custom_trades.number") + 1; i++) {
                ItemStack price1 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_1", new ItemStack(Material.AIR));
                ItemStack price2 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_2", new ItemStack(Material.AIR));
                ItemStack itemstack = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".itemstack", new ItemStack(Material.AIR));
                MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.traderCustom.getInt("custom_trades.trade_" + i + ".quantity", 1));
                recipe.setExperienceReward(true);
                recipe.addIngredient(price1);
                recipe.addIngredient(price2);
                custometrade_recipes.add(recipe);
            }
            mmh.log(Level.INFO, custometrade_recipes.size() + " CustomTrades Recipes ADDED...");
            mmh.log(Level.INFO, "EventHandler_1_16_R2 took " + (System.currentTimeMillis() - startTime) + "ms to load");
        }
        command.blockHeads = blockHeads;
        command.blockHeads2 = blockHeads2;
        command.blockHeads3 = blockHeads3;
    }

    /**
     * Events go here
     */
    @SuppressWarnings({"deprecation", "unchecked", "unused", "rawtypes"})
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {// TODO: EnityDeathEvent
        LivingEntity entity = event.getEntity();
        World world = event.getEntity().getWorld();
        List<ItemStack> Drops = event.getDrops();
        world_whitelist = mmh.getConfig().getString("world.whitelist", "");
        world_blacklist = mmh.getConfig().getString("world.blacklist", "");
        mob_whitelist = mmh.getConfig().getString("mob.whitelist", "");
        mob_blacklist = mmh.getConfig().getString("mob.blacklist", "");
        if (debug) {
            mmh.logDebug("EDE - world_whitelist=" + world_whitelist);
        }
        if (debug) {
            mmh.logDebug("EDE - world_blacklist=" + world_blacklist);
        }
        if (debug) {
            mmh.logDebug("EDE - mob_whitelist=" + mob_whitelist);
        }
        if (debug) {
            mmh.logDebug("EDE - mob_blacklist=" + mob_blacklist);
        }

        if (world_whitelist != null && !world_whitelist.isEmpty() && world_blacklist != null && !world_blacklist.isEmpty()) {
            if (!StrUtils.stringContains(world_whitelist, world.getName()) && StrUtils.stringContains(world_blacklist, world.getName())) {
                if (debug) {
                    mmh.logDebug("EDE - World - On blacklist and Not on whitelist.");
                }
                return;
            } else if (!StrUtils.stringContains(world_whitelist, world.getName()) && !StrUtils.stringContains(world_blacklist, world.getName())) {
                if (debug) {
                    mmh.logDebug("EDE - World - Not on whitelist.");
                }
                return;
            } else if (!StrUtils.stringContains(world_whitelist, world.getName())) {

            }
        } else if (world_whitelist != null && !world_whitelist.isEmpty()) {
            if (!StrUtils.stringContains(world_whitelist, world.getName())) {
                if (debug) {
                    mmh.logDebug("EDE - World - Not on whitelist.");
                }
                return;
            }
        } else if (world_blacklist != null && !world_blacklist.isEmpty()) {
            if (StrUtils.stringContains(world_blacklist, world.getName())) {
                if (debug) {
                    mmh.logDebug("EDE - World - On blacklist.");
                }
                return;
            }
        }
        if (entity instanceof Player) {
            if (debug) {
                mmh.logDebug("EDE Entity is Player line:877");
            }

            if (entity.getKiller() instanceof Player) {
                if (entity.getKiller().hasPermission("moremobheads.players")) {
                    if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.player", 0.50))) {
                        //Player daKiller = entity.getKiller();
                        if (debug) {
                            mmh.logDebug("EDE Killer is Player line:1073");
                        }
                        ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
                        SkullMeta meta = (SkullMeta) helmet.getItemMeta();
                        meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(entity.getUniqueId()));
                        meta.setDisplayName(((Player) entity).getDisplayName() + "'s Head");
                        ArrayList<String> lore = new ArrayList();
                        if (mmh.getConfig().getBoolean("lore.show_killer", true)) {
                            lore.add(ChatColor.RESET + "Killed by " + ChatColor.RESET + ChatColor.YELLOW + entity.getKiller().getDisplayName());
                        }
                        if (mmh.getConfig().getBoolean("lore.show_plugin_name", true)) {
                            lore.add(ChatColor.AQUA + "" + mmh.getName());
                        }
                        meta.setLore(lore);
                        helmet.setItemMeta(meta);//																	 e2d4c388-42d5-4a96-b4c9-623df7f5e026
                        helmet.setItemMeta(meta);

                        //entity.getWorld().dropItemNaturally(entity.getLocation(), helmet);
                        Drops.add(helmet);
                        if (debug) {
                            mmh.logDebug("EDE " + ((Player) entity).getDisplayName() + " Player Head Dropped");
                        }
                    }
                    return;
                }
            }
        } else if (event.getEntity() instanceof LivingEntity) {
            if (entity.getKiller() instanceof Player) {
                String name = event.getEntityType().toString().replace(" ", "_");
                if (debug) {
                    mmh.logDebug("EDE name=" + name);
                }
                String isNametag = null;
                @Nonnull
                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                isNametag = entity.getPersistentDataContainer().get(mmh.NAMETAG_KEY, PersistentDataType.STRING);//.getScoreboardTags();
                if (debug && isNametag != null) {
                    mmh.logDebug("EDE isNametag=" + isNametag);
                }

                if (entity.getKiller().hasPermission("moremobheads.mobs")) {
                    if (entity.getKiller().hasPermission("moremobheads.nametag") && isNametag != null) {
                        if (entity.getCustomName() != null && !(entity.getCustomName().contains("jeb_"))
                                && !(entity.getCustomName().contains("Toast"))) {
                            if (debug) {
                                mmh.logDebug("EDE customname=" + entity.getCustomName());
                            }
                            if (entity instanceof Skeleton || entity instanceof Zombie || entity instanceof PigZombie) {
                                if (mmh.getServer().getPluginManager().getPlugin("SilenceMobs") != null) {
                                    if (entity.getCustomName().toLowerCase().contains("silenceme") || entity.getCustomName().toLowerCase().contains("silence me")) {
                                        return;
                                    }
                                }
                                boolean enforcewhitelist = mmh.getConfig().getBoolean("whitelist.enforce", false);
                                boolean enforceblacklist = mmh.getConfig().getBoolean("blacklist.enforce", false);
                                boolean onwhitelist = mmh.getConfig().getString("whitelist.player_head_whitelist", "").toLowerCase().contains(entity.getCustomName().toLowerCase());
                                boolean onblacklist = mmh.getConfig().getString("blacklist.player_head_blacklist", "").toLowerCase().contains(entity.getCustomName().toLowerCase());
                                if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("named_mob", 0.10))) {
                                    if (enforcewhitelist && enforceblacklist) {
                                        if (onwhitelist && !(onblacklist)) {
                                            Drops.add(mmh.getCommonHandler().dropMobHead(entity, entity.getCustomName(), entity.getKiller()));
                                            if (debug) {
                                                mmh.logDebug("EDE " + entity.getCustomName() + " Head Dropped");
                                            }
                                        }
                                    } else if (enforcewhitelist && !enforceblacklist) {
                                        if (onwhitelist) {
                                            Drops.add(mmh.getCommonHandler().dropMobHead(entity, entity.getCustomName(), entity.getKiller()));
                                            if (debug) {
                                                mmh.logDebug("EDE " + entity.getCustomName() + " Head Dropped");
                                            }
                                        }
                                    } else if (!enforcewhitelist && enforceblacklist) {
                                        if (!onblacklist) {
                                            Drops.add(mmh.getCommonHandler().dropMobHead(entity, entity.getCustomName(), entity.getKiller()));
                                            if (debug) {
                                                mmh.logDebug("EDE " + entity.getCustomName() + " Head Dropped");
                                            }
                                        }
                                    } else {
                                        Drops.add(mmh.getCommonHandler().dropMobHead(entity, entity.getCustomName(), entity.getKiller()));
                                        if (debug) {
                                            mmh.logDebug("EDE " + entity.getCustomName() + " Head Dropped");
                                        }
                                    }
                                }
                            }
                            return;
                        }
                    }
                    //String name = event.getEntity().getName().toUpperCase().replace(" ", "_");

                    if (mob_whitelist != null && !mob_whitelist.isEmpty() && mob_blacklist != null && !mob_blacklist.isEmpty()) {
                        if (!StrUtils.stringContains(mob_whitelist, name)) {//mob_whitelist.contains(name)
                            mmh.log(Level.INFO, "EDE - Mob - Not on whitelist. Mob=" + name);
                            return;
                        }
                    } else if (mob_whitelist != null && !mob_whitelist.isEmpty()) {
                        if (!StrUtils.stringContains(mob_whitelist, name) && StrUtils.stringContains(mob_blacklist, name)) {//mob_whitelist.contains(name)
                            mmh.log(Level.INFO, "EDE - Mob - Not on whitelist - Is on blacklist. Mob=" + name);
                            return;
                        }
                    } else if (mob_blacklist != null && !mob_blacklist.isEmpty()) {
                        if (StrUtils.stringContains(mob_blacklist, name)) {
                            mmh.log(Level.INFO, "EDE - Mob - On blacklist. Mob=" + name);
                            return;
                        }
                    }

                    switch (name) {
                        case "CREEPER":
                            Creeper creeper = (Creeper) event.getEntity();
                            double cchance = mmh.chanceConfig.getDouble("chance_percent.creeper", defpercent);
                            if (creeper.isPowered()) {
                                name = "CREEPER_CHARGED";
                                cchance = 1.00;
                            }
                            if (mmh.getCommonHandler().DropIt(event, cchance)) {
                                if (mmh.getConfig().getBoolean("vanilla_heads.creeper", false) && !name.equals("CREEPER_CHARGED")) {
                                    Drops.add(new ItemStack(Material.CREEPER_HEAD));
                                } else { // mmh.langName
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
                                } // MobHeads.valueOf(name).getName() + " Head"
                                if (debug) {
                                    mmh.logDebug("EDE Creeper vanilla=" + mmh.getConfig().getBoolean("vanilla_heads.creeper", false));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Creeper Head Dropped");
                                }
                            }
                            break;
                        case "ZOMBIE":
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.zombie", defpercent))) {
                                if (mmh.getConfig().getBoolean("vanilla_heads.zombie", false)) {
                                    Drops.add(new ItemStack(Material.ZOMBIE_HEAD));
                                } else {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Zombie vanilla=" + mmh.getConfig().getBoolean("vanilla_heads.zombie", false));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Zombie Head Dropped");
                                }
                            }
                            break;
                        case "SKELETON":
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.skeleton", defpercent))) {
                                if (mmh.getConfig().getBoolean("vanilla_heads.skeleton", false)) {
                                    Drops.add(new ItemStack(Material.SKELETON_SKULL));
                                } else {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Skeleton vanilla=" + mmh.getConfig().getBoolean("vanilla_heads.skeleton", false));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Skeleton Head Dropped");
                                }
                            }
                            break;
                        case "WITHER_SKELETON":
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.wither_skeleton", defpercent))) {
                                if (mmh.getConfig().getBoolean("vanilla_heads.wither_skeleton", false)) {
                                    Drops.add(new ItemStack(Material.WITHER_SKELETON_SKULL));
                                } else {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Wither Skeleton Head Dropped");
                                }
                            }
                            break;
                        case "ENDER_DRAGON":
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.ender_dragon", defpercent))) {
                                if (mmh.getConfig().getBoolean("vanilla_heads.ender_dragon", false)) {
                                    Drops.add(new ItemStack(Material.DRAGON_HEAD));
                                } else {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Ender Dragon Head Dropped");
                                }
                            }
                            break;
                        case "WITHER":
                            int random = Utils.randomBetween(1, 4);
                            if (debug) {
                                mmh.logDebug("EDE Wither random=" + random + "");
                            }
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.wither", defpercent))) {
                                Drops.add(mmh.makeSkull(MobHeads.valueOf(name + "_" + random).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + random, MobHeads.valueOf(name + "_" + random).getName() + " Head"), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Wither_" + random + " Head Dropped");
                                }
                            }
                            break;
                        case "WOLF":
                            Wolf wolf = (Wolf) event.getEntity();
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))) {
                                if (wolf.isAngry()) {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf(name + "_ANGRY").getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase() + "_angry", MobHeads.valueOf(name + "_ANGRY").getName() + " Head"), entity.getKiller()));
                                    if (debug) {
                                        mmh.logDebug("EDE Angry Wolf Head Dropped");
                                    }
                                } else {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase(), event.getEntity().getName() + " Head"), entity.getKiller()));
                                    if (debug) {
                                        mmh.logDebug("EDE Wolf Head Dropped");
                                    }
                                }
                            }
                            break;
                        case "FOX":
                            Fox dafox = (Fox) entity;
                            String dafoxtype = dafox.getFoxType().toString();
                            if (debug) {
                                mmh.logDebug("EDE dafoxtype=" + dafoxtype);
                            }
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.fox." + dafoxtype.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(MobHeads.valueOf(name + "_" + dafoxtype).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + dafoxtype.toLowerCase(), MobHeads.valueOf(name + "_" + dafoxtype).getName() + " Head"), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Fox Head Dropped");
                                }
                            }

                            break;
                        case "CAT":
                            Cat dacat = (Cat) entity;
                            String dacattype = dacat.getCatType().toString();
                            if (debug) {
                                mmh.logDebug("entity cat=" + dacat.getCatType());
                            }
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.cat." + dacattype.toLowerCase(), defpercent))) {
                                Drops.add(
                                        mmh.makeSkull(CatHeads.valueOf(dacattype).getTexture(),
                                                mmh.mobNames.getString(name.toLowerCase() + "." + dacattype.toLowerCase(), CatHeads.valueOf(dacattype).getName() + " Head"), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Cat Head Dropped");
                                }
                            }
                            break;
                        case "OCELOT":
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                        mmh.mobNames.getString(MobHeads.valueOf(name).getNameString(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE " + name + " Head Dropped");
                                }
                            }
                            if (debug) {
                                mmh.logDebug("EDE " + MobHeads.valueOf(name) + " killed");
                            }

                            break;
                        case "BEE":
                            Bee daBee = (Bee) entity;
                            int daAnger = daBee.getAnger();
                            if (debug) {
                                mmh.logDebug("EDE daAnger=" + daAnger);
                            }
                            boolean daNectar = daBee.hasNectar();
                            if (debug) {
                                mmh.logDebug("EDE daNectar=" + daNectar);
                            }
                            if (daAnger >= 1 && daNectar == true) {
                                if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.bee.angry_pollinated", defpercent))) {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf("BEE_ANGRY_POLLINATED").getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase() + ".angry_pollinated", "Angry Pollinated Bee Head"), entity.getKiller()));
                                    if (debug) {
                                        mmh.logDebug("EDE Angry Pollinated Bee Head Dropped");
                                    }
                                }
                            } else if (daAnger >= 1 && daNectar == false) {
                                if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.bee.angry", defpercent))) {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf("BEE_ANGRY").getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase() + ".angry", "Angry Bee Head"), entity.getKiller()));
                                    if (debug) {
                                        mmh.logDebug("EDE Angry Bee Head Dropped");
                                    }
                                }
                            } else if (daAnger == 0 && daNectar == true) {
                                if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.bee.pollinated", defpercent))) {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf("BEE_POLLINATED").getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase() + ".pollinated", "Pollinated Bee Head"), entity.getKiller()));
                                    if (debug) {
                                        mmh.logDebug("EDE Pollinated Bee Head Dropped");
                                    }
                                }
                            } else if (daAnger == 0 && daNectar == false) {
                                if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.bee.chance_percent", defpercent))) {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf("BEE").getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase() + ".none", "Bee Head"), entity.getKiller()));
                                    if (debug) {
                                        mmh.logDebug("EDE Bee Head Dropped");
                                    }
                                }
                            }
                            break;
                        case "LLAMA":
                            Llama daLlama = (Llama) entity;
                            String daLlamaColor = daLlama.getColor().toString();
                            String daLlamaName = LlamaHeads.valueOf(name + "_" + daLlamaColor).getName() + " Head";//daLlamaColor.toLowerCase().replace("b", "B").replace("c", "C").replace("g", "G").replace("wh", "Wh") + " Llama Head";
                            //log(name + "_" + daLlamaColor);
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.llama." + daLlamaColor.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(LlamaHeads.valueOf(name + "_" + daLlamaColor).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daLlamaColor.toLowerCase(), daLlamaName), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Llama Head Dropped");
                                }
                            }
                            break;
                        case "HORSE":
                            Horse daHorse = (Horse) entity;
                            String daHorseColor = daHorse.getColor().toString();
                            String daHorseName = HorseHeads.valueOf(name + "_" + daHorseColor).getName() + " Head";//daHorseColor.toLowerCase().replace("b", "B").replace("ch", "Ch").replace("cr", "Cr").replace("d", "D")
                            //.replace("g", "G").replace("wh", "Wh").replace("_", " ") + " Horse Head";
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.horse." + daHorseColor.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(HorseHeads.valueOf(name + "_" + daHorseColor).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daHorseColor.toLowerCase(), daHorseName), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Horse Head Dropped");
                                }
                            }
                            break;
                        case "MUSHROOM_COW":
                            MushroomCow daMushroom = (MushroomCow) entity;
                            String daCowVariant = daMushroom.getVariant().toString();
                            String daCowName = daCowVariant.toLowerCase().replace("br", "Br").replace("re", "Re") + " Mooshroom Head";
                            if (debug) {
                                mmh.logDebug("EDE " + name + "_" + daCowVariant);
                            }
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.mushroom_cow." + daCowVariant.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(MobHeads.valueOf(name + "_" + daCowVariant).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daCowVariant.toLowerCase(), daCowName), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Mooshroom Head Dropped");
                                }
                            }
                            break;
                        case "PANDA":
                            Panda daPanda = (Panda) entity;
                            String daPandaGene = daPanda.getMainGene().toString();
                            String daPandaName = daPandaGene.toLowerCase().replace("br", "Br").replace("ag", "Ag").replace("la", "La")
                                    .replace("no", "No").replace("p", "P").replace("we", "We").replace("wo", "Wo") + " Panda Head";
                            if (daPandaGene.equalsIgnoreCase("normal")) {
                                daPandaName.replace("normal ", "");
                            }
                            if (debug) {
                                mmh.logDebug("EDE " + name + "_" + daPandaGene);
                            }
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.panda." + daPandaGene.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(MobHeads.valueOf(name + "_" + daPandaGene).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daPandaGene.toLowerCase(), daPandaName), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Panda Head Dropped");
                                }
                            }
                            break;
                        case "PARROT":
                            Parrot daParrot = (Parrot) entity;
                            String daParrotVariant = daParrot.getVariant().toString();
                            String daParrotName = daParrotVariant.toLowerCase().replace("b", "B").replace("c", "C").replace("g", "G")
                                    .replace("red", "Red") + " Parrot Head";
                            if (debug) {
                                mmh.logDebug("EDE " + name + "_" + daParrotVariant);
                            }
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.parrot." + daParrotVariant.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(MobHeads.valueOf(name + "_" + daParrotVariant).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daParrotVariant.toLowerCase(), daParrotName), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Parrot Head Dropped");
                                }
                            }
                            break;
                        case "RABBIT":
                            String daRabbitType;
                            Rabbit daRabbit = (Rabbit) entity;
                            daRabbitType = daRabbit.getRabbitType().toString();
                            if (daRabbit.getCustomName() != null) {
                                if (daRabbit.getCustomName().contains("Toast")) {
                                    daRabbitType = "Toast";
                                }
                            }
                            String daRabbitName = RabbitHeads.valueOf(name + "_" + daRabbitType).getName() + " Head";
                            if (debug) {
                                mmh.logDebug("EDE " + name + "_" + daRabbitType);
                            }
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.rabbit." + daRabbitType.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(RabbitHeads.valueOf(name + "_" + daRabbitType).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daRabbitType.toLowerCase(), daRabbitName), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Rabbit Head Dropped");
                                }
                            }
                            break;
                        case "VILLAGER":
                            Villager daVillager = (Villager) entity; // Location jobsite = daVillager.getMemory(MemoryKey.JOB_SITE);
                            String daVillagerType = daVillager.getVillagerType().toString();
                            String daVillagerProfession = daVillager.getProfession().toString();
                            if (debug) {
                                mmh.logDebug("EDE name=" + name);
                            }
                            if (debug) {
                                mmh.logDebug("EDE profession=" + daVillagerProfession);
                            }
                            if (debug) {
                                mmh.logDebug("EDE type=" + daVillagerType);
                            }
                            String daName = name + "_" + daVillagerProfession + "_" + daVillagerType;
                            if (debug) {
                                mmh.logDebug("EDE " + daName + "		 " + name + "_" + daVillagerProfession + "_" + daVillagerType);
                            }
                            String daVillagerName = VillagerHeads.valueOf(daName).getName() + " Head";
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.villager." + daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(VillagerHeads.valueOf(name + "_" + daVillagerProfession + "_" + daVillagerType).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase()
                                                , daVillagerName), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Villager Head Dropped");
                                }
                            }
                            break;
                        case "ZOMBIE_VILLAGER":
                            ZombieVillager daZombieVillager = (ZombieVillager) entity;
                            String daZombieVillagerProfession = daZombieVillager.getVillagerProfession().toString();
                            String daZombieVillagerName = ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getName() + " Head";
                            if (debug) {
                                mmh.logDebug("EDE " + name + "_" + daZombieVillagerProfession);
                            }
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.zombie_villager", defpercent))) {
                                Drops.add(mmh.makeSkull(ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daZombieVillagerProfession.toLowerCase(), daZombieVillagerName), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Zombie Villager Head Dropped");
                                }
                            }
                            break;
                        case "SHEEP":
                            Sheep daSheep = (Sheep) entity;
                            String daSheepColor = daSheep.getColor().toString();
                            String daSheepName;

                            if (daSheep.getCustomName() != null) {
                                if (daSheep.getCustomName().contains("jeb_")) {
                                    daSheepColor = "jeb_";
                                } else {
                                    daSheepColor = daSheep.getColor().toString();
                                }
                            }
                            daSheepName = SheepHeads.valueOf(name + "_" + daSheepColor).getName() + " Head";
                            if (debug) {
                                mmh.logDebug("EDE " + daSheepColor + "_" + name);
                            }
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.sheep." + daSheepColor.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(SheepHeads.valueOf(name + "_" + daSheepColor).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daSheepColor.toLowerCase(), daSheepName), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Sheep Head Dropped");
                                }
                            }
                            break;
                        /*case "STRIDER":
                         Strider strider = (Strider) entity;

                         break;*/
                        case "TRADER_LLAMA":
                            TraderLlama daTraderLlama = (TraderLlama) entity;
                            String daTraderLlamaColor = daTraderLlama.getColor().toString();
                            String daTraderLlamaName = LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getName() + " Head";
                            if (debug) {
                                mmh.logDebug("EDE " + daTraderLlamaColor + "_" + name);
                            }
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.trader_llama." + daTraderLlamaColor.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daTraderLlamaColor.toLowerCase(), daTraderLlamaName), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE Trader Llama Head Dropped");
                                }
                            }
                            break;
                        case "STRIDER":
                            Strider daStrider = (Strider) entity;
                            PersistentDataContainer pdc2 = daStrider.getPersistentDataContainer();
                            boolean isShivering = Boolean.parseBoolean(daStrider.getPersistentDataContainer().get(mmh.SHIVERING_KEY, PersistentDataType.STRING));
                            if (mmh.chance25oftrue()) { // isShivering
                                name = name.concat("_SHIVERING");
                                if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))) {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase(), "Shivering " + event.getEntity().getName() + " Head"), entity.getKiller()));
                                    if (debug) {
                                        mmh.logDebug("EDE " + name + " Head Dropped");
                                    }
                                }
                            } else {
                                if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))) {
                                    Drops.add(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                            mmh.mobNames.getString(name.toLowerCase(), event.getEntity().getName() + " Head"), entity.getKiller()));
                                    if (debug) {
                                        mmh.logDebug("EDE " + name + " Head Dropped");
                                    }
                                }
                            }

                            if (debug) {
                                mmh.logDebug("EDE " + MobHeads.valueOf(name) + " killed");
                            }
                            break;
                        default:
                            //mmh.makeSkull(MobHeads.valueOf(name).getTexture(), name);
                            if (debug) {
                                mmh.logDebug("EDE name=" + name + " line:1005");
                            }
                            if (debug) {
                                mmh.logDebug("EDE texture=" + MobHeads.valueOf(name).getTexture() + " line:1006");
                            }
                            if (debug) {
                                mmh.logDebug("EDE location=" + entity.getLocation() + " line:1007");
                            }
                            if (debug) {
                                mmh.logDebug("EDE getName=" + event.getEntity().getName() + " line:1008");
                            }
                            if (debug) {
                                mmh.logDebug("EDE killer=" + entity.getKiller().toString() + " line:1009");
                            }
                            if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))) {
                                Drops.add(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase(), event.getEntity().getName() + " Head"), entity.getKiller()));
                                if (debug) {
                                    mmh.logDebug("EDE " + name + " Head Dropped");
                                }
                            }
                            if (debug) {
                                mmh.logDebug("EDE " + MobHeads.valueOf(name) + " killed");
                            }
                            break;
                    }
                }
                //}
                //}
                return;
            }
        }
    }

    @SuppressWarnings({"static-access", "unused"})
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) { //onEntitySpawn(EntitySpawnEvent e) { // TODO: onCreatureSpawn
        if (mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)) {
            Entity entity = event.getEntity();
            if (entity instanceof WanderingTrader) {
                //traderHeads2 = YamlConfiguration.loadConfiguration(traderFile2);
                if (debug) {
                    mmh.logDebug("CSE WanderingTrader spawned");
                }
                WanderingTrader trader = (WanderingTrader) entity;
                List<MerchantRecipe> recipes = new ArrayList<>();
                List<MerchantRecipe> oldRecipes = trader.getRecipes();

                /* if(!cached_recipes.isEmpty()) {
                 recipes.addAll(cached_recipes);
                 if(mmh.getConfig().getBoolean("wandering_trades.keep_default_trades", true)){
                 recipes.addAll(oldRecipes);
                 }
                 trader.setRecipes(recipes);
                 cached_recipes.clear();
                 cached_recipes.clear();
                 if(debug){mmh.logDebug("CSE WT loaded, list cleared.");}
                 }else {
                 if(debug){mmh.logDebug("CSE WT Error.");}
                 }*/


                // BukkitTask updateTask = mmh.getServer().getScheduler().runTaskAsynchronously(mmh, new Runnable() {

                //public void run() {*/

                /*
                   Player Heads
                 */
                if (mmh.getConfig().getBoolean("wandering_trades.player_heads.enabled", true)) {
                    int playerRandom = Utils.randomBetween(mmh.getConfig().getInt("wandering_trades.player_heads.min", 0), mmh.getConfig().getInt("wandering_trades.player_heads.max", 3));
                    if (debug) {
                        mmh.logDebug("CSE playerRandom=" + playerRandom);
                    }
                    if (playerRandom > 0) {
                        if (debug) {
                            mmh.logDebug("CSE playerRandom > 0");
                        }
                        int numOfplayerheads = mmh.playerHeads.getInt("players.number", 0) - 1;
                        if (debug) {
                            mmh.logDebug("CSE numOfplayerheads=" + numOfplayerheads);
                        }
                        HashSet<Integer> used = new HashSet<>();
                        for (int i = 0; i < playerRandom; i++) {
                            int randomPlayerHead = Utils.randomBetween(0, numOfplayerheads);
                            while (used.contains(randomPlayerHead)) { //while we have already used the number
                                randomPlayerHead = Utils.randomBetween(0, numOfplayerheads); //generate a new one because it's already used
                            }
                            //by this time, add will be unique
                            used.add(randomPlayerHead);
                            /*    if(debug){mmh.logDebug("CSE randomPlayerHead=" + randomPlayerHead);}
                             ItemStack price1 = mmh.playerHeads.getItemStack("players.player_" + randomPlayerHead + ".price_1", new ItemStack(Material.AIR));
                             if(debug){mmh.logDebug("CSE price1=" + price1);}
                             ItemStack price2 = mmh.playerHeads.getItemStack("players.player_" + randomPlayerHead + ".price_2", new ItemStack(Material.AIR));
                             if(debug){mmh.logDebug("CSE price2=" + price2);}
                             ItemStack itemstack = mmh.playerHeads.getItemStack("players.player_" + randomPlayerHead + ".itemstack", new ItemStack(Material.AIR));
                             if(debug){mmh.logDebug("CSE itemstack=" + itemstack);}
                             MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.playerHeads.getInt("players.player_" + randomPlayerHead + ".quantity", (int) 3));
                             recipe.setExperienceReward(true);
                             recipe.addIngredient(price1);
                             recipe.addIngredient(price2);//*/
                            recipes.add(playerhead_recipes.get(randomPlayerHead));
                        }
                        used.clear();
                    }
                }
                /*
                   Block Heads
                 */
                if (mmh.getConfig().getBoolean("wandering_trades.block_heads.enabled", true)) {
                    int min = mmh.getConfig().getInt("wandering_trades.block_heads.min", 0);
                    int max;
                    if (mmh.getMCVersion().startsWith("1.16") || mmh.getMCVersion().startsWith("1.17")) {
                        max = mmh.getConfig().getInt("wandering_trades.block_heads.pre_116.max", 5);
                    } else {
                        max = mmh.getConfig().getInt("wandering_trades.block_heads.pre_116.max", 5);
                    }
                    if (debug) {
                        mmh.logDebug("CSE BH1 min=" + min + " max=" + max);
                    }
                    int blockRandom = Utils.randomBetween(min, max);
                    if (debug) {
                        mmh.logDebug("CSE blockRandom=" + blockRandom);
                    }
                    if (blockRandom > 0) {
                        if (debug) {
                            mmh.logDebug("CSE blockRandom > 0");
                        }
                        int numOfblockheads = BHNum >= 0 ? BHNum : 0;
                        if (debug) {
                            mmh.logDebug("CSE numOfblockheads=" + numOfblockheads);
                        }
                        HashSet<Integer> used = new HashSet<>();
                        for (int i = 0; i < blockRandom; i++) {
                            if (debug) {
                                mmh.logDebug("CSE i=" + i);
                            }
                            int randomBlockHead = Utils.randomBetween(0, numOfblockheads);
                            while (used.contains(randomBlockHead)) { //while we have already used the number
                                randomBlockHead = Utils.randomBetween(0, numOfblockheads); //generate a new one because it's already used
                            }
                            //by this time, add will be unique
                            used.add(randomBlockHead);
                            /*    if(debug){mmh.logDebug("CSE randomBlockHead=" + randomBlockHead);}
                             ItemStack price1 = mmh.blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".price_1", new ItemStack(Material.AIR));
                             if(debug){mmh.logDebug("CSE price1=" + price1);}
                             ItemStack price2 = mmh.blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".price_2", new ItemStack(Material.AIR));
                             if(debug){mmh.logDebug("CSE price2=" + price2);}
                             ItemStack itemstack = mmh.blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                             if(debug){mmh.logDebug("CSE itemstack=" + itemstack);}
                             MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.blockHeads.getInt("blocks.block_" + randomBlockHead + ".quantity", (int) 1));
                             recipe.setExperienceReward(true);
                             recipe.addIngredient(price1);
                             recipe.addIngredient(price2);//*/
                            recipes.add(blockhead_recipes.get(randomBlockHead));
                        }
                        used.clear();
                    }
                }

                if (mmh.getMCVersion().startsWith("1.16") || mmh.getMCVersion().startsWith("1.17")) {
                    /*
                       Block Heads 2
                     */
                    if (mmh.getConfig().getBoolean("wandering_trades.block_heads.enabled", true)) {
                        int min = mmh.getConfig().getInt("wandering_trades.block_heads.is_116.min", 0);
                        int max = mmh.getConfig().getInt("wandering_trades.block_heads.is_116.max", 5) / 2;
                        if (debug) {
                            mmh.logDebug("CSE BH2 min=" + min + " max=" + max);
                        }
                        int blockRandom = Utils.randomBetween(min, max);
                        if (debug) {
                            mmh.logDebug("CSE blockRandom=" + blockRandom);
                        }
                        if (blockRandom > 0) {
                            if (debug) {
                                mmh.logDebug("CSE blockRandom > 0");
                            }
                            int numOfblockheads = (BHNum + BHNum2 - 1) >= 0 ? BHNum + BHNum2 - 1 : 0;
                            if (debug) {
                                mmh.logDebug("CSE numOfblockheads=" + numOfblockheads);
                            }
                            HashSet<Integer> used = new HashSet<>();
                            for (int i = 0; i < blockRandom; i++) {
                                if (debug) {
                                    mmh.logDebug("CSE i=" + i);
                                }
                                int randomBlockHead = Utils.randomBetween(BHNum, numOfblockheads);
                                while (used.contains(randomBlockHead)) { //while we have already used the number
                                    randomBlockHead = Utils.randomBetween(BHNum, numOfblockheads); //generate a new one because it's already used
                                }
                                //by this time, add will be unique
                                used.add(randomBlockHead);
                                /*    if(debug){mmh.logDebug("CSE randomBlockHead=" + randomBlockHead);}
                                 ItemStack price1 = mmh.blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".price_1", new ItemStack(Material.AIR));
                                 if(debug){mmh.logDebug("CSE price1=" + price1);}
                                 ItemStack price2 = mmh.blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".price_2", new ItemStack(Material.AIR));
                                 if(debug){mmh.logDebug("CSE price2=" + price2);}
                                 ItemStack itemstack = mmh.blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                                 if(debug){mmh.logDebug("CSE itemstack=" + itemstack);}
                                 MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.blockHeads2.getInt("blocks.block_" + randomBlockHead + ".quantity", (int) 1));
                                 recipe.setExperienceReward(true);
                                 recipe.addIngredient(price1);
                                 recipe.addIngredient(price2);//*/
                                recipes.add(blockhead_recipes.get(randomBlockHead));
                            }
                            used.clear();
                        }
                    }
                }

                /*
                   Custom Trades
                 */
                if (mmh.getConfig().getBoolean("wandering_trades.custom_trades.enabled", false)) {
                    int customRandom = Utils.randomBetween(mmh.getConfig().getInt("wandering_trades.custom_trades.min", 0), mmh.getConfig().getInt("wandering_trades.custom_trades.max", 5));
                    int numOfCustomTrades = mmh.traderCustom.getInt("custom_trades.number", -1) - 1;
                    //if(debug){logDebug("CSE numOfCustomTrades=" + numOfCustomTrades);}
                    //int customRandom = randomBetween(getConfig().getInt("wandering_trades.min_custom_trades", 0), getConfig().getInt("wandering_trades.max_custom_trades", 3));
                    if (debug) {
                        mmh.logDebug("CSE customRandom=" + customRandom);
                    }
                    if (customRandom > 0) {
                        if (debug) {
                            mmh.logDebug("CSE customRandom > 0");
                        }
                        //for(int randomCustomTrade=1; randomCustomTrade<numOfCustomTrades; randomCustomTrade++){
                        HashSet<Integer> used = new HashSet<>();
                        for (int i = 0; i < customRandom; i++) {
                            int randomCustomTrade = Utils.randomBetween(0, numOfCustomTrades);
                            while (used.contains(randomCustomTrade)) { //while we have already used the number
                                randomCustomTrade = Utils.randomBetween(0, numOfCustomTrades); //generate a new one because it's already used
                            }
                            //by this time, add will be unique
                            used.add(randomCustomTrade);
                            if (debug) {
                                mmh.logDebug("CSE randomCustomTrade=" + randomCustomTrade);
                            }
                            /* Fix chance later */
                            double chance = Math.random();
                            if (debug) {
                                mmh.logDebug("CSE chance=" + chance + " line:1540");
                            }
                            if (mmh.traderCustom.getDouble("custom_trades.trade_" + randomCustomTrade + ".chance", 0.002) > chance) {
                                /*    if(debug){mmh.logDebug("CSE randomCustomTrade=" + randomCustomTrade);}
                                 ItemStack price1 = mmh.traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".price_1", new ItemStack(Material.AIR));
                                 if(debug){mmh.logDebug("CSE price1=" + price1.toString());}
                                 ItemStack price2 = mmh.traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".price_2", new ItemStack(Material.AIR));
                                 if(debug){mmh.logDebug("CSE price2=" + price2.toString());}
                                 ItemStack itemstack = mmh.traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".itemstack", new ItemStack(Material.AIR));
                                 if(debug){mmh.logDebug("CSE itemstack=" + itemstack.toString());}
                                 MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.traderCustom.getInt("custom_trades.trade_" + randomCustomTrade + ".quantity", (int) 1));
                                 recipe.setExperienceReward(true);
                                 recipe.addIngredient(price1);
                                 recipe.addIngredient(price2);//*/
                                recipes.add(custometrade_recipes.get(randomCustomTrade));
                            }
                        }
                        used.clear();
                    }
                }

                if (mmh.getConfig().getBoolean("wandering_trades.keep_default_trades", true)) {
                    recipes.addAll(oldRecipes);
                }
                trader.setRecipes(recipes);
            }
        }

    }

    private void log(Level lvl, String msg) {
        mmh.log(lvl, msg);
    }

    private void logDebug(String msg) {
        mmh.logDebug(msg);
    }

    @EventHandler
    public void onStriderShiver(StriderTemperatureChangeEvent event) {
        Strider strider = event.getEntity();
        PersistentDataContainer pdc = strider.getPersistentDataContainer();
        if (event.isShivering()) {
            pdc.set(mmh.SHIVERING_KEY, PersistentDataType.STRING, "true");
        } else {
            pdc.set(mmh.SHIVERING_KEY, PersistentDataType.STRING, "false");
        }
    }

}