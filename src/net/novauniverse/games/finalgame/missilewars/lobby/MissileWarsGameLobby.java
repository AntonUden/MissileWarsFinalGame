package net.novauniverse.games.finalgame.missilewars.lobby;

import org.bukkit.World;

import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.map.GameLobbyMap;

public class MissileWarsGameLobby extends GameLobbyMap {
	public MissileWarsGameLobby(World world, MissileWarsLobbyMapData mapData) {
		super(world, mapData, mapData.getSpawnLocation().toLocation(world));
	}
}