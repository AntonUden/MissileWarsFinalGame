package net.novauniverse.games.finalgame.missilewars.game.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FinalGameMissileWarsGameStartEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	public FinalGameMissileWarsGameStartEvent() {
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}