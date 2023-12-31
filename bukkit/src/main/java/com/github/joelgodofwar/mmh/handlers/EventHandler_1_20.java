package com.github.joelgodofwar.mmh.handlers;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.MoreMobHeadsLib;
import com.github.joelgodofwar.mmh.commands.MMHCommand;
import com.github.joelgodofwar.mmh.enums.*;
import com.github.joelgodofwar.mmh.util.ChatColorUtils;
import com.github.joelgodofwar.mmh.util.ConfigHelper;
import com.github.joelgodofwar.mmh.util.StrUtils;
import com.github.joelgodofwar.mmh.util.Utils;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.StriderTemperatureChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static com.github.joelgodofwar.mmh.MoreMobHeads.debug;

/**
 * 1.8 1_8_R1 1.8.3 1_8_R2 1.8.8 1_8_R3 1.9 1_9_R1 1.9.4 1_9_R2 1.10 1_10_R1
 * 1.11 1_11_R1 1.12 1_12_R1 1.13 1_13_R1 1.13.1 1_13_R2 1.14 1_14_R1 1.15
 * 1_15_R1 1.16.1 1_16_R1 1.16.2 1_16_R2 1.17 1_17_R1
 */

@SuppressWarnings("deprecation")
public class EventHandler_1_20 implements Listener, Reloadable {
    /**
     * Variables
     */
    MoreMobHeads mmh;
    double defpercent = 13.0;
    String world_whitelist;
    String world_blacklist;
    String mob_whitelist;
    String mob_blacklist;
    File blockFile117;
    File blockFile1172;
    File blockFile1173;
    File blockFile119;
    File blockFile120;
    public FileConfiguration blockHeads = new YamlConfiguration();
    public FileConfiguration blockHeads2 = new YamlConfiguration();
    public FileConfiguration blockHeads3 = new YamlConfiguration();
    public FileConfiguration blockHeads4 = new YamlConfiguration();

    List<MerchantRecipe> playerhead_recipes = new ArrayList<>();
    List<MerchantRecipe> blockhead_recipes = new ArrayList<>();
    List<MerchantRecipe> custometrade_recipes = new ArrayList<>();
    int BHNum, BHNum2, BHNum3, BHNum4;
    MMHCommand command;

    public EventHandler_1_20(final MoreMobHeads plugin) {
        /* Set variables */
        mmh = plugin;
        mmh.log(Level.INFO, "Loading 1.20 EventHandler...");
        command = new MMHCommand(plugin);
        onReload();
    }

    @Override
    public void onReload() {
        long startTime = System.currentTimeMillis();
        world_whitelist = mmh.getConfig().getString("world.whitelist", "");
        world_blacklist = mmh.getConfig().getString("world.blacklist", "");
        mob_whitelist = mmh.getConfig().getString("mob.whitelist", "");
        mob_blacklist = mmh.getConfig().getString("mob.blacklist", "");
        blockFile117 = new File(mmh.getDataFolder(), "block_heads_1_17.yml");
        blockFile1172 = new File(mmh.getDataFolder(), "block_heads_1_17_2.yml");
        blockFile1173 = new File(mmh.getDataFolder(), "block_heads_1_17_3.yml");
        blockFile119 = new File(mmh.getDataFolder(), "block_heads_1_19.yml");
        blockFile120 = new File(mmh.getDataFolder(), "block_heads_1_20.yml");
        if (mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)) {
            if (!blockFile117.exists()) {
                mmh.saveResource("block_heads_1_17.yml", true);
                mmh.log(Level.INFO, "block_heads_1_17.yml not found! Creating in " + mmh.getDataFolder() + "");
            }
            if (!blockFile1172.exists()) {
                mmh.saveResource("block_heads_1_17_2.yml", true);
                mmh.log(Level.INFO, "block_heads_1_17_2.yml not found! Creating in " + mmh.getDataFolder() + "");
            }
            if (!blockFile1173.exists()) {
                mmh.saveResource("block_heads_1_17_3.yml", true);
                mmh.log(Level.INFO, "block_heads_1_17_3.yml not found! Creating in " + mmh.getDataFolder() + "");
            }
            if (!blockFile119.exists()) {
                mmh.saveResource("block_heads_1_19.yml", true);
                mmh.log(Level.INFO, "block_heads_1_19.yml not found! Creating in " + mmh.getDataFolder() + "");
            }
            if (!blockFile120.exists()) {
                mmh.saveResource("block_heads_1_20.yml", true);
                mmh.log(Level.INFO, "block_heads_1_20.yml not found! Creating in " + mmh.getDataFolder() + "");
            }
            blockHeads = new YamlConfiguration();
            try {
                mmh.log(Level.INFO, "Loading " + blockFile117 + "...");
                blockHeads.load(blockFile117);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }
            blockHeads2 = new YamlConfiguration();
            try {
                mmh.log(Level.INFO, "Loading " + blockFile1172 + "...");
                blockHeads2.load(blockFile1172);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }
            if ((Double.parseDouble(mmh.getMCVersion().substring(0, 4)) >= 1.19)
                    && !(Double.parseDouble(mmh.getMCVersion().substring(0, 4)) >= 1.20)) {
                blockFile1173 = blockFile119;
            } else if (Double.parseDouble(mmh.getMCVersion().substring(0, 4)) >= 1.20) {
                blockFile1173 = blockFile120;
            }
            blockHeads3 = new YamlConfiguration();
            try {
                mmh.log(Level.INFO, "Loading " + blockFile1173 + "...");
                blockHeads3.load(blockFile1173);
            } catch (IOException | InvalidConfigurationException e1) {
                mmh.stacktraceInfo();
                e1.printStackTrace();
            }

            boolean showlore = mmh.getConfig().getBoolean("lore.show_plugin_name", true);
            ArrayList<String> headlore = new ArrayList();
            headlore.add(ChatColor.AQUA + "" + mmh.getName());

            mmh.log(Level.INFO, "Loading PlayerHead Recipes...");
            for (int i = 1; i < (mmh.playerHeads.getInt("players.number") + 1); i++) {
                ItemStack price1 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_1",
                        new ItemStack(Material.AIR));
                ItemStack price2 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_2",
                        new ItemStack(Material.AIR));
                ItemStack itemstack = mmh.playerHeads.getItemStack("players.player_" + i + ".itemstack",
                        new ItemStack(Material.AIR));
                if (showlore) {
                    SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
                    meta.setLore(headlore);
                    itemstack.setItemMeta(meta);
                    itemstack.setItemMeta(meta);
                }
                MerchantRecipe recipe = new MerchantRecipe(itemstack,
                        mmh.playerHeads.getInt("players.player_" + i + ".quantity", 3));
                recipe.addIngredient(price1);
                recipe.addIngredient(price2);
                playerhead_recipes.add(recipe);
            }
            mmh.log(Level.INFO, playerhead_recipes.size() + " PlayerHead Recipes ADDED...");
            mmh.log(Level.INFO, "Loading BlockHead Recipes...");
            BHNum = blockHeads.getInt("blocks.number");
            // BlockHeads
            mmh.log(Level.INFO, "BlockHeads=" + BHNum);
            for (int i = 1; i < (BHNum + 1); i++) {
                ItemStack price1 = blockHeads.getItemStack("blocks.block_" + i + ".price_1",
                        new ItemStack(Material.AIR));
                ItemStack price2 = blockHeads.getItemStack("blocks.block_" + i + ".price_2",
                        new ItemStack(Material.AIR));
                ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + i + ".itemstack",
                        new ItemStack(Material.AIR));
                if (showlore) {
                    SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
                    meta.setLore(headlore);
                    itemstack.setItemMeta(meta);
                    itemstack.setItemMeta(meta);
                }
                MerchantRecipe recipe = new MerchantRecipe(itemstack,
                        blockHeads.getInt("blocks.block_" + i + ".quantity", 8));
                recipe.setExperienceReward(true);
                recipe.addIngredient(price1);
                recipe.addIngredient(price2);
                blockhead_recipes.add(recipe);
            }
            BHNum2 = blockHeads2.getInt("blocks.number");
            // blockHeads 2
            mmh.log(Level.INFO, "BlockHeads2=" + BHNum2);
            for (int i = 1; i < (BHNum2 + 1); i++) {
                ItemStack price1 = blockHeads2.getItemStack("blocks.block_" + i + ".price_1",
                        new ItemStack(Material.AIR));
                ItemStack price2 = blockHeads2.getItemStack("blocks.block_" + i + ".price_2",
                        new ItemStack(Material.AIR));
                ItemStack itemstack = blockHeads2.getItemStack("blocks.block_" + i + ".itemstack",
                        new ItemStack(Material.AIR));
                if (showlore) {
                    SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
                    meta.setLore(headlore);
                    itemstack.setItemMeta(meta);
                    itemstack.setItemMeta(meta);
                }
                MerchantRecipe recipe = new MerchantRecipe(itemstack,
                        blockHeads2.getInt("blocks.block_" + i + ".quantity", 8));
                recipe.setExperienceReward(true);
                recipe.addIngredient(price1);
                recipe.addIngredient(price2);
                blockhead_recipes.add(recipe);
            }
            BHNum3 = blockHeads3.getInt("blocks.number");
            // blockHeads 3
            mmh.log(Level.INFO, "BlockHeads3=" + BHNum3);
            for (int i = 1; i < (BHNum3 + 1); i++) {
                ItemStack price1 = blockHeads3.getItemStack("blocks.block_" + i + ".price_1",
                        new ItemStack(Material.AIR));
                ItemStack price2 = blockHeads3.getItemStack("blocks.block_" + i + ".price_2",
                        new ItemStack(Material.AIR));
                ItemStack itemstack = blockHeads3.getItemStack("blocks.block_" + i + ".itemstack",
                        new ItemStack(Material.AIR));
                if (showlore) {
                    SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
                    meta.setLore(headlore);
                    itemstack.setItemMeta(meta);
                    itemstack.setItemMeta(meta);
                }
                MerchantRecipe recipe = new MerchantRecipe(itemstack,
                        blockHeads3.getInt("blocks.block_" + i + ".quantity", 8));
                recipe.setExperienceReward(true);
                recipe.addIngredient(price1);
                recipe.addIngredient(price2);
                blockhead_recipes.add(recipe);
            }
            mmh.log(Level.INFO, blockhead_recipes.size() + " BlockHead Recipes ADDED...");
            mmh.log(Level.INFO, "Loading CustomTrades Recipes...");
            for (int i = 1; i < (mmh.traderCustom.getInt("custom_trades.number") + 1); i++) {
                ItemStack price1 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_1",
                        new ItemStack(Material.AIR));
                ItemStack price2 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_2",
                        new ItemStack(Material.AIR));
                ItemStack itemstack = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".itemstack",
                        new ItemStack(Material.AIR));
                MerchantRecipe recipe = new MerchantRecipe(itemstack,
                        mmh.traderCustom.getInt("custom_trades.trade_" + i + ".quantity", 1));
                recipe.setExperienceReward(true);
                recipe.addIngredient(price1);
                recipe.addIngredient(price2);
                custometrade_recipes.add(recipe);
            }
            mmh.log(Level.INFO, custometrade_recipes.size() + " CustomTrades Recipes ADDED...");
            mmh.log(Level.INFO, "EventHandler_1_20 took " + (System.currentTimeMillis() - startTime) + "ms to load");
        }
        command.blockHeads = blockHeads;
        command.blockHeads2 = blockHeads2;
        command.blockHeads3 = blockHeads3;
    }

    @SuppressWarnings({})
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (debug) {
            mmh.logDebug("EDEE - getEntity=" + event.getEntity().getName());
        }
        if (debug) {
            mmh.logDebug("EDEE - getDamager=" + event.getDamager().getName());
        }

        Player player = null;
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
            if ((event.getEntity() instanceof EnderCrystal)) {
                /* Is Player and Is End Crystal */
                EnderCrystal ec = (EnderCrystal) event.getEntity();
                mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
            }
        } else if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if ((arrow.getShooter() instanceof Player) && !(event.getEntity() instanceof EnderCrystal)) {
                /* Is Player but Not End Crystal */
                player = (Player) arrow.getShooter();
            } else if ((arrow.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
                /* Is Player and Is End Crystal */
                player = (Player) arrow.getShooter();
                EnderCrystal ec = (EnderCrystal) event.getEntity();
                mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
            } else {
                return; // Not Player or Not End Crystal
            }
        } else if (event.getDamager() instanceof ThrownPotion) {
            ThrownPotion potion = (ThrownPotion) event.getDamager();
            if (!(potion.getShooter() instanceof Player)) {
                return;
            }
            player = (Player) potion.getShooter();
        } else if (event.getDamager() instanceof Snowball) {
            Snowball snowball = (Snowball) event.getDamager();
            if ((snowball.getShooter() instanceof Player) && !(event.getEntity() instanceof EnderCrystal)) {
                /* Is Player but Not End Crystal */
                player = (Player) snowball.getShooter();
            } else if ((snowball.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
                /* Is Player and Is End Crystal */
                player = (Player) snowball.getShooter();
                EnderCrystal ec = (EnderCrystal) event.getEntity();
                mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
            } else {
                return; // Not Player or Not End Crystal
            }
        } else if (event.getDamager() instanceof Egg) {
            Egg egg = (Egg) event.getDamager();
            if ((egg.getShooter() instanceof Player) && !(event.getEntity() instanceof EnderCrystal)) {
                /* Is Player but Not End Crystal */
                player = (Player) egg.getShooter();
            } else if ((egg.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
                /* Is Player and Is End Crystal */
                player = (Player) egg.getShooter();
                EnderCrystal ec = (EnderCrystal) event.getEntity();
                mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
            } else {
                return; // Not Player or Not End Crystal
            }
        } else if (event.getDamager() instanceof Trident) {
            Trident trident = (Trident) event.getDamager();
            if ((trident.getShooter() instanceof Player) && !(event.getEntity() instanceof EnderCrystal)) {
                /* Is Player but Not End Crystal */
                player = (Player) trident.getShooter();
            } else if ((trident.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
                /* Is Player and Is End Crystal */
                player = (Player) trident.getShooter();
                EnderCrystal ec = (EnderCrystal) event.getEntity();
                mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
            } else {
                return; // Not Player or Not End Crystal
            }
        } else if (event.getDamager() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) event.getDamager();
            if (!(tnt.getSource() instanceof Player)) {
                return;
            }
            player = (Player) tnt.getSource();
        } else if (event.getDamager() instanceof EnderCrystal) {
            EnderCrystal ec = (EnderCrystal) event.getDamager();
            UUID pUUID = mmh.endCrystals.get(ec.getUniqueId());
            player = Bukkit.getPlayer(pUUID);
            mmh.playerWeapons.put(player.getUniqueId(), new ItemStack(Material.END_CRYSTAL));
            return;
        } else if (event.getDamager() instanceof Creeper) {
            Creeper creeper = (Creeper) event.getDamager();
            creeper.isPowered();
            return;
        } else {

            return;
        }

        // Store the damaging player's UUID and the damaging weapon in the map
        if (debug) {
            mmh.logDebug("EDEE - DamageCause=" + event.getCause());
        }
        if (debug) {
            mmh.logDebug("EDEE - UUID=" + player.getUniqueId());
        }
        if (debug) {
            mmh.logDebug(
                    "EDEE - Weapon=" + resolveDamagingWeapon(player.getInventory(), event.getCause()).orElse(null));
        }
        mmh.playerWeapons.put(player.getUniqueId(),
                resolveDamagingWeapon(player.getInventory(), event.getCause()).orElse(null));
    }

    /**
     * Events go here
     */
    @SuppressWarnings({"unchecked", "unused", "rawtypes"})
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeathEvent(EntityDeathEvent event) {// TODO: EnityDeathEvent
        LivingEntity entity = event.getEntity();
        // Verify that the killer is a player
        if (!(entity.getKiller() instanceof Player) && !(entity.getKiller() instanceof Creeper)) {
            return;
        }
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

        if ((world_whitelist != null) && !world_whitelist.isEmpty() && (world_blacklist != null)
                && !world_blacklist.isEmpty()) {
            if (!StrUtils.stringContains(world_whitelist, world.getName())
                    && StrUtils.stringContains(world_blacklist, world.getName())) {
                if (debug) {
                    mmh.logDebug("EDE - World - On blacklist and Not on whitelist.");
                }
                return;
            } else if (!StrUtils.stringContains(world_whitelist, world.getName())
                    && !StrUtils.stringContains(world_blacklist, world.getName())) {
                if (debug) {
                    mmh.logDebug("EDE - World - Not on whitelist.");
                }
                return;
            } else if (!StrUtils.stringContains(world_whitelist, world.getName())) {

            }
        } else if ((world_whitelist != null) && !world_whitelist.isEmpty()) {
            if (!StrUtils.stringContains(world_whitelist, world.getName())) {
                if (debug) {
                    mmh.logDebug("EDE - World - Not on whitelist.");
                }
                return;
            }
        } else if ((world_blacklist != null) && !world_blacklist.isEmpty()) {
            if (StrUtils.stringContains(world_blacklist, world.getName())) {
                if (debug) {
                    mmh.logDebug("EDE - World - On blacklist.");
                }
                return;
            }
        }
        if ((entity instanceof Player)
                && ((entity.getKiller() instanceof Player) || (entity.getKiller() instanceof Creeper))) {
            if (debug) {
                mmh.logDebug("EDE Entity is Player");
            }
            if (entity.getKiller().hasPermission("moremobheads.players") || (entity.getKiller() instanceof Creeper)) {
                if (debug) {
                    mmh.logDebug(
                            "EDE DropIt=" + mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.player", 50.0)));
                }
                if (debug) {
                    mmh.logDebug("EDE chance_percent.player=" + mmh.chanceConfig.getDouble("chance_percent.player", 50.0));
                }
                if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("chance_percent.player", 50.0))) {
                    // Player daKiller = entity.getKiller();
                    if (debug) {
                        mmh.logDebug("EDE Killer is Player line:1073");
                    }
                    ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
                    SkullMeta meta = (SkullMeta) helmet.getItemMeta();
                    meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(entity.getUniqueId()));
                    meta.setDisplayName(((Player) entity).getDisplayName() + "'s Head");
                    ArrayList<String> lore = new ArrayList();
                    if (mmh.getConfig().getBoolean("lore.show_killer", true)) {
                        // lore.add(ChatColor.RESET + "Killed by " + ChatColor.RESET + ChatColor.YELLOW
                        // + entity.getKiller().getDisplayName() );
                        lore.add(ChatColor.RESET + "" + ChatColorUtils.setColors(mmh.mobNames.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>").replace("<player>",
                                "" + entity.getKiller().getDisplayName())));
                    }
                    if (mmh.getConfig().getBoolean("lore.show_plugin_name", true)) {
                        lore.add(ChatColor.AQUA + "" + mmh.getName());
                    }
                    meta.setLore(lore);
                    helmet.setItemMeta(meta);// e2d4c388-42d5-4a96-b4c9-623df7f5e026
                    helmet.setItemMeta(meta);

                    world.dropItemNaturally(entity.getLocation(), MoreMobHeadsLib.addSound(helmet, entity));
                    if (debug) {
                        mmh.logDebug("EDE " + ((Player) entity).getDisplayName() + " Player Head Dropped");
                    }
                    if (mmh.getConfig().getBoolean("announce.players.enabled", true)) {
                        Player daKiller = entity.getKiller();
                        Player daDead = (Player) entity;
                        String killerName = daKiller.getDisplayName();
                        String entityName = daDead.getDisplayName();
                        if (mmh.getConfig().getBoolean("announce.players.displayname", true)) {
                            killerName = daKiller.getDisplayName();
                            entityName = daDead.getDisplayName();
                        } else {
                            killerName = daKiller.getName();
                            entityName = daDead.getName();
                        }
                        announceBeheading(entity, entityName, daKiller,
                                mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                    }
                }
                return;
            } else if (debug) {
                mmh.logDebug("EDE Killer does not have permission \"moremobheads.players\"");
            }
        } else if (event.getEntity() instanceof LivingEntity) {
            if ((entity.getKiller() instanceof Player) || (entity.getKiller() instanceof Creeper)) {
                String name = event.getEntityType().toString().replace(" ", "_");
                if (debug) {
                    mmh.logDebug("EDE name=" + name);
                }
                String isNametag = null;
                @Nonnull
                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                isNametag = entity.getPersistentDataContainer().get(mmh.NAMETAG_KEY, PersistentDataType.STRING);// .getScoreboardTags();
                if (debug && (isNametag != null)) {
                    mmh.logDebug("EDE isNametag=" + isNametag);
                }

                if ((entity.getKiller() instanceof Creeper) || entity.getKiller().hasPermission("moremobheads.mobs")) {
                    if (((entity.getKiller() instanceof Creeper) || entity.getKiller().hasPermission("moremobheads.nametag")) && (isNametag != null)) {
                        if ((entity.getCustomName() != null) && !(entity.getCustomName().contains("jeb_")) && !(entity.getCustomName().contains("Toast"))) {
                            if (debug) {
                                mmh.logDebug("EDE customname=" + entity.getCustomName());
                            }
                            if ((entity instanceof Skeleton) || (entity instanceof Zombie) || (entity instanceof PigZombie)) {
                                if (mmh.getServer().getPluginManager().getPlugin("SilenceMobs") != null) {
                                    if (entity.getCustomName().toLowerCase().contains("silenceme")
                                            || entity.getCustomName().toLowerCase().contains("silence me")) {
                                        return;
                                    }
                                }
                                boolean enforcewhitelist = mmh.getConfig().getBoolean("whitelist.enforce", false);
                                boolean enforceblacklist = mmh.getConfig().getBoolean("blacklist.enforce", false);
                                boolean onwhitelist = mmh.getConfig().getString("whitelist.player_head_whitelist", "")
                                        .toLowerCase().contains(entity.getCustomName().toLowerCase());
                                boolean onblacklist = mmh.getConfig().getString("blacklist.player_head_blacklist", "")
                                        .toLowerCase().contains(entity.getCustomName().toLowerCase());
                                if (mmh.getCommonHandler().DropIt(event, mmh.chanceConfig.getDouble("named_mob", 10.0))) {
                                    if (enforcewhitelist && enforceblacklist) {
                                        if (onwhitelist && !(onblacklist)) {
                                            Drops.add(MoreMobHeadsLib.addSound(mmh.getCommonHandler().dropMobHead(entity, entity.getCustomName(), entity.getKiller()), entity));
                                            if (debug) {
                                                mmh.logDebug("EDE " + entity.getCustomName() + " Head Dropped");
                                            }
                                        }
                                    } else if (enforcewhitelist && !enforceblacklist) {
                                        if (onwhitelist) {
                                            Drops.add(MoreMobHeadsLib.addSound(
                                                    mmh.getCommonHandler().dropMobHead(entity, entity.getCustomName(), entity.getKiller()),
                                                    entity));
                                            if (debug) {
                                                mmh.logDebug("EDE " + entity.getCustomName() + " Head Dropped");
                                            }
                                        }
                                    } else if (!enforcewhitelist && enforceblacklist) {
                                        if (!onblacklist) {
                                            Drops.add(MoreMobHeadsLib.addSound(
                                                    mmh.getCommonHandler().dropMobHead(entity, entity.getCustomName(), entity.getKiller()),
                                                    entity));
                                            if (debug) {
                                                mmh.logDebug("EDE " + entity.getCustomName() + " Head Dropped");
                                            }
                                        }
                                    } else {
                                        Drops.add(MoreMobHeadsLib.addSound(
                                                mmh.getCommonHandler().dropMobHead(entity, entity.getCustomName(), entity.getKiller()),
                                                entity));
                                        if (debug) {
                                            mmh.logDebug("EDE " + entity.getCustomName() + " Head Dropped");
                                        }
                                    }
                                }
                            }
                            return;
                        }
                    }
                    if ((mob_whitelist != null) && !mob_whitelist.isEmpty() && (mob_blacklist != null)
                            && !mob_blacklist.isEmpty()) {
                        if (!StrUtils.stringContains(mob_whitelist, name)) {// mob_whitelist.contains(name)
                            if (debug) {
                                mmh.log(Level.INFO, "EDE - Mob - Not on whitelist. Mob=" + name);
                            }
                            return;
                        }
                    } else if ((mob_whitelist != null) && !mob_whitelist.isEmpty()) {
                        if (!StrUtils.stringContains(mob_whitelist, name)
                                && StrUtils.stringContains(mob_blacklist, name)) {// mob_whitelist.contains(name)
                            if (debug) {
                                mmh.log(Level.INFO, "EDE - Mob - Not on whitelist - Is on blacklist. Mob=" + name);
                            }
                            return;
                        }
                    } else if ((mob_blacklist != null) && !mob_blacklist.isEmpty()) {
                        if (StrUtils.stringContains(mob_blacklist, name)) {
                            if (debug) {
                                mmh.log(Level.INFO, "EDE - Mob - On blacklist. Mob=" + name);
                            }
                            return;
                        }
                    }

                    switch (name) {
                        case "CREEPER":
                            // ConfigHelper.Double(mmh.chanceConfig,
                            // "chance_percent.creeper", defpercent)
                            Creeper creeper = (Creeper) event.getEntity();
                            double cchance = ConfigHelper.Double(mmh.chanceConfig, "chance_percent.creeper", defpercent);
                            if (creeper.isPowered()) {
                                name = "CREEPER_CHARGED";
                                cchance = ConfigHelper.Double(mmh.chanceConfig, "chance_percent.creeper_charged", defpercent);
                            }
                            if (mmh.getCommonHandler().DropIt(event, cchance)) {
                                if (mmh.getConfig().getBoolean("vanilla_heads.creeper", false)
                                        && (!name.equals("CREEPER_CHARGED"))) {
                                    Drops.add(new ItemStack(Material.CREEPER_HEAD));
                                } else { // mmh.langName
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture(), mmh.mobNames.getString(name.toLowerCase(),
                                            MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()), entity));
                                } // MobHeads.valueOf(name).getName() + " Head"
                                if (debug) {
                                    mmh.logDebug("EDE Creeper vanilla="
                                            + mmh.getConfig().getBoolean("vanilla_heads.creeper", false));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Creeper Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "").replace(" Head", ""),
                                            entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "ENDER_DRAGON":
                            // ConfigHelper.Double(mmh.chanceConfig,
                            // "chance_percent.ender_dragon", defpercent)
                            if (mmh.getCommonHandler().DropIt(event,
                                    ConfigHelper.Double(mmh.chanceConfig, "chance_percent.ender_dragon", defpercent))) {
                                if (mmh.getConfig().getBoolean("vanilla_heads.ender_dragon", false)) {
                                    Drops.add(new ItemStack(Material.DRAGON_HEAD));
                                } else {
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture(), mmh.mobNames.getString(name.toLowerCase()
                                            , MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()), entity));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Ender Dragon Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "").replace(" Head", "")
                                            , entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "PIGLIN":
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.ender_dragon", defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent.piglin", defpercent))) {
                                if (mmh.getConfig().getBoolean("vanilla_heads.piglin", false)) {
                                    Drops.add(MoreMobHeadsLib.getVanilla(entity.getType()));
                                } else {
                                    Drops.add(MoreMobHeadsLib
                                            .addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                                    mmh.mobNames.getString(name.toLowerCase(),
                                                            MobHeads.valueOf(name).getName() + " Head"),
                                                    entity.getKiller()), entity));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Ender Dragon Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
                                            .replace(" Head", ""), entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "SKELETON":
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.skeleton", defpercent)
                            if (mmh.getCommonHandler().DropIt(event,
                                    ConfigHelper.Double(mmh.chanceConfig, "chance_percent.skeleton", defpercent))) {
                                if (mmh.getConfig().getBoolean("vanilla_heads.skeleton", false)) {
                                    Drops.add(new ItemStack(Material.SKELETON_SKULL));
                                } else {
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture(), mmh.mobNames.getString(name.toLowerCase()
                                            , MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()), entity));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Skeleton vanilla="
                                            + mmh.getConfig().getBoolean("vanilla_heads.skeleton", false));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Skeleton Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
                                            .replace(" Head", ""), entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "WITHER_SKELETON":
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.wither_skeleton",
                            // defpercent)
                            if (mmh.getCommonHandler().DropIt(event,
                                    ConfigHelper.Double(mmh.chanceConfig, "chance_percent.wither_skeleton", defpercent))) {
                                if (mmh.getConfig().getBoolean("vanilla_heads.wither_skeleton", false)) {
                                    Drops.add(new ItemStack(Material.WITHER_SKELETON_SKULL));
                                } else {
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture(), mmh.mobNames.getString(name.toLowerCase()
                                            , MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()), entity));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Wither Skeleton Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
                                            .replace(" Head", ""), entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "ZOMBIE":
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.zombie", defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent.zombie", defpercent))) {
                                if (mmh.getConfig().getBoolean("vanilla_heads.zombie", false)) {
                                    Drops.add(new ItemStack(Material.ZOMBIE_HEAD));
                                } else {

                                    Drops.add(MoreMobHeadsLib
                                            .addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture(), mmh.mobNames.getString(name.toLowerCase(),
                                                    MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()), entity));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Zombie vanilla=" + mmh.getConfig().getBoolean("vanilla_heads.zombie", false));
                                }
                                if (debug) {
                                    mmh.logDebug("EDE Zombie Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
                                            .replace(" Head", ""), entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "TROPICAL_FISH":
                            TropicalFish daFish = (TropicalFish) entity;
                            DyeColor daFishBody = daFish.getBodyColor();
                            DyeColor daFishPatternColor = daFish.getPatternColor();
                            Pattern daFishType = daFish.getPattern();
                            log("bodycolor=" + daFishBody + "\nPatternColor=" + daFishPatternColor
                                    + "\nPattern=" + daFishType);
                            // TropicalFishHeads daFishEnum = TropicalFishHeads.getIfPresent(name);
                            String daFishName = mmh.getNamedTropicalFishName(daFishType, daFishBody, daFishPatternColor);
                            log("daFishName: " + daFishName);
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent.tropical_fish." + daFishName.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(TropicalFishHeads.valueOf(name + "_" + daFishName).getTexture()
                                        , mmh.mobNames.getString(name.toLowerCase() + "." + daFishName.toLowerCase(), TropicalFishHeads.valueOf(name + "_" + daFishName).getName() + " Head")
                                        , entity.getKiller()), entity));
                                if (debug) {
                                    mmh.logDebug("EDE TROPICAL_FISH:" + daFishName + " head dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase() + "." + daFishName.toLowerCase(), TropicalFishHeads.valueOf(name + "_" + daFishName).getName() + "")
                                            .replace(" Head", ""), entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "WITHER":
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(), defpercent))) {
                                String name2 = name + "_NORMAL";
                                Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name2).getTexture(), mmh.mobNames.getString(name2.toLowerCase().replace("_", ".")
                                        , MobHeads.valueOf(name2).getName() + " Head"), entity.getKiller()), entity));
                                if (debug) {
                                    mmh.logDebug("EDE " + name2 + " Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(name2.toLowerCase().replace("_", ".")
                                            , MobHeads.valueOf(name2).getName() + "").replace(" Head", ""), entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                                if (coinFlip()) {
                                    name2 = name + "_PROJECTILE";
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name2).getTexture(), mmh.mobNames.getString(name2.toLowerCase()
                                            .replace("_", "."), event.getEntity().getName() + " Head"), entity.getKiller()), entity));
                                    if (debug) {
                                        mmh.logDebug("EDE " + name2 + " Head Dropped");
                                    }
                                }
                                if (coinFlip()) {
                                    name2 = name + "_BLUE_PROJECTILE";
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name2).getTexture(), mmh.mobNames.getString(name2.toLowerCase()
                                            .replaceFirst("_", "."), event.getEntity().getName() + " Head"), entity.getKiller()), entity));
                                    if (debug) {
                                        mmh.logDebug("EDE " + name2 + " Head Dropped");
                                    }
                                }
                            }
                            break;
                        case "WOLF":
                            Wolf wolf = (Wolf) event.getEntity();
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(),
                            // defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(),
                                    defpercent))) {
                                if (wolf.isAngry()) {
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name + "_ANGRY").getTexture(), mmh.mobNames.getString(name.toLowerCase() + "_angry"
                                            , MobHeads.valueOf(name + "_ANGRY").getName() + " Head"), entity.getKiller()), entity));
                                    if (debug) {
                                        mmh.logDebug("EDE Angry Wolf Head Dropped");
                                    }
                                    if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                        announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase() + "_angry", MobHeads.valueOf(name + "_ANGRY").getName() + "")
                                                .replace(" Head", ""), entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                    }
                                } else {
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture(), mmh.mobNames.getString(name.toLowerCase()
                                            , event.getEntity().getName() + " Head"), entity.getKiller()), entity));
                                    if (debug) {
                                        mmh.logDebug("EDE Wolf Head Dropped");
                                    }
                                    if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                        announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
                                                .replace(" Head", ""), entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
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
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.fox." +
                            // dafoxtype.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.fox." + dafoxtype.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name + "_" + dafoxtype).getTexture()
                                        , mmh.mobNames.getString(name.toLowerCase() + "." + dafoxtype.toLowerCase(), MobHeads.valueOf(name + "_" + dafoxtype).getName() + " Head"), entity.getKiller()), entity));
                                if (debug) {
                                    mmh.logDebug("EDE Fox Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + dafoxtype.toLowerCase(),
                                                            MobHeads.valueOf(name + "_" + dafoxtype).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }

                            break;
                        case "CAT":
                            Cat dacat = (Cat) entity;
                            String dacattype = dacat.getCatType().toString();
                            if (debug) {
                                mmh.logDebug("entity cat=" + dacat.getCatType());
                            }
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.cat." +
                            // dacattype.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.cat." + dacattype.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib
                                        .addSound(mmh.makeSkull(CatHeads.valueOf(dacattype).getTexture(),
                                                mmh.mobNames.getString(name.toLowerCase() + "." + dacattype.toLowerCase(),
                                                        CatHeads.valueOf(dacattype).getName() + " Head"),
                                                entity.getKiller()), entity));
                                if (debug) {
                                    mmh.logDebug("EDE Cat Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + dacattype.toLowerCase(),
                                                            CatHeads.valueOf(dacattype).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "OCELOT":
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(),
                            // defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(),
                                    defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(
                                        mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                                mmh.mobNames.getString(MobHeads.valueOf(name).getNameString(),
                                                        MobHeads.valueOf(name).getName() + " Head"),
                                                entity.getKiller()),
                                        entity));
                                if (debug) {
                                    mmh.logDebug("EDE " + name + " Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames.getString(MobHeads.valueOf(name).getNameString(),
                                                    MobHeads.valueOf(name).getName() + "").replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
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
                            if ((daAnger >= 1) && (daNectar == true)) {
                                // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.bee.angry_pollinated",
                                // defpercent)
                                if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                        "chance_percent.bee.angry_pollinated", defpercent))) {
                                    Drops.add(
                                            MoreMobHeadsLib.addSound(
                                                    mmh.makeSkull(
                                                            MobHeads.valueOf("BEE_ANGRY_POLLINATED").getTexture(),
                                                            mmh.mobNames.getString(name.toLowerCase() + ".angry_pollinated",
                                                                    "Angry Pollinated Bee Head"),
                                                            entity.getKiller()),
                                                    entity));
                                    if (debug) {
                                        mmh.logDebug("EDE Angry Pollinated Bee Head Dropped");
                                    }
                                    if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                        announceBeheading(entity,
                                                mmh.mobNames
                                                        .getString(name.toLowerCase() + ".angry_pollinated",
                                                                MobHeads.valueOf("BEE_ANGRY_POLLINATED").getName() + "")
                                                        .replace(" Head", ""),
                                                entity.getKiller(),
                                                mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                    }
                                }
                            } else if ((daAnger >= 1) && (daNectar == false)) {
                                // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.bee.angry", defpercent)
                                if (mmh.getCommonHandler().DropIt(event,
                                        ConfigHelper.Double(mmh.chanceConfig, "chance_percent.bee.angry", defpercent))) {
                                    Drops.add(
                                            MoreMobHeadsLib
                                                    .addSound(
                                                            mmh.makeSkull(
                                                                    MobHeads.valueOf("BEE_ANGRY").getTexture(),
                                                                    mmh.mobNames.getString(name.toLowerCase() + ".angry",
                                                                            "Angry Bee Head"),
                                                                    entity.getKiller()),
                                                            entity));
                                    if (debug) {
                                        mmh.logDebug("EDE Angry Bee Head Dropped");
                                    }
                                    if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                        announceBeheading(entity,
                                                mmh.mobNames
                                                        .getString(name.toLowerCase() + ".angry",
                                                                MobHeads.valueOf("BEE_ANGRY").getName() + "")
                                                        .replace(" Head", ""),
                                                entity.getKiller(),
                                                mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                    }
                                }
                            } else if ((daAnger == 0) && (daNectar == true)) {
                                // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.bee.pollinated",
                                // defpercent)
                                if (mmh.getCommonHandler().DropIt(event,
                                        ConfigHelper.Double(mmh.chanceConfig, "chance_percent.bee.pollinated", defpercent))) {
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(
                                            MobHeads.valueOf("BEE_POLLINATED").getTexture(), mmh.mobNames
                                                    .getString(name.toLowerCase() + ".pollinated", "Pollinated Bee Head"),
                                            entity.getKiller()), entity));
                                    if (debug) {
                                        mmh.logDebug("EDE Pollinated Bee Head Dropped");
                                    }
                                    if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                        announceBeheading(entity,
                                                mmh.mobNames
                                                        .getString(name.toLowerCase() + ".pollinated",
                                                                MobHeads.valueOf("BEE_POLLINATED").getName() + "")
                                                        .replace(" Head", ""),
                                                entity.getKiller(),
                                                mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                    }
                                }
                            } else if ((daAnger == 0) && (daNectar == false)) {
                                // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.bee.chance_percent",
                                // defpercent)
                                if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent.bee.normal",
                                        defpercent))) {
                                    Drops.add(MoreMobHeadsLib
                                            .addSound(mmh.makeSkull(MobHeads.valueOf("BEE").getTexture(),
                                                    mmh.mobNames.getString(name.toLowerCase() + ".none", "Bee Head"),
                                                    entity.getKiller()), entity));
                                    if (debug) {
                                        mmh.logDebug("EDE Bee Head Dropped");
                                    }
                                    if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                        announceBeheading(entity,
                                                mmh.mobNames
                                                        .getString(name.toLowerCase() + ".none",
                                                                MobHeads.valueOf("BEE").getName() + "")
                                                        .replace(" Head", ""),
                                                entity.getKiller(),
                                                mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                    }
                                }
                            }
                            break;
                        case "LLAMA":
                            Llama daLlama = (Llama) entity;
                            String daLlamaColor = daLlama.getColor().toString();
                            String daLlamaName = LlamaHeads.valueOf(name + "_" + daLlamaColor).getName() + " Head";// daLlamaColor.toLowerCase().replace("b",
                            // "B").replace("c",
                            // "C").replace("g",
                            // "G").replace("wh",
                            // "Wh")
                            // + "
                            // Llama
                            // Head";
                            // log(name + "_" + daLlamaColor);
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.llama." +
                            // daLlamaColor.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.llama." + daLlamaColor.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(
                                        mmh.makeSkull(LlamaHeads.valueOf(name + "_" + daLlamaColor).getTexture(),
                                                mmh.mobNames.getString(
                                                        name.toLowerCase() + "." + daLlamaColor.toLowerCase(), daLlamaName),
                                                entity.getKiller()),
                                        entity));
                                if (debug) {
                                    mmh.logDebug("EDE Llama Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + daLlamaColor.toLowerCase(),
                                                            LlamaHeads.valueOf(name + "_" + daLlamaColor).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "HORSE":
                            Horse daHorse = (Horse) entity;
                            String daHorseColor = daHorse.getColor().toString();
                            String daHorseName = HorseHeads.valueOf(name + "_" + daHorseColor).getName() + " Head";// daHorseColor.toLowerCase().replace("b",
                            // "B").replace("ch",
                            // "Ch").replace("cr",
                            // "Cr").replace("d",
                            // "D")
                            // .replace("g", "G").replace("wh", "Wh").replace("_", " ") + " Horse Head";
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.horse." +
                            // daHorseColor.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.horse." + daHorseColor.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(
                                        mmh.makeSkull(HorseHeads.valueOf(name + "_" + daHorseColor).getTexture(),
                                                mmh.mobNames.getString(
                                                        name.toLowerCase() + "." + daHorseColor.toLowerCase(), daHorseName),
                                                entity.getKiller()),
                                        entity));
                                if (debug) {
                                    mmh.logDebug("EDE Horse Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + daHorseColor.toLowerCase(),
                                                            HorseHeads.valueOf(name + "_" + daHorseColor).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "MOOSHROOM":
                            name = "MUSHROOM_COW";
                        case "MUSHROOM_COW":
                            MushroomCow daMushroom = (MushroomCow) entity;
                            String daCowVariant = daMushroom.getVariant().toString();
                            String daCowName = daCowVariant.toLowerCase().replace("br", "Br").replace("re", "Re")
                                    + " Mooshroom Head";
                            if (debug) {
                                mmh.logDebug("EDE " + name + "_" + daCowVariant);
                            }
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.mushroom_cow." +
                            // daCowVariant.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.mushroom_cow." + daCowVariant.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(
                                        mmh.makeSkull(MobHeads.valueOf(name + "_" + daCowVariant).getTexture(),
                                                mmh.mobNames.getString(
                                                        name.toLowerCase() + "." + daCowVariant.toLowerCase(), daCowName),
                                                entity.getKiller()),
                                        entity));
                                if (debug) {
                                    mmh.logDebug("EDE Mooshroom Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + daCowVariant.toLowerCase(),
                                                            MobHeads.valueOf(name + "_" + daCowVariant).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "PANDA":
                            Panda daPanda = (Panda) entity;
                            String daPandaGene = daPanda.getMainGene().toString();
                            String daPandaName = daPandaGene.toLowerCase().replace("br", "Br").replace("ag", "Ag")
                                    .replace("la", "La").replace("no", "No").replace("p", "P").replace("we", "We")
                                    .replace("wo", "Wo") + " Panda Head";
                            if (daPandaGene.equalsIgnoreCase("normal")) {
                                daPandaName.replace("normal ", "");
                            }
                            if (debug) {
                                mmh.logDebug("EDE " + name + "_" + daPandaGene);
                            }
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.panda." +
                            // daPandaGene.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.panda." + daPandaGene.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(
                                        mmh.makeSkull(MobHeads.valueOf(name + "_" + daPandaGene).getTexture(),
                                                mmh.mobNames.getString(name.toLowerCase() + "." + daPandaGene.toLowerCase(),
                                                        daPandaName),
                                                entity.getKiller()),
                                        entity));
                                if (debug) {
                                    mmh.logDebug("EDE Panda Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + daPandaGene.toLowerCase(),
                                                            MobHeads.valueOf(name + "_" + daPandaGene).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "PARROT":
                            Parrot daParrot = (Parrot) entity;
                            String daParrotVariant = daParrot.getVariant().toString();
                            String daParrotName = daParrotVariant.toLowerCase().replace("b", "B").replace("c", "C")
                                    .replace("g", "G").replace("red", "Red") + " Parrot Head";
                            if (debug) {
                                mmh.logDebug("EDE " + name + "_" + daParrotVariant);
                            }
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.parrot." +
                            // daParrotVariant.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.parrot." + daParrotVariant.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(
                                        MobHeads.valueOf(name + "_" + daParrotVariant).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daParrotVariant.toLowerCase(),
                                                daParrotName),
                                        entity.getKiller()), entity));
                                if (debug) {
                                    mmh.logDebug("EDE Parrot Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + daParrotVariant.toLowerCase(),
                                                            MobHeads.valueOf(name + "_" + daParrotVariant).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
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
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.rabbit." +
                            // daRabbitType.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.rabbit." + daRabbitType.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(
                                        RabbitHeads.valueOf(name + "_" + daRabbitType).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daRabbitType.toLowerCase(),
                                                daRabbitName),
                                        entity.getKiller()), entity));
                                if (debug) {
                                    mmh.logDebug("EDE Rabbit Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + daRabbitType.toLowerCase(),
                                                            RabbitHeads.valueOf(name + "_" + daRabbitType).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "VILLAGER":
                            Villager daVillager = (Villager) entity; // Location jobsite =
                            // daVillager.getMemory(MemoryKey.JOB_SITE);
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
                                mmh.logDebug("EDE " + daName + "		 " + name + "_" + daVillagerProfession + "_"
                                        + daVillagerType);
                            }
                            String daVillagerName = VillagerHeads.valueOf(daName).getName() + " Head";
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.villager." +
                            // daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase(),
                            // defpercent)
                            if (mmh.getCommonHandler().DropIt(event,
                                    ConfigHelper.Double(mmh.chanceConfig, "chance_percent.villager."
                                                    + daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase(),
                                            defpercent))) {
                                Drops.add(
                                        MoreMobHeadsLib
                                                .addSound(
                                                        mmh.makeSkull(
                                                                VillagerHeads.valueOf(name + "_" + daVillagerProfession
                                                                        + "_" + daVillagerType).getTexture(),
                                                                mmh.mobNames
                                                                        .getString(
                                                                                name.toLowerCase() + "."
                                                                                        + daVillagerType.toLowerCase() + "."
                                                                                        + daVillagerProfession
                                                                                        .toLowerCase(),
                                                                                daVillagerName),
                                                                entity.getKiller()),
                                                        entity));
                                if (debug) {
                                    mmh.logDebug("EDE Villager Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(
                                                            name.toLowerCase() + "." + daVillagerType.toLowerCase() + "."
                                                                    + daVillagerProfession.toLowerCase(),
                                                            VillagerHeads.valueOf(name + "_" + daVillagerProfession + "_" + daVillagerType)
                                                                    .getName() + "")
                                                    .replace(" Head", ""), entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "ZOMBIE_VILLAGER":
                            ZombieVillager daZombieVillager = (ZombieVillager) entity;
                            String daZombieVillagerProfession = daZombieVillager.getVillagerProfession().toString();
                            String daZombieVillagerName = ZombieVillagerHeads
                                    .valueOf(name + "_" + daZombieVillagerProfession).getName() + " Head";
                            if (debug) {
                                mmh.logDebug("EDE " + name + "_" + daZombieVillagerProfession);
                            }
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.zombie_villager",
                            // defpercent)
                            if (mmh.getCommonHandler().DropIt(event,
                                    ConfigHelper.Double(mmh.chanceConfig, "chance_percent.zombie_villager", defpercent))) {
                                Drops.add(
                                        MoreMobHeadsLib
                                                .addSound(
                                                        mmh.makeSkull(
                                                                ZombieVillagerHeads
                                                                        .valueOf(name + "_" + daZombieVillagerProfession)
                                                                        .getTexture(),
                                                                mmh.mobNames
                                                                        .getString(
                                                                                name.toLowerCase() + "."
                                                                                        + daZombieVillagerProfession
                                                                                        .toLowerCase(),
                                                                                daZombieVillagerName),
                                                                entity.getKiller()),
                                                        entity));
                                if (debug) {
                                    mmh.logDebug("EDE Zombie Villager Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames.getString(
                                                            name.toLowerCase() + "." + daZombieVillagerProfession.toLowerCase(),
                                                            ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession)
                                                                    .getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
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
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.sheep." +
                            // daSheepColor.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.sheep." + daSheepColor.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(
                                        mmh.makeSkull(SheepHeads.valueOf(name + "_" + daSheepColor).getTexture(),
                                                mmh.mobNames.getString(
                                                        name.toLowerCase() + "." + daSheepColor.toLowerCase(), daSheepName),
                                                entity.getKiller()),
                                        entity));
                                if (debug) {
                                    mmh.logDebug("EDE Sheep Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + daSheepColor.toLowerCase(),
                                                            SheepHeads.valueOf(name + "_" + daSheepColor).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "TRADER_LLAMA":
                            TraderLlama daTraderLlama = (TraderLlama) entity;
                            String daTraderLlamaColor = daTraderLlama.getColor().toString();
                            String daTraderLlamaName = LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getName()
                                    + " Head";
                            if (debug) {
                                mmh.logDebug("EDE " + daTraderLlamaColor + "_" + name);
                            }
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.trader_llama." +
                            // daTraderLlamaColor.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.trader_llama." + daTraderLlamaColor.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(
                                        LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daTraderLlamaColor.toLowerCase(),
                                                daTraderLlamaName),
                                        entity.getKiller()), entity));
                                if (debug) {
                                    mmh.logDebug("EDE Trader Llama Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + daTraderLlamaColor.toLowerCase(),
                                                            LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getName() + "")
                                                    .replace(" Head", ""), entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "AXOLOTL":
                            Axolotl daAxolotl = (Axolotl) entity;
                            String daAxolotlVariant = daAxolotl.getVariant().toString();
                            String daAxolotlName = MobHeads117.valueOf(name + "_" + daAxolotlVariant).getName() + " Head";
                            if (debug) {
                                mmh.logDebug("EDE " + daAxolotlVariant + "_" + name);
                            }
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.axolotl." +
                            // daAxolotlVariant.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.axolotl." + daAxolotlVariant.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(
                                        MobHeads117.valueOf(name + "_" + daAxolotlVariant).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase() + "." + daAxolotlVariant.toLowerCase(),
                                                daAxolotlName),
                                        entity.getKiller()), entity));
                                if (debug) {
                                    mmh.logDebug("EDE Axolotl Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + daAxolotlVariant.toLowerCase(),
                                                            MobHeads117.valueOf(name + "_" + daAxolotlVariant).getName() + "")
                                                    .replace(" Head", ""), entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "GOAT":
                            Goat daGoat = (Goat) entity;
                            String daGoatVariant;
                            String daGoatName;// = MobHeads117.valueOf(name + "_" + daAxolotlVariant).getName() + " Head";
                            if (daGoat.isScreaming()) {
                                // Giving screaming goat head
                                daGoatVariant = "SCREAMING";
                                daGoatName = MobHeads117.valueOf(name + "_" + daGoatVariant).getName() + " Head";
                            } else {
                                // give goat head
                                daGoatVariant = "NORMAL";
                                daGoatName = MobHeads117.valueOf(name + "_" + daGoatVariant).getName() + " Head";
                            }
                            if (debug) {
                                mmh.logDebug("EDE " + daGoatVariant + "_" + name);
                            }
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent.goat." +
                            // daGoatVariant.toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig,
                                    "chance_percent.goat." + daGoatVariant.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(mmh
                                                .makeSkull(MobHeads117.valueOf(name + "_" + daGoatVariant).getTexture(),
                                                        mmh.mobNames.getString(
                                                                name.toLowerCase() + "." + daGoatVariant.toLowerCase(), daGoatName),
                                                        entity.getKiller()),
                                        entity));
                                if (debug) {
                                    mmh.logDebug("EDE Goat Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase() + "." + daGoatVariant.toLowerCase(),
                                                            MobHeads117.valueOf(name + "_" + daGoatVariant).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            break;
                        case "STRIDER":
                            Strider daStrider = (Strider) entity;
                            PersistentDataContainer pdc2 = daStrider.getPersistentDataContainer();
                            boolean isShivering = Boolean.parseBoolean(daStrider.getPersistentDataContainer().get(mmh.SHIVERING_KEY, PersistentDataType.STRING));
                            if (mmh.chance25oftrue()) { // chance50oftrue()
                                // isShivering
                                name = name.concat("_SHIVERING");
                                // ConfigHelper.Double(mmh.chanceConfig,
                                // "chance_percent." + name.toLowerCase(),
                                // defpercent)
                                if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(), defpercent))) {
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture(), mmh.mobNames.getString(name.toLowerCase() + "_shivering", "Shivering " + event.getEntity().getName() + " Head"), entity.getKiller()), entity));
                                    if (debug) {
                                        mmh.logDebug("EDE " + name + " Head Dropped");
                                    }
                                    if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                        announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "").replace(" Head", ""), entity.getKiller(),
                                                mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                    }
                                }
                            } else // ConfigHelper.Double(mmh.chanceConfig,
                                // "chance_percent." + name.toLowerCase(),
                                // defpercent)
                                if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(), defpercent))) {
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture(), mmh.mobNames.getString(name.toLowerCase(),
                                            event.getEntity().getName() + " Head"), entity.getKiller()), entity));
                                    if (debug) {
                                        mmh.logDebug("EDE " + name + " Head Dropped");
                                    }
                                    if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                        announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "").replace(" Head", ""), entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                    }
                                }

                            if (debug) {
                                mmh.logDebug("EDE " + MobHeads.valueOf(name) + " killed");
                            }
                            break;
                        case "FROG":
                            String frogName = MoreMobHeadsLib.getName(name, entity);
                            String daFrogName = MobHeads119.valueOf(frogName).getName() + " Head";
                            // mmh.chanceConfig.getDouble("chance_percent." + frogName.replace("_",
                            // ".").toLowerCase(), defpercent)
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + frogName.replace("_",
                            // ".").toLowerCase(), defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + frogName.replace("_", ".").toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads119.valueOf(frogName).getTexture(), mmh.mobNames.getString(frogName.replace("_", ".").toLowerCase(),
                                        daFrogName), entity.getKiller()), entity));
                                if (debug) {
                                    mmh.logDebug("EDE Frog Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(frogName.replace("_", ".").toLowerCase(), MobHeads119.valueOf(frogName).getName() + "").replace(" Head", ""),
                                            entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }

                            break;
                        case "TADPOLE":
                        case "ALLAY":
                        case "WARDEN":
                            if (debug) {
                                mmh.logDebug("EDE name=" + name);
                            }
                            if (debug) {
                                mmh.logDebug("EDE texture=" + MobHeads119.valueOf(name).getTexture());
                            }
                            if (debug) {
                                mmh.logDebug("EDE location=" + entity.getLocation());
                            }
                            if (debug) {
                                mmh.logDebug("EDE getName=" + event.getEntity().getName());
                            }
                            if (debug) {
                                mmh.logDebug("EDE killer=" + entity.getKiller().toString());
                            }
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(),
                            // defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(), defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads119.valueOf(name).getTexture(), mmh.mobNames.getString(name.toLowerCase(),
                                        event.getEntity().getName() + " Head"), entity.getKiller()), entity));
                                if (debug) {
                                    mmh.logDebug("EDE " + name + " Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase(), MobHeads119.valueOf(name).getName() + "").replace(" Head", ""),
                                            entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            if (debug) {
                                mmh.logDebug("EDE " + MobHeads119.valueOf(name) + " killed");
                            }
                            break;
                        case "CAMEL":
                        case "SNIFFER":
                            if (debug) {
                                mmh.logDebug("EDE name=" + name);
                            }
                            if (debug) {
                                mmh.logDebug("EDE texture=" + MobHeads120.valueOf(name).getTexture());
                            }
                            if (debug) {
                                mmh.logDebug("EDE location=" + entity.getLocation());
                            }
                            if (debug) {
                                mmh.logDebug("EDE getName=" + event.getEntity().getName());
                            }
                            if (debug) {
                                mmh.logDebug("EDE killer=" + entity.getKiller().toString());
                            }
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(),
                            // defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(),
                                    defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(
                                        mmh.makeSkull(MobHeads120.valueOf(name).getTexture(),
                                                mmh.mobNames.getString(name.toLowerCase(),
                                                        event.getEntity().getName() + " Head"),
                                                entity.getKiller()),
                                        entity));
                                if (debug) {
                                    mmh.logDebug("EDE " + name + " Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase(), MobHeads120.valueOf(name).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            if (debug) {
                                mmh.logDebug("EDE " + MobHeads120.valueOf(name) + " killed");
                            }
                            break;
                        case "VEX":
                            if (debug) {
                                mmh.logDebug("EDE name=" + name);
                            }
                            if (debug) {
                                mmh.logDebug("EDE texture=" + MobHeads.valueOf(name).getTexture());
                            }
                            if (debug) {
                                mmh.logDebug("EDE location=" + entity.getLocation());
                            }
                            if (debug) {
                                mmh.logDebug("EDE getName=" + event.getEntity().getName());
                            }
                            if (debug) {
                                mmh.logDebug("EDE killer=" + entity.getKiller().toString());
                            }
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(),
                                    defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                        mmh.mobNames.getString(name.toLowerCase(), event.getEntity().getName() + " Head"), entity.getKiller()), entity));
                                if (debug) {
                                    mmh.logDebug("EDE " + name + " Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity, mmh.mobNames.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "").replace(" Head", ""),
                                            entity.getKiller(), mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                                if (coinFlip()) {
                                    String name2 = name;
                                    name2 = name2 + "_ANGRY";
                                    Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name2).getTexture(),
                                            mmh.mobNames.getString(name2.toLowerCase(), event.getEntity().getName() + " Head"), entity.getKiller()), entity));
                                    if (debug) {
                                        mmh.logDebug("EDE " + name2 + " Head Dropped");
                                    }
                                }
                            }
                            if (debug) {
                                mmh.logDebug("EDE " + MobHeads.valueOf(name) + " killed");
                            }
                            break;
                        default:
                            // mmh.makeSkull(MobHeads.valueOf(name).getTexture(), name);
                            if (debug) {
                                mmh.logDebug("EDE name=" + name + " line:1122");
                            }
                            if (debug) {
                                mmh.logDebug(
                                        "EDE texture=" + MobHeads.valueOf(name).getTexture() + " line:1123");
                            }
                            if (debug) {
                                mmh.logDebug("EDE location=" + entity.getLocation() + " line:1124");
                            }
                            if (debug) {
                                mmh.logDebug("EDE getName=" + event.getEntity().getName() + " line:1125");
                            }
                            if (debug) {
                                mmh.logDebug("EDE killer=" + entity.getKiller().toString() + " line:1126");
                            }
                            // ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(),
                            // defpercent)
                            if (mmh.getCommonHandler().DropIt(event, ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + name.toLowerCase(),
                                    defpercent))) {
                                Drops.add(MoreMobHeadsLib.addSound(
                                        mmh.makeSkull(MobHeads.valueOf(name).getTexture(),
                                                mmh.mobNames.getString(name.toLowerCase(),
                                                        event.getEntity().getName() + " Head"),
                                                entity.getKiller()),
                                        entity));
                                if (debug) {
                                    mmh.logDebug("EDE " + name + " Head Dropped");
                                }
                                if (mmh.getConfig().getBoolean("announce.mobs.enabled", true)) {
                                    announceBeheading(entity,
                                            mmh.mobNames
                                                    .getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
                                                    .replace(" Head", ""),
                                            entity.getKiller(),
                                            mmh.getConfig().getBoolean("announce.mobs.displayname", true));
                                }
                            }
                            if (debug) {
                                mmh.logDebug("EDE " + MobHeads.valueOf(name) + " killed");
                            }
                            break;
                    }
                }
                // }
                // }
                return;
            }
        }
    }

    @SuppressWarnings({"static-access", "unused"})
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) { // onEntitySpawn(EntitySpawnEvent e) { // TODO:
        // onCreatureSpawn
        if (mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)) {
            Entity entity = event.getEntity();
            if (entity instanceof WanderingTrader) {
                // traderHeads2 = YamlConfiguration.loadConfiguration(traderFile2);
                if (debug) {
                    mmh.logDebug("CSE WanderingTrader spawned");
                }
                WanderingTrader trader = (WanderingTrader) entity;
                List<MerchantRecipe> recipes = new ArrayList<>();
                final List<MerchantRecipe> oldRecipes = trader.getRecipes();

                /*
                  Player Heads
                 */
                if (mmh.getConfig().getBoolean("wandering_trades.player_heads.enabled", true)) {
                    // Check if default max heads is larger than number of playerheads
                    int numOfplayerheads = (playerhead_recipes.size() - 1) >= 0 ? playerhead_recipes.size() - 1 : 0;
                    int phMaxDef = (3 >= numOfplayerheads) ? 3 : numOfplayerheads;

                    int playerRandom = Utils.randomBetween(mmh.getConfig().getInt("wandering_trades.player_heads.min", 0),
                            mmh.getConfig().getInt("wandering_trades.player_heads.max", phMaxDef));
                    if (debug) {
                        mmh.logDebug("CSE.PH playerRandom=" + playerRandom);
                    }
                    if (playerRandom > 0) {
                        if (debug) {
                            mmh.logDebug("CSE.PH playerRandom > 0");
                        }

                        if (debug) {
                            mmh.logDebug("CSE.PH numOfplayerheads=" + numOfplayerheads);
                        }
                        HashSet<Integer> used = new HashSet<>();
                        outerLoop:
                        for (int i = 0; i < playerRandom; i++) {
                            int randomPlayerHead = Utils.randomBetween(0, numOfplayerheads);
                            while (used.contains(randomPlayerHead) || (randomPlayerHead > numOfplayerheads)) { // while we
                                // have
                                // already
                                // used
                                // the
                                // number
                                randomPlayerHead = Utils.randomBetween(0, numOfplayerheads); // generate a new one because
                                // it's already used
                                // infinite loop catch
                                if (i >= 500) {
                                    if (debug) {
                                        mmh.logDebug("CSE.PH timed out");
                                    }
                                    break outerLoop;
                                }
                            }
                            // by this time, add will be unique
                            used.add(randomPlayerHead);
                            recipes.add(playerhead_recipes.get(randomPlayerHead));
                        }
                        used.clear();
                    }
                }
                /*
                  Block Heads
                 */
                if (mmh.getConfig().getBoolean("wandering_trades.block_heads.enabled", true)) {
                    // check if default max block heads is larger than number of blockheads
                    int numOfblockheads = BHNum >= 0 ? BHNum : 0;
                    int bhMaxDef = (5 >= numOfblockheads) ? 5 : numOfblockheads;

                    int min = mmh.getConfig().getInt("wandering_trades.block_heads.pre_116.min", 0);
                    int max;
                    if (Double.parseDouble(mmh.getMCVersion().substring(0, 4)) >= 1.16) {
                        max = mmh.getConfig().getInt("wandering_trades.block_heads.pre_116.max", bhMaxDef);
                    } else {
                        max = mmh.getConfig().getInt("wandering_trades.block_heads.pre_116.max", bhMaxDef);
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

                        if (debug) {
                            mmh.logDebug("CSE numOfblockheads=" + numOfblockheads);
                        }
                        HashSet<Integer> used = new HashSet<>();
                        outerLoop:
                        for (int i = 0; i < blockRandom; i++) {
                            if (debug) {
                                mmh.logDebug("CSE i=" + i);
                            }
                            int randomBlockHead = Utils.randomBetween(0, numOfblockheads);
                            while (used.contains(randomBlockHead)) { // while we have already used the number
                                randomBlockHead = Utils.randomBetween(0, numOfblockheads); // generate a new one because
                                // it's already used
                                if (i >= 500) {
                                    if (debug) {
                                        mmh.logDebug("CSE.BH1 timed out");
                                    }
                                    break outerLoop;
                                }
                            }
                            // by this time, add will be unique
                            used.add(randomBlockHead);

                            recipes.add(blockhead_recipes.get(randomBlockHead));
                        }
                        used.clear();
                    }
                }

                /*
                  Block Heads 2
                 */
                if (mmh.getConfig().getBoolean("wandering_trades.block_heads.enabled", true)) {
                    if (Double.parseDouble(mmh.getMCVersion().substring(0, 4)) >= 1.16) {
                        // check if default max BH is larger than number of block heads
                        int numOfblockheads = ((BHNum + BHNum2) - 1) >= 0 ? (BHNum + BHNum2) - 1 : 0;
                        int bhMaxDef = (5 >= numOfblockheads) ? 5 : numOfblockheads;

                        int min = mmh.getConfig().getInt("wandering_trades.block_heads.is_116.min", 0);
                        int max = mmh.getConfig().getInt("wandering_trades.block_heads.is_116.max", bhMaxDef);
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

                            if (debug) {
                                mmh.logDebug("CSE numOfblockheads=" + numOfblockheads);
                            }
                            HashSet<Integer> used = new HashSet<>();
                            outerLoop:
                            for (int i = 0; i < blockRandom; i++) {
                                if (debug) {
                                    mmh.logDebug("CSE i=" + i);
                                }
                                int randomBlockHead = Utils.randomBetween(BHNum - 1, numOfblockheads);
                                while (used.contains(randomBlockHead)) { // while we have already used the number
                                    randomBlockHead = Utils.randomBetween(BHNum - 1, numOfblockheads); // generate a new
                                    // one because
                                    // it's already
                                    // used
                                    if (i >= 500) {
                                        if (debug) {
                                            mmh.logDebug("CSE.BH2 timed out");
                                        }
                                        break outerLoop;
                                    }
                                }
                                // by this time, add will be unique
                                used.add(randomBlockHead);

                                recipes.add(blockhead_recipes.get(randomBlockHead));
                            }
                            used.clear();
                        }
                    }
                    if (Double.parseDouble(mmh.getMCVersion().substring(0, 4)) >= 1.17) {
                        // check if default max BH is larger than number of blockheads
                        int numOfblockheads = ((BHNum + BHNum2 + BHNum3) - 1) >= 0 ? (BHNum + BHNum2 + BHNum3) - 1 : 0;
                        int bhMaxDef = (5 >= numOfblockheads) ? 5 : numOfblockheads;

                        int min1 = mmh.getConfig().getInt("wandering_trades.block_heads.is_117.min", 0);
                        // int max1 = mmh.getConfig().getInt("wandering_trades.block_heads.is_117.max",
                        // 5) / 2;
                        int max1 = mmh.getConfig().getInt("wandering_trades.block_heads.is_117.max", 5);
                        if (debug) {
                            mmh.logDebug("CSE BH2 min=" + min1 + " max=" + max1);
                        }
                        int blockRandom1 = Utils.randomBetween(min1, max1);
                        if (debug) {
                            mmh.logDebug("CSE blockRandom=" + blockRandom1);
                        }
                        if (blockRandom1 > 0) {
                            if (debug) {
                                mmh.logDebug("CSE blockRandom > 0");
                            }

                            if (debug) {
                                mmh.logDebug("CSE numOfblockheads=" + numOfblockheads);
                            }
                            HashSet<Integer> used = new HashSet<>();
                            outerLoop:
                            for (int i = 0; i < blockRandom1; i++) {
                                if (debug) {
                                    mmh.logDebug("CSE i=" + i);
                                }
                                int randomBlockHead = Utils.randomBetween((BHNum + BHNum2) - 1, numOfblockheads);
                                while (used.contains(randomBlockHead) || (randomBlockHead > numOfblockheads)) { // while
                                    // we
                                    // have
                                    // already
                                    // used
                                    // the
                                    // number
                                    randomBlockHead = Utils.randomBetween((BHNum + BHNum2) - 1, numOfblockheads); // generate
                                    // a new
                                    // one
                                    // because
                                    // it's
                                    // already
                                    // used
                                    if (i >= 500) {
                                        if (debug) {
                                            mmh.logDebug("CSE.BH3 timed out");
                                        }
                                        break outerLoop;
                                    }
                                }
                                // by this time, add will be unique
                                used.add(randomBlockHead);

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
                    int numOfCustomTrades = (custometrade_recipes.size() - 1) >= 0 ? custometrade_recipes.size() - 1
                            : 0;
                    numOfCustomTrades = numOfCustomTrades - 1;
                    int ctMaxDef = (5 >= numOfCustomTrades) ? 5 : numOfCustomTrades;

                    int customRandom = Utils.randomBetween(
                            mmh.getConfig().getInt("wandering_trades.custom_trades.min", 0),
                            mmh.getConfig().getInt("wandering_trades.custom_trades.max", ctMaxDef));

                    // if(debug){logDebug("CSE numOfCustomTrades=" + numOfCustomTrades);}
                    // int customRandom =
                    // randomBetween(getConfig().getInt("wandering_trades.min_custom_trades", 0),
                    // mmh.getConfig().getInt("wandering_trades.max_custom_trades", 3));
                    if (debug) {
                        mmh.logDebug("CSE customRandom=" + customRandom);
                    }
                    if (customRandom > 0) {
                        if (debug) {
                            mmh.logDebug("CSE customRandom > 0");
                        }
                        // for(int randomCustomTrade=1; randomCustomTrade<numOfCustomTrades;
                        // randomCustomTrade++){
                        HashSet<Integer> used = new HashSet<>();
                        for (int i = 0; i < customRandom; i++) {

                            double chance = Math.random();
                            if (debug) {
                                mmh.logDebug("CSE chance=" + chance + " line:1540");
                            }
                            if (mmh.traderCustom.getDouble("custom_trades.trade_" + i + ".chance", 0.002) > chance) {

                                recipes.add(custometrade_recipes.get(i));
                            }
                            if (i >= 500) {
                                if (debug) {
                                    mmh.logDebug("CSE.CT timed out");
                                }
                                break;
                            }
                        }
                        used.clear();
                    }
                }

                if (mmh.getConfig().getBoolean("wandering_trades.keep_default_trades", true)) {
                    recipes.addAll(oldRecipes);
                }
                trader.setRecipes(recipes);
                /* }});// */
            }
        }
    }

    private void log(String msg) {
        mmh.log(msg);
    }

    private void logDebug(String msg) {
        mmh.logDebug(msg);
    }

    public boolean coinFlip() {
        return new Random().nextBoolean();
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

    public void announceBeheading(Entity entity, String entityName2, Player player, boolean display) {
        UUID damagingPlayerUUID = player != null ? player.getUniqueId() : null;
        ItemStack damagingWeapon = mmh.playerWeapons.get(damagingPlayerUUID);
        String killerName;
        String entityName;
        if (display) {
            killerName = player.getDisplayName();
            entityName = entityName2;
        } else {
            killerName = player.getName();
            entityName = entity.getName();
        }

        if ((damagingPlayerUUID != null) && (damagingWeapon != null)) {
            Player damagingPlayer = Bukkit.getPlayer(damagingPlayerUUID);
            if (damagingPlayer != null) {
                String weaponName = damagingWeapon.getItemMeta().getDisplayName();
                if (!damagingWeapon.getItemMeta().hasDisplayName()) {
                    weaponName = damagingWeapon.getType().name();
                }
                int randomIndex = (int) (Math.random() * mmh.beheadingMessages.getConfigurationSection("messages").getKeys(false).size()) + 1;
                String announcement = mmh.beheadingMessages.getString("messages.message_" + randomIndex, "%killerName% beheaded %entityName% with %weaponName%.")
                        .replace("%killerName%", killerName).replace("%entityName%", entityName).replace("%weaponName%", weaponName);

                Bukkit.broadcastMessage(ChatColorUtils.setColors(announcement));
            }
        } else if ((damagingPlayerUUID != null) && (damagingWeapon == null)) { // Bare Hands?
            String weaponName = "Bare Hands";
            int randomIndex = (int) (Math.random() * mmh.beheadingMessages.getConfigurationSection("messages").getKeys(false).size()) + 1;
            String announcement = mmh.beheadingMessages.getString("messages.message_" + randomIndex, "%killerName% beheaded %entityName% with %weaponName%.")
                    .replace("%killerName%", killerName).replace("%entityName%", entityName).replace("%weaponName%", weaponName);

            Bukkit.broadcastMessage(ChatColorUtils.setColors(announcement));

        }

        mmh.playerWeapons.remove(damagingPlayerUUID);
    }

    // public class EquipmentSlotResolver {

    public Optional<ItemStack> resolveDamagingWeapon(PlayerInventory playerInventory, DamageCause damageCause) {
        if (debug) {
            mmh.logDebug("DamageCause=" + damageCause.toString());
        }
        switch (damageCause) {
            case ENTITY_ATTACK:
                // Check if the player is holding any item in the main hand
                ItemStack mainHandItem = playerInventory.getItemInMainHand();
                if (debug) {
                    mmh.logDebug("mainHandItem=" + mainHandItem.getType());
                }
                if (!mainHandItem.getType().equals(Material.AIR)) {
                    return Optional.of(mainHandItem);
                }
                break;
            case PROJECTILE:
                // Ranged damage
                Optional<ItemStack> bowItem = getWeaponItem(playerInventory, Material.BOW);
                if (bowItem.isPresent()) {
                    return bowItem;
                }
                Optional<ItemStack> crossbowItem = getWeaponItem(playerInventory, Material.CROSSBOW);
                if (crossbowItem.isPresent()) {
                    return crossbowItem;
                }
                Optional<ItemStack> tridentItem = getWeaponItem(playerInventory, Material.TRIDENT);
                if (tridentItem.isPresent()) {
                    return tridentItem;
                }
                Optional<ItemStack> snowItem = getWeaponItem(playerInventory, Material.SNOWBALL);
                if (snowItem.isPresent()) {
                    return snowItem;
                }
                Optional<ItemStack> eggItem = getWeaponItem(playerInventory, Material.EGG);
                if (eggItem.isPresent()) {
                    return eggItem;
                }
                break;
            case MAGIC:
                // Potion
                return getWeaponItem(playerInventory, Material.SPLASH_POTION);
            case ENTITY_EXPLOSION:
                return Optional.of(new ItemStack(Material.TNT));

            default:
                break;
        }

        return Optional.empty();
    }

    private Optional<ItemStack> getWeaponItem(PlayerInventory playerInventory, Material material) {
        ItemStack mainHandItem = playerInventory.getItemInMainHand();
        ItemStack offHandItem = playerInventory.getItemInOffHand();

        if (mainHandItem.getType() == material) {
            return Optional.of(mainHandItem);
        } else if (offHandItem.getType() == material) {
            return Optional.of(offHandItem);
        }

        return Optional.empty();
    }
    // }

}