package me.saharnooby.plugins.customarmor.locale;

import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author saharNooby
 * @since 19:13 16.03.2020
 */
public final class Locale {

	private final Map<String, String> strings = new HashMap<>();

	public Locale(@NonNull ConfigurationSection section) {
		for (String key : section.getKeys(false)) {
			this.strings.put(key, ChatColor.translateAlternateColorCodes('&', section.getString(key)));
		}
	}

	public String getMessage(@NonNull String key, @NonNull Object... params) {
		String message = this.strings.get(key);

		if (message == null) {
			return "§cMessage " + key + " not found " + Arrays.toString(params);
		}

		return String.format(message, params);
	}

}
