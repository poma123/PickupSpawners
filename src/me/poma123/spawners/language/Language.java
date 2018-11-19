package me.poma123.spawners.language;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.poma123.spawners.Command;
import me.poma123.spawners.Listener;
import me.poma123.spawners.SettingsManager;

public class Language {
	static SettingsManager s = SettingsManager.getInstance();
	private static Plugin plugin = Command.getPlugin(Command.class);


	public enum LocalePath {
		BREAK("break"), 
		PLACE("place"),
		LIMIT_REACH("limit-reach");

		private final String name;

		private LocalePath(String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			// (otherName == null) check is not needed because name.equals(null) returns
			// false
			return name.equals(otherName);
		}

		public String toString() {
			return this.name;
		}
	}

	public static void saveLocales() {
		File enFile = new File(
				plugin.getDataFolder().getAbsolutePath() + File.separator + "language" + File.separator + "en_US.yml");
		if (!enFile.exists()) {
			enFile.mkdirs();
		}
		
		FileConfiguration en = YamlConfiguration.loadConfiguration(enFile);
		en.addDefault("break", "§7You have broken out one §e%type%§7 spawner.");
		en.addDefault("place", "§7You have placen one §e%type%§7 spawner.");
		en.addDefault("limit-reach", "§cYou have reached the daily spawner break limit (%limit%).");
		en.options().copyDefaults(true);
		try {
			en.save(enFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File huFile = new File(
				plugin.getDataFolder().getAbsolutePath() + File.separator + "language" + File.separator + "hu_HU.yml");
		if (!huFile.exists()) {
			huFile.mkdirs();
		}
		FileConfiguration hu = YamlConfiguration.loadConfiguration(huFile);
		hu.addDefault("break", "§7Kiütöttél egy §e%type%§7 spawnert.");
		hu.addDefault("place", "§7Letettél egy §e%type%§7 spawnert.");
		hu.addDefault("limit-reach", "§cElérted a napi kiüthető spawner limitet (%limit%).");
		
		hu.options().copyDefaults(true);
		try {
			hu.save(huFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getLocale(Player player, LocalePath messagePath) {
		String localeFile = "en_US.yml";
		String output;
		if (s.getConfig().getBoolean("auto-locale") == true) {
			String lang = Listener.getLang(player);
			if (lang.equals("hu_HU")) {
				localeFile = "hu_HU";
			} else if (lang.contains("en_")) {
				localeFile = "en_US.yml";
			} else {
				localeFile = s.getConfig().getString("locale") + ".yml";
			}
		}

		FileConfiguration file = YamlConfiguration.loadConfiguration(new File(
				plugin.getDataFolder().getAbsolutePath() + File.separator + "language" + File.separator + localeFile));
		output = file.getString(messagePath.name);

		return output;
	}

	public static String getReplacedLocale(Player player, LocalePath messagePath, String replaceFrom,
			String replaceTo) {
		
		String localeFile = "en_US.yml";
		String output;
		if (s.getConfig().getBoolean("auto-locale") == true) {
			String lang = Listener.getLangExact(player);

			if (lang.equals("hu_hu")) {
				localeFile = "hu_HU.yml";
			} else if (lang.contains("en_")) {
				localeFile = "en_US.yml";
			} else {
				localeFile = s.getConfig().getString("locale") + ".yml";
			}
		} else {
			localeFile = s.getConfig().getString("locale") + ".yml";
		}
		
		
		FileConfiguration file = YamlConfiguration.loadConfiguration(new File(
				plugin.getDataFolder().getAbsolutePath() + File.separator + "language" + File.separator + localeFile));
		output = file.getString(messagePath.name);
		
		output = output.replace(replaceFrom, replaceTo);

		return output;
	}
}
