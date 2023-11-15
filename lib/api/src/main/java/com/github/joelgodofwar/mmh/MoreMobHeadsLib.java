package com.github.joelgodofwar.mmh;

import com.github.joelgodofwar.mmh.version.VersionMatcher;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class MoreMobHeadsLib {
    private static VersionWrapper WRAPPER = new VersionMatcher().match();

    public static String getName(String name, Entity entity) {
        return WRAPPER.getName(name, entity);
    }

    public static ItemStack addSound(ItemStack item, Entity entity) {
        return WRAPPER.addSound(item, entity);
    }

    public static ItemStack addSound(ItemStack item, EntityType eType) {
        return WRAPPER.addSound(item, eType);
    }

    public static ItemStack getVanilla(EntityType eType) {
        return WRAPPER.getVanilla(eType);
    }
}
