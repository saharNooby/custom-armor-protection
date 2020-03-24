package me.saharnooby.plugins.customarmor;

import lombok.Getter;
import lombok.NonNull;
import me.saharnooby.plugins.customarmor.command.MainCommand;
import me.saharnooby.plugins.customarmor.config.PluginConfig;
import me.saharnooby.plugins.customarmor.listener.AnvilListener;
import me.saharnooby.plugins.customarmor.listener.DamageListener;
import me.saharnooby.plugins.customarmor.locale.Locale;
import me.saharnooby.plugins.customarmor.metrics.Metrics;
import me.saharnooby.plugins.customarmor.util.IOUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.logging.Logger;

/**
 * @author saharNooby
 * @since 15:12 16.03.2020
 */
public final class CustomArmorProtection extends JavaPlugin {

	@Getter
	private PluginConfig pluginConfig;

	@Getter
	private Locale locale;

	@Override
	public void onEnable() {
		try {
			loadConfig();
		} catch (Exception e) {
			reportInvalidConfig(e);

			throw e;
		}

		Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
		Bukkit.getPluginManager().registerEvents(new AnvilListener(this), this);

		getCommand("custom-armor-protection").setExecutor(new MainCommand(this));

		new Metrics(this, 6862);
	}

	public void loadConfig() {
		saveDefaultConfig();

		this.pluginConfig = new PluginConfig(getConfig());

		try {
			loadLocale(this.pluginConfig.getLocale());
		} catch (Exception e) {
			throw new IllegalStateException("Failed to load locale", e);
		}

		getLogger().info("Loaded " + this.pluginConfig.getItems().size() + " items");
		getLogger().info("Loaded " + this.pluginConfig.getModifiers().size() + " damage modifiers");
	}

	private void loadLocale(@NonNull String locale) throws IOException, InvalidConfigurationException {
		InputStream test = CustomArmorProtection.class.getResourceAsStream("/locale/" + locale + ".yml");

		if (test == null) {
			locale = "en";
		} else {
			test.close();
		}

		File localeFile = new File(getDataFolder(), "locale" + File.separator + locale + ".yml");

		if (!localeFile.exists()) {
			File dir = localeFile.getParentFile();

			if (!dir.exists() && !dir.mkdirs()) {
				throw new IOException("Failed to create directory " + dir);
			}

			InputStream in = CustomArmorProtection.class.getResourceAsStream("/locale/" + locale + ".yml");

			if (in == null) {
				throw new IllegalStateException(locale + " locale file not found in the plugin JAR");
			}

			try (OutputStream out = new FileOutputStream(localeFile)) {
				IOUtil.transferBytes(in, out);
			} finally {
				in.close();
			}
		}

		YamlConfiguration section = new YamlConfiguration();
		section.load(localeFile);
		this.locale = new Locale(section);
	}

	public void reportInvalidConfig(@NonNull Exception e) {
		Logger logger = getLogger();

		logger.severe("");
		logger.severe("Invalid configuration of CustomArmorProtection: " + e);

		Throwable ex = e.getCause();

		while (ex != null) {
			logger.severe("Reason of the above: " + ex.getMessage());

			ex = ex.getCause();
		}

		logger.severe("");
	}

}
