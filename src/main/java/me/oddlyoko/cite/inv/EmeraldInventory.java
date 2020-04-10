package me.oddlyoko.cite.inv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.oddlyoko.cite.Cite;
import me.oddlyoko.cite.inventory.ClickableItem;
import me.oddlyoko.cite.inventory.Inventory;
import me.oddlyoko.cite.inventory.InventoryProvider;
import me.oddlyoko.cite.inventory.ItemBuilder;
import me.oddlyoko.cite.team.Team;

public class EmeraldInventory implements InventoryProvider {
	public static final String UPDATE = "update";

	private final String title;
	private final List<String> list;

	private final ClickableItem ROUND = ClickableItem
			.of(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name("").build());
	private final ClickableItem BACKGROUND = ClickableItem
			.of(ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name("").build());

	public EmeraldInventory() {
		title = Cite.get().getLanguage().get("inventory.emerald_deposit.item.title");
		list = Cite.get().getLanguage().getList("inventory.emerald_deposit.item.lore");
	}

	@Override
	public String title(Inventory inv) {
		return Cite.get().getLanguage().get("inventory.emerald_deposit.title");
	}

	@Override
	public int rows(Inventory inv) {
		return 3;
	}

	@Override
	public void init(Inventory inv) {
		inv.put(UPDATE, true);
		// Background
		inv.fill(BACKGROUND);
		inv.rectangle(1, 1, 9, 3, ROUND);
	}

	@Override
	public void update(Inventory inv) {
		boolean update = (Boolean) inv.get(UPDATE);
		if (!update)
			return;
		Team t = Cite.get().getPlayerManager().getPlayer(inv.getPlayer().getUniqueId()).getTeam();
		int currentEmerald = t.getEmerald();
		// Count the number of emerald in player's inventory
		HashMap<Integer, ? extends ItemStack> emeralds = inv.getPlayer().getInventory().all(Material.EMERALD);
		int countEmerald = count(emeralds.values());
		inv.set(2, 2, ClickableItem.of(create(Math.min(countEmerald, 1), currentEmerald, countEmerald), e -> {
			int count = Math.min(countEmerald, 1);
			if (count > 0) {
				remove(count, emeralds);
				inv.getPlayer().updateInventory();
				t.setEmerald(t.getEmerald() + count);
				Cite.get().getTeamManager().saveTeam(t);
				inv.put(UPDATE, true);
			}
		}));
		inv.set(3, 2, ClickableItem.of(create(Math.min(countEmerald, 2), currentEmerald, countEmerald), e -> {
			int count = Math.min(countEmerald, 2);
			if (count > 0) {
				remove(count, emeralds);
				inv.getPlayer().updateInventory();
				t.setEmerald(t.getEmerald() + count);
				Cite.get().getTeamManager().saveTeam(t);
				inv.put(UPDATE, true);
			}
		}));
		inv.set(4, 2, ClickableItem.of(create(Math.min(countEmerald, 4), currentEmerald, countEmerald), e -> {
			int count = Math.min(countEmerald, 4);
			if (count > 0) {
				remove(count, emeralds);
				inv.getPlayer().updateInventory();
				t.setEmerald(t.getEmerald() + count);
				Cite.get().getTeamManager().saveTeam(t);
				inv.put(UPDATE, true);
			}
		}));
		inv.set(5, 2, ClickableItem.of(create(Math.min(countEmerald, 8), currentEmerald, countEmerald), e -> {
			int count = Math.min(countEmerald, 8);
			if (count > 0) {
				remove(count, emeralds);
				inv.getPlayer().updateInventory();
				t.setEmerald(t.getEmerald() + count);
				Cite.get().getTeamManager().saveTeam(t);
				inv.put(UPDATE, true);
			}
		}));
		inv.set(6, 2, ClickableItem.of(create(Math.min(countEmerald, 16), currentEmerald, countEmerald), e -> {
			int count = Math.min(countEmerald, 16);
			if (count > 0) {
				remove(count, emeralds);
				inv.getPlayer().updateInventory();
				t.setEmerald(t.getEmerald() + count);
				Cite.get().getTeamManager().saveTeam(t);
				inv.put(UPDATE, true);
			}
		}));
		inv.set(7, 2, ClickableItem.of(create(Math.min(countEmerald, 32), currentEmerald, countEmerald), e -> {
			int count = Math.min(countEmerald, 32);
			if (count > 0) {
				remove(count, emeralds);
				inv.getPlayer().updateInventory();
				t.setEmerald(t.getEmerald() + count);
				Cite.get().getTeamManager().saveTeam(t);
				inv.put(UPDATE, true);
			}
		}));
		inv.set(8, 2, ClickableItem.of(create(Math.min(countEmerald, 64), currentEmerald, countEmerald), e -> {
			int count = Math.min(countEmerald, 64);
			if (count > 0) {
				remove(count, emeralds);
				inv.getPlayer().updateInventory();
				t.setEmerald(t.getEmerald() + count);
				Cite.get().getTeamManager().saveTeam(t);
				inv.put(UPDATE, true);
			}
		}));
	}

	private ItemStack create(int number, int emerald, int max) {
		int emeraldAfter = emerald + Math.min(number, max);
		String title = this.title.replaceAll("\\{number\\}", Integer.toString(number))
				.replaceAll("\\{emerald\\}", Integer.toString(emerald))
				.replaceAll("\\{emerald_after\\}", Integer.toString(emeraldAfter));
		List<String> list = new ArrayList<>(this.list.size());
		for (String str : this.list)
			list.add(str.replaceAll("\\{number\\}", Integer.toString(number))
					.replaceAll("\\{emerald\\}", Integer.toString(emerald))
					.replaceAll("\\{emerald_after\\}", Integer.toString(emeraldAfter)));
		return ItemBuilder.of(Material.EMERALD, number).name(title).lore(list).build();
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
}
