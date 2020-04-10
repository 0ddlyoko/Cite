package me.oddlyoko.cite.command;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.cite.Cite;
import me.oddlyoko.cite.__;
import me.oddlyoko.cite.team.Team;

public class TeamCommand implements CommandExecutor {
	private HashMap<UUID, Team> invitations;

	public TeamCommand() {
		invitations = new HashMap<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!"team".equalsIgnoreCase(command.getLabel()))
			return false;
		if (!(sender instanceof org.bukkit.entity.Player)) {
			sender.sendMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "US" + ChatColor.YELLOW + "] " + ChatColor.RED
					+ "You must be a player to execute this command");
		}
		Player p = (Player) sender;
		me.oddlyoko.cite.player.Player citePlayer = Cite.get().getPlayerManager().getPlayer(p.getUniqueId());
		if (citePlayer == null) {
			p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.other.error"));
			return true;
		}
		if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
			sender.sendMessage(
					ChatColor.YELLOW + "-----------[" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW + "]-----------");
			sender.sendMessage(ChatColor.AQUA + "- /team help" + ChatColor.YELLOW + " : Show this help");
			sender.sendMessage(ChatColor.AQUA + "- /team invite <player>" + ChatColor.YELLOW
					+ " : Inviter un joueur à rejoindre votre équipe");
			sender.sendMessage(ChatColor.AQUA + "- /team kick <player>" + ChatColor.YELLOW
					+ " : Expulser un joueur de votre équipe");
			sender.sendMessage(
					ChatColor.AQUA + "- /team name <name>" + ChatColor.YELLOW + " : Modifier le nom de votre équipe");
			sender.sendMessage(ChatColor.AQUA + "- /team leave" + ChatColor.YELLOW + " : Quitter votre équipe");
			sender.sendMessage(ChatColor.AQUA + "- /team accept <player>" + ChatColor.YELLOW
					+ " : Accepter l'invitation d'une équipe");
			sender.sendMessage(
					ChatColor.AQUA + "- /team toggle" + ChatColor.YELLOW + " : Activer / désactiver les invitations");
		} else if ("invite".equalsIgnoreCase(args[0])) {
			// Invite a player
			if (args.length != 2) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.other.syntax")
						.replaceAll("\\{syntax\\}", "/team invite <player>"));
				return true;
			}
			Team t = citePlayer.getTeam();
			// Player is not the owner of the team
			if (!t.getOwnerUuid().equals(p.getUniqueId())) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.invite.notOwner"));
				return true;
			}
			// Player wants to invite himself
			if (p.getName().equalsIgnoreCase(args[1])) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.invite.yourself"));
				return true;
			}
			// Player invite a player that is in his team
			if (t.contains(args[1])) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.invite.alreadyTeam"));
				return true;
			}
			// Player is not connected
			Player player = Bukkit.getPlayer(args[1]);
			if (player == null || !player.isOnline()) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.invite.notConnected"));
				return true;
			}
			me.oddlyoko.cite.player.Player otherPlayer = Cite.get().getPlayerManager().getPlayer(player.getUniqueId());
			// Check if player is already in a team
			if (!otherPlayer.getTeam().getOwnerUuid().equals(otherPlayer.getUuid())) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.invite.hasTeam"));
				return true;
			} else {
				// Here the player is the owner of the team, check if there is more than one
				// player in his team
				if (otherPlayer.getTeam().getOthers().size() > 1) {
					p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.invite.hasTeam"));
					return true;
				}
			}
			// Check if the team is full
			if (t.getOthers().size() >= Cite.get().getCiteConfig().getMaxPlayer()
					&& Cite.get().getCiteConfig().getMaxPlayer() != -1) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.invite.full"));
				return true;
			}
			// Send an invitation
			if (!otherPlayer.isInvitation()) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.invite.invitation"));
				return true;
			}
			invitations.put(player.getUniqueId(), t);
			p.sendMessage(__.PREFIX + ChatColor.GREEN
					+ Cite.get().getLanguage().get("commands.invite.send").replaceAll("\\{player\\}", args[1]));
			player.sendMessage(__.PREFIX + ChatColor.GREEN + Cite.get().getLanguage().get("commands.invite.receive")
					.replaceAll("\\{player\\}", p.getName()).replaceAll("\\{team\\}", t.getName()));
		} else if ("kick".equalsIgnoreCase(args[0])) {
			// Remove a player
			if (args.length != 2) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.other.syntax")
						.replaceAll("\\{syntax\\}", "/team kick <player>"));
				return true;
			}
			Team t = citePlayer.getTeam();
			// Player is not the owner of the team
			if (!t.getOwnerUuid().equals(p.getUniqueId())) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.kick.notOwner"));
				return true;
			}
			// Player wants to remove himself
			if (p.getName().equalsIgnoreCase(args[1])) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.kick.yourself"));
				return true;
			}
			// Player wants to remove a player that is not in his team
			me.oddlyoko.cite.player.Player found = null;
			for (me.oddlyoko.cite.player.Player player : t.getOthers()) {
				if (player.getName().equals(args[1])) {
					found = player;
					break;
				}
			}
			if (found == null) {
				// Player not found
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.kick.notTeammate"));
				return true;
			}
			// Remove from old team
			t.removePlayer(found);
			// Create a new team for him
			Team newTeam = Cite.get().getTeamManager().createTeam(found.getUuid(), found.getName());
			newTeam.addPlayer(found);
			found.setTeam(newTeam);
			// And save it
			Cite.get().getPlayerManager().savePlayer(found);
			Player other = Bukkit.getPlayer(found.getUuid());
			if (other != null && other.isOnline())
				other.sendMessage(__.PREFIX + ChatColor.RED
						+ Cite.get().getLanguage().get("commands.kick.kicked").replaceAll("\\{player\\}", p.getName()));
			// Send message to all players
			t.sendMessage(__.PREFIX + ChatColor.GREEN + Cite.get().getLanguage().get("commands.kick.other")
					.replaceAll("\\{player\\}", found.getName()).replaceAll("\\{player2\\}", p.getName()));
		} else if ("name".equalsIgnoreCase(args[0])) {
			// Edit team name
			if (args.length != 2) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.other.syntax")
						.replaceAll("\\{syntax\\}", "/team name <name>"));
				return true;
			}
			// Player must be the owner of the team
			Team t = citePlayer.getTeam();
			if (!t.getOwnerUuid().equals(p.getUniqueId())) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.name.notOwner"));
				return true;
			}
			String name = ChatColor.stripColor(args[1]);
			if (name.length() > Cite.get().getCiteConfig().getNameLength()) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.name.limit"));
				return true;
			}
			t.setName(name);
			Cite.get().getTeamManager().saveTeam(t);
			t.sendMessage(__.PREFIX + ChatColor.GREEN
					+ Cite.get().getLanguage().get("commands.name.changed").replaceAll("\\{name\\}", name));
		} else if ("leave".equalsIgnoreCase(args[0])) {
			Team t = citePlayer.getTeam();
			// Player must NOT be the owner of the team
			if (t.getOwnerUuid().equals(p.getUniqueId())) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.leave.owner"));
				return true;
			}
			t.removePlayer(citePlayer);
			// Create a new team for him
			Team newTeam = Cite.get().getTeamManager().createTeam(citePlayer.getUuid(), citePlayer.getName());
			newTeam.addPlayer(citePlayer);
			citePlayer.setTeam(newTeam);
			// And save it
			Cite.get().getPlayerManager().savePlayer(citePlayer);
			p.sendMessage(__.PREFIX + ChatColor.GREEN + Cite.get().getLanguage().get("commands.leave.leave"));
			// Send message to all players
			t.sendMessage(__.PREFIX + ChatColor.RED
					+ Cite.get().getLanguage().get("commands.leave.other").replaceAll("\\{player\\}", p.getName()));
		} else if ("accept".equalsIgnoreCase(args[0])) {
			// Syntax
			if (args.length != 2) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.other.syntax")
						.replaceAll("\\{syntax\\}", "/team accept <player>"));
				return true;
			}
			Team t = invitations.get(p.getUniqueId());
			if (t == null) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.accept.noInvitation"));
				return true;
			}
			// Check if he got an invitation from this player
			if (!t.getOwner().getName().equalsIgnoreCase(args[1])) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.accept.noInvitation"));
				return true;
			}
			// Check if team is full
			if (t.getOthers().size() >= Cite.get().getCiteConfig().getMaxPlayer()) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.accept.full"));
				return true;
			}
			// Team is empty
			if (t.getOthers().size() == 0 || citePlayer.getTeam().getOthers().size() != 1) {
				p.sendMessage(__.PREFIX + ChatColor.RED + Cite.get().getLanguage().get("commands.accept.noInvitation"));
				return true;
			}
			// Quit old team
			citePlayer.getTeam().removePlayer(citePlayer);
			// Join team
			t.addPlayer(citePlayer);
			citePlayer.setTeam(t);
			// Save player
			Cite.get().getPlayerManager().savePlayer(citePlayer);
			p.sendMessage(__.PREFIX + ChatColor.GREEN
					+ Cite.get().getLanguage().get("commands.accept.sent").replaceAll("\\{team\\}", t.getName()));
			t.sendMessage(__.PREFIX + ChatColor.GREEN
					+ Cite.get().getLanguage().get("commands.accept.other").replaceAll("\\{player\\}", p.getName()));
		} else if ("toggle".equalsIgnoreCase(args[0])) {
			citePlayer.setInvitation(!citePlayer.isInvitation());
			Cite.get().getPlayerManager().savePlayer(citePlayer);
			p.sendMessage(__.PREFIX + ChatColor.GREEN + Cite.get().getLanguage()
					.get("commands.toggle." + (citePlayer.isInvitation() ? "enable" : "disable")));
		}
		return true;
	}
}
