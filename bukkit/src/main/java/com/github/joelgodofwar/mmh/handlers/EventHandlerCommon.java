package com.github.joelgodofwar.mmh.handlers;

import com.github.joelgodofwar.mmh.Heads;
import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.util.ChatColorUtils;
import com.github.joelgodofwar.mmh.util.Networks;
import com.github.joelgodofwar.mmh.util.Utils;
import com.github.joelgodofwar.mmh.util.datatypes.JsonDataType;
import com.github.joelgodofwar.mmh.util.mob.NameTag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.TileState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.logging.Level;

import static com.github.joelgodofwar.mmh.MoreMobHeads.*;

@SuppressWarnings({"deprecation"})
public class EventHandlerCommon implements Listener {

    // Persistent Heads
    private final NamespacedKey NAME_KEY;
    private final NamespacedKey LORE_KEY;
    private final PersistentDataType<String, String[]> LORE_PDT = new JsonDataType<>(String[].class);

    MoreMobHeads mmh;
    public EventHandlerCommon(MoreMobHeads mmh) {
        this.mmh = mmh;
        NAME_KEY = new NamespacedKey(mmh, "head_name");
        LORE_KEY = new NamespacedKey(mmh, "head_lore");
        Bukkit.getPluginManager().registerEvents(this, mmh);
    }
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEntityEvent event) {
        try {
            Player player = event.getPlayer();
            if (player.hasPermission("moremobheads.nametag")) {
                if (debug) {
                    mmh.log("moremobheads.nametag=true");
                }
                if (mmh.config.getBoolean("mob.nametag", false)) {
                    if (debug) {
                        mmh.log("mob.nametag=true");
                    }
                    PlayerInventory inv = player.getInventory();
                    Material material = inv.getItemInMainHand().getType();
                    Material material2 = inv.getItemInOffHand().getType();
                    String name = "";
                    if (material.equals(Material.NAME_TAG)) {
                        name = Utils.getItemMeta(inv.getItemInMainHand()).getDisplayName();
                        if (debug) {
                            mmh.logDebug("PIEE" + player.getDisplayName() + " Main hand name=" + name);
                        }
                    }
                    if (material2.equals(Material.NAME_TAG)) {
                        name = Utils.getItemMeta(inv.getItemInOffHand()).getDisplayName();
                        if (debug) {
                            mmh.logDebug("PIEE " + player.getDisplayName() + " Off hand name=" + name);
                        }
                    }

                    if (material.equals(Material.NAME_TAG) || material2.equals(Material.NAME_TAG)) {
                        if (Bukkit.getPluginManager().getPlugin("SilenceMobs") != null) {
                            if (name.toLowerCase().contains("silenceme") || name.toLowerCase().contains("silence me")) {
                                return;
                            }
                        }
                        LivingEntity mob = (LivingEntity) event.getRightClicked();
                        if (debug) {
                            mmh.log("canwearhead=" + NameTag.canWearHead(mob));
                        }
                        if (NameTag.canWearHead(mob)) {
                            boolean enforcewhitelist = mmh.config.getBoolean("whitelist.enforce", false);
                            boolean enforceblacklist = mmh.config.getBoolean("blacklist.enforce", false);
                            boolean onwhitelist = mmh.config.getString("whitelist.player_head_whitelist", "").toLowerCase().contains(name.toLowerCase());
                            boolean onblacklist = mmh.config.getString("blacklist.player_head_blacklist", "").toLowerCase().contains(name.toLowerCase());
                            if (enforcewhitelist && enforceblacklist) {
                                if (onwhitelist && !(onblacklist)) {
                                    Heads.giveMobHead(mob, name);
                                } else {
                                    event.setCancelled(true); // return;
                                    if (debug) {
                                        mmh.log(Level.INFO, "PIE - Name Error 1");
                                    }
                                }
                            } else if (enforcewhitelist) {
                                if (onwhitelist) {
                                    Heads.giveMobHead(mob, name);
                                } else {
                                    event.setCancelled(true); // return;
                                    if (debug) {
                                        mmh.log(Level.INFO, "PIE - Name not on whitelist.");
                                    }
                                }
                            } else if (enforceblacklist) {
                                if (!onblacklist) {
                                    Heads.giveMobHead(mob, name);
                                } else {
                                    event.setCancelled(true); // return;
                                    if (debug) {
                                        mmh.log(Level.INFO, "PIE - Name is on blacklist.");
                                    }
                                }
                            } else {
                                Heads.giveMobHead(mob, name);
                            }
                        }
                    }
                } else if (debug) {
                    mmh.log("mob.nametag=false");
                }
            } else
            if (debug) {
                mmh.log("moremobheads.nametag=false");
            }
        } catch (Exception e) {
            mmh.stacktraceInfo();
            e.printStackTrace();
        }

    }

    public ItemStack dropMobHead(Entity entity, String name, Player killer) {
        ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = Utils.getItemMeta(helmet);
        meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(name));
        meta.setDisplayName(name + "'s Head");
        ArrayList<String> lore = new ArrayList<>();
        if (mmh.config.getBoolean("lore.show_killer", true)) {
            String killed_by = ChatColorUtils.setColors(mmh.mobNames.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>"));
            lore.add(ChatColor.RESET + killed_by.replace("<player>", "" + killer.getDisplayName()));
        }
        if (mmh.config.getBoolean("lore.show_plugin_name", true)) {
            lore.add(ChatColor.AQUA + "" + pluginDisplayName());
        }
        meta.setLore(lore);
        meta.setLore(lore);
        helmet.setItemMeta(meta);
        entity.getWorld().dropItemNaturally(entity.getLocation(), helmet);
        return helmet;
    }

    public boolean DropIt(EntityDeathEvent event, double chance) {
        if (chance == 0) {
            return false;
        }
        Player player = event.getEntity().getKiller();
        if (player == null) return false;
        ItemStack itemstack = player.getInventory().getItemInMainHand();

        int enchLevel = 0;
        if (mmh.config.getBoolean("apply_looting", true)) {
            enchLevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }

        if (debug) {
            mmh.logDebug("DI itemstack=" + itemstack.getType());
            mmh.logDebug("DI enchantmentlevel=" + enchLevel);
        }

        int rand = new Random().nextInt(10000);

        if (debug) {
            mmh.logDebug("DI rand=" + rand);
            mmh.logDebug("DI chance=" + chance);
        }

        chance = (1.0d - (chance / 100.0d + enchLevel * 0.005f)) * 10000;
        if (debug) {
            mmh.logDebug("DI fail chance=" + chance);
        }


        return (rand >= chance) || mmh.isDev;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        /* Notify Ops */
        if (Networks.isUpdateAvailable() && (player.isOp() || player.hasPermission("moremobheads.showUpdateAvailable"))) {
            String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>\"}}]";
            links = links.replace("<DownloadLink>", Networks.downloadLink).replace("<Download>", get("mmh.version.download"))
                    .replace("<Donate>", get("mmh.version.donate")).replace("<please_update>", get("mmh.version.please_update"))
                    .replace("<Donate_msg>", get("mmh.version.donate.message")).replace("<Notes>", get("mmh.version.notes"))
                    .replace("<Notes_msg>", get("mmh.version.notes.message"));
            String versions = "" + ChatColor.GRAY + get("mmh.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + get("mmh.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
            player.sendMessage("" + ChatColor.GRAY + get("mmh.version.message").replace("<MyPlugin>", ChatColor.GOLD + pluginDisplayName() + ChatColor.GRAY));
            Utils.sendJson(player, links);
            player.sendMessage(versions.replace("{nVers}", Networks.getUpdateNewVersion()).replace("{oVers}", Networks.getUpdateOldVersion()));
        }
    }

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
        String name = meta.getDisplayName();
        @Nullable List<String> lore = meta.getLore();
        Block block = event.getBlockPlaced();
        TileState skullState = (TileState) block.getState();
        PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
        if (lore != null) {
            skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0]));
        }
        skullState.update();

        if (debug) {
            String strLore = "no lore";
            if (lore != null) {
                strLore = lore.toString();
            }
            mmh.log(Level.INFO, "Player " + event.getPlayer().getName() + " placed a head named \"" + name + "\" with lore='" + strLore + "' at " + event.getBlockPlaced().getLocation());
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
            mmh.log(Level.INFO, "BDIE - Persistent head completed.");
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
        if (!mmh.config.getBoolean("event.piston_extend", true)) {
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
            mmh.log(Level.INFO, "HB - Persistent head completed.");
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
            mmh.log(Level.INFO, "HE - Persistent head completed.");
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
            mmh.log(Level.INFO, "UD - Persistent head completed.");
        }
        return false;
    }

}
