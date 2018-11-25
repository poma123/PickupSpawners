/*******************************************************************************
 * This file is part of PickupSpawners.
 *  
 *       PickupSpawners is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *  
 *       PickupSpawners is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *  
 *       You should have received a copy of the GNU General Public License
 *       along with PickupSpawners.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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

public class PickupSpawners extends JavaPlugin implements org.bukkit.event.Listener {

	private static PickupSpawners instance;
	public static boolean debug = false;

	public PickupSpawners() {
		instance = this;
	}

	public static PickupSpawners getInstance() {
		return instance;
	}

	SettingsManager s = SettingsManager.getInstance();
	public static Material material;
	public static List<String> entities = new ArrayList<String>();
	public int ID = 62455;
	private Metrics metrics;
	static VaultAPI vault;

	public static String generateRandomString(int length) {

		boolean useLetters = true;
		boolean useNumbers = true;
		String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

		return generatedString;
	}

	public static VaultAPI getVault() {
		return vault;
	}

	@Override
	public void onEnable() {
		/*
		 * Vault setup
		 */

		vault = new VaultAPI(this);

		/*
		 * Language saving
		 */
		Language.saveLocales();

		/*
		 * Just why not?
		 */
		getLogger().info("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
		getLogger().info("-+      PickupSpawners       +-");
		getLogger().info("-+        by poma123         +-");
		getLogger().info("-+                           +-");
		getLogger().info("-+        Made with <3       +-");
		getLogger().info("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");

		/*
		 * Spawner material setting by version
		 */
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

		/*
		 * Getting and saving available entities
		 */
		for (EntityType entity : EntityType.values()) {

			if (getVersion().contains("1_13_R")) {
				if (Material.getMaterial(entity.toString().toUpperCase() + "_SPAWN_EGG") != null) {
					entities.add(entity.toString().toLowerCase());
				}

			} else {
				String list = "ELDER_GUARDIAN,WITHER_SKELETON,STRAY,HUSK,ZOMBIE_VILLAGER,SKELETON_HORSE,ZOMBIE_HORSE,DONKEY,MULE,EVOKER,VEX,VINDICATOR,CREEPER,SKELETON,SPIDER,"
						+ "ZOMBIE,SLIME,GHAST,ZOMBIE_PIGMAN,ENDERMAN,CAVE_SPIDER,SILVERFISH,BLAZE,MAGMA_CUBE,BAT,WITCH,ENDERMITE,GUARDIAN,SHULKER,PIG,SHEEP,COW,CHICKEN,SQUID,"
						+ "WOLF,MOOSHROOM,OCELOT,HORSE,RABBIT,POLAR_BEAR,LLAMA,PARROT,VILLAGER,TURTLE,PHANTOM,COD,SALMON,PUFFERFISH,TROPICAL_FISH,DROWNED,DOLPHIN";
				if (list.contains(entity.toString().toUpperCase())) {

					entities.add(entity.toString().toLowerCase());
				}

			}

		}

		// Debug to find all the livingentities

		/*
		 * File f = new File(this.getDataFolder() + File.separator + getVersion() +
		 * "mobs.yml"); FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
		 * conf.set("mobs", mobList); try { conf.save(f); } catch (IOException e) { //
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		/*
		 * Command registering
		 */
		getCommand("pickupspawners").setExecutor(new PSCommand());
		getCommand("pickupspawners").setTabCompleter(new PSCommand());
		/*
		 * Listener registering
		 */
		Bukkit.getPluginManager().registerEvents(new Listener(), this);
		Bukkit.getPluginManager().registerEvents(this, this);
		/*
		 * Config saving
		 */
		saveConfig();
		saveDefaultConfig();
		s.setup(PickupSpawners.getPlugin(PickupSpawners.class));

		/*
		 * Update check
		 */
		if (s.getConfig().getBoolean("update-check")) {
			new Updater(this, ID, this.getFile(), Updater.UpdateType.VERSION_CHECK, true);
		}

		/*
		 * Setting the default spawner breaker item if the list is empty
		 */
		if (s.getConfig().getConfigurationSection("item").getKeys(false).isEmpty()) {

			s.getConfig().set("item.default.material", "DIAMOND_PICKAXE");
			s.getConfig().set("item.default.enchants", Arrays.asList("SILK_TOUCH"));
			s.saveConfig();
		}

		/*
		 * Metrics setup
		 */
		metrics = new Metrics(this);
		this.metrics.addCustomChart(new Metrics.SingleLineChart("spawners_broken", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				int c = Listener.breakedSpawners;

				return c;

			}
		}));
		this.metrics.addCustomChart(new Metrics.SimplePie("auto_language_use", new Callable<String>() {
			@Override
			public String call() throws Exception {
				String c;
				if (s.getConfig().getBoolean("auto-locale") == true) {
					c = "using";
				} else {
					c = "not using";
				}

				return c;

			}
		}));
		this.metrics.addCustomChart(new Metrics.SimplePie("spawner_breaker_items", new Callable<String>() {
			@Override
			public String call() throws Exception {
				String c = "N/A";

				if (s.getConfig().getConfigurationSection("item").getValues(true) != null) {
					c = String.valueOf(s.getConfig().getConfigurationSection("item").getValues(true).size());
				}

				return c;

			}
		}));

	}

	public static boolean isInteger(String object) {
		try {
			Integer.parseInt(object);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	public static boolean isDouble(String object) {
		try {
			Double.parseDouble(object);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	private String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
}
