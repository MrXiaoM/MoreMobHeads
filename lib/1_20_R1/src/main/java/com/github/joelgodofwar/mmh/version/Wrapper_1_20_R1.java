package com.github.joelgodofwar.mmh.version;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Panda.Gene;
//import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.joelgodofwar.mmh.VersionWrapper;

public final class Wrapper_1_20_R1 implements VersionWrapper {
	public final static Logger logger = Logger.getLogger("Minecraft");
	
	public String getName(String name, Entity entity) {
		switch (name) {
		case "FROG":
			Frog daFrog = (Frog) entity;
			String daFrogVariant = daFrog.getVariant().toString();
			
			return name + "_" + daFrogVariant;
		case "CAMEL":
			//Camel daCamel = (Camel) entity;
			
			return name;
		case "SNIFFER":
			//Sniffer daSniffer = (Sniffer) entity;
			
			return name;
		}
		return null;
	}
	
	public ItemStack addSound(ItemStack item, Entity entity) {
		EntityType eType = entity.getType();
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		String name = eType.name();
		String soundType = "ambient";
		switch (eType) {
		case TURTLE:
			soundType = "ambient_land";
			break;
		case SLIME:
			soundType = "squish_small";
			break;
		case PANDA:
			Panda panda = (Panda) entity;
			Gene gene = panda.getMainGene();
			switch (gene) {
			case WORRIED:
				soundType = "worried_ambient";
				break;
			case AGGRESSIVE:
				soundType = "aggressive_ambient";
				break;
			case WEAK:
				soundType = "sneeze";
				break;
			default:
				soundType = "ambient";
				break;
			}
			break;
		case MUSHROOM_COW:
			name = "COW";
			soundType = "ambient";
			break;
		case MAGMA_CUBE:
			soundType = "squish";
			break;
		case IRON_GOLEM:
		case PLAYER:
		case SNOWMAN:
			soundType = "hurt";
			break;
		case CREEPER:
			soundType = "primed";
			break;
		case BEE:
			soundType = "loop";
			break;
		case AXOLOTL:
			soundType = "idle_air";
			break;
		case SNIFFER:
			soundType = "scenting";
			break;
		case ALLAY:
			soundType = "ambient_without_item";
			break;
		case SPIDER:
		case CAVE_SPIDER:
			name = "SPIDER";
			soundType = "step";
			break;
		case COD:
		case SALMON:
		case TROPICAL_FISH:
		case TADPOLE:
			soundType = "flop";
			break;
		case PUFFERFISH:
			soundType = "blow_up";
			break;
		case TRADER_LLAMA:
			name = "LLAMA";
			soundType = "ambient";
			break;
		default:
			soundType = "ambient";
			break;
		}
		meta.setNoteBlockSound( NamespacedKey.minecraft( "entity." + name.toLowerCase() + "." + soundType ) );
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack addSound(ItemStack item, EntityType eType) {
		//EntityType eType = entity.getType();
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		String name = eType.name();
		String soundType = "ambient";
		switch (eType) {
		case TURTLE:
			soundType = "ambient_land";
			break;
		case SLIME:
			soundType = "squish_small";
			break;
		case MUSHROOM_COW:
			name = "COW";
			soundType = "ambient";
			break;
		case MAGMA_CUBE:
			soundType = "squish";
			break;
		case IRON_GOLEM:
		case PLAYER:
		case SNOWMAN:
			soundType = "hurt";
			break;
		case CREEPER:
			soundType = "primed";
			break;
		case BEE:
			soundType = "loop";
			break;
		case AXOLOTL:
			soundType = "idle_air";
			break;
		case SNIFFER:
			soundType = "scenting";
			break;
		case ALLAY:
			soundType = "ambient_without_item";
			break;
		case SPIDER:
		case CAVE_SPIDER:
			name = "SPIDER";
			soundType = "step";
			break;
		case COD:
		case SALMON:
		case TROPICAL_FISH:
		case TADPOLE:
			soundType = "flop";
			break;
		case PUFFERFISH:
			soundType = "blow_up";
			break;
		case TRADER_LLAMA:
			name = "LLAMA";
			soundType = "ambient";
			break;
		default:
			soundType = "ambient";
			break;
		}
		meta.setNoteBlockSound( NamespacedKey.minecraft( "entity." + name.toLowerCase() + "." + soundType ) );
		item.setItemMeta(meta);
		return item;
	}
	
	
	public ItemStack getVanilla(EntityType eType) {
		//EntityType eType = entity.getType();
		Material material = null;
		switch(eType) {
		case CREEPER:
			material = Material.CREEPER_HEAD;
			break;
		case ENDER_DRAGON:
			material = Material.DRAGON_HEAD;
			break;
		case PIGLIN:
			material = Material.PIGLIN_HEAD;
			break;
		case SKELETON:
			material = Material.SKELETON_SKULL;
			break;
		case WITHER_SKELETON:
			material = Material.WITHER_SKELETON_SKULL;
			break;
		case ZOMBIE:
			material = Material.ZOMBIE_HEAD;
			break;
		default:
			
			break;
		}
		return new ItemStack(material);
	}
	
	public void log(String string) {
		log(Level.INFO, string);
	}
	
	public	void log(Level level, String dalog){// TODO: log
		logger.log(level, ChatColor.YELLOW + "MoreMobHeadsLib v" + ChatColor.RESET + " " + dalog );
	}

}