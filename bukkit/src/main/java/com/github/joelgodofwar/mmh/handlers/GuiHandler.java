package com.github.joelgodofwar.mmh.handlers;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.gui.IGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiHandler implements Listener {
	final Map<UUID, IGui> playersGui = new HashMap<>();
	MoreMobHeads mmh;
	public GuiHandler(MoreMobHeads mmh) {
		this.mmh = mmh;
		Bukkit.getPluginManager().registerEvents(this, mmh);
	}
	
	public void openGui(IGui gui) {
		Player player = gui.getPlayer();
		if(player == null) return;
		player.closeInventory();
		playersGui.remove(player.getUniqueId());
		playersGui.put(player.getUniqueId(), gui);
		player.openInventory(gui.newInventory());
	}

	public void onDisable() {
		for (Map.Entry<UUID, IGui> entry : playersGui.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			if (player == null) continue;
			player.closeInventory();
		}
		playersGui.clear();
	}

	@Nullable
	public IGui getOpeningGui(Player player) {
		return playersGui.get(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		IGui remove = playersGui.remove(player.getUniqueId());
		if(remove != null) {
			remove.onClose(player.getOpenInventory());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		if(playersGui.containsKey(player.getUniqueId())) {
			playersGui.get(player.getUniqueId()).onClick(event.getAction(), event.getClick(), event.getSlotType(),
					event.getRawSlot(), event.getCurrentItem(), event.getCursor(), event.getView(), event);
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		if(playersGui.containsKey(player.getUniqueId())) {
			playersGui.get(player.getUniqueId()).onDrag( event.getView(), event);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if(!(event.getPlayer() instanceof Player)) return;
		Player player = (Player) event.getPlayer();
		IGui remove = playersGui.remove(player.getUniqueId());
		if(remove != null) {
			remove.onClose(event.getView());
		}
	}
}
