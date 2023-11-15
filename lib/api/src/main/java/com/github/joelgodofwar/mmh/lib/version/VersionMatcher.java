package com.github.joelgodofwar.mmh.lib.version;

import org.bukkit.Bukkit;

import com.github.joelgodofwar.mmh.lib.VersionWrapper;

public class VersionMatcher {
	 public VersionWrapper match() {
	        final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
	        try {
	            return (VersionWrapper) Class.forName(getClass().getPackage().getName() + ".Wrapper_" + serverVersion).newInstance();
	        } catch (IllegalAccessException | InstantiationException exception) {
	            throw new IllegalStateException("Failed to instantiate version wrapper for version " + serverVersion, exception);
	        } catch (ClassNotFoundException exception) {
	            throw new IllegalStateException("AnvilGUI does not support server version \"" + serverVersion + "\"", exception);
	        }
	 }
}
