package me.oddlyoko.cite;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.oddlyoko.cite.command.TeamCommand;
import me.oddlyoko.cite.config.ConfigManager;
import me.oddlyoko.cite.config.LangManager;
import me.oddlyoko.cite.depends.PlaceHolderDepends;
import me.oddlyoko.cite.depends.WorldGuardDepends;
import me.oddlyoko.cite.house.HouseManager;
import me.oddlyoko.cite.inventory.InventoryManager;
import me.oddlyoko.cite.listener.EntityListener;
import me.oddlyoko.cite.listener.PlayerListener;
import me.oddlyoko.cite.listener.SignListener;
import me.oddlyoko.cite.player.PlayerManager;
import me.oddlyoko.cite.team.TeamManager;

public class Cite extends JavaPlugin {
	public static Cite cite;
	@Getter
	private InventoryManager inventoryManager;
	@Getter
	private PlayerManager playerManager;
	@Getter
	private TeamManager teamManager;
	private ConfigManager config;
	@Getter
	private LangManager language;
	@Getter
	private HouseManager houseManager;
	@Getter
	private PlaceHolderDepends placeHolderDepends;
	@Getter
	private WorldGuardDepends worldGuardDepends;

	public Cite() {
		cite = this;
	}

	@Override
	public void onEnable() {
		File dir = new File("plugins" + File.separator + "Cite");
		if (!dir.exists())
			dir.mkdirs();
		saveResource("config.yml", false);
		config = new ConfigManager();
		saveResource("lang.yml", false);
		language = new LangManager();
		inventoryManager = new InventoryManager();
		inventoryManager.init();
		houseManager = new HouseManager();
		teamManager = new TeamManager();
		playerManager = new PlayerManager();
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new SignListener(), this);
		Bukkit.getPluginCommand("team").setExecutor(new TeamCommand());
		// Load PlaceHolderAPI
		if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
			placeHolderDepends = new PlaceHolderDepends();
			placeHolderDepends.onEnable();
		}
		// Load WorldGuard
		if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
			worldGuardDepends = new WorldGuardDepends();
		}

		// Check if there is players connected
		for (Player p : Bukkit.getOnlinePlayers()) {
			me.oddlyoko.cite.player.Player player = playerManager.getPlayer(p.getUniqueId());
			if (player == null) {
				// Player not found, creating player ...
				Cite.get().getPlayerManager().createPlayer(p);
			}
		}
	}

	@Override
	public void onDisable() {
		inventoryManager.closeInventories();
	}

	public ConfigManager getCiteConfig() {
		return config;
	}

	public static Cite get() {
		return cite;
	}
}
