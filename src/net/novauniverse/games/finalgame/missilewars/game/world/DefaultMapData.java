package net.novauniverse.games.finalgame.missilewars.game.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.Vector;

import net.novauniverse.games.finalgame.missilewars.team.MissilewarsTeam;
import net.zeeraa.novacore.commons.utils.Rotation;
import net.zeeraa.novacore.spigot.utils.VectorArea;

public class DefaultMapData {
	/* -=-= Spawn =-=- */
	public static final Vector SPAWN_LOCATION = new Vector(-100.5, 70, 0.5);
	public static final Rotation SPAWN_ROTATION = new Rotation(90F, 0F);

	/* -=-= Portals =-=- */
	public static final List<PortalLocation> PORTAL_LOCATIONS = new ArrayList<PortalLocation>();
	static {
		// Red
		DefaultMapData.PORTAL_LOCATIONS.add(new PortalLocation(new Vector(-27.5, 52, -71.5), MissilewarsTeam.RED));
		DefaultMapData.PORTAL_LOCATIONS.add(new PortalLocation(new Vector(-25.5, 52, -71.5), MissilewarsTeam.RED));

		// Green
		DefaultMapData.PORTAL_LOCATIONS.add(new PortalLocation(new Vector(-25.5, 52, 72.5), MissilewarsTeam.GREEN));
		DefaultMapData.PORTAL_LOCATIONS.add(new PortalLocation(new Vector(-27.5, 52, 72.5), MissilewarsTeam.GREEN));
	}

	/* -=-= Team spawn =-=- */
	public static final Vector RED_TEAM_SPAWN_LOCATION = new Vector(-26.5, 77, -57.5);
	public static final Rotation RED_TEAM_SPAWN_ROTATION = new Rotation(0F, 0F);

	public static final Vector GREEN_TEAM_SPAWN_LOCATION = new Vector(-26.5, 77, 58.5);
	public static final Rotation GREEN_TEAM_SPAWN_ROTATION = new Rotation(180, 0F);

	/* -=-= Join team area =-=- */
	public static final VectorArea RED_TEAM_JOIN_AREA = new VectorArea(new Vector(-118, 66, -5), new Vector(-118, 68, -9));
	public static final VectorArea GREEN_TEAM_JOIN_AREA = new VectorArea(new Vector(-118, 66, 9), new Vector(-118, 68, 6));
	
	/* -=-= Team waiting locations =-=- */
	public static final Vector RED_TEAM_WAITING_LOCATION = new Vector(-81.5, 78, -18.5);
	public static final Rotation RED_TEAM_WAITING_ROTATION = new Rotation(-90, 0);
	
	public static final Vector GREEN_TEAM_WAITING_LOCATION = new Vector(-81.5, 78, 19.5);
	public static final Rotation GREEN_TEAM_WAITING_ROTATION = new Rotation(-90, 0);
}
