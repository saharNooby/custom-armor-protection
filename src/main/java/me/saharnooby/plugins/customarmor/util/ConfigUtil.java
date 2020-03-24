package me.saharnooby.plugins.customarmor.util;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author saharNooby
 * @since 15:21 16.03.2020
 */
public final class ConfigUtil {

	public static ConfigurationSection requireSection(@NonNull ConfigurationSection section, @NonNull String key, @NonNull String fullPath) {
		ConfigurationSection sub = section.getConfigurationSection(key);

		if (sub == null) {
			throw new IllegalArgumentException("Expected " + fullPath + " to be a section");
		}

		return sub;
	}

	public static Material parseMaterial(@NonNull String s) {
		// Allows the default config to work on legacy Spigot versions.
		if (s.toUpperCase().equals("PLAYER_HEAD") && NMSUtil.getMinorVersion() < 13) {
			return Material.valueOf("SKULL_ITEM");
		}

		try {
			return Material.valueOf(s.toUpperCase());
		} catch (Exception e) {
			try {
				return Material.getMaterial(Integer.parseInt(s));
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Unknown material " + s);
			} catch (Throwable ex) {
				throw new IllegalArgumentException("Invalid material " + s, ex);
			}
		}
	}

	public static int parseColor(@NonNull String hex) {
		if (!hex.matches("#?[\\da-fA-F]{6}")) {
			throw new IllegalArgumentException("Invalid HEX RGB color '" + hex + "'");
		}

		if (hex.startsWith("#")) {
			hex = hex.substring(1);
		}

		return (int) (Long.parseLong(hex, 16) & 0xFFFFFF);
	}

}
