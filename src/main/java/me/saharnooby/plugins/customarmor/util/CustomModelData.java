package me.saharnooby.plugins.customarmor.util;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.OptionalInt;

/**
 * @author saharNooby
 * @since 14:18 25.07.2019
 */
@SuppressWarnings("JavaReflectionMemberAccess")
public final class CustomModelData {

	public static boolean isSupported() {
		return NMSUtil.getMinorVersion() >= 14;
	}

	public static boolean has(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return false;
		}

		ItemMeta meta = item.getItemMeta();

		if (meta == null) {
			return false;
		}

		try {
			return (boolean) ItemMeta.class.getMethod("hasCustomModelData").invoke(meta);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public static OptionalInt get(ItemStack item) {
		if (!has(item)) {
			return OptionalInt.empty();
		}

		ItemMeta meta = item.getItemMeta();

		try {
			return OptionalInt.of((int) ItemMeta.class.getMethod("getCustomModelData").invoke(meta));
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public static int getAsInt(ItemStack item) {
		return get(item).orElse(0);
	}

	public static void set(@NonNull ItemStack item, int data) {
		ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());

		try {
			ItemMeta.class.getMethod("setCustomModelData", Integer.class).invoke(meta, data);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}

		item.setItemMeta(meta);
	}

}
