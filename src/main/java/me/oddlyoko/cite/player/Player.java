package me.oddlyoko.cite.player;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import me.oddlyoko.cite.house.House;
import me.oddlyoko.cite.team.Team;

@Getter
public class Player {
	private UUID uuid;
	@Setter
	private String name;
	@Setter
	private Team team;
	@Setter
	private boolean invitation;
	@Setter
	private House house;

	public Player(UUID uuid, String name, Team team, boolean invitation) {
		this.uuid = uuid;
		this.name = name;
		this.team = team;
		this.invitation = invitation;
	}
}
