package net.novauniverse.games.finalgame.missilewars.game.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import net.novauniverse.games.finalgame.missilewars.team.MissilewarsTeam;
import net.zeeraa.novacore.spigot.utils.LocationUtils;

public class PortalLocation {
	private Vector vector;
	private MissilewarsTeam color;

	public PortalLocation(Vector vector, MissilewarsTeam color) {
		this.vector = vector;
		this.color = color;
	}

	public Vector getVector() {
		return vector;
	}

	public MissilewarsTeam getColor() {
		return color;
	}

	public boolean isBroken(World world) {
		Location location = LocationUtils.getLocation(world, vector);
		return location.getBlock().getType() != Material.PORTAL;
	}
}