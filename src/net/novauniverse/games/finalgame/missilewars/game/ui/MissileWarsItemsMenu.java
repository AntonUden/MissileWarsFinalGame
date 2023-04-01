package net.novauniverse.games.finalgame.missilewars.game.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.novauniverse.games.finalgame.missilewars.game.item.GunBlade;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.ArrowItem;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.FireballItem;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.ShieldItem;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Guardian;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Juggernaut;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Lightning;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Shieldbuster;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Tomahawk;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItem;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItemManager;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.module.modules.gui.callbacks.GUIClickCallback;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIHolder;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;

public class MissileWarsItemsMenu {
	public static final List<Class<? extends CustomItem>> ITEMS = new ArrayList<Class<? extends CustomItem>>();
	static {
		ITEMS.add(GunBlade.class);
		ITEMS.add(ArrowItem.class);
		ITEMS.add(FireballItem.class);
		ITEMS.add(ShieldItem.class);
		ITEMS.add(Guardian.class);
		ITEMS.add(Juggernaut.class);
		ITEMS.add(Lightning.class);
		ITEMS.add(Shieldbuster.class);
		ITEMS.add(Tomahawk.class);
	};

	public static void show(Player player) {
		GUIHolder holder = new GUIReadOnlyHolder();
		Inventory inventory = Bukkit.getServer().createInventory(holder, 9, ChatColor.RED + "Missile Wars items");

		int i = 0;
		for (Class<? extends CustomItem> clazz : ITEMS) {
			addItem(clazz, i, inventory);
			i++;
		}

		player.openInventory(inventory);
	}

	private static void addItem(Class<? extends CustomItem> clazz, int slot, Inventory inventory) {
		if (!(inventory.getHolder() instanceof GUIHolder)) {
			return;
		}

		ItemStack icon = CustomItemManager.getInstance().getCustomItemStack(clazz, null);

		ItemMeta meta = icon.getItemMeta();

		List<String> lore;

		if (meta.hasLore()) {
			lore = meta.getLore();
		} else {
			lore = new ArrayList<String>();
		}

		lore.add("");
		lore.add(ChatColor.GREEN + "Click to spawn item");

		meta.setLore(lore);

		icon.setItemMeta(meta);

		GUIHolder holder = (GUIHolder) inventory.getHolder();

		inventory.setItem(slot, icon);

		holder.addClickCallback(slot, new GUIClickCallback() {

			@Override
			public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
				Player player = (Player) entity;

				player.getInventory().addItem(CustomItemManager.getInstance().getCustomItemStack(clazz, player));

				player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 1F);

				return GUIAction.CANCEL_INTERACTION;
			}
		});
	}
}