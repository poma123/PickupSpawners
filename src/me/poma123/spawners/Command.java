package me.poma123.spawners;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Command extends JavaPlugin implements org.bukkit.event.Listener {
	SettingsManager s = SettingsManager.getInstance();
	public void onEnable() {

		Bukkit.getPluginManager().registerEvents(new Listener(), this);
		saveConfig();
		saveDefaultConfig();
		s.setup(Command.getPlugin(Command.class));
		
	}

}
