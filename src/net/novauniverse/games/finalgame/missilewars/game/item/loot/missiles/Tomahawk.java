package net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import net.novauniverse.games.finalgame.missilewars.game.gameobject.GameObjectType;
import net.novauniverse.games.finalgame.missilewars.game.item.MissileItem;

public class Tomahawk extends MissileItem {
	public Tomahawk() {
		super(ChatColor.DARK_GREEN + "Tomahawk", EntityType.CREEPER, GameObjectType.TOMAHAWK);
	}
}