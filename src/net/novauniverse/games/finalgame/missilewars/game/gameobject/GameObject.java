package net.novauniverse.games.finalgame.missilewars.game.gameobject;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import net.novauniverse.games.finalgame.missilewars.game.world.MWStructureUtil;
import net.novauniverse.games.finalgame.missilewars.team.MissilewarsTeam;
import net.zeeraa.novacore.commons.utils.Pair;

public class GameObject {
	private final GameObjectType type;
	private final Vector position;
	private final Vector relative;
	private final Pair<Vector> bounds;
	
	public GameObject(GameObjectType type, Vector position, Vector relative, Pair<Vector> bounds) {
		this.type = type;
		this.position = position;
		this.relative = relative;
		this.bounds = bounds;
	}
	
	public GameObjectType getType() {
		return type;
	}
	
	public Pair<Vector> getBounds() {
		return bounds;
	}
	
	public Vector getPosition() {
		return position;
	}
	
	public Vector getRelative() {
		return relative;
	}
	
	public void spawn(MissilewarsTeam team, World world) {
		this.spawn(team, new Location(world, 0D, 0D, 0D));
	}
	
	public void spawn(MissilewarsTeam team, Location location) {
		Vector position = this.position.clone();
		
		Vector relative = this.relative.clone();
		
		Vector start = bounds.getObject1();
		Vector end = bounds.getObject2();
		
		if (team == MissilewarsTeam.TEAM_2) {
			position = position.clone().setZ(-position.getZ());
			relative = relative.clone().setZ(-relative.getZ());
			start = start.clone().setZ(-start.getZ());
			end = end.clone().setZ(-end.getZ());

			if (type == GameObjectType.GUARDIAN) {
				start.setX(start.getX() - 1);
				end.setX(end.getX() - 1);
				relative.setX(relative.getX() - 1);
			} else if (type == GameObjectType.TOMAHAWK) {
				relative.setX(relative.getX() + 1);
			}
		}
		
		Location rel = location.add(position);
		Location pasteRel = new Location(rel.getWorld(), relative.getX(), relative.getY(), relative.getZ());
		Location pasteStart = new Location(rel.getWorld(), start.getX(), start.getY(), start.getZ());
		Location pasteEnd = new Location(rel.getWorld(), end.getX(), end.getY(), end.getZ());

		MWStructureUtil.clone(pasteRel, pasteStart, pasteEnd, rel, true);
	}
}