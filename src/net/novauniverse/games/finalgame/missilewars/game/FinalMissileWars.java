package net.novauniverse.games.finalgame.missilewars.game;

import java.io.File;
import java.util.ArrayList;
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
import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.games.finalgame.missilewars.NovaFinalMissileWars;
import net.novauniverse.games.finalgame.missilewars.game.event.FinalGameMissileWarsGameEndEvent;
import net.novauniverse.games.finalgame.missilewars.game.event.FinalGameMissileWarsGameStartEvent;
import net.novauniverse.games.finalgame.missilewars.game.gameobject.GameObject;
import net.novauniverse.games.finalgame.missilewars.game.gameobject.GameObjectType;
import net.novauniverse.games.finalgame.missilewars.game.item.GunBlade;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerBoots;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerChestplate;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerHelmet;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerLeggings;
import net.novauniverse.games.finalgame.missilewars.game.loot.LootManager;
import net.novauniverse.games.finalgame.missilewars.game.world.PortalLocation;
import net.novauniverse.games.finalgame.missilewars.game.world.TeamConfig;
import net.novauniverse.games.finalgame.missilewars.team.MissilewarsTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.commons.utils.Pair;
import net.zeeraa.novacore.commons.utils.Rotation;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.PlayerDamageReason;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependentPlayerAchievementAwardedEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.Game;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.GameTrigger;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.RepeatingGameTrigger;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.TriggerCallback;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.TriggerFlag;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItem;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItemManager;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.LocationData;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;
import net.zeeraa.novacore.spigot.utils.VectorUtils;

public class FinalMissileWars extends Game implements Listener {
	public static int INSTANT_KILL_Y = -1;

	public static final long PLAYER_CHECK_TASK_INTERVAL = 5L;

	private boolean started;
	private boolean ended;
	private List<TeamConfig> teamConfigs;

	private GameTrigger lootTrigger;

	private Task winCheckTask;
	private Task playerCheckTask;

	private MissilewarsTeam winner;

	private Team team1;
	private Team team2;

	private List<PortalLocation> portalLocations;
	private List<GameObject> gameObjects;

	private LocationData spawnLocation;

	public Team getTeam1() {
		return team1;
	}

	public Team getTeam2() {
		return team2;
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}

	public GameObject getGameObject(GameObjectType type) {
		return gameObjects.stream().filter(o -> o.getType() == type).findFirst().orElse(null);
	}

	public List<TeamConfig> getTeamConfigs() {
		return teamConfigs;
	}

	public TeamConfig getTeamConfig(MissilewarsTeam team) {
		return teamConfigs.stream().filter(t -> t.getTeam() == team).findFirst().orElse(null);
	}

	public MissilewarsTeam getPlayerMissilewarsTeam(Player player) {
		if (team1.isMember(player)) {
			return MissilewarsTeam.TEAM_1;
		}

		if (team2.isMember(player)) {
			return MissilewarsTeam.TEAM_2;
		}
		return null;
	}

	public FinalMissileWars() {
		super(NovaFinalMissileWars.getInstance());
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

	public LocationData getSpawnLocationData() {
		return spawnLocation;
	}

	public Location getSpawnLocation() {
		return spawnLocation.toLocation(world);
	}

	@Override
	public void onLoad() {
		this.world = NovaFinalMissileWars.getInstance().getWorld().getWorld();
		this.winner = null;
		this.started = false;
		this.ended = false;
		this.gameObjects = new ArrayList<>();
		this.teamConfigs = new ArrayList<>();
		this.portalLocations = new ArrayList<>();

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

				boolean team1Online = team1.getMembers().stream().filter(players::contains).findFirst().isPresent();
				boolean team2Online = team2.getMembers().stream().filter(players::contains).findFirst().isPresent();

				if (!team1Online && !team2Online) {
					Bukkit.getServer().broadcastMessage(ChatColor.RED + "Game ended since both teams where eliminated");
					endGame(GameEndReason.DRAW);
				}

				if (!team1Online || !team2Online) {
					if (team1Online) {
						winner = MissilewarsTeam.TEAM_1;
					}

					if (team2Online) {
						winner = MissilewarsTeam.TEAM_2;
					}

					Bukkit.getServer().broadcastMessage(ChatColor.RED + "Game ended since only 1 team is remaining");

					endGame(GameEndReason.WIN);
				}
			}
		}, PLAYER_CHECK_TASK_INTERVAL);

		File worldFolder = world.getWorldFolder();
		File mapConfigFile = new File(worldFolder.getAbsolutePath() + File.separator + "MissileWarsMapData.json");
		Log.info("MissileWars", "Reading game config from " + mapConfigFile.getAbsolutePath());
		JSONObject mapConfig = null;
		try {
			mapConfig = JSONFileUtils.readJSONObjectFromFile(mapConfigFile);
		} catch (Exception e) {
			Log.fatal("MissileWars", "Failed to read file " + mapConfigFile.getAbsolutePath());
			Bukkit.getServer().shutdown();
			return;
		}

		JSONObject gameObjectData = mapConfig.getJSONObject("game_objects");

		JSONObject gameObjectPosition = gameObjectData.getJSONObject("position");
		JSONObject gameObjectRelative = gameObjectData.getJSONObject("relative");
		JSONObject gameObjectBounds = gameObjectData.getJSONObject("bounds");

		JSONObject spawnLocations = mapConfig.getJSONObject("spawn_locations");
		JSONObject portalSamplingAreas = mapConfig.getJSONObject("portal_sampling_areas");

		for (GameObjectType type : GameObjectType.values()) {
			Vector position = VectorUtils.fromJSONObject(gameObjectPosition.getJSONObject(type.name()));
			Vector relative = VectorUtils.fromJSONObject(gameObjectRelative.getJSONObject(type.name()));
			Pair<Vector> bounds = VectorUtils.vectorPairFromJSON(gameObjectBounds.getJSONObject(type.name()));

			gameObjects.add(new GameObject(type, position, relative, bounds));

			Log.debug("MissileWars", "Loaded game object " + type.name());
		}

		for (MissilewarsTeam team : MissilewarsTeam.values()) {
			JSONObject spawnLocationData = spawnLocations.getJSONObject(team.name());
			JSONArray portals = portalSamplingAreas.getJSONArray(team.name());

			List<Vector> portalSamplingAreaList = new ArrayList<>();
			LocationData locationData = LocationData.fromJSON(spawnLocationData);
			for (int i = 0; i < portals.length(); i++) {
				portalSamplingAreaList.add(VectorUtils.fromJSONObject(portals.getJSONObject(i)));
			}

			teamConfigs.add(new TeamConfig(team, portalSamplingAreaList, locationData));
		}

		teamConfigs.forEach(tc -> tc.getPortalSamplingAreas().forEach(psa -> portalLocations.add(new PortalLocation(psa, tc.getTeam()))));

		spawnLocation = LocationData.fromJSON(mapConfig.getJSONObject("spawn_location"));
		NovaFinalMissileWars.getInstance().getGameLobbyMap().setSpawnLocation(spawnLocation.toLocation(world));

		INSTANT_KILL_Y = mapConfig.optInt("instant_kill_y", -1);

		this.lootTrigger = new RepeatingGameTrigger("missilewars.loot", 1L, mapConfig.optLong("loot_interval", 280), new TriggerCallback() {
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

		team1 = participants.getObject1();
		team2 = participants.getObject2();

		team1.getOnlinePlayers().forEach(this::addPlayer);
		team2.getOnlinePlayers().forEach(this::addPlayer);
		
		ModuleManager.disable(GameLobby.class);

		Log.debug("MissileWars", "Player count: " + this.players.size());

		Bukkit.getServer().getOnlinePlayers().forEach((player) -> {
			if (!isPlayerInGame(player)) {
				player.setGameMode(GameMode.SPECTATOR);
			}
		});

		VersionIndependentUtils.get().broadcastTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "Missilewars", team1.getTeamColor() + team1.getDisplayName() + ChatColor.GOLD + " vs " + team2.getTeamColor() + team2.getDisplayName(), 0, 80, 20);

		started = true;
		winCheckTask.start();
		playerCheckTask.start();

		Bukkit.getServer().getOnlinePlayers().stream().filter(p -> isPlayerInGame(p)).forEach(this::setupPlayer);

		FinalGameMissileWarsGameStartEvent e = new FinalGameMissileWarsGameStartEvent();
		Bukkit.getServer().getPluginManager().callEvent(e);

		this.sendBeginEvent();
	}

	public void setupPlayer(Player player) {
		PlayerUtils.clearPlayerInventory(player);
		givePlayerItems(player);
		setSpawnAndTeleportToTeam(player);
	}

	public void givePlayerItems(Player player) {
		player.getInventory().addItem(CustomItemManager.getInstance().getCustomItemStack(GunBlade.class, player));

		player.getInventory().setHelmet(CustomItemManager.getInstance().getCustomItemStack(PlayerHelmet.class, player));
		player.getInventory().setChestplate(CustomItemManager.getInstance().getCustomItemStack(PlayerChestplate.class, player));
		player.getInventory().setLeggings(CustomItemManager.getInstance().getCustomItemStack(PlayerLeggings.class, player));
		player.getInventory().setBoots(CustomItemManager.getInstance().getCustomItemStack(PlayerBoots.class, player));
	}

	private boolean setSpawnAndTeleportToTeam(Player player) {
		MissilewarsTeam team = MissilewarsTeam.get(player);

		if (team == null) {
			Log.trace("setSpawnAndTeleportToTeam()", "Player " + player.getName() + " has no team for some reason");
			return false;
		}

		Location location = getTeamConfig(team).getSpawnLocation().toLocation(world);

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

		VersionIndependentSound.WITHER_DEATH.broadcast();
		Bukkit.getServer().getOnlinePlayers().forEach(p -> {
			p.setGameMode(GameMode.SPECTATOR);
		});
		
		
		if (reason == GameEndReason.WIN) {
			String message = winner.toTeam().getTeamColor() + ChatColor.BOLD.toString() + winner.toTeam().getDisplayName() + ChatColor.GREEN + ChatColor.BOLD + " won the game";
			VersionIndependentUtils.get().broadcastTitle("", message, 0, 80, 20);

			Bukkit.getServer().broadcastMessage(message);

			FinalGameMissileWarsGameEndEvent e = new FinalGameMissileWarsGameEndEvent(winner.toTeam(), reason);
			Bukkit.getPluginManager().callEvent(e);
			
			getGameObject(GameObjectType.WIN).spawn(winner, world);
		}
	}

	/* -=-=-=-=-= Listeners =-=-=-=-=- */

	// Fix rotation
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();

		if (hasStarted()) {
			MissilewarsTeam team = MissilewarsTeam.get(player);
			Rotation rotation = new Rotation(0, 0);
			if (team != null) {
				rotation = getTeamConfig(team).getSpawnLocation().getRotation();
			}

			Location location = e.getRespawnLocation();

			location.setYaw(rotation.getYaw());
			location.setPitch(rotation.getPitch());

			e.setRespawnLocation(location);
		}
	}

	// Handle join
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (hasStarted()) {
			if (players.contains(player.getUniqueId())) {
				setupPlayer(player);
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
		ChatColor playerColor = TeamManager.getTeamManager().tryGetPlayerTeamColor(e.getEntity(), ChatColor.AQUA);
		e.setDeathMessage(e.getDeathMessage().replaceAll(Pattern.compile(e.getEntity().getName()).pattern(), playerColor + e.getEntity().getName() + ChatColor.RESET));

		Player killer = e.getEntity().getKiller();
		if (killer != null) {
			ChatColor killerColor = TeamManager.getTeamManager().tryGetPlayerTeamColor(killer, ChatColor.AQUA);
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