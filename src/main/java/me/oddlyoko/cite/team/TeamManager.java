package me.oddlyoko.cite.team;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import me.oddlyoko.cite.Cite;
import me.oddlyoko.cite.config.Config;

public class TeamManager implements Listener {
	private HashMap<UUID, Team> teams;
	private File directory;

	public TeamManager() {
		teams = new HashMap<>();
		directory = new File("plugins" + File.separator + "Cite" + File.separator + "teams");
		if (!directory.exists())
			directory.mkdirs();
	}

	public Team getOrLoad(UUID team) {
		Team t = teams.get(team);
		if (t != null)
			return t;
		// Load team
		Config config = new Config(new File(directory, team.toString() + ".yml"));
		UUID uuid = UUID.fromString(config.getString("uuid"));
		String name = config.getString("name");
		int emerald = config.getInt("emerald");
		t = new Team(uuid, name, emerald);
		teams.put(uuid, t);
		return t;
	}

	public Team saveTeam(Team team) {
		Bukkit.getScheduler().runTaskAsynchronously(Cite.get(), () -> {
			Config c = new Config(new File(directory, team.getOwnerUuid().toString() + ".yml"));
			c.set("uuid", team.getOwnerUuid().toString());
			c.set("name", team.getName());
			c.set("emerald", team.getEmerald());
			c.save();
		});
		return team;
	}

	public Team createTeam(UUID uuid, String name) {
		if (name.length() > Cite.get().getCiteConfig().getNameLength())
			name = name.substring(0, Cite.get().getCiteConfig().getNameLength());
		Team t = new Team(uuid, name, 0);
		teams.put(uuid, t);
		return saveTeam(t);
	}
}
