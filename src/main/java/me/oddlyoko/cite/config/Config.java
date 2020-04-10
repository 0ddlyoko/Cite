package me.oddlyoko.cite.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class Config {
	private File fichierConfig = null;
	private FileConfiguration fconfig = null;

	public Config(File file) {
		this.fichierConfig = file;
		loadConfig();
	}

	public void save() {
		try {
			fconfig.save(fichierConfig);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed while saving file " + fichierConfig.getName(), ex);
		}
	}

	private void loadConfig() {
		fconfig = YamlConfiguration.loadConfiguration(fichierConfig);
	}

	public void set(String path, Object obj) {
		if (obj instanceof Location) {
			Location loc = (Location) obj;
			fconfig.set(path + ".x", loc.getX());
			fconfig.set(path + ".y", loc.getY());
			fconfig.set(path + ".z", loc.getZ());
			fconfig.set(path + ".yaw", loc.getYaw());
			fconfig.set(path + ".pitch", loc.getPitch());
			fconfig.set(path + ".world", loc.getWorld().getName());
		} else
			fconfig.set(path, obj);
		save();
	}

	public String getString(String path) {
		String name = fconfig.getString(path);
		return name == null ? null : ChatColor.translateAlternateColorCodes('&', name);
	}

	public int getInt(String path) {
		return fconfig.getInt(path);
	}

	public long getLong(String path) {
		return fconfig.getLong(path);
	}

	public boolean getBoolean(String path) {
		return fconfig.getBoolean(path);
	}

	public double getDouble(String path) {
		return fconfig.getDouble(path);
	}

	public List<String> getStringList(String path) {
		List<String> name = new ArrayList<>();
		for (String nom : fconfig.getStringList(path))
			name.add(ChatColor.translateAlternateColorCodes('&', nom));
		return name;
	}

	public List<Integer> getIntegerList(String path) {
		List<Integer> name = new ArrayList<>();
		for (Integer nom : fconfig.getIntegerList(path))
			name.add(nom);
		return name;
	}

	public List<String> getKeys(String path) {
		List<String> list = new ArrayList<>();
		if ("".equalsIgnoreCase(path))
			for (String section : fconfig.getKeys(false))
				list.add(section);
		else {
			ConfigurationSection cs = fconfig.getConfigurationSection(path);
			if (cs == null)
				return list;
			for (String section : cs.getKeys(false))
				list.add(section);
		}
		return list;
	}

	public ItemStack getItem(String path) {
		return fconfig.getItemStack(path);
	}

	public Location getLocation(String path) {
		double x = fconfig.getDouble(path + ".x");
		double y = fconfig.getDouble(path + ".y");
		double z = fconfig.getDouble(path + ".z");
		float yaw = (float) fconfig.getDouble(path + ".yaw");
		float pitch = (float) fconfig.getDouble(path + ".pitch");
		String world = fconfig.getString(path + ".world");
		if (world == null || "".equalsIgnoreCase(world))
			return null;
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	public Set<String> getAllKeys() {
		return fconfig.getKeys(true);
	}

	public boolean exist(String path) {
		return fconfig.contains(path);
	}

	public void reload() {
		loadConfig();
	}

	public File getFile() {
		return fichierConfig;
	}
}
