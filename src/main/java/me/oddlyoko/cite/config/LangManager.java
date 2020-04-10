package me.oddlyoko.cite.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LangManager {
	private Config config;
	private HashMap<String, String> langs;
	private HashMap<String, List<String>> langsList;

	public LangManager() {
		File languageFile = new File("plugins" + File.separator + "Cite" + File.separator + "lang.yml");
		config = new Config(languageFile);
		langs = new HashMap<>();
		langsList = new HashMap<>();
		load("commands.toggle.enable");
		load("commands.toggle.disable");

		load("commands.invite.yourself");
		load("commands.invite.notOwner");
		load("commands.invite.alreadyTeam");
		load("commands.invite.notConnected");
		load("commands.invite.hasTeam");
		load("commands.invite.full");
		load("commands.invite.invitation");
		load("commands.invite.send");
		load("commands.invite.receive");

		load("commands.kick.yourself");
		load("commands.kick.notTeammate");
		load("commands.kick.notOwner");
		load("commands.kick.other");
		load("commands.kick.kicked");

		load("commands.name.notOwner");
		load("commands.name.limit");
		load("commands.name.changed");

		load("commands.leave.owner");
		load("commands.leave.other");
		load("commands.leave.leave");

		load("commands.accept.noInvitation");
		load("commands.accept.full");
		load("commands.accept.sent");
		load("commands.accept.other");

		load("commands.other.error");
		load("commands.other.syntax");

		load("inventory.emerald_deposit.title");
		load("inventory.emerald_deposit.item.title");
		loadList("inventory.emerald_deposit.item.lore");

		loadList("sign.emerald");

		loadList("sign.house.free");
		loadList("sign.house.taken");
		load("sign.house.create");
		load("sign.house.delete");
		load("sign.house.buy.self");
		load("sign.house.buy.self");
		load("sign.house.buy.other");
	}

	private void load(String key) {
		langs.put(key, config.getString(key));
	}

	private void loadList(String key) {
		langsList.put(key, config.getStringList(key));
	}

	public String get(String key) {
		return langs.getOrDefault(key, key);
	}

	public List<String> getList(String key) {
		List<String> list = langsList.get(key);
		return list == null ? new ArrayList<>() : list;
	}
}
