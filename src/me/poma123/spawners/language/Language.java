/*******************************************************************************
* This file is part of PickupSpawners.
*
*     ASkyBlock is free software: you can redistribute it and/or modify
*     it under the terms of the GNU General Public License as published by
*     the Free Software Foundation, either version 3 of the License, or
*     (at your option) any later version.
*
*     ASkyBlock is distributed in the hope that it will be useful,
*     but WITHOUT ANY WARRANTY; without even the implied warranty of
*     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*     GNU General Public License for more details.
*
*     You should have received a copy of the GNU General Public License
*     along with PickupSpawners.  If not, see <http://www.gnu.org/licenses/>.
*******************************************************************************/
package me.poma123.spawners.language;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.poma123.spawners.PickupSpawners;
import me.poma123.spawners.Listener;
import me.poma123.spawners.SettingsManager;
import net.md_5.bungee.api.ChatColor;

public class Language {
	static SettingsManager s = SettingsManager.getInstance();
	private static Plugin plugin = PickupSpawners.getPlugin(PickupSpawners.class);

	public enum LocalePath {
		BREAK("break"), PLACE("place"), LIMIT_REACH("limit-reach"), NO_ENOUGH_MONEY(
				"no-enough-money"), NO_ENOUGH_SPACE_INV("no-enough-space-inv"), NO_PERM("no-perm"), GIVE("give");

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
		File folder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "language");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		if (!enFile.exists()) {
			try {
				enFile.createNewFile();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		FileConfiguration en = YamlConfiguration.loadConfiguration(enFile);
		en.addDefault("break", "&7You have broken out one &e%type%&7 spawner.");
		en.addDefault("place", "&7You have placen one &e%type%&7 spawner.");
		en.addDefault("limit-reach", "&cYou have reached the daily spawner break limit (%limit%).");
		en.addDefault("no-enough-money", "&cYou do not have enough money!");
		en.addDefault("no-enough-space-inv", "&cThere are not enough space in your inventory!");
		en.addDefault("give", "&aGave %count% &e%type%&a spawner to you.");
		en.addDefault("no-perm", "&cYou do not have permission to perform this action.");

		en.options().copyDefaults(true);

		try {
			en.save(enFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File huFile = new File(
				plugin.getDataFolder().getAbsolutePath() + File.separator + "language" + File.separator + "hu_HU.yml");
		if (!huFile.exists()) {
			try {
				huFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileConfiguration hu = YamlConfiguration.loadConfiguration(huFile);
		hu.addDefault("break", "&7Kiütöttél egy &e%type%&7 spawnert.");
		hu.addDefault("place", "&7Letettél egy &e%type%&7 spawnert.");
		hu.addDefault("limit-reach", "&cElérted a napi kiüthető spawner limitet (%limit%).");
		hu.addDefault("no-enough-money", "&cNincs elég pénzed!");
		hu.addDefault("no-enough-space-inv", "&cNincs elég hely a táskádban!");
		hu.addDefault("give", "&aKaptál %count% &e%type%&a spawnert.");
		hu.addDefault("no-perm", "&cEhhez nincs jogod.");
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

			String lang = Listener.getLangExact(player);
			/*
			 * if (PickupSpawners.debug == true) { System.out.println(player.getName() +
			 * "''s lang: " + lang); }
			 */
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

		if (output.contains("&")) {
			output = ChatColor.translateAlternateColorCodes('&', output);
		}
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

		if (messagePath.equals(LocalePath.GIVE)) {
			output = output.replace(replaceFrom.split(" ")[0], replaceTo.split(" ")[0])
					.replace(replaceFrom.split(" ")[1], replaceTo.split(" ")[1]);
			;

		} else {
			output = output.replace(replaceFrom, replaceTo);
		}
		if (output.contains("&")) {
			output = ChatColor.translateAlternateColorCodes('&', output);
		}

		return output;
	}
}
