package net.novauniverse.games.finalgame.missilewars.game;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.games.finalgame.missilewars.NovaFinalMissileWars;
import net.novauniverse.games.finalgame.missilewars.game.event.FinalGameMissileWarsGameEndEvent;
import net.novauniverse.games.finalgame.missilewars.game.event.FinalGameMissileWarsGameStartEvent;
import net.novauniverse.games.finalgame.missilewars.game.gameobject.GameObjectIndex;
import net.novauniverse.games.finalgame.missilewars.game.gameobject.GameObjectType;
import net.novauniverse.games.finalgame.missilewars.game.item.GunBlade;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerBoots;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerChestplate;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerHelmet;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerLeggings;
import net.novauniverse.games.finalgame.missilewars.game.loot.LootManager;
import net.novauniverse.games.finalgame.missilewars.game.world.DefaultMapData;
import net.novauniverse.games.finalgame.missilewars.game.world.PortalLocation;
import net.novauniverse.games.finalgame.missilewars.team.MissilewarsTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.Pair;
import net.zeeraa.novacore.commons.utils.Rotation;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.enums.PlayerDamageReason;
import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependentPlayerAchievementAwardedEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.Game;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.GameTrigger;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.RepeatingGameTrigger;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.TriggerCallback;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.TriggerFlag;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItem;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItemManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;

public class FinalMissileWars extends Game implements Listener {
	public static final int INSTANT_KILL_Y = -1;
	public static final long LOOT_DELAY = 280L;

	public static final long PLAYER_CHECK_TASK_INTERVAL = 5L;

	private boolean started;
	private boolean ended;
	private List<PortalLocation> portalLocations;

	private GameTrigger lootTrigger;

	private Task winCheckTask;
	private Task playerCheckTask;

	private MissilewarsTeam winner;

	private Team redTeam;
	private Team greenTeam;

	public Team getRedTeam() {
		return redTeam;
	}

	public Team getGreenTeam() {
		return greenTeam;
	}

	public MissilewarsTeam getPlayerMissilewarsTeam(Player player) {
		if (redTeam.isMember(player)) {
			return MissilewarsTeam.RED;
		}

		if (greenTeam.isMember(player)) {
			return MissilewarsTeam.GREEN;
		}
		return null;
	}

	public FinalMissileWars(List<PortalLocation> portalLocations) {
		super(NovaFinalMissileWars.getInstance());

		this.portalLocations = portalLocations;
	}

	@Override
	public String getName() {
		return "missilewars_finalgame";
	}

	@Override
	public String getDisplayName() {
		return "Missile Wars";
	}

	@Override
	public PlayerQuitEliminationAction getPlayerQuitEliminationAction() {
		return PlayerQuitEliminationAction.NONE;
	}

	@Override
	public boolean eliminatePlayerOnDeath(Player player) {
		return false;
	}

	@Override
	public boolean isPVPEnabled() {
		return hasStarted();
	}

	@Override
	public boolean autoEndGame() {
		return false;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

	@Override
	public boolean hasEnded() {
		return ended;
	}

	@Override
	public boolean isFriendlyFireAllowed() {
		return false;
	}

	@Override
	public boolean canAttack(LivingEntity attacker, LivingEntity target) {
		return true;
	}

	@Override
	public boolean canStart() {
		return true;
	}

	@Override
	public void onLoad() {
		this.world = NovaFinalMissileWars.getInstance().getWorld().getWorld();
		this.winner = null;
		this.started = false;
		this.ended = false;

		this.winCheckTask = new SimpleTask(() -> {
			portalLocations.stream().filter(p -> p.isBroken(world)).findFirst().ifPresent(portalLocation -> {
				winner = portalLocation.getColor().getOpposite();

				Log.debug("MissileWars", "Portal at " + portalLocation.getVector().toString() + " owned by " + portalLocation.getColor().name() + " team was destroyed. Ending game with " + winner.name() + " team as winner");
				Log.trace("MissileWars", "Material at portal is " + LocationUtils.getLocation(world, portalLocation.getVector()).getBlock().getType().name() + " world: " + world.getName());

				endGame(GameEndReason.WIN);
			});
		}, 4L);

		this.playerCheckTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().getOnlinePlayers().stream().filter(p -> p.getLocation().getY() < INSTANT_KILL_Y).forEach(player -> {
					if (players.contains(player.getUniqueId())) {
						NovaCore.getInstance().getVersionIndependentUtils().damagePlayer(player, PlayerDamageReason.OUT_OF_WORLD, 1000F);
					} else {
						player.setFallDistance(0);
						player.teleport(NovaFinalMissileWars.getInstance().getSpawnLocation());
					}
				});

				boolean redTeamOnline = false;
				boolean greenTeamOnline = false;

				if (!redTeamOnline && !greenTeamOnline) {
					Bukkit.getServer().broadcastMessage(ChatColor.RED + "Game ended since both teams where eliminated");
					endGame(GameEndReason.DRAW);
				}

				if (!redTeamOnline || !greenTeamOnline) {
					if (redTeamOnline) {
						winner = MissilewarsTeam.RED;
					}

					if (greenTeamOnline) {
						winner = MissilewarsTeam.GREEN;
					}

					Bukkit.getServer().broadcastMessage(ChatColor.RED + "Game ended since only 1 team is remaining");
					
					endGame(GameEndReason.WIN);
				}
			}
		}, PLAYER_CHECK_TASK_INTERVAL);

		this.lootTrigger = new RepeatingGameTrigger("missilewars.loot", 1L, LOOT_DELAY, new TriggerCallback() {
			@Override
			public void run(GameTrigger trigger, TriggerFlag reason) {
				Class<? extends CustomItem> item = LootManager.getRandom(random);

				for (UUID uuid : players) {
					Player player = Bukkit.getServer().getPlayer(uuid);

					if (player != null) {
						if (player.isOnline()) {
							ItemStack stack = CustomItemManager.getInstance().getCustomItemStack(item, player);
							player.getInventory().addItem(stack);
						}
					}
				}
			}
		});
		lootTrigger.setDescription("Give players loot");
		lootTrigger.addFlag(TriggerFlag.START_ON_GAME_START);
		lootTrigger.addFlag(TriggerFlag.STOP_ON_GAME_END);

		addTrigger(lootTrigger);
	}

	@Override
	public void onStart() {
		Pair<Team> participants = NovaFinalMissileWars.getInstance().getFinalGameTeamProvider().getParticipants();

		redTeam = participants.getObject1();
		greenTeam = participants.getObject2();
		
		redTeam.getOnlinePlayers().forEach(this::addPlayer);
		greenTeam.getOnlinePlayers().forEach(this::addPlayer);
		
		Bukkit.getServer().getOnlinePlayers().forEach((player) -> {
			if (!greenTeam.isMember(player) && !redTeam.isMember(player)) {
				player.setGameMode(GameMode.SPECTATOR);
			}
		});

		started = true;
		winCheckTask.start();
		playerCheckTask.start();

		Bukkit.getServer().getOnlinePlayers().forEach((player) -> {
			PlayerUtils.clearPlayerInventory(player);

			givePlayerItems(player);

			setSpawnAndTeleportToTeam(player);
		});

		this.sendBeginEvent();

		FinalGameMissileWarsGameStartEvent e = new FinalGameMissileWarsGameStartEvent();
		Bukkit.getServer().getPluginManager().callEvent(e);
	}

	private void givePlayerItems(Player player) {
		player.getInventory().addItem(CustomItemManager.getInstance().getCustomItemStack(GunBlade.class, player));

		player.getInventory().setHelmet(CustomItemManager.getInstance().getCustomItemStack(PlayerHelmet.class, player));
		player.getInventory().setChestplate(CustomItemManager.getInstance().getCustomItemStack(PlayerChestplate.class, player));
		player.getInventory().setLeggings(CustomItemManager.getInstance().getCustomItemStack(PlayerLeggings.class, player));
		player.getInventory().setBoots(CustomItemManager.getInstance().getCustomItemStack(PlayerBoots.class, player));
	}

	private boolean setSpawnAndTeleportToTeam(Player player) {
		MissilewarsTeam team = MissilewarsTeam.get(player);

		if (team == null) {
			return false;
		}

		Location location;

		switch (team) {
		case GREEN:
			location = LocationUtils.getLocation(world, DefaultMapData.GREEN_TEAM_SPAWN_LOCATION, DefaultMapData.GREEN_TEAM_SPAWN_ROTATION);
			break;

		case RED:
			location = LocationUtils.getLocation(world, DefaultMapData.RED_TEAM_SPAWN_LOCATION, DefaultMapData.RED_TEAM_SPAWN_ROTATION);
			break;

		default:
			return false;
		}

		player.setDisplayName(team.getChatColor() + player.getName());

		NetherBoardScoreboard.getInstance().setPlayerNameColorBungee(player, team.getChatColor());

		player.setBedSpawnLocation(location, true);
		player.teleport(location);
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setFallDistance(0);
		player.setFireTicks(0);

		return true;
	}

	@Override
	public void onEnd(GameEndReason reason) {
		ended = true;

		Task.tryStopTask(winCheckTask);
		Task.tryStopTask(playerCheckTask);

		if (reason == GameEndReason.WIN) {
			GameObjectIndex.spawnObject(winner, GameObjectType.WIN, world);

			// TODO: Detect winner team
			String message = winner.toTeam().getDisplayName() + " won the game";

			Bukkit.getServer().broadcastMessage(message);
		}

		FinalGameMissileWarsGameEndEvent e = new FinalGameMissileWarsGameEndEvent(winner.toTeam(), reason);
		Bukkit.getPluginManager().callEvent(e);
	}

	/* -=-=-=-=-= Listeners =-=-=-=-=- */

	// Fix rotation
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();

		if (hasStarted()) {
			MissilewarsTeam team = MissilewarsTeam.get(player);

			if (team != null) {
				Rotation rotation = null;

				switch (team) {
				case GREEN:
					rotation = DefaultMapData.GREEN_TEAM_SPAWN_ROTATION;
					break;

				case RED:
					rotation = DefaultMapData.RED_TEAM_SPAWN_ROTATION;
					break;

				default:
					rotation = new Rotation(0, 0);
					break;
				}

				Location location = e.getRespawnLocation();

				location.setYaw(rotation.getYaw());
				location.setPitch(rotation.getPitch());

				e.setRespawnLocation(location);
			}
		}
	}

	// Handle join
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (hasStarted()) {
			if (players.contains(e.getPlayer().getUniqueId())) {
				setSpawnAndTeleportToTeam(e.getPlayer());
			}
		}
	}

	// Prevent portals from working
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (e.getCause() == TeleportCause.NETHER_PORTAL || e.getCause() == TeleportCause.END_PORTAL) {
			e.setCancelled(true);
		}
	}

	// Auto respawning players
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e) {
		ChatColor playerColor = TeamManager.getTeamManager().tryGetPlayerTeamColor(e.getEntity(), net.md_5.bungee.api.ChatColor.AQUA);
		e.setDeathMessage(e.getDeathMessage().replaceAll(Pattern.compile(e.getEntity().getName()).pattern(), playerColor + e.getEntity().getName() + ChatColor.RESET));

		Player killer = e.getEntity().getKiller();
		if (killer != null) {
			ChatColor killerColor = TeamManager.getTeamManager().tryGetPlayerTeamColor(killer, net.md_5.bungee.api.ChatColor.AQUA);
			e.setDeathMessage(e.getDeathMessage().replace(Pattern.compile(killer.getName()).pattern(), killerColor + killer.getName() + ChatColor.RESET));
		}

		e.getEntity().setFallDistance(0F);
		e.getEntity().setFireTicks(0);
		e.getEntity().setVelocity(new Vector(0, 0, 0));

		new BukkitRunnable() {
			@Override
			public void run() {
				e.getEntity().spigot().respawn();
				e.getEntity().setFallDistance(0F);
				e.getEntity().setFireTicks(0);
				e.getEntity().setVelocity(new Vector(0, 0, 0));
			}
		}.runTaskLater(NovaFinalMissileWars.getInstance(), 2L);
	}

	// Prevent food decrease
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (((Player) e.getEntity()).getFoodLevel() > e.getFoodLevel()) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onVersionIndependantPlayerAchievementAwarded(VersionIndependentPlayerAchievementAwardedEvent e) {
		e.setCancelled(true);
	}

	// stop kicking falling players
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.getReason().equalsIgnoreCase("Flying is not enabled on this server")) {
			final Player p = event.getPlayer();
			if (p.getVelocity().getY() < 0) {
				event.setCancelled(true);
			}
		}
	}
}