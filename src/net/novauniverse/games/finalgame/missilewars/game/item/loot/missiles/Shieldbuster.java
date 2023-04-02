package net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import net.novauniverse.games.finalgame.missilewars.game.gameobject.GameObjectType;
import net.novauniverse.games.finalgame.missilewars.game.item.MissileItem;

public class Shieldbuster extends MissileItem {
	public Shieldbuster() {
		super(ChatColor.DARK_RED + "Shieldbuster", EntityType.WITCH, GameObjectType.SHIELD_BUSTER);
	}
}