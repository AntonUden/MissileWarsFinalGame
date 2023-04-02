package net.novauniverse.games.finalgame.missilewars.lobby;

import java.util.ArrayList;

import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.map.GameLobbyMapData;
import net.zeeraa.novacore.spigot.utils.LocationData;

public class MissileWarsLobbyMapData extends GameLobbyMapData {
	public MissileWarsLobbyMapData() {
		super(new LocationData(0D, 0D, 0D), "MissileWars", "MissileWars", "Default waiting map for missilewars", null, new ArrayList<>());
	}
}