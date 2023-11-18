package com.github.joelgodofwar.mmh.gui;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public interface IGui {
	Player getPlayer();
	MoreMobHeads getPlugin();
	Inventory newInventory();
	void onClick(InventoryAction action, ClickType click, InventoryType.SlotType slotType, int slot, ItemStack currentItem, ItemStack cursor, InventoryView view, InventoryClickEvent event);
	default void onDrag(InventoryView view, InventoryDragEvent event) {
		event.setCancelled(true);
	}
	default void onClose(InventoryView view) {

	}
	default void refresh(){
		getPlugin().getGuiHandler().openGui(this);
	}
}
