package net.novauniverse.games.finalgame.missilewars.game.world;

import java.util.List;

import org.bukkit.util.Vector;

import net.novauniverse.games.finalgame.missilewars.team.MissilewarsTeam;
import net.zeeraa.novacore.spigot.utils.LocationData;

public class TeamConfig {
	private final MissilewarsTeam team;
	private final List<Vector> portalSamplingAreas;
	private final LocationData spawnLocation;

	public TeamConfig(MissilewarsTeam team, List<Vector> portalSamplingAreas, LocationData spawnLocation) {
		this.team = team;
		this.portalSamplingAreas = portalSamplingAreas;
		this.spawnLocation = spawnLocation;
	}

	public MissilewarsTeam getTeam() {
		return team;
	}

	public List<Vector> getPortalSamplingAreas() {
		return portalSamplingAreas;
	}

	public LocationData getSpawnLocation() {
		return spawnLocation;
	}
}