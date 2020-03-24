package me.saharnooby.plugins.customarmor.listener;

import lombok.RequiredArgsConstructor;
import me.saharnooby.plugins.customarmor.CustomArmorProtection;
import me.saharnooby.plugins.customarmor.config.ItemConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * @author saharNooby
 * @since 15:56 16.03.2020
 */
@RequiredArgsConstructor
public final class AnvilListener implements Listener {

	private final CustomArmorProtection plugin;

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null || e.getClickedInventory().getType() != InventoryType.ANVIL) {
			return;
		}

		if (e.getSlot() != 2) {
			return;
		}

		ItemStack sourceItem = e.getClickedInventory().getItem(0);

		for (ItemConfig config : this.plugin.getPluginConfig().getDisableAnvilRenaming()) {
			if (config.matches(sourceItem)) {
				e.setCancelled(true);

				e.getWhoClicked().sendMessage(this.plugin.getLocale().getMessage("canNotRenameThisItem"));

				e.getWhoClicked().closeInventory();

				break;
			}
		}
	}

}
