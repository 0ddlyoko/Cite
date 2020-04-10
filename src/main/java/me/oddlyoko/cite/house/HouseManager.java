package me.oddlyoko.cite.house;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.oddlyoko.cite.Cite;
import me.oddlyoko.cite.config.Config;
import me.oddlyoko.cite.player.Player;

public class HouseManager {
	private HashMap<Integer, House> houses;
	private File configFile;
	private Config config;

	private Object sync = new Object();

	public HouseManager() {
		houses = new HashMap<>();
		configFile = new File("plugins" + File.separator + "Cite" + File.separator + "houses.yml");
		config = new Config(configFile);
		// Load all houses
		for (String key : config.getKeys("houses")) {
			String k = "houses." + key;
			int id = config.getInt(k + ".id");
			int cost = config.getInt(k + ".cost");
			List<String> regions = config.getStringList(k + ".regions");
			Location sign = config.getLocation(k + ".sign");
			House h = new House(id, cost, regions, sign);
			houses.put(id, h);
		}
	}

	public House createHouse(int id, int cost, List<String> regions, Location sign) {
		House house = new House(id, cost, regions, sign);
		houses.put(id, house);
		return saveHouse(house);
	}

	public House deleteHouse(House house) {
		if (house.getOwner() != null) {
			for (String region : house.getRegions())
				Cite.get().getWorldGuardDepends().removePlayerFromRegion(house.getOwner().getUuid(),
						house.getSign().getWorld(), region);
			house.getOwner().setHouse(null);
			Cite.get().getPlayerManager().savePlayer(house.getOwner());
			house.setOwner(null);
		}
		houses.remove(house.getId());

		Bukkit.getScheduler().runTaskAsynchronously(Cite.get(), () -> {
			synchronized (sync) {
				config.set("houses." + house.getId(), null);
				config.save();
			}
		});
		return house;
	}

	public House saveHouse(House house) {
		Bukkit.getScheduler().runTaskAsynchronously(Cite.get(), () -> {
			// Save one at time
			synchronized (sync) {
				String k = "houses." + house.getId();
				config.set(k + ".id", house.getId());
				config.set(k + ".cost", house.getCost());
				config.set(k + ".regions", house.getRegions());
				config.set(k + ".sign", house.getSign());
				config.save();
			}
		});
		return house;
	}

	public void setHouse(House house, Player player) {
		house.setOwner(player);
		player.setHouse(house);
		for (String region : house.getRegions())
			Cite.get().getWorldGuardDepends().addPlayerInRegion(house.getOwner().getUuid(), house.getSign().getWorld(),
					region);
		Cite.get().getPlayerManager().savePlayer(player);
	}

	public House getHouse(int house) {
		return houses.get(house);
	}

	public House getHouse(Location loc) {
		for (House h : houses.values())
			if (h.getSign().equals(loc))
				return h;
		return null;
	}
}
