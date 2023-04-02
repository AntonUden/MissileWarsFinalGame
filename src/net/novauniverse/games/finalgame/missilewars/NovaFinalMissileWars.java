package net.novauniverse.games.finalgame.missilewars;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.games.finalgame.missilewars.game.FinalMissileWars;
import net.novauniverse.games.finalgame.missilewars.game.commands.MissileWarsItemsCommand;
import net.novauniverse.games.finalgame.missilewars.game.item.GunBlade;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.ArrowItem;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.FireballItem;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.ShieldItem;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerBoots;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerChestplate;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerHelmet;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.armor.PlayerLeggings;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Guardian;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Juggernaut;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Lightning;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Shieldbuster;
import net.novauniverse.games.finalgame.missilewars.game.item.loot.missiles.Tomahawk;
import net.novauniverse.games.finalgame.missilewars.lobby.MissileWarsGameLobby;
import net.novauniverse.games.finalgame.missilewars.lobby.MissileWarsLobbyMapData;
import net.novauniverse.games.finalgame.missilewars.team.MissileWarsFinalGameTeamProvider;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.UnzipUtility;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItemManager;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIManager;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseManager;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseWorld;
import net.zeeraa.novacore.spigot.module.modules.multiverse.PlayerUnloadOption;
import net.zeeraa.novacore.spigot.module.modules.multiverse.WorldUnloadOption;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;

public class NovaFinalMissileWars extends JavaPlugin implements Listener {

	private static NovaFinalMissileWars instance;

	private File mapFile;

	private FinalMissileWars game;

	private MultiverseWorld world;
	
	private MissileWarsFinalGameTeamProvider finalGameTeamProvider;
	
	private MissileWarsGameLobby gameLobbyMap;
	
	public MissileWarsGameLobby getGameLobbyMap() {
		return gameLobbyMap;
	}

	public static NovaFinalMissileWars getInstance() {
		return instance;
	}

	public MultiverseWorld getWorld() {
		return world;
	}

	public FinalMissileWars getGame() {
		return game;
	}
	
	public MissileWarsFinalGameTeamProvider getFinalGameTeamProvider() {
		return finalGameTeamProvider;
	} 
	
	public void setFinalGameTeamProvider(MissileWarsFinalGameTeamProvider finalGameTeamProvider) {
		this.finalGameTeamProvider = finalGameTeamProvider;
	}

	@Override
	public void onEnable() {
		NovaFinalMissileWars.instance = this;

		saveDefaultConfig();

		File dataDirectory = new File(getDataFolder().getPath() + File.separator + "game_data");
		
		mapFile = new File(dataDirectory.getPath() + File.separator + "missilewars_map");

		String mapDownloadUrl = getConfig().getString("map_download_url");

		if (!mapFile.exists()) {
			File tempFile = new File(getDataFolder().getPath() + File.separator + "temporary_download_data");
			if(tempFile.exists()) {
				tempFile.delete();
			}
			tempFile.mkdir();
			
			File zipFile = new File(dataDirectory.getPath() + File.separator + "missilewars.zip");
			if (zipFile.exists()) {
				zipFile.delete();
			}
			try {
				Log.info(this.getName(), "Downloading map form " + mapDownloadUrl);
				URL url = new URL(mapDownloadUrl);
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("User-Agent", "NovaUniverse MissileWars 1.0.0");
				conn.connect();
				FileUtils.copyInputStreamToFile(conn.getInputStream(), zipFile);

				UnzipUtility.unzip(zipFile.getAbsolutePath(), tempFile.getAbsolutePath());

				Log.info(this.getName(), "Download complete. Moving file to final directory");
				
				File[]filesInTemp = tempFile.listFiles();
				if(filesInTemp.length > 0) {
					File first = filesInTemp[0];
					FileUtils.moveDirectory(first, mapFile);
				} else {
					Log.fatal(this.getName(), "Something went wrong while extracting zip file since " + tempFile.getAbsolutePath() + " is empty");
					Bukkit.getServer().shutdown();
				}

				zipFile.delete();
				tempFile.delete();
			} catch (IOException e) {
				Log.fatal(this.getName(), "Failed to download map from " + mapDownloadUrl);
				e.printStackTrace();
				Bukkit.getServer().shutdown();
				return;
			}
		} else {
			Log.info(this.getName(), "Map already downloaded");
		}

		ModuleManager.require(MultiverseManager.class);
		ModuleManager.require(GameManager.class);
		ModuleManager.require(CustomItemManager.class);
		ModuleManager.require(GUIManager.class);
		ModuleManager.require(GameLobby.class);
		
		Log.info(this.getName(), "Loading world");
		try {
			world = MultiverseManager.getInstance().createFromFile(mapFile, WorldUnloadOption.DELETE);
			world.getWorld().setAutoSave(false);
			world.getWorld().setStorm(false);
			world.getWorld().setTime(1000);
			world.setLockWeather(true);
			world.setPlayerUnloadOptions(PlayerUnloadOption.SEND_TO_FIRST);
			world.setSaveOnUnload(false);
		} catch (Exception e) {
			e.printStackTrace();
			Log.fatal(this.getName(), "Failed to load world " + e.getClass().getName() + " " + e.getMessage());
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		gameLobbyMap = new MissileWarsGameLobby(world.getWorld(), new MissileWarsLobbyMapData());
		
		GameLobby.getInstance().setDisableAutoAddPlayers(true);
		GameLobby.getInstance().unloadActiveMap(true);
		GameLobby.getInstance().setActiveMap(gameLobbyMap);

		game = new FinalMissileWars();

		GameManager.getInstance().setUseTeams(true);
		GameManager.getInstance().setShowDeathMessage(true);
		GameManager.getInstance().loadGame(game);

		try {
			// Armor
			CustomItemManager.getInstance().addCustomItem(PlayerHelmet.class);
			CustomItemManager.getInstance().addCustomItem(PlayerChestplate.class);
			CustomItemManager.getInstance().addCustomItem(PlayerLeggings.class);
			CustomItemManager.getInstance().addCustomItem(PlayerBoots.class);

			// Items
			CustomItemManager.getInstance().addCustomItem(GunBlade.class);

			// Loot items
			CustomItemManager.getInstance().addCustomItem(FireballItem.class);
			CustomItemManager.getInstance().addCustomItem(ShieldItem.class);
			CustomItemManager.getInstance().addCustomItem(ArrowItem.class);

			// Missiles
			CustomItemManager.getInstance().addCustomItem(Guardian.class);
			CustomItemManager.getInstance().addCustomItem(Juggernaut.class);
			CustomItemManager.getInstance().addCustomItem(Lightning.class);
			CustomItemManager.getInstance().addCustomItem(Shieldbuster.class);
			CustomItemManager.getInstance().addCustomItem(Tomahawk.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Bukkit.getPluginManager().registerEvents(this, this);
		CommandRegistry.registerCommand(new MissileWarsItemsCommand());
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);
	}

	public Location getSpawnLocation() {
		return game.getSpawnLocation();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		Location spawnLocation = getSpawnLocation();

		if (game.hasStarted()) {
			if (!game.getPlayers().contains(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "Joined as spectator since the game has already started");
				player.setGameMode(GameMode.SPECTATOR);
				player.setDisplayName(ChatColor.GRAY + player.getName());
				player.spigot().respawn();
				PlayerUtils.clearPlayerInventory(player);

				new BukkitRunnable() {
					@Override
					public void run() {
						player.teleport(spawnLocation);
					}
				}.runTaskLater(this, 2L);
			}
		} else {
			PlayerUtils.clearPlayerInventory(player);
			player.setGameMode(GameMode.ADVENTURE);
			PlayerUtils.resetMaxHealth(player);
			PlayerUtils.resetPlayerXP(player);
			PlayerUtils.fullyHealPlayer(player);
			player.spigot().respawn();

			Team team = TeamManager.getTeamManager().getPlayerTeam(player);

			if (team == null) {
				player.setDisplayName(ChatColor.WHITE + player.getName());
			} else {
				player.setDisplayName(team.getTeamColor() + player.getName());
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					player.teleport(spawnLocation);
				}
			}.runTaskLater(this, 2L);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (!game.hasStarted()) {
				e.setCancelled(true);
			}
		}
	}
}