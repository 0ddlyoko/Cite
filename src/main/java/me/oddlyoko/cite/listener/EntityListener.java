package me.oddlyoko.cite.listener;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import me.oddlyoko.cite.Cite;

public class EntityListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVillagerSpawn(EntitySpawnEvent e) {
		if (e.getEntity() instanceof Villager && Cite.get().getCiteConfig().isDisableVillager())
			e.setCancelled(true);
	}
}
