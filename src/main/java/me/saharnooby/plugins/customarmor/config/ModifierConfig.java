package me.saharnooby.plugins.customarmor.config;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;

/**
 * @author saharNooby
 * @since 15:17 16.03.2020
 */
@Getter
public final class ModifierConfig {

	private static final EntityDamageEvent.DamageCause[] DEFAULT_CAUSES = {
			CONTACT,
			ENTITY_ATTACK,
			PROJECTILE,
			FIRE,
			LAVA,
			BLOCK_EXPLOSION,
			ENTITY_EXPLOSION
	};

	private final String id;
	private final Set<ItemConfig> wearingAll;
	private final Set<ItemConfig> wearingAny;
	private final String permission;
	private final Map<EntityDamageEvent.DamageCause, Amount> damageModifiers = new EnumMap<>(EntityDamageEvent.DamageCause.class);

	public ModifierConfig(@NonNull PluginConfig config, @NonNull String id, @NonNull ConfigurationSection section) {
		this.id = id;

		this.wearingAll = parseItemSet(section, "playerWearingAll", config);
		this.wearingAny = parseItemSet(section, "playerWearingAny", config);

		if (this.wearingAll != null && this.wearingAny != null) {
			throw new IllegalArgumentException("Specify either playerWearingAll or playerWearingAny");
		}

		this.permission = section.getString("playerHasPermission");

		if (section.isConfigurationSection("modifyDamage")) {
			ConfigurationSection damage = section.getConfigurationSection("modifyDamage");

			for (String key : damage.getKeys(false)) {
				Amount amount = parseAmount(damage, key);

				key = key.toUpperCase();

				if (key.equals("ALL")) {
					for (EntityDamageEvent.DamageCause cause : values()) {
						this.damageModifiers.put(cause, amount);
					}
				} else if (key.equals("DEFAULT")) {
					for (EntityDamageEvent.DamageCause cause : DEFAULT_CAUSES) {
						this.damageModifiers.put(cause, amount);
					}
				} else {
					try {
						this.damageModifiers.put(valueOf(key), amount);
					} catch (Exception e) {
						throw new IllegalArgumentException("Unknown damage type '" + key + "'", e);
					}
				}
			}
		}
	}

	private Amount parseAmount(@NonNull ConfigurationSection section, @NonNull String key) {
		try {
			return new Amount(section.getString(key));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid amount for damage type " + key, e);
		}
	}

	private Set<ItemConfig> parseItemSet(@NonNull ConfigurationSection section, @NonNull String key, @NonNull PluginConfig config) {
		if (!section.isSet(key)) {
			return null;
		}

		if (section.isString(key)) {
			String itemId = section.getString(key);

			ItemConfig item = getItem(config, itemId);

			return Collections.singleton(item);
		} else if (section.isList(key)) {
			Set<ItemConfig> set = new HashSet<>();

			for (String itemId : section.getStringList(key)) {
				set.add(getItem(config, itemId));
			}

			return set;
		} else {
			throw new IllegalArgumentException("Expected " + key + " to be string or list");
		}
	}

	private ItemConfig getItem(@NonNull PluginConfig config, @NonNull String itemId) {
		ItemConfig item = config.getItems().get(itemId);

		if (item == null) {
			throw new IllegalArgumentException("Item " + itemId + " not found");
		}

		return item;
	}

}
