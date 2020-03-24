package me.saharnooby.plugins.customarmor.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.plugins.customarmor.CustomArmorProtection;
import me.saharnooby.plugins.customarmor.config.Amount;
import me.saharnooby.plugins.customarmor.config.ItemConfig;
import me.saharnooby.plugins.customarmor.config.ModifierConfig;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author saharNooby
 * @since 15:55 16.03.2020
 */
@RequiredArgsConstructor
public final class DamageListener implements Listener {

	private final CustomArmorProtection plugin;

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent e) {
		Entity entity = e.getEntity();

		if (!(entity instanceof LivingEntity)) {
			return;
		}

		if (entity instanceof ArmorStand) {
			return;
		}

		for (ModifierConfig modifier : this.plugin.getPluginConfig().getModifiers()) {
			if (!modifier.getDamageModifiers().containsKey(e.getCause())) {
				continue;
			}

			if (modifier.getPermission() != null && entity instanceof Player && !entity.hasPermission(modifier.getPermission())) {
				debug("Skipping modifier %s because player %s has no permission %s", modifier.getId(), entity.getName(), modifier.getPermission());
				continue;
			}

			applyModifier(e, modifier);
		}
	}

	private void applyModifier(@NonNull EntityDamageEvent e, @NonNull ModifierConfig modifier) {
		if (modifier.getWearingAny() != null) {
			ItemStack[] contents = getArmorContents(e.getEntity());

			for (ItemConfig config : modifier.getWearingAny()) {
				for (ItemStack stack : contents) {
					if (config.matches(stack)) {
						debug("%s is matched by item %s in modifier %s (playerWearingAny)", stack, config.getId(), modifier.getId());

						// This is applied once per matched item
						modifyDamage(e, modifier);

						break;
					}
				}
			}
		} else if (modifier.getWearingAll() != null) {
			ItemStack[] contents = getArmorContents(e.getEntity());

			for (ItemConfig config : modifier.getWearingAll()) {
				if (!anyMatch(config, contents)) {
					for (ItemStack stack : contents) {
						debug("%s is NOT matched by item %s", stack, config.getId());
					}

					debug("Skipping modifier %s (playerWearingAll)", modifier.getId());

					return;
				}
			}

			// This is applied once per modifier
			modifyDamage(e, modifier);
		} else {
			modifyDamage(e, modifier);
		}
	}

	private void modifyDamage(@NonNull EntityDamageEvent e, @NonNull ModifierConfig modifier) {
		double prev = e.getDamage();

		Amount amount = modifier.getDamageModifiers().get(e.getCause());

		e.setDamage(amount.apply(e.getDamage()));

		debug(
				"Modified damage %s to %s by %s (%s -> %s, final %s) because of modifier %s",
				e.getCause(), e.getEntity().getName(), amount, prev, e.getDamage(), e.getFinalDamage(), modifier.getId()
		);
	}

	private static boolean anyMatch(@NonNull ItemConfig config, ItemStack[] stacks) {
		for (ItemStack stack : stacks) {
			if (config.matches(stack)) {
				return true;
			}
		}

		return false;
	}

	private static ItemStack[] getArmorContents(@NonNull Entity entity) {
		return ((LivingEntity) entity).getEquipment().getArmorContents();
	}

	private void debug(String message, Object... params) {
		if (this.plugin.getPluginConfig().isDebug()) {
			this.plugin.getLogger().info(String.format(message, params));
		}
	}

}
