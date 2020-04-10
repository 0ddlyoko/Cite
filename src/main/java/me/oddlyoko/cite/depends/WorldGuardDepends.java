package me.oddlyoko.cite.depends;

import java.util.List;
import java.util.UUID;

import org.bukkit.World;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardDepends {

	public void addPlayerInRegion(UUID uuid, World w, String region) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager rm = container.get(BukkitAdapter.adapt(w));
		if (rm == null)
			return;
		ProtectedRegion pr = rm.getRegion(region);
		if (pr == null)
			return;
		DefaultDomain domain = pr.getMembers();
		domain.addPlayer(uuid);
		System.out.println("Added " + uuid + " to region " + region);
	}

	public void removePlayerFromRegion(UUID uuid, World w, String region) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager rm = container.get(BukkitAdapter.adapt(w));
		if (rm == null)
			return;
		ProtectedRegion pr = rm.getRegion(region);
		if (pr == null)
			return;
		DefaultDomain domain = pr.getMembers();
		domain.removePlayer(uuid);
	}

	public boolean isInRegions(org.bukkit.Location loc, List<String> regions, World w) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager rm = container.get(BukkitAdapter.adapt(w));
		if (rm == null)
			return false;
		for (String region : regions) {
			ProtectedRegion pr = rm.getRegion(region);
			if (pr == null)
				return false;
			if (pr.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
				return true;
		}
		return false;
	}

	public boolean exist(World w, String region) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager rm = container.get(BukkitAdapter.adapt(w));
		if (rm == null)
			return false;
		return rm.hasRegion(region);
	}
}
