package me.oddlyoko.cite.house;

import java.util.List;

import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;
import me.oddlyoko.cite.player.Player;

@Getter
public class House {
	private int id;
	private int cost;
	private List<String> regions;
	private Location sign;
	@Setter
	private Player owner;

	public House(int id, int cost, List<String> regions, Location sign) {
		this.id = id;
		this.cost = cost;
		this.regions = regions;
		this.sign = sign;
	}
}
