package me.oddlyoko.cite.player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.oddlyoko.cite.Cite;
import me.oddlyoko.cite.config.Config;
import me.oddlyoko.cite.house.House;
import me.oddlyoko.cite.team.Team;

public class PlayerManager {
	private HashMap<UUID, Player> players;
	private File directory;

	public PlayerManager() {
		players = new HashMap<>();
		directory = new File("plugins" + File.separator + "Cite" + File.separator + "players");
		if (!directory.exists())
			directory.mkdirs();
		// Load all players
		for (File file : directory.listFiles()) {
			// Load player
			Config playerConfig = new Config(file);
			UUID uuid = UUID.fromString(playerConfig.getString("uuid"));
			String name = playerConfig.getString("name");
			String teamUuid = playerConfig.getString("team");
			Team team = Cite.get().getTeamManager().getOrLoad(UUID.fromString(teamUuid));
			boolean invitation = playerConfig.getBoolean("invitation");
			int house = playerConfig.getInt("house");
			House h = Cite.get().getHouseManager().getHouse(house);
			Player p = new Player(uuid, name, team, invitation);
			if (h != null) {
				p.setHouse(h);
				h.setOwner(p);
			}
			team.addPlayer(p);
			players.put(uuid, p);
		}
	}

	public Player getPlayer(UUID uuid) {
		return players.get(uuid);
	}

	public Player savePlayer(Player p) {
		Bukkit.getScheduler().runTaskAsynchronously(Cite.get(), () -> {
			Config c = new Config(new File(directory, p.getUuid().toString() + ".yml"));
			c.set("uuid", p.getUuid().toString());
			c.set("name", p.getName());
			c.set("team", p.getTeam().getOwnerUuid().toString());
			c.set("invitation", p.isInvitation());
			c.set("house", p.getHouse() == null ? 0 : p.getHouse().getId());
			c.save();
		});
		return p;
	}

	/**
	 * Create player file and set player to his team
	 * 
	 * @param p
	 */
	public Player createPlayer(org.bukkit.entity.Player p) {
		// Create new team
		Team t = Cite.get().getTeamManager().createTeam(p.getUniqueId(), p.getName());
		// Create player
		Player player = new Player(p.getUniqueId(), p.getName(), t, true);
		t.addPlayer(player);
		players.put(p.getUniqueId(), player);
		return savePlayer(player);
	}
}
