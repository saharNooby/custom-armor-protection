package me.saharnooby.plugins.customarmor.util;

import org.bukkit.Bukkit;

public final class NMSUtil {

	private static final String VERSION;

	static {
		String name = Bukkit.getServer().getClass().getName();

		VERSION = name.substring(23, name.lastIndexOf('.'));

		if (!VERSION.matches("v1_\\d{1,2}_R\\d{1,2}")) {
			throw new IllegalStateException("Invalid server version '" + VERSION + "', server class is " + name);
		}
	}

	/**
	 * @return Server version, for example 'v1_14_R1'.
	 */
	public static String getVersion() {
		return VERSION;
	}

	/**
	 * @return Minor server version, for example '14' in 'v1_14_R1'.
	 */
	public static int getMinorVersion() {
		String ver = getVersion();
		ver = ver.substring(ver.indexOf('_') + 1);
		return Integer.parseInt(ver.substring(0, ver.indexOf('_')));
	}

}
