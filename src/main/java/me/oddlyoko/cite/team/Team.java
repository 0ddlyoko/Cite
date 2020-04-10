package me.oddlyoko.cite.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;
import me.oddlyoko.cite.player.Player;

@Getter
public class Team {
	@Setter
	private Player owner;
	private UUID ownerUuid;
	@Setter
	private String name;
	@Setter
	private int emerald;
	private List<Player> others;

	public Team(UUID ownerUuid, String name, int emerald) {
		this.ownerUuid = ownerUuid;
		this.name = name;
		this.emerald = emerald;
		others = new ArrayList<>();
	}

	public void addPlayer(Player player) {
		others.add(player);
		// Check if player is owner
		if (ownerUuid.equals(player.getUuid()))
			this.owner = player;
	}

	public void removePlayer(Player player) {
		others.remove(player);
	}

	public boolean contains(UUID uuid) {
		for (Player p : others)
			if (p.getUuid().equals(uuid))
				return true;
		return false;
	}

	public boolean contains(String name) {
		for (Player p : others)
			if (p.getName().equalsIgnoreCase(name))
				return true;
		return false;
	}

	public void sendMessage(String msg) {
		for (Player p : others) {
			org.bukkit.entity.Player p2 = Bukkit.getPlayer(p.getUuid());
			if (p2 != null && p2.isOnline())
				p2.sendMessage(msg);
		}
	}
}
