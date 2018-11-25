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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.poma123.spawners.language.Language;
import me.poma123.spawners.language.Language.LocalePath;

public class PSCommand implements CommandExecutor, TabCompleter {

	EntityType[] values = EntityType.values();
	SettingsManager sett = SettingsManager.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {

			if (sender.hasPermission("pickupspawners.give") || sender.hasPermission("pickupspawners.additem")
					|| sender.hasPermission("pickupspawners.removeitem")
					|| sender.hasPermission("pickupspawners.itemlist")) {
				if (args.length < 1) {
					// sender.sendMessage("§e[PickupSpawners] §7Commands:\n§f- §7/pickupspawners
					// §bgive <entity_name>");
					sender.sendMessage(
							"§e[PickupSpawners] §7Commands:\n§7/pspawners §bgive <entity_name> §f- Gives you one spawner\n"
									+ "§7/pspawners §badditem §f- Adds spawner breaker item to the db\n"
									+ "§7/pspawners §bremoveitem <breakerID> §f- Removes spawner breaker item from the db\n"
									+ "§7/pspawners §bitemlist §f- Spawner breaker item list (with breakerID)");
					((Player) sender).spigot().sendMessage(Listener.getHoverClick("§e[PickupSpawners] §b§l[How to make a spawner buy sign]", "§7======================"
							+ "\n§b     Here is the syntax:\n"
							+ "\n§7§o1st line:   §f[PickupSpawners]"
							+ "\n§7§o2nd line:      §fB <price>"
							+ "\n§7§o3rd line:    §f<entity type>"
							+ "\n§7§o4th line:      §f<amount>"
							+ "\n\n"
							+ "§7Example:\n"
							+ "     [PickupSpawners]\n"
							+ "         B 500\n"
							+ "         zombie\n"
							+ "           2\n"
							+ "§7======================", ""));
				}
				
			} else {
				sender.sendMessage(Language.getLocale((Player) sender, LocalePath.NO_PERM));
				return true;
			}

			if (args.length > 0) {
				Player p = (Player) sender;
				if (args[0].equalsIgnoreCase("give")) {
					if (sender.hasPermission("pickupspawners.give")) {
						if (args.length >= 2) {

							String spawnedType = args[1].toUpperCase();
							if (me.poma123.spawners.PickupSpawners.entities.contains(spawnedType.toLowerCase())) {
								EntityType.valueOf(spawnedType);
								ItemStack spawner = new ItemStack(me.poma123.spawners.PickupSpawners.material, 1);
								ItemMeta swmeta = spawner.getItemMeta();
								// swmeta.setLocalizedName();
								swmeta.setDisplayName("§e" + spawnedType.toLowerCase() + " §7Spawner");

								spawner.setItemMeta(swmeta);

								p.getInventory().addItem(spawner);

								p.sendMessage(Language.getReplacedLocale(p, LocalePath.GIVE, "%count% %type%", 1 + " " + spawnedType.toLowerCase()));

							} else {
								p.sendMessage(me.poma123.spawners.Listener.getLang(p).equals("hu")
										? "§cA megadott entitás típus nem létezik."
										: "§cThis entity type is invalid.");
							}

						} else {
							sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bgive <entity_name>");

						}

					} else {
						sender.sendMessage(Language.getLocale(p, LocalePath.NO_PERM));
					}
				} else if (args[0].equalsIgnoreCase("additem")) {
					if (sender.hasPermission("pickupspawners.additem")) {
						if (!p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
							String random = "random" + me.poma123.spawners.PickupSpawners.generateRandomString(7);
							if (sett.getConfig().get("item." + random) != null) {
								random = "random" + me.poma123.spawners.PickupSpawners.generateRandomString(7);
							}
							Material mat = p.getInventory().getItemInMainHand().getType();
							List<String> enchants = new ArrayList<String>();
							if (!p.getInventory().getItemInMainHand().getEnchantments().isEmpty()) {
								for (Enchantment e : p.getInventory().getItemInMainHand().getEnchantments().keySet()) {
									String s = e.getName();
									int value = p.getInventory().getItemInMainHand().getEnchantments().get(e);
									enchants.add(s + ":" + value);
								}

								sett.getConfig().set("item." + random + ".enchants", enchants);
							}
							sett.getConfig().set("item." + random + ".material", mat.toString());
							sett.saveConfig();
							sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
									? "§aKész! " + mat.toString() + " hozzáadva a spawnert kiütő tárgyak adatbázishoz!"
									: "§aDone! " + mat.toString() + " added to the spawner breaker items database!");
							p.spigot().sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
									? Listener.getHoverClickcmd("§c[Visszavonás]", "§c/pspawners removeitem " + random,
											"/pspawners removeitem " + random)
									: Listener.getHoverClickcmd("§c[Undo]", "§c/pspawners removeitem " + random,
											"/pspawners removeitem " + random));

						} else {
							sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
									? "§cNincs semmi a kezedben a beállításhoz."
									: "§cYou have nothing in your hand for add a new spawner breaker item to the database.");
						}

					} else {
						sender.sendMessage(
								Language.getLocale(p, LocalePath.NO_PERM));
					}
				} else if (args[0].equalsIgnoreCase("removeitem")) {
					if (sender.hasPermission("pickupspawners.removeitem")) {
						if (args.length == 2) {
							if (sett.getConfig().get("item." + args[1]) != null) {
								p.spigot().sendMessage(Listener.getHoverClickcmd(
										Listener.getLang((Player) sender).equalsIgnoreCase("hu")
												? "§c[Törlés megerősítése]"
												: "§c[Confirm]",
										"§c/pspawners removeitem" + args[1] + " confirm",
										"/pspawners removeitem " + args[1] + " confirm"));
							} else {
								sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
										? "§cEz nem található az adatbázisban."
										: "§cThis is not in the database.");
							}
						} else if (args.length >= 3) {
							if (args[2].equalsIgnoreCase("confirm")) {
								if (sett.getConfig().get("item." + args[1]) != null) {
									sett.getConfig().set("item." + args[1], null);
									sett.saveConfig();
									sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
											? "§aSikeresen eltávolítottad ezt a tárgyat a spawner kiütő tárgyak adatbázisból."
											: "§aSuccesfully removed this item from the database.");
								} else {
									sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
											? "§cEz nem található az adatbázisban."
											: "§cThis is not in the database.");
								}
							} else {
								sender.sendMessage(
										"§e[PickupSpawners] §7Usage:\n§7/pspawners §bremoveitem <breakerID>");
							}
						} else {
							sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bremoveitem <breakerID>");
						}
					} else {
						sender.sendMessage(
								Language.getLocale(p, LocalePath.NO_PERM));
					}
				} else if (args[0].equalsIgnoreCase("itemlist")) {
					if (sender.hasPermission("pickupspawners.itemlist")) {
						sender.sendMessage("§8+-");

						if (sett.getConfig().getConfigurationSection("item").getKeys(false).isEmpty()) {
							sender.sendMessage(
									Listener.getLang((Player) sender).equalsIgnoreCase("hu") ? "§cNincs találat."
											: "§cNo results.");
						} else {
							for (String s : sett.getConfig().getConfigurationSection("item").getKeys(false)) {
								String ench = "";
								if (sett.getConfig().getStringList("item." + s + ".enchants") != null) {
									for (String enchant : sett.getConfig().getStringList("item." + s + ".enchants")) {
										ench = ench + "\n" + enchant;
									}
								}

								p.spigot()
										.sendMessage(Listener.getHoverClick(
												"§8+ §b" + sett.getConfig().getString("item." + s + ".material"),
												Listener.getLang((Player) sender).equalsIgnoreCase("hu")
														? "§7BreakerID: §e" + s + "\n\n§7Enchantok:§e" + ench
														: "§7BreakerID: §e" + s + "\n\n§7Enchants:§e" + ench,
												""));
							}
						}
						sender.sendMessage("§8+-");
					} else {
						sender.sendMessage(
								Language.getLocale(p, LocalePath.NO_PERM));
					}
				} else {
					sender.sendMessage(
							"§e[PickupSpawners] §7Commands:\n§7/pspawners §bgive <entity_name> §f- Gives you one spawner\n"
									+ "§7/pspawners §badditem §f- Adds spawner breaker item to the db\n"
									+ "§7/pspawners §bremoveitem <breakerID> §f- Removes spawner breaker item from the db\n"
									+ "§7/pspawners §bitemlist §f- Spawner breaker item list (with breakerID)");
					((Player) sender).spigot().sendMessage(Listener.getHoverClick("§e[PickupSpawners] §b&l[How to make a spawner buy sign]", "§7======================"
							+ "\n§b     Here is the syntax:\n"
							+ "\n§7§o1st line:   §f[PickupSpawners]"
							+ "\n§7§o2nd line:      §fB <price>"
							+ "\n§7§o3rd line:    §f<entity type>"
							+ "\n§7§o4th line:      §f<amount>"
							+ "\n\n"
							+ "§7Example:\n"
							+ "     [PickupSpawners]\n"
							+ "         B 500\n"
							+ "         zombie\n"
							+ "           2\n"
							+ "§7======================", ""));
				}
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
					List<String> list = Arrays.asList("give", "additem", "removeitem", "itemlist");

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
					if (args[0].equalsIgnoreCase("give")) {
						ArrayList<String> names = new ArrayList<String>();
						List<String> list = me.poma123.spawners.PickupSpawners.entities;

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
		}
		return null;
	}

}
