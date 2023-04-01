package net.novauniverse.games.finalgame.missilewars.game.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.novauniverse.games.finalgame.missilewars.game.item.loot.ArrowItem;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.FireballItem;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.ShieldItem;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Guardian;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Juggernaut;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Lightning;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Shieldbuster;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Tomahawk;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItem;

public class LootManager {
	public static final List<Class<? extends CustomItem>> ITEMS = new ArrayList<Class<? extends CustomItem>>();
	static {
		LootManager.ITEMS.add(ArrowItem.class);
		LootManager.ITEMS.add(FireballItem.class);
		LootManager.ITEMS.add(ShieldItem.class);
		LootManager.ITEMS.add(Guardian.class);
		LootManager.ITEMS.add(Juggernaut.class);
		LootManager.ITEMS.add(Lightning.class);
		LootManager.ITEMS.add(Shieldbuster.class);
		LootManager.ITEMS.add(Tomahawk.class);
	}

	public static Class<? extends CustomItem> getRandom() {
		return LootManager.getRandom(new Random());
	}

	public static Class<? extends CustomItem> getRandom(Random random) {
		int i = random.nextInt(LootManager.ITEMS.size());
		return LootManager.ITEMS.get(i);
	}
}