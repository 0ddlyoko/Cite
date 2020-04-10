package me.oddlyoko.cite.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.oddlyoko.cite.Cite;
import me.oddlyoko.cite.__;
import me.oddlyoko.cite.house.House;
import me.oddlyoko.cite.inv.EmeraldInventory;

public class SignListener implements Listener {

	private EmeraldInventory emeraldInventory;
	private Pattern space = Pattern.compile(" ");

	public SignListener() {
		emeraldInventory = new EmeraldInventory();
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		Player p = e.getPlayer();
		if ("[Emerald]".equalsIgnoreCase(e.getLine(0)) && p.hasPermission("cite.sign.emerald.create")) {
			e.setLine(0, ChatColor.GREEN + "[Emerald]");
			List<String> sign = Cite.get().getLanguage().getList("sign.emerald");
			int count = Math.min(sign.size(), 3);
			for (int i = 0; i < count; i++)
				e.setLine(i + 1, ChatColor.translateAlternateColorCodes('&', sign.get(i)));
		} else if ("[House]".equalsIgnoreCase(e.getLine(0)) && p.hasPermission("cite.sign.house.create")) {
			int id = 0;
			int cost = 0;
			try {
				String[] strs = space.split(e.getLine(1));
				id = Integer.parseInt(strs[0]);
				cost = Integer.parseInt(strs[1]);
			} catch (Exception ex) {
				e.getBlock().breakNaturally();
				return;
			}
			List<String> regions = new ArrayList<>();
			if (e.getLine(2) != null && !"".equalsIgnoreCase(e.getLine(2).trim()))
				regions.add(e.getLine(2));
			if (e.getLine(3) != null && !"".equalsIgnoreCase(e.getLine(3).trim()))
				regions.add(e.getLine(3));
			// Check id
			if (Cite.get().getHouseManager().getHouse(id) != null) {
				// ID already exist
				e.getBlock().breakNaturally();
				return;
			}
			// Check if regions exist
			for (String region : regions) {
				if (!Cite.get().getWorldGuardDepends().exist(e.getBlock().getWorld(), region)) {
					e.getBlock().breakNaturally();
					return;
				}
			}
			Cite.get().getHouseManager().createHouse(id, cost, regions, e.getBlock().getLocation());
			e.setLine(0, ChatColor.GREEN + "[House]");
			List<String> sign = Cite.get().getLanguage().getList("sign.house.free");
			int count = Math.min(sign.size(), 3);
			for (int i = 0; i < count; i++)
				e.setLine(i + 1,
						ChatColor.translateAlternateColorCodes('&',
								sign.get(i).replaceAll("\\{id\\}", Integer.toString(id)).replaceAll("\\{cost\\}",
										Integer.toString(cost))));
			p.sendMessage(__.PREFIX + ChatColor.GREEN + Cite.get().getLanguage().get("sign.house.create"));
		}
	}

	@EventHandler
	public void onRightClickSign(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null)
			return;
		if (e.getClickedBlock().getType() != Material.SIGN && e.getClickedBlock().getType() != Material.WALL_SIGN)
			return;
		Sign sign = (Sign) e.getClickedBlock().getState();
		if ((ChatColor.GREEN + "[Emerald]").equalsIgnoreCase(sign.getLine(0))) {
			Player p = e.getPlayer();
			if (!p.hasPermission("cite.sign.emerald.use"))
				return;
			// Open inventory
			Cite.get().getInventoryManager().openInventory(emeraldInventory, p);
		} else if ((ChatColor.GREEN + "[House]").equalsIgnoreCase(sign.getLine(0))) {
			Player p = e.getPlayer();
			if (!p.hasPermission("cite.sign.house.use"))
				return;
			House house = Cite.get().getHouseManager().getHouse(e.getClickedBlock().getLocation());
			if (house == null)
				return;
			if (house.getOwner() != null)
				// House is taken
				return;
			// Check if player has an house
			me.oddlyoko.cite.player.Player citePlayer = Cite.get().getPlayerManager().getPlayer(p.getUniqueId());
			if (citePlayer == null)
				// Wtf ?
				return;
			if (citePlayer.getHouse() != null)
				// Player has an house
				return;
			// Count emerald
			HashMap<Integer, ? extends ItemStack> emeralds = p.getInventory().all(Material.EMERALD);
			int count = count(emeralds.values());
			if (count >= house.getCost()) {
				// Buy
				remove(house.getCost(), emeralds);
				Cite.get().getHouseManager().setHouse(house, citePlayer);
				sign.setLine(0, ChatColor.GREEN + "[House]");
				List<String> signMessage = Cite.get().getLanguage().getList("sign.house.taken");
				int countSign = Math.min(signMessage.size(), 3);
				for (int i = 0; i < countSign; i++)
					sign.setLine(i + 1,
							ChatColor.translateAlternateColorCodes('&',
									signMessage.get(i).replaceAll("\\{id\\}", Integer.toString(house.getId()))
											.replaceAll("\\{cost\\}", Integer.toString(house.getCost()))
											.replaceAll("\\{player\\}", p.getName())));
				sign.update();
				p.sendMessage(__.PREFIX + ChatColor.GREEN + Cite.get().getLanguage().get("sign.house.buy.self")
						.replaceAll("\\{id\\}", Integer.toString(house.getId())));
				p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
				if (Cite.get().getCiteConfig().isBroadcastHouse())
					Bukkit.broadcastMessage(__.PREFIX + ChatColor.GREEN
							+ Cite.get().getLanguage().get("sign.house.buy.other")
									.replaceAll("\\{id\\}", Integer.toString(house.getId()))
									.replaceAll("\\{player\\}", p.getName()));
			}
		}
	}

	/**
	 * Count the amount of emeralds
	 * 
	 * @param emeralds
	 *                     Emeralds
	 * @return The amount of emeralds
	 */
	private int count(Collection<? extends ItemStack> emeralds) {
		int countEmerald = 0;
		for (ItemStack is : emeralds)
			countEmerald += is.getAmount();
		return countEmerald;
	}

	/**
	 * Remove specific amount of emerald from inventory
	 * 
	 * @param inventory
	 *                      The inventory
	 * @param count
	 *                      The number of emerald to remove
	 * @param emeralds
	 *                      The position
	 */
	private void remove(int count, HashMap<Integer, ? extends ItemStack> emeralds) {
		for (ItemStack is : emeralds.values()) {
			int removed = Math.min(count, is.getAmount());
			count -= removed;

			if (is.getAmount() == count)
				is.setType(Material.AIR);
			else
				is.setAmount(is.getAmount() - removed);

			if (count <= 0)
				return;
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (e.getBlock().getType() != Material.SIGN && e.getBlock().getType() != Material.WALL_SIGN)
			return;
		Sign sign = (Sign) e.getBlock().getState();
		if ((ChatColor.GREEN + "[House]").equalsIgnoreCase(sign.getLine(0))) {
			House house = Cite.get().getHouseManager().getHouse(e.getBlock().getLocation());
			if (house == null)
				return;
			if (!p.hasPermission("cite.sign.house.remove")) {
				e.setCancelled(true);
				return;
			}
			Cite.get().getHouseManager().deleteHouse(house);
			p.sendMessage(__.PREFIX + ChatColor.GREEN + Cite.get().getLanguage().get("sign.house.delete"));
		} else if ((ChatColor.GREEN + "[House]").equalsIgnoreCase(sign.getLine(0))) {
			if (!p.hasPermission("cite.sign.emerald.remove")) {
				e.setCancelled(true);
				return;
			}
		}
	}
}
