package net.novauniverse.games.finalgame.missilewars.team;

import net.zeeraa.novacore.commons.utils.Pair;
import net.zeeraa.novacore.spigot.teams.Team;

public interface MissileWarsFinalGameTeamProvider {
	Pair<Team> getParticipants();
}