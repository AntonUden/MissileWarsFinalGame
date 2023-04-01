package net.novauniverse.games.finalgame.missilewars.team;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.games.finalgame.missilewars.NovaFinalMissileWars;
import net.zeeraa.novacore.spigot.teams.Team;

public enum MissilewarsTeam {
	RED(ChatColor.RED), GREEN(ChatColor.GREEN);

	private MissilewarsTeam(ChatColor chatColor) {
		this.chatColor = chatColor;
	}

	private ChatColor chatColor;

	public MissilewarsTeam getOpposite() {
		return this == RED ? GREEN : RED;
	}

	public Team toTeam() {
		return this == RED ? NovaFinalMissileWars.getInstance().getGame().getRedTeam() : NovaFinalMissileWars.getInstance().getGame().getGreenTeam();
	}

	public ChatColor getChatColor() {
		return chatColor;
	}

	@Nullable
	public static MissilewarsTeam get(Player player) {
		return NovaFinalMissileWars.getInstance().getGame().getPlayerMissilewarsTeam(player);
	}
}