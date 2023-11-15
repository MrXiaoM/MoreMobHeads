package com.github.joelgodofwar.mmh;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface VersionWrapper {
	
	String getName(String name, Entity entity);
	
	ItemStack addSound(ItemStack item, Entity entity);
	
	ItemStack addSound(ItemStack item, EntityType eType);
	
	ItemStack getVanilla(EntityType eType);

}
