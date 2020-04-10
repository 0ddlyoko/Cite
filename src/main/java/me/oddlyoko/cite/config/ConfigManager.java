package me.oddlyoko.cite.config;

import java.io.File;
import java.util.List;

import lombok.Getter;

@Getter
public class ConfigManager {
	private Config config;
	private int maxPlayer;
	private int nameLength;
	private boolean disableVillager;
	private boolean broadcastHouse;

	private String portalWorld;
	private List<String> portalRegions;

	private boolean preventGodApple;

	public ConfigManager() {
		File languageFile = new File("plugins" + File.separator + "Cite" + File.separator + "config.yml");
		config = new Config(languageFile);
		maxPlayer = config.getInt("max_player");
		nameLength = config.getInt("name_length");
		disableVillager = config.getBoolean("disable_villager");
		broadcastHouse = config.getBoolean("broadcast_house");

		portalWorld = config.getString("portal.world");
		portalRegions = config.getStringList("portal.regions");

		preventGodApple = config.getBoolean("prevent_god_apple");
	}
}
