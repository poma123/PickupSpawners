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

import me.poma123.spawners.gui.PickupGui;
import me.poma123.spawners.language.Language;
import me.poma123.spawners.language.Language.LocalePath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PSCommand implements CommandExecutor, TabCompleter {
    PickupSpawners ps = PickupSpawners.getInstance();
    EntityType[] values = EntityType.values();
    SettingsManager sett = SettingsManager.getInstance();

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(
                "§e[PickupSpawners] §7Commands:\n§7/pspawners §bgive <entity_name> <player> <amount> §f- Gives you one spawner\n"
                        + "§7/pspawners §badditem §f- Adds spawner breaker item to the db\n"
                        + "§7/pspawners §bupdateitem <breakerID> §f- Change an item breaker item to a new item\n"
                        + "§7/pspawners §bremoveitem <breakerID> §f- Removes spawner breaker item from the db\n"
                        + "§7/pspawners §bitemlist §f- Spawner breaker item list (with breakerID)\n"
                        + "§7/pspawners §bsetitempermission <breakerID> <permission> §f- Set a permission for an item\n"
                        + "§7/pspawners §bremoveitempermission <breakerID> §f- Remove an item's permission\n"
                        + "§7/pspawners §bitemlist §f- Spawner breaker item list (with breakerID)\n"
                        + "§7/pspawners §bgui §f- Open the rich PickupSpawners GUI\n"
                        + "§7/pspawners §bchange <type>§f- Change spawner type");
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {

            if (sender.hasPermission("pickupspawners.give") || sender.hasPermission("pickupspawners.additem")
                    || sender.hasPermission("pickupspawners.removeitem")
                    || sender.hasPermission("pickupspawners.itemlist")) {
                if (args.length < 1) {
                    // sender.sendMessage("§e[PickupSpawners] §7Commands:\n§f- §7/pickupspawners
                    // §bgive <entity_name>");
                    sendHelp(sender);
                }

            } else {
                sender.sendMessage(Language.getLocale((Player) sender, LocalePath.NO_PERM));
                return true;
            }

            if (args.length > 0) {
                Player p = (Player) sender;
                if (args[0].equalsIgnoreCase("gui")) {
                    if (sender.hasPermission("pickupspawners.gui")) {
                        PickupGui i = new PickupGui();
                        i.mainSpawnersGui((Player) sender);

                    } else {
                        sender.sendMessage(Language.getLocale((Player) sender, LocalePath.NO_PERM));
                        return true;
                    }

                } else if (args[0].equalsIgnoreCase("setitempermission")) {

                    if (sender.hasPermission("pickupspawners.setitempermission")) {
                        if (args.length > 2) {
                            if (sett.getConfig().get("item." + args[1]) != null) {


                                String perm = ChatColor.stripColor(args[2].toLowerCase());
                                sett.getConfig().set("item." + args[1] + ".permission", null);
                                sett.getConfig().set("item." + args[1] + ".permission", perm);
                                sett.saveConfig();
                                sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
                                        ? "§aKész! " + args[1] + " tárgy jogosultsága beállítva: " + perm
                                        : "§aDone! " + args[1] + " item's permission succesfully set: " + perm);


                            } else {
                                sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
                                        ? "§cEz nem található az adatbázisban."
                                        : "§cThis is not in the database.");
                            }
                        } else {
                            sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bsetitempermission <breakerID> <permission>");

                        }

                    } else {
                        sender.sendMessage(Language.getLocale(p, LocalePath.NO_PERM));
                    }
                } else if (args[0].equalsIgnoreCase("removeitempermission")) {

                    if (sender.hasPermission("pickupspawners.removeitempermission")) {
                        if (args.length > 1) {
                            if (sett.getConfig().get("item." + args[1] + ".permission") != null) {


                                sett.getConfig().set("item." + args[1] + ".permission", null);
                                sett.saveConfig();
                                sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
                                        ? "§aKész! " + args[1] + " tárgy jogosultsága eltávolítva: "
                                        : "§aDone! " + args[1] + " item's permission removed.");


                            } else {
                                sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
                                        ? "§cEz nem található az adatbázisban."
                                        : "§cThis is not in the database.");
                            }
                        } else {
                            sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bremoveitempermission <breakerID>");

                        }

                    } else {
                        sender.sendMessage(Language.getLocale(p, LocalePath.NO_PERM));
                    }
                } else if (args[0].equalsIgnoreCase("change")) {
                    if (sender.hasPermission("pickupspawners.change")) {
                        if (args.length > 1) {
                            List<String> localEntities = ps.entities;
                            Player player = (Player) sender;
                            if (sett.getConfig().getStringList("spawner-change.disabledTypes") != null) {
                                for (String str : sett.getConfig().getStringList("spawner-change.disabledTypes")) {
                                    if (localEntities.contains(str.toLowerCase())) {
                                        localEntities.remove(str.toLowerCase());
                                    }
                                }
                            }

                            if (localEntities.contains(args[1].toLowerCase())) {


                                Block b = null;

                                if (ps.isOnePointThirteen) {
                                    b = player.getTargetBlock(5);
                                } else {
                                    b = player.getTargetBlock((HashSet<Material>) null, 5);
                                }


                                if (b != null) {
                                    if (b.getType().equals(ps.material)) {
                                        CreatureSpawner spawner = (CreatureSpawner) b.getState();
                                        String from = spawner.getCreatureTypeName();
                                        String to = args[1].toUpperCase();
                                        try {
                                            spawner.setSpawnedType(EntityType.valueOf(to));
                                            spawner.update(true, true);

                                            player.sendMessage(Language.getReplacedLocale(player, LocalePath.CHANGE_SUCCESFUL,
                                                    "%type_from%", from.toLowerCase()).replace("%type_to%", to.toLowerCase()));

                                            // event.getPlayer().sendMessage(lang.equals("hu")? "§7Letettél egy §e" +
                                            // spawnerName.toLowerCase() + " §7spawnert!" : "§7You have placen one §e" +
                                            // spawnerName.toLowerCase() + "§7 spawner.");
                                        } catch (IllegalArgumentException e) {
                                            spawner.setSpawnedType(EntityType.valueOf("PIG"));
                                            spawner.update(true, true);

                                            player.sendMessage(Language.getReplacedLocale(player, LocalePath.CHANGE_ERROR,
                                                    "%type_from%", from.toLowerCase()).replace("%type_to%", to.toLowerCase()));

                                            // event.getPlayer().sendMessage(lang.equals("hu") ? "§7Letettél egy §epig
                                            // §7spawnert!": "§7You have placen one §epig §7spawner.");
                                        }
                                    } else {
                                        player.sendMessage(Language.getLocale(player, LocalePath.CHANGE_ERROR_NOT_SPAWNER));
                                    }
                                } else {
                                    player.sendMessage(Language.getLocale(player, LocalePath.CHANGE_ERROR_NOT_SPAWNER));
                                }

                            } else {
                                player.sendMessage(Language.getLocale(player, LocalePath.CHANGE_ERROR_DISABLED_TYPE));
                            }
                        } else {
                            sendHelp(sender);

                        }


                    } else {
                        sender.sendMessage(Language.getLocale(p, LocalePath.NO_PERM));
                    }

                } else if (args[0].equalsIgnoreCase("give")) {
                    if (sender.hasPermission("pickupspawners.give")) {
                        if (args.length >= 3) {

                            String spawnedType = args[1].toUpperCase();
                            if (me.poma123.spawners.PickupSpawners.entities.contains(spawnedType.toLowerCase())) {
                                if (Bukkit.getPlayer(args[2]) != null) {
                                    Player player = Bukkit.getPlayer(args[2]);
                                    EntityType.valueOf(spawnedType);
                                    int amount = 1;
                                    if (args.length >= 4) {
                                        try {
                                            amount = Integer.parseInt(args[3]);
                                        } catch (Exception e) {
                                            sender.sendMessage("§cThe amount argument is not a number.");
                                            sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bgive <entity_name> <player> <amount>");
                                            return true;
                                        }
                                    }
                                    ItemStack spawner = new ItemStack(me.poma123.spawners.PickupSpawners.material, amount);
                                    ItemMeta swmeta = spawner.getItemMeta();
                                    // swmeta.setLocalizedName();
                                    swmeta.setDisplayName("§e" + spawnedType.toLowerCase() + " §7Spawner");

                                    spawner.setItemMeta(swmeta);

                                    player.getInventory().addItem(spawner);

                                    player.sendMessage(Language.getReplacedLocale(player, LocalePath.GIVE, "%count% %type%", amount + " " + spawnedType.toLowerCase()));

                                    p.sendMessage(me.poma123.spawners.Listener.getLang(p).equals("hu")
                                            ? "§aAdtál " + amount + " §e" + spawnedType.toLowerCase() + " §aspawnert " + player.getName() + " játékosnak."

                                            : "§aGave " + amount + " §e" + spawnedType.toLowerCase() + " §aspawner to " + player.getName() + ".");
                                } else {
                                    p.sendMessage(me.poma123.spawners.Listener.getLang(p).equals("hu")
                                            ? "§cEz a játékos nem elérhető."
                                            : "§cThis player isn't online.");
                                }
                            } else {
                                p.sendMessage(me.poma123.spawners.Listener.getLang(p).equals("hu")
                                        ? "§cA megadott entitás típus nem létezik."
                                        : "§cThis entity type is invalid.");
                            }

                        } else {
                            sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bgive <entity_name> <player> <amount>");

                        }

                    } else {
                        sender.sendMessage(Language.getLocale(p, LocalePath.NO_PERM));
                    }
                } else if (args[0].equalsIgnoreCase("updateitem")) {
                    //TODO 1.8-1.12 materialdata support
                    if (args.length > 1) {
                        if (sender.hasPermission("pickupspawners.updateitem")) {
                            if (sett.getConfig().get("item." + args[1]) != null) {
                                if (!p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                                    String random = args[1];
                                    sett.getConfig().set("item." + random, null);


                                    List<String> enchants = new ArrayList<String>();
                                    if (!p.getInventory().getItemInMainHand().getEnchantments().isEmpty()) {
                                        for (Enchantment e : p.getInventory().getItemInMainHand().getEnchantments().keySet()) {
                                            String s = e.getName();
                                            int value = p.getInventory().getItemInMainHand().getEnchantments().get(e);
                                            enchants.add(s + ":" + value);
                                        }
                                        sett.getConfig().set("item." + random + ".itemstack", p.getInventory().getItemInMainHand());
                                        sett.getConfig().set("item." + random + ".enchants", enchants);
                                    }

                                    sett.saveConfig();
                                    sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
                                            ? "§aKész! " + args[1] + " sikeresen frissítve!"
                                            : "§aDone! " + args[1] + " updated succesfully!");


                                } else {
                                    sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
                                            ? "§cNincs semmi a kezedben a beállításhoz."
                                            : "§cYou have nothing in your hand.");
                                }
                            } else {
                                sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
                                        ? "§cEz nem található az adatbázisban."
                                        : "§cThis is not in the database.");
                            }
                        } else {
                            sender.sendMessage(Language.getLocale(p, LocalePath.NO_PERM));
                        }
                    } else {
                        sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bupdateitem <breakerID>");
                    }
                } else if (args[0].equalsIgnoreCase("additem")) {
                    //TODO 1.8-1.12 materialdata support
                    if (sender.hasPermission("pickupspawners.additem")) {
                        ItemStack itemstack;
                       /* XMaterial xmat;
                        ItemStack output;
                        byte data;*/
                        if (ps.getVersion().contains("1_8_R")) {

                            itemstack = p.getInventory().getItemInHand();

                        } else {
                            itemstack = p.getInventory().getItemInMainHand();

                        }
                        if (!itemstack.getType().equals(Material.AIR)) {
                         /*   if (ps.getVersion().contains("1_8_R")) {


                                xmat = XMaterial.requestXMaterial(itemstack.getType().toString(), itemstack.getData().getData());
                                output = xmat.parseItem();
                                data =  output.getData().getData();
                            } else {

                                xmat = XMaterial.fromString(itemstack.getType().toString());
                                output = xmat.parseItem();
                                data =  (byte) 0;
                            }*/
                            String random = "random" + PickupSpawners.generateRandomString(7);
                            if (sett.getConfig().get("item." + random) != null) {
                                random = "random" + PickupSpawners.generateRandomString(7);
                            }


                            //   Material mat = itemstack.getType();


                            List<String> enchants = new ArrayList<String>();
                            if (!itemstack.getEnchantments().isEmpty()) {
                                for (Enchantment e : itemstack.getEnchantments().keySet()) {
                                    String s = e.getName();
                                    int value = itemstack.getEnchantments().get(e);
                                    enchants.add(s + ":" + value);
                                }

                                sett.getConfig().set("item." + random + ".enchants", enchants);
                            }
                            sett.getConfig().set("item." + random + ".itemstack", itemstack);
                           /* sett.getConfig().set("item." + random + ".material", output.getType().toString());
                            sett.getConfig().set("item." + random + ".data", data);*/
                            sett.saveConfig();
                            sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
                                    ? "§aKész! " + itemstack.getType().toString() + " hozzáadva a spawnert kiütő tárgyak adatbázishoz!"
                                    : "§aDone! " + itemstack.getType().toString() + " added to the spawner breaker items database!");
                            p.spigot().sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
                                    ? Listener.getHoverClickcmd("§c[Visszavonás]", "§c/pspawners removeitem " + random,
                                    "/pspawners removeitem " + random)
                                    : Listener.getHoverClickcmd("§c[Undo]", "§c/pspawners removeitem " + random,
                                    "/pspawners removeitem " + random));

                        } else {
                            sender.sendMessage(Listener.getLang((Player) sender).equalsIgnoreCase("hu")
                                    ? "§cNincs semmi a kezedben a beállításhoz."
                                    : "§cYou have nothing in your hand.");
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
                    //TODO 1.8-1.12 materialdata support
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
                                                "§8+ §b" + ((ItemStack) sett.getConfig().get("item." + s + ".itemstack")).getType().toString(),
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
                    sendHelp(sender);
                }
            }
        } else {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("setitempermission")) {


                        if (args.length > 2) {
                            if (sett.getConfig().get("item." + args[1]) != null) {


                                String perm = ChatColor.stripColor(args[2].toLowerCase());
                                sett.getConfig().set("item." + args[1] + ".permission", null);
                                sett.getConfig().set("item." + args[1] + ".permission", perm);
                                sett.saveConfig();
                                sender.sendMessage("§aDone! " + args[1] + " item's permission succesfully set: " + perm);


                            } else {
                                sender.sendMessage("§cThis is not in the database.");
                            }
                        } else {
                            sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bsetitempermission <breakerID> <permission>");

                        }


                } else if (args[0].equalsIgnoreCase("removeitempermission")) {


                        if (args.length > 1) {
                            if (sett.getConfig().get("item." + args[1] + ".permission") != null) {


                                sett.getConfig().set("item." + args[1] + ".permission", null);
                                sett.saveConfig();
                                sender.sendMessage("§aDone! " + args[1] + " item's permission removed.");


                            } else {
                                sender.sendMessage("§cThis is not in the database.");
                            }
                        } else {
                            sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bremoveitempermission <breakerID>");

                        }


                } else if (args[0].equalsIgnoreCase("give")) {

                    if (args.length >= 3) {

                        String spawnedType = args[1].toUpperCase();
                        if (me.poma123.spawners.PickupSpawners.entities.contains(spawnedType.toLowerCase())) {
                            if (Bukkit.getPlayer(args[2]) != null) {
                                Player player = Bukkit.getPlayer(args[2]);
                                EntityType.valueOf(spawnedType);
                                int amount = 1;
                                if (args.length >= 4) {
                                    try {
                                        amount = Integer.parseInt(args[3]);
                                    } catch (Exception e) {
                                        sender.sendMessage("§cThe amount argument is not a number.");
                                        sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bgive <entity_name> <player> <amount>");
                                        return true;
                                    }
                                }
                                ItemStack spawner = new ItemStack(me.poma123.spawners.PickupSpawners.material, amount);
                                ItemMeta swmeta = spawner.getItemMeta();
                                // swmeta.setLocalizedName();
                                swmeta.setDisplayName("§e" + spawnedType.toLowerCase() + " §7Spawner");

                                spawner.setItemMeta(swmeta);

                                player.getInventory().addItem(spawner);

                                player.sendMessage(Language.getReplacedLocale(player, LocalePath.GIVE, "%count% %type%", amount + " " + spawnedType.toLowerCase()));

                                sender.sendMessage("§aGave " + amount + " §e" + spawnedType.toLowerCase() + " §aspawner to " + player.getName() + ".");
                            } else {
                                sender.sendMessage("§cThis player isn't online.");
                            }
                        } else {
                            sender.sendMessage("§cThis entity type is invalid.");
                        }

                    } else {
                        sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bgive <entity_name> <player> <amount>");

                    }


                } else if (args[0].equalsIgnoreCase("removeitem")) {
                    if (args.length == 2) {
                        if (sett.getConfig().get("item." + args[1]) != null) {
                            sender.sendMessage("§cPlease type in, to confirm: /pspawners removeitem " + args[1] + " confirm");
                        } else {
                            sender.sendMessage("§cThis is not in the database.");
                        }
                    } else if (args.length >= 3) {
                        if (args[2].equalsIgnoreCase("confirm")) {
                            if (sett.getConfig().get("item." + args[1]) != null) {
                                sett.getConfig().set("item." + args[1], null);
                                sett.saveConfig();
                                sender.sendMessage("§aSuccesfully removed this item from the database.");
                            } else {
                                sender.sendMessage("§cThis is not in the database.");
                            }
                        } else {
                            sender.sendMessage(
                                    "§e[PickupSpawners] §7Usage:\n§7/pspawners §bremoveitem <breakerID>");
                        }
                    } else {
                        sender.sendMessage("§e[PickupSpawners] §7Usage:\n§7/pspawners §bremoveitem <breakerID>");
                    }

                } else {
                    sender.sendMessage("     - - - - - - - -");
                    sender.sendMessage("PickupSpawners Console Help:");
                    sender.sendMessage("");
                    sender.sendMessage("/pspawners give <entity_name> <player> <amount>");
                    sender.sendMessage("/pspawners removeitem <breakerID>");
                    sender.sendMessage("/pspawners removeitempermission <breakerID>");
                    sender.sendMessage("/pspawners setitempermission <breakerID>");
                    sender.sendMessage("     - - - - - - - -");
                }
            } else {
                sender.sendMessage("     - - - - - - - -");
                sender.sendMessage("PickupSpawners Console Help:");
                sender.sendMessage("");
                sender.sendMessage("/pspawners give <entity_name> <player> <amount>");
                sender.sendMessage("/pspawners removeitem <breakerID>");
                sender.sendMessage("/pspawners removeitempermission <breakerID>");
                sender.sendMessage("/pspawners setitempermission <breakerID>");
                sender.sendMessage("     - - - - - - - -");
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
                    List<String> list = Arrays.asList("give", "additem", "removeitem", "itemlist", "gui", "change", "updateitem", "setitempermission", "removeitempermission");

                    if (!args[0].equals("")) {
                        for (String name : list) {
                            if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                                names.add(name);
                            }

                        }
                    } else {
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
                        } else {
                            for (String name : list) {
                                names.add(name);
                            }
                        }

                        Collections.sort(names);

                        return names;
                    }
                    if (args[0].equalsIgnoreCase("updateitem") || args[0].equalsIgnoreCase("removeitem") || args[0].equalsIgnoreCase("removeitempermission")
                            || args[0].equalsIgnoreCase("setitempermission")) {
                        ArrayList<String> names = new ArrayList<String>();
                        List<String> list = new ArrayList<>();
                        for (String str : sett.getConfig().getConfigurationSection("item").getKeys(false)) {
                            list.add(str);
                        }


                        if (!args[1].equals("")) {
                            for (String name : list) {
                                if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                                    names.add(name);
                                }

                            }
                        } else {
                            for (String name : list) {
                                names.add(name);
                            }
                        }

                        Collections.sort(names);

                        return names;
                    }

                }

                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("change")) {
                        ArrayList<String> names = new ArrayList<String>();
                        List<String> list = me.poma123.spawners.PickupSpawners.entities;
                        if (sett.getConfig().getStringList("spawner-change.disabledTypes") != null) {
                            for (String str : sett.getConfig().getStringList("spawner-change.disabledTypes")) {
                                list.remove(str.toLowerCase());
                            }
                        }


                        if (!args[1].equals("")) {
                            for (String name : list) {
                                if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                                    names.add(name);
                                }

                            }
                        } else {
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
