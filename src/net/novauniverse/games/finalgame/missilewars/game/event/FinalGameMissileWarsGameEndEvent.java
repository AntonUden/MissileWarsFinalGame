package net.novauniverse.games.finalgame.missilewars.game.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.teams.Team;

public class FinalGameMissileWarsGameEndEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private Team winningTeam;
	private GameEndReason gameEndReason;

	public FinalGameMissileWarsGameEndEvent(Team winningTeam, GameEndReason gameEndReason) {
		this.winningTeam = winningTeam;
		this.gameEndReason = gameEndReason;
	}

	public Team getWinningTeam() {
		return winningTeam;
	}

	public GameEndReason getGameEndReason() {
		return gameEndReason;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}