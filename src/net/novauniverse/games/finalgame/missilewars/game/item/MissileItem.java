package net.novauniverse.games.finalgame.missilewars.game.item;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.novauniverse.games.finalgame.missilewars.game.gameobject.GameObjectIndex;
import net.novauniverse.games.finalgame.missilewars.game.gameobject.GameObjectType;
import net.novauniverse.games.finalgame.missilewars.team.MissilewarsTeam;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItem;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.ItemUtils;

public abstract class MissileItem extends CustomItem {
	private String name;
	private EntityType mob;
	private GameObjectType missile;

	public MissileItem(String name, EntityType mob, GameObjectType missile) {
		this.name = name;
		this.mob = mob;
		this.missile = missile;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
				return;
			}

			Block block = e.getClickedBlock();

			MissilewarsTeam team = MissilewarsTeam.get(null);

			if (team == null) {
				return;
			}

			GameObjectIndex.spawnObject(team, missile, block.getLocation());
			ItemUtils.removeOneFromHand(e.getPlayer());
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected ItemStack createItemStack(Player player) {
		ItemStack stack = new ItemBuilder(Material.MONSTER_EGG).setName(name).build();

		stack.setDurability(mob.getTypeId());

		ItemMeta meta = stack.getItemMeta();

		meta.setDisplayName(name);

		stack.setItemMeta(meta);

		return stack;
	}
}