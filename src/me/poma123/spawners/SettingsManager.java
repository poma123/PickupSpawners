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

package me.poma123.spawners;
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager {

	private SettingsManager() { }
	
	static SettingsManager instance = new SettingsManager();
	
	public static SettingsManager getInstance() {
		return instance;
	}
	
	static Plugin p;
	FileConfiguration config;
	File cfile;
	
	public void setup(Plugin p) {
		config = p.getConfig();
		config.options().copyDefaults(true);
	
		cfile = new File(p.getDataFolder(), "config.yml");
		saveConfig();
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public void saveConfig() {
		try {
			config.save(cfile);
		}
		catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");
		}
	}
	
	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(cfile);
	}
	
	public PluginDescriptionFile getDesc() {
		return p.getDescription();
	}
}
