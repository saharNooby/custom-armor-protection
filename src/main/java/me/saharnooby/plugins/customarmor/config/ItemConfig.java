package me.saharnooby.plugins.customarmor.config;

import lombok.Getter;
import lombok.NonNull;
import me.saharnooby.plugins.customarmor.util.ConfigUtil;
import me.saharnooby.plugins.customarmor.util.CustomModelData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author saharNooby
 * @since 15:17 16.03.2020
 */
@Getter
public final class ItemConfig {

	private final String id;

	private final Material type;
	private final String nameContains;
	private final String loreContains;
	private final Integer color;
	private final Integer customModelData;

	public ItemConfig(@NonNull String id, @NonNull ConfigurationSection section) {
		this.id = id;

		String type = section.getString("hasType");

		if (type == null) {
			throw new IllegalArgumentException("Expected hasType to be set");
		}

		this.type = ConfigUtil.parseMaterial(type);
		this.nameContains = getColoredString(section, "nameContains");
		this.loreContains = getColoredString(section, "loreContains");
		this.color = section.isSet("hasColor") ? ConfigUtil.parseColor(section.getString("hasColor")) : null;
		this.customModelData = section.isSet("customModelData") ? section.getInt("customModelData") : null;
	}

	/**
	 * @param stack Item stack to check.
	 * @return Whether specified item matches.
	 */
	public boolean matches(ItemStack stack) {
		if (stack == null) {
			return false;
		}

		if (this.type != null && stack.getType() != this.type) {
			return false;
		}

		ItemMeta meta = stack.hasItemMeta() ? stack.getItemMeta() : null;

		if (this.nameContains != null && (meta == null || !meta.hasDisplayName() || !meta.getDisplayName().contains(this.nameContains))) {
			return false;
		}

		if (this.loreContains != null && (meta == null || !meta.hasLore() || meta.getLore().stream().noneMatch(l -> l.contains(this.loreContains)))) {
			return false;
		}

		if (this.color != null && meta instanceof LeatherArmorMeta && ((LeatherArmorMeta) meta).getColor().asRGB() != this.color) {
			return false;
		}

		//noinspection RedundantIfStatement
		if (this.customModelData != null && CustomModelData.isSupported() && (!CustomModelData.has(stack) || CustomModelData.getAsInt(stack) != this.customModelData)) {
			return false;
		}

		return true;
	}

	/**
	 * Creates an item which can be matched by this config.
	 * @return Item stack.
	 */
	public ItemStack create() {
		ItemStack stack = new ItemStack(this.type);
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(stack.getType());

		if (this.nameContains != null) {
			meta.setDisplayName(this.nameContains);
		}

		if (this.loreContains != null) {
			meta.setLore(new ArrayList<>(Collections.singletonList(this.loreContains)));
		}

		if (this.color != null && meta instanceof LeatherArmorMeta) {
			((LeatherArmorMeta) meta).setColor(Color.fromRGB(this.color));
		}

		stack.setItemMeta(meta);

		if (this.customModelData != null && CustomModelData.isSupported()) {
			CustomModelData.set(stack, this.customModelData);
		}

		return stack;
	}

	private String getColoredString(@NonNull ConfigurationSection section, @NonNull String key) {
		String string = section.getString(key);

		return string != null ? ChatColor.translateAlternateColorCodes('&', string) : null;
	}

}
