package net.novauniverse.games.finalgame.missilewars.team;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.games.finalgame.missilewars.NovaFinalMissileWars;
import net.zeeraa.novacore.spigot.teams.Team;

public enum MissilewarsTeam {
	TEAM_1(ChatColor.RED), TEAM_2(ChatColor.GREEN);

	private MissilewarsTeam(ChatColor chatColor) {
		this.chatColor = chatColor;
	}

	private ChatColor chatColor;

	public MissilewarsTeam getOpposite() {
		return this == TEAM_1 ? TEAM_2 : TEAM_1;
	}

	public Team toTeam() {
		return this == TEAM_1 ? NovaFinalMissileWars.getInstance().getGame().getTeam1() : NovaFinalMissileWars.getInstance().getGame().getTeam2();
	}

	public ChatColor getChatColor() {
		return chatColor;
	}

	@Nullable
	public static MissilewarsTeam get(Player player) {
		return NovaFinalMissileWars.getInstance().getGame().getPlayerMissilewarsTeam(player);
	}
}