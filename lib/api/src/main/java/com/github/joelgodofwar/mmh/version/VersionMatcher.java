package com.github.joelgodofwar.mmh.version;

import org.bukkit.Bukkit;

import com.github.joelgodofwar.mmh.VersionWrapper;

public class VersionMatcher {
	 @SuppressWarnings("deprecation")
	public VersionWrapper match() {
	        final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
	        try {
	        	//System.out.println(getClass().getPackage().getName() + ".Wrapper_" + serverVersion);
	            return (VersionWrapper) Class.forName(getClass().getPackage().getName() + ".Wrapper_" + serverVersion).newInstance();
	        } catch (IllegalAccessException | InstantiationException exception) {
	            throw new IllegalStateException("Failed to instantiate version wrapper for version " + serverVersion, exception);
	        } catch (ClassNotFoundException exception) {
	            throw new IllegalStateException("MoreMobHeadsLib does not support server version \"" + serverVersion + "\"", exception);
	        }
	 }
}
