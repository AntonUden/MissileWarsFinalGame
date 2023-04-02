package net.novauniverse.games.finalgame.missilewars.game.world;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;

import net.novauniverse.games.finalgame.missilewars.NovaFinalMissileWars;
import net.novauniverse.games.finalgame.missilewars.game.gameobject.GameObjectType;
import net.novauniverse.games.finalgame.missilewars.team.MissilewarsTeam;

public class SpawnItems {
	public static void spawnFireball(Player player) {
		final Fireball fireball = player.launchProjectile(Fireball.class);
		fireball.setVelocity(player.getLocation().getDirection().multiply(2));
		fireball.setBounce(false);
		fireball.setIsIncendiary(true);
		fireball.setCustomName(ChatColor.GOLD + "Fireball");
		fireball.setCustomNameVisible(false);
		fireball.setShooter(player);

	}

	public static void spawnShield(Player player) {
		MissilewarsTeam team = NovaFinalMissileWars.getInstance().getGame().getPlayerMissilewarsTeam(player);

		if (team == null) {
			return;
		}

		final Snowball shield = player.launchProjectile(Snowball.class);
		shield.setCustomName(ChatColor.DARK_BLUE + "Shield");
		shield.setCustomNameVisible(false);
		shield.setBounce(false);

		Bukkit.getScheduler().scheduleSyncDelayedTask(NovaFinalMissileWars.getInstance(), () -> {
			if (shield != null && !shield.isDead() && !shield.isOnGround()) {
				NovaFinalMissileWars.getInstance().getGame().getGameObject(GameObjectType.SHIELD).spawn(team, shield.getLocation());
				shield.remove();
			}
		}, 20L);
	}
}