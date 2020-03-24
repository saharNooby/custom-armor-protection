package me.saharnooby.plugins.customarmor.config;

import lombok.Getter;
import lombok.NonNull;
import me.saharnooby.plugins.customarmor.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * @author saharNooby
 * @since 15:17 16.03.2020
 */
@Getter
public final class PluginConfig {

	private final Map<String, ItemConfig> items = new HashMap<>();
	private final List<ModifierConfig> modifiers = new ArrayList<>();
	private final Set<ItemConfig> disableAnvilRenaming = new HashSet<>();
	private final boolean debug;
	private final String locale;

	public PluginConfig(@NonNull ConfigurationSection section) {
		ConfigurationSection items = ConfigUtil.requireSection(section, "items", "items");

		for (String key : items.getKeys(false)) {
			ConfigurationSection sub = ConfigUtil.requireSection(items, key, "items." + key);

			try {
				this.items.put(key, new ItemConfig(key, sub));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid item " + key, e);
			}
		}

		ConfigurationSection modifiers = ConfigUtil.requireSection(section, "modifiers", "modifiers");

		for (String key : modifiers.getKeys(false)) {
			ConfigurationSection sub = ConfigUtil.requireSection(modifiers, key, "modifiers." + key);

			try {
				this.modifiers.add(new ModifierConfig(this, key, sub));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid modifier " + key, e);
			}
		}

		if (section.isList("disableAnvilRenaming")) {
			for (String itemId : section.getStringList("disableAnvilRenaming")) {
				ItemConfig config = this.items.get(itemId);

				if (config == null) {
					throw new IllegalArgumentException("Unknown item " + itemId + " in disableAnvilRenaming list");
				}

				this.disableAnvilRenaming.add(config);
			}
		}

		this.debug = section.getBoolean("debugDamageModification");

		this.locale = section.getString("language", "en").toLowerCase();
	}

}
