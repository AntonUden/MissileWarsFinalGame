package net.novauniverse.games.finalgame.missilewars.game.gameobject;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import net.novauniverse.games.finalgame.missilewars.game.world.MWStructureUtil;
import net.novauniverse.games.finalgame.missilewars.team.MissilewarsTeam;

public class GameObjectIndex {
	public static HashMap<GameObjectType, Vector> positions = new HashMap<GameObjectType, Vector>();
	static {
		positions.put(GameObjectType.SHIELD, new Vector(0, 0, 0));
		positions.put(GameObjectType.TOMAHAWK, new Vector(0, -3, 4));
		positions.put(GameObjectType.SHIELD_BUSTER, new Vector(0, -3, 4));
		positions.put(GameObjectType.JUGGERNAUT, new Vector(0, -3, 4));
		positions.put(GameObjectType.LIGHTNING, new Vector(0, -3, 5));
		positions.put(GameObjectType.GUARDIAN, new Vector(0, -3, 4));
		positions.put(GameObjectType.WIN, new Vector(-27, 88, -51));
	}
	public static HashMap<GameObjectType, Vector> missilesRelative = new HashMap<GameObjectType, Vector>();
	static {
		missilesRelative.put(GameObjectType.SHIELD, new Vector(-115, 62, -1));
		missilesRelative.put(GameObjectType.TOMAHAWK, new Vector(-113, 61, -24));
		missilesRelative.put(GameObjectType.SHIELD_BUSTER, new Vector(-108, 61, -24));
		missilesRelative.put(GameObjectType.JUGGERNAUT, new Vector(-103, 62, -24));
		missilesRelative.put(GameObjectType.LIGHTNING, new Vector(-98, 62, 16));
		missilesRelative.put(GameObjectType.GUARDIAN, new Vector(-93, 62, -25));
		missilesRelative.put(GameObjectType.WIN, new Vector(-268, 200, -100));
	}
	public static HashMap<GameObjectType, Vector> missilesStart = new HashMap<GameObjectType, Vector>();
	static {
		missilesStart.put(GameObjectType.SHIELD, new Vector(-112, 65, -1));
		missilesStart.put(GameObjectType.TOMAHAWK, new Vector(-113, 62, -12));
		missilesStart.put(GameObjectType.SHIELD_BUSTER, new Vector(-107, 63, -24));
		missilesStart.put(GameObjectType.JUGGERNAUT, new Vector(-102, 61, -24));
		missilesStart.put(GameObjectType.LIGHTNING, new Vector(-97, 61, 16));
		missilesStart.put(GameObjectType.GUARDIAN, new Vector(-91, 61, -25));
		missilesStart.put(GameObjectType.WIN, new Vector(-253, 217, -91));
	}
	public static HashMap<GameObjectType, Vector> missilesEnd = new HashMap<GameObjectType, Vector>();
	static {
		missilesEnd.put(GameObjectType.SHIELD, new Vector(-118, 59, -1));
		missilesEnd.put(GameObjectType.TOMAHAWK, new Vector(-111, 61, -24));
		missilesEnd.put(GameObjectType.SHIELD_BUSTER, new Vector(-109, 61, -10));
		missilesEnd.put(GameObjectType.JUGGERNAUT, new Vector(-104, 63, -14));
		missilesEnd.put(GameObjectType.LIGHTNING, new Vector(-99, 62, 24));
		missilesEnd.put(GameObjectType.GUARDIAN, new Vector(-94, 63, -18));
		missilesEnd.put(GameObjectType.WIN, new Vector(-283, 200, -113));
	}

	public static void spawnObject(MissilewarsTeam team, GameObjectType missile, Location location) {
		Vector position = positions.get(missile);
		Vector relative = missilesRelative.get(missile);
		Vector start = missilesStart.get(missile);
		Vector end = missilesEnd.get(missile);

		if (team == MissilewarsTeam.TEAM_2) {
			position = position.clone().setZ(-position.getZ());
			relative = relative.clone().setZ(-relative.getZ());
			start = start.clone().setZ(-start.getZ());
			end = end.clone().setZ(-end.getZ());

			if (missile == GameObjectType.GUARDIAN) {
				start.setX(start.getX() - 1);
				end.setX(end.getX() - 1);
				relative.setX(relative.getX() - 1);
			} else if (missile == GameObjectType.TOMAHAWK) {
				relative.setX(relative.getX() + 1);
			}
		}

		Location rel = location.add(position);
		Location pasteRel = new Location(rel.getWorld(), relative.getX(), relative.getY(), relative.getZ());
		Location pasteStart = new Location(rel.getWorld(), start.getX(), start.getY(), start.getZ());
		Location pasteEnd = new Location(rel.getWorld(), end.getX(), end.getY(), end.getZ());

		MWStructureUtil.clone(pasteRel, pasteStart, pasteEnd, rel, true);
	}

	public static void spawnObject(MissilewarsTeam team, GameObjectType object, World world) {
		spawnObject(team, object, new Location(world, 0, 0, 0));
	}
}
