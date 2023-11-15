package com.github.joelgodofwar.mmh.lib;

import org.bukkit.entity.Entity;

import com.github.joelgodofwar.mmh.lib.version.VersionMatcher;

public class MoreMobHeadsLib {
	private static VersionWrapper WRAPPER = new VersionMatcher().match();
	public static String getName(Entity entity) {
		return WRAPPER.getName(entity);
	}
}
