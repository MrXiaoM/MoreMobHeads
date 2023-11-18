package com.github.joelgodofwar.mmh.gui;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.enums.*;
import com.github.joelgodofwar.mmh.util.ConfigHelper;
import com.github.joelgodofwar.mmh.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.github.joelgodofwar.mmh.MoreMobHeads.get;

public class GuiHeadsList implements IGui, InventoryHolder {
    public static class Item {
        public String name;
        public String translateString;
        public String texture;
        public ItemStack getItemStack(MoreMobHeads mmh) {
            ItemStack item = mmh.makeSkulls(texture, mmh.mobNames.getString(translateString, name), 1);
            double chance = ConfigHelper.Double(mmh.chanceConfig, "chance_percent." + translateString, 0.013);
            if (chance > 0) {
                ItemMeta meta = Utils.getItemMeta(item);
                List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
                lore.add("");
                lore.add(ChatColor.GRAY + get("mmh.gui.headslist.dropchance").replace("<chance>", String.valueOf(chance)));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            return item;
        }
        static Item of(String name, String translateString, String texture) {
            Item item = new Item();
            item.name = name;
            item.translateString = translateString;
            item.texture = texture;
            return item;
        }
    }
    static List<Item> items = new ArrayList<>() {{
        double mcVersion = Double.parseDouble(MoreMobHeads.getMCVersion().substring(0, 4));

        for (MobHeads head : MobHeads.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
        if (mcVersion >= 1.17) for (MobHeads117 head : MobHeads117.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
        if (mcVersion >= 1.19) for (MobHeads119 head : MobHeads119.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
        if (mcVersion >= 1.20) for (MobHeads120 head : MobHeads120.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
        for (CatHeads head : CatHeads.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
        for (HorseHeads head : HorseHeads.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
        for (LlamaHeads head : LlamaHeads.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
        for (RabbitHeads head : RabbitHeads.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
        for (SheepHeads head : SheepHeads.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
        for (TropicalFishHeads head : TropicalFishHeads.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
        for (VillagerHeads head : VillagerHeads.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
        for (ZombieVillagerHeads head : ZombieVillagerHeads.values()) {
            add(Item.of(head.getName(), head.getNameString(), head.getTexture()));
        }
    }};
    MoreMobHeads mmh;
    Player player;
    int page;
    Inventory inv;
    public GuiHeadsList(MoreMobHeads mmh, Player player, int page) {
        this.mmh = mmh;
        this.player = player;
        this.page = page > 0 ? page : 1;
    }
    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public MoreMobHeads getPlugin() {
        return mmh;
    }

    @Override
    public Inventory newInventory() {
        inv = Bukkit.createInventory(this, 54, get("mmh.gui.headslist.title"));
        if (page < 1) page = 1;
        int offset = (page - 1) * 45;
        for (int i = 0; i < 45; i++) {
            if (offset + i >= items.size()) break;
            ItemStack item = items.get(offset + i).getItemStack(mmh);
            inv.setItem(i, item);
        }
        ItemStack frame = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = Utils.getItemMeta(frame);
        meta.setDisplayName(ChatColor.WHITE.toString());
        frame.setItemMeta(meta);
        for (int i = inv.getSize() - 9; i < inv.getSize(); i++) {
            inv.setItem(i, frame);
        }
        if (prevEnable()) {
            ItemStack prevPage = new ItemStack(Material.FEATHER);
            meta = Utils.getItemMeta(prevPage);
            meta.setDisplayName(ChatColor.YELLOW + get("mmh.gui.headslist.prev-page"));
            prevPage.setItemMeta(meta);
            inv.setItem(45, prevPage);
        }
        if (nextEnable()) {
            ItemStack nextPage = new ItemStack(Material.FEATHER);
            meta = Utils.getItemMeta(nextPage);
            meta.setDisplayName(ChatColor.YELLOW + get("mmh.gui.headslist.next-page"));
            nextPage.setItemMeta(meta);
            inv.setItem(53, nextPage);
        }
        return inv;
    }

    private boolean prevEnable() {
        return page > 1;
    }

    private boolean nextEnable() {
        return page < items.size() / 45;
    }

    @Override
    public void onClick(InventoryAction action, ClickType click, InventoryType.SlotType slotType, int slot, ItemStack currentItem, ItemStack cursor, InventoryView view, InventoryClickEvent event) {
        event.setCancelled(true);
        // prev page
        if (slot == 45 && prevEnable()) {
            page++;
            refresh();
        }
        // next page
        if (slot == 53 && nextEnable()) {
            page--;
            refresh();
        }
    }
}
