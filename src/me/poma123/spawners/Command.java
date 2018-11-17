package me.poma123.spawners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class Command extends JavaPlugin implements org.bukkit.event.Listener {
	SettingsManager s = SettingsManager.getInstance();
	public static Material material;

	public void onEnable() {

		if (getVersion().contains("1_13")) {
			material = Material.getMaterial("SPAWNER");
			getLogger().info("1.13 native version detected. Configuring 1.13-1.13.2 compatibility...");
			getLogger().info("Done!");
		} else if (getVersion().contains("1_12") || getVersion().contains("1_11") || getVersion().contains("1_10")
				|| getVersion().contains("1_9")) {
			material = Material.getMaterial("MOB_SPAWNER");
			getLogger().info(getVersion() + " version detected. Configuring compatibility...");
			getLogger().info("Done!");
		}

		Bukkit.getPluginManager().registerEvents(new Listener(), this);
		saveConfig();
		saveDefaultConfig();
		s.setup(Command.getPlugin(Command.class));

	}

	private String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
}
