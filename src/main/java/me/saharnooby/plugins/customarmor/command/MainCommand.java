package me.saharnooby.plugins.customarmor.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.plugins.customarmor.CustomArmorProtection;
import me.saharnooby.plugins.customarmor.config.ItemConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Set;

/**
 * @author saharNooby
 * @since 19:01 16.03.2020
 */
@RequiredArgsConstructor
public final class MainCommand implements CommandExecutor {

	private static final String PERM_RELOAD = "customarmorprotection.command.main.reload";
	private static final String PERM_GIVE = "customarmorprotection.command.main.give";

	private final CustomArmorProtection plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!sender.hasPermission(PERM_RELOAD) && !sender.hasPermission(PERM_GIVE)) {
			sender.sendMessage(getMessage("noPermissionsForCommand"));
			return true;
		}

		if (args.length == 0) {
			return false;
		}

		switch (args[0].toLowerCase()) {
			case "reload": {
				if (!sender.hasPermission(PERM_RELOAD)) {
					sender.sendMessage(getMessage("noPermissionsForCommand"));
					return true;
				}

				try {
					this.plugin.reloadConfig();
					this.plugin.loadConfig();
					sender.sendMessage(getMessage("configWasReloaded"));
				} catch (Exception e) {
					sender.sendMessage(getMessage("configWasNotReloaded"));

					this.plugin.reportInvalidConfig(e);

					e.printStackTrace();
				}

				return true;
			}
			case "give": {
				if (!sender.hasPermission(PERM_GIVE)) {
					sender.sendMessage(getMessage("noPermissionsForCommand"));
					return true;
				}

				if (!(sender instanceof Player)) {
					sender.sendMessage(getMessage("noPermissionsForCommand"));
					return true;
				}

				PlayerInventory inv = ((Player) sender).getInventory();

				if (inv.firstEmpty() == -1) {
					sender.sendMessage(getMessage("yourInventoryIsFull"));
					return true;
				}

				if (args.length != 2) {
					return false;
				}

				ItemConfig config = this.plugin.getPluginConfig().getItems().get(args[1]);

				if (config == null) {
					Set<String> ids = this.plugin.getPluginConfig().getItems().keySet();

					sender.sendMessage(getMessage("itemNotFound", ids));

					return true;
				}

				inv.addItem(config.create());

				sender.sendMessage(getMessage("itemWasGiven", config.getId()));

				return true;
			}
		}

		return false;
	}

	private String getMessage(@NonNull String key, @NonNull Object... params) {
		return this.plugin.getLocale().getMessage(key, params);
	}

}
