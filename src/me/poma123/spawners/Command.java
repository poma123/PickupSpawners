package me.poma123.spawners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import me.poma123.spawners.language.Language;

public class Command extends JavaPlugin implements org.bukkit.event.Listener {
	SettingsManager s = SettingsManager.getInstance();
	public static Material material;
	public static List<String> entities = new ArrayList<String>();
	public int ID = 62455;
	private Metrics metrics;

	public static String generateRandomString(int length) {

		boolean useLetters = true;
		boolean useNumbers = true;
		String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

		return generatedString;
	}

	@Override
	public void onEnable() {
		
		
		Language.saveLocales();
		
		
		
		getLogger().info("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
		getLogger().info("-+      PickupSpawners       +-");
		getLogger().info("-+        by poma123         +-");
		getLogger().info("-+                           +-");
		getLogger().info("-+        Made with <3       +-");
		getLogger().info("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");

		
		metrics = new Metrics(this);
		
		this.metrics.addCustomChart(new Metrics.SingleLineChart("spawners_broken", new Callable<Integer>() {
			 @Override
		        public Integer call() throws Exception {
				 int c = Listener.breakedSpawners;
			     
			        return c;
		      
		            
		        }
		    }));
	     
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

		for (EntityType string : EntityType.values()) {
			entities.add(string.toString().toLowerCase());
		}

		
		getCommand("pickupspawners").setExecutor(new PSCommand());
		getCommand("pickupspawners").setTabCompleter(new PSCommand());
		Bukkit.getPluginManager().registerEvents(new Listener(), this);
		saveConfig();
		saveDefaultConfig();
		s.setup(Command.getPlugin(Command.class));

		if (s.getConfig().getBoolean("update-check")) {
			new Updater(this, ID, this.getFile(), Updater.UpdateType.VERSION_CHECK, true);
		}

		if (s.getConfig().getConfigurationSection("item").getKeys(false).isEmpty()) {

			s.getConfig().set("item.default.material", "DIAMOND_PICKAXE");
			s.getConfig().set("item.default.enchants", Arrays.asList("SILK_TOUCH"));
			s.saveConfig();
		}

	}

	private String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
}
