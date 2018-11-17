package me.poma123.spawners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PSCommand implements CommandExecutor, TabCompleter {

	EntityType[] values = EntityType.values();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("pickupspawners.give")) {
				if (args.length < 1) {
					sender.sendMessage("§e[PickupSpawners] §7Commands:\n§f- §7/pickupspawners §bgive <entity_name>");
				}
				if (args.length > 0) {
					Player p = (Player) sender;
					if (args[0].equalsIgnoreCase("give")) {
						if (args.length >= 2) {
							
							String spawnedType = args[1].toUpperCase();
							try {
								EntityType.valueOf(spawnedType);
								ItemStack spawner = new ItemStack(me.poma123.spawners.Command.material, 1);
								ItemMeta swmeta = spawner.getItemMeta();
								// swmeta.setLocalizedName();
								swmeta.setDisplayName("§e" + spawnedType.toLowerCase() + " §7Spawner");

								spawner.setItemMeta(swmeta);

								p.getInventory().addItem(spawner);

								p.sendMessage(me.poma123.spawners.Listener.getLang(p).equals("hu")
										? "§aKaptál egy §e" + spawnedType.toLowerCase() + " §aspawnert!"
										: "§aGave one §e" + spawnedType.toLowerCase() + "§a spawner to you.");

							} catch (IllegalArgumentException e) {
								p.sendMessage(me.poma123.spawners.Listener.getLang(p).equals("hu")
										? "§cA megadott entitás típus nem létezik."
										: "§cThis entity type is invalid.");
							}

						} else {
							sender.sendMessage(
									"§e[PickupSpawners] §7Commands:\n§f- §7/pickupspawners §bgive <entity_name>");

						}

					}
				}
			} else {
				sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu") ? "§cEhhez nincs jogod."
						: "§cYou do not have permission to perform this command.");
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pickupspawners")) {
			if (sender.hasPermission("pickupspawners.give")) {

				if (args.length == 1) {
					ArrayList<String> names = new ArrayList<String>();
					List<String> list = Arrays.asList("give");

					if (!args[0].equals("")) {
						for (String name : list) {
							if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
								names.add(name);
							}

						}
					}

					else {
						for (String name : list) {
							names.add(name);
						}
					}

					Collections.sort(names);

					return names;
				}
				if (args.length == 2) {
					ArrayList<String> names = new ArrayList<String>();
					List<String> list = me.poma123.spawners.Command.entities;

					if (!args[1].equals("")) {
						for (String name : list) {
							if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
								names.add(name);
							}

						}
					}

					else {
						for (String name : list) {
							names.add(name);
						}
					}

					Collections.sort(names);

					return names;
				}

			}
		}
		return null;
	}

}
