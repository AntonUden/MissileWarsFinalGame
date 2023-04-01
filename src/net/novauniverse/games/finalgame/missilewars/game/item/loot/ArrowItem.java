package net.novauniverse.games.finalgame.missilewars.game.item.loot;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItem;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class ArrowItem extends CustomItem {
	@Override
	protected ItemStack createItemStack(Player player) {
		return new ItemBuilder(Material.ARROW).setName(ChatColor.GOLD + "Arrow").setAmount(3).build();
	}
}