package me.oddlyoko.cite.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.oddlyoko.cite.Cite;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		me.oddlyoko.cite.player.Player player = Cite.get().getPlayerManager().getPlayer(p.getUniqueId());
		if (player == null) {
			// First connection
			player = Cite.get().getPlayerManager().createPlayer(p);
		}
		// Check if name has changed
		if (!name.equals(player.getName())) {
			player.setName(name);
			Cite.get().getPlayerManager().savePlayer(player);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		me.oddlyoko.cite.player.Player p = Cite.get().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
		int emerald = (p == null) ? 0 : p.getTeam().getEmerald();
		e.setFormat("§e[§6US§e] [§6" + p.getTeam().getName() + "§e] [§6" + emerald + "§e] %s §r➡ §f%s");
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p1 = (Player) e.getEntity();
			Player p2 = (Player) e.getDamager();
			me.oddlyoko.cite.player.Player player = Cite.get().getPlayerManager().getPlayer(p1.getUniqueId());
			if (player == null)
				return;
			if (player.getTeam().contains(p2.getUniqueId())) {
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent e) {
		Location to = e.getTo();
		if (!to.getWorld().getName().equalsIgnoreCase(Cite.get().getCiteConfig().getPortalWorld()))
			return;
		if (Cite.get().getWorldGuardDepends().isInRegions(to, Cite.get().getCiteConfig().getPortalRegions(),
				to.getWorld())) {
			e.setCancelled(true);
			return;
		}
		Location portal = e.getPortalTravelAgent().findPortal(e.getTo());
		if (portal != null && Cite.get().getWorldGuardDepends().isInRegions(portal,
				Cite.get().getCiteConfig().getPortalRegions(), portal.getWorld()))
			return;
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
		if (e.getItem().getType() == Material.ENCHANTED_GOLDEN_APPLE && Cite.get().getCiteConfig().isPreventGodApple())
			e.setCancelled(true);
	}
}
