package com.github.joelgodofwar.mmh.lib;

import com.github.joelgodofwar.mmh.lib.version.VersionMatcher;
import org.bukkit.entity.Entity;

public class MoreMobHeadsLib {
    private static VersionWrapper WRAPPER = new VersionMatcher().match();

    public static String getName(Entity entity) {
        return WRAPPER.getName(entity);
    }
}
