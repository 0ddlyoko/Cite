package me.oddlyoko.cite.depends;

import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import me.oddlyoko.cite.Cite;
import me.oddlyoko.cite.__;
import me.oddlyoko.cite.player.Player;

public class PlaceHolderDepends {

	public void onEnable() {
		Bukkit.getLogger().log(Level.INFO, __.PREFIX + ChatColor.GOLD + "Loading placeHolder ...");
		PlaceholderAPI.registerPlaceholder(Cite.get(), "cite_player", e -> {
			UUID uuid = e.isOnline() ? e.getPlayer().getUniqueId() : e.getOfflinePlayer().getUniqueId();
			Player p = Cite.get().getPlayerManager().getPlayer(uuid);
			if (p == null)
				return "????";
			return p.getName();
		});
		PlaceholderAPI.registerPlaceholder(Cite.get(), "cite_team_name", e -> {
			UUID uuid = e.isOnline() ? e.getPlayer().getUniqueId() : e.getOfflinePlayer().getUniqueId();
			Player p = Cite.get().getPlayerManager().getPlayer(uuid);
			if (p == null)
				return "????";
			return p.getTeam().getName();
		});
		PlaceholderAPI.registerPlaceholder(Cite.get(), "cite_team_owner", e -> {
			UUID uuid = e.isOnline() ? e.getPlayer().getUniqueId() : e.getOfflinePlayer().getUniqueId();
			Player p = Cite.get().getPlayerManager().getPlayer(uuid);
			if (p == null)
				return "????";
			return p.getTeam().getOwner().getName();
		});
		PlaceholderAPI.registerPlaceholder(Cite.get(), "cite_team_emerald", e -> {
			UUID uuid = e.isOnline() ? e.getPlayer().getUniqueId() : e.getOfflinePlayer().getUniqueId();
			Player p = Cite.get().getPlayerManager().getPlayer(uuid);
			if (p == null)
				return "0";
			return Integer.toString(p.getTeam().getEmerald());
		});
		PlaceholderAPI.registerPlaceholder(Cite.get(), "cite_team_other", e -> {
			UUID uuid = e.isOnline() ? e.getPlayer().getUniqueId() : e.getOfflinePlayer().getUniqueId();
			Player p = Cite.get().getPlayerManager().getPlayer(uuid);
			if (p == null)
				return "";
			return p.getTeam().getOthers().stream().map(player -> player.getName()).collect(Collectors.joining(", "));
		});
		PlaceholderAPI.registerPlaceholder(Cite.get(), "cite_team_size", e -> {
			UUID uuid = e.isOnline() ? e.getPlayer().getUniqueId() : e.getOfflinePlayer().getUniqueId();
			Player p = Cite.get().getPlayerManager().getPlayer(uuid);
			if (p == null)
				return "0";
			return Integer.toString(p.getTeam().getOthers().size());
		});
		Bukkit.getLogger().log(Level.INFO, __.PREFIX + ChatColor.GOLD + "PlaceHolder loaded");
	}
}
