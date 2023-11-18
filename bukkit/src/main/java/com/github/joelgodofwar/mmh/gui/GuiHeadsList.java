package com.github.joelgodofwar.mmh.gui;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.github.joelgodofwar.mmh.MoreMobHeads.get;

public class GuiHeadsList implements IGui, InventoryHolder {

    public enum Type {
        ALL, CAT, HORSE, LLAMA, RABBIT, SHEEP, TROPICAL_FISH, VILLAGER, ZOMBIE_VILLAGER;
    }
    MoreMobHeads mmh;
    Player player;
    Type type;
    int page;
    Inventory inv;
    public GuiHeadsList(MoreMobHeads mmh, Player player, Type type, int page) {
        this.mmh = mmh;
        this.player = player;
        this.type = type;
        this.page = page;
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
        return inv;
    }

    @Override
    public void onClick(InventoryAction action, ClickType click, InventoryType.SlotType slotType, int slot, ItemStack currentItem, ItemStack cursor, InventoryView view, InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
