/*******************************************************************************
 * Copyright (C) 2020 Linux4
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.novauniverse.games.finalgame.missilewars.game.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import net.zeeraa.novacore.spigot.NovaCore;

public class MWStructureUtil {

	public static void clone(Location relativeTo, Location start, Location end, Location target, boolean ignoreAir) {
		int minX = Math.min(start.getBlockX(), end.getBlockX());
		int minY = Math.min(start.getBlockY(), end.getBlockY());
		int minZ = Math.min(start.getBlockZ(), end.getBlockZ());
		int maxX = Math.max(start.getBlockX(), end.getBlockX());
		int maxY = Math.max(start.getBlockY(), end.getBlockY());
		int maxZ = Math.max(start.getBlockZ(), end.getBlockZ());
		int relX = relativeTo.getBlockX();
		int relY = relativeTo.getBlockY();
		int relZ = relativeTo.getBlockZ();

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					Location source = new Location(start.getWorld(), x, y, z);
					Block sourceBlock = source.getBlock();
					Vector rel = new Vector(x - relX, y - relY, z - relZ);

					Location to = target.clone().add(rel);
					Block targetBlock = to.getBlock();

					//System.out.println("Source X: " + source.getBlockX() + " Y: " + source.getBlockY() + " Z: " + source.getBlockZ()+ " Type: " + sourceBlock.getType().name());
					//System.out.println("To X: " + to.getBlockX() + " Y: " + to.getBlockY() + " Z: " + to.getBlockZ());

					if (sourceBlock.getType() != Material.AIR || !ignoreAir) {
						targetBlock.setType(sourceBlock.getType(), true);

						NovaCore.getInstance().getVersionIndependentUtils().cloneBlockData(sourceBlock, targetBlock);

						try {
							targetBlock.getState().setData(sourceBlock.getState().getData());
						} catch (IllegalArgumentException ex) {
							// ex.printStackTrace();
						}
					} else if (targetBlock.getType() == Material.AIR) {
						targetBlock.setType(Material.STONE); // TODO: dirty fix
						targetBlock.setType(Material.AIR);
					}

					targetBlock.getState().update(true, true);
				}
			}
		}
	}
}