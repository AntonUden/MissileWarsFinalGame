package net.novauniverse.games.finalgame.missilewars.game.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItem;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class GunBlade extends CustomItem {
	@Override
	protected ItemStack createItemStack(Player player) {
		ItemBuilder builder = new ItemBuilder(Material.BOW);

		builder.setName("GunBlade");
		
		builder.addEnchant(Enchantment.ARROW_FIRE, 1, true);
		builder.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
		builder.addEnchant(Enchantment.DURABILITY, 10, true);

		builder.setUnbreakable(true);

		return builder.build();
	}

	@Override
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
}