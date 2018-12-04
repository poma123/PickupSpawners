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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Listener implements org.bukkit.event.Listener {
    PickupSpawners ps = PickupSpawners.getInstance();
    public static int breakedSpawners = 0;
    Map<String, Integer> limit = new TreeMap<String, Integer>();
    SettingsManager sett = SettingsManager.getInstance();
    private Plugin plugin = PickupSpawners.getPlugin(PickupSpawners.class);
    private Material material = PickupSpawners.material;

    public static String getLang(Player p) {
        String[] s = StringUtils.split(p.spigot().getLocale(), '_');
        return s[0];
    }

    public static String getLangExact(Player p) {

        return p.spigot().getLocale();

    }

    public static TextComponent getHoverClick(String message, String hover, String click) {
        TextComponent text = new TextComponent(message);
        text.setClickEvent(
                new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, click));
        text.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(hover).create()));
        return text;
    }

    public static TextComponent getHoverSuggest(String message, String hover, String suggestedcommand) {
        TextComponent text = new TextComponent(message);
        text.setClickEvent(
                new net.md_5.bungee.api.chat.ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(suggestedcommand)));
        text.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(hover).create()));
        return text;
    }

    public static TextComponent getHoverClickcmd(String message, String hover, String click) {
        TextComponent text = new TextComponent(message);
        text.setClickEvent(
                new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, click));
        text.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(hover).create()));
        return text;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player)e.getWhoClicked();

        Inventory open = e.getClickedInventory();
        ItemStack items = e.getCurrentItem();

        int slot = e.getSlot();
        if (open == null) {
            return;
        }
        if (e.getView().getTopInventory().getName().equalsIgnoreCase("§1PickupSpawners Main Page")) {
            e.setCancelled(true);
            if (e.isShiftClick()) {
                return;
            }
            if ((items == null) || (!items.hasItemMeta())) {
                return;
            }
            if (!items.getItemMeta().hasDisplayName()) {
                return;
            }
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lGive spawners")) {
                    PickupGui gui = new PickupGui();
                    e.getWhoClicked().closeInventory();
                    gui.spawnerGiveList((Player) e.getWhoClicked(), 1);


                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lBreaker items")) {
                    PickupGui gui = new PickupGui();
                    e.getWhoClicked().closeInventory();
                    gui.breakerItems((Player) e.getWhoClicked(), 1);


                }
            }


        }
        if (e.getView().getTopInventory().getName().equalsIgnoreCase("§1PickupSpawners > Breaker Items")) {
            e.setCancelled(true);
            if (e.isShiftClick()) {
                return;
            }
            if ((items == null) || (!items.hasItemMeta())) {
                return;
            }


            if (items.getItemMeta().getLore().toString().contains("BreakerID")) {

                String str = ChatColor.stripColor(items.getItemMeta().getLore().get(items.getItemMeta().getLore().size()-1)).replace("BreakerID: ", "");
                player.closeInventory();
                sendBreakerItemCommands(player, str);
            }
            if (!items.getItemMeta().hasDisplayName()) {
                return;
            }
            if (items.getItemMeta().getDisplayName().contains("Back")) {

                PickupGui gui = new PickupGui();
                e.getWhoClicked().closeInventory();
                String lore = items.getItemMeta().getLore().get(0);
                if (lore.contains("Back to page")) {
                    int page = Integer.parseInt(lore.replace("§7Back to page ", ""));
                    e.getWhoClicked().closeInventory();
                    gui.breakerItems((Player) e.getWhoClicked(), page);
                } else {
                    e.getWhoClicked().closeInventory();
                    gui.mainSpawnersGui((Player) e.getWhoClicked());
                }


            }


            if (items.getItemMeta().getDisplayName().contains("Next")) {
                PickupGui gui = new PickupGui();
                e.getWhoClicked().closeInventory();
                String lore = items.getItemMeta().getLore().get(0);
                if (lore.contains("Go to page")) {
                    int page = Integer.parseInt(lore.replace("§7Go to page ", ""));
                    e.getWhoClicked().closeInventory();
                    gui.breakerItems((Player) e.getWhoClicked(), page);
                }
            }
        }
        if (e.getView().getTopInventory().getName().equalsIgnoreCase("§1PickupSpawners > Give")) {
            e.setCancelled(true);
            if (e.isShiftClick()) {
                return;
            }
            if ((items == null) || (!items.hasItemMeta())) {
                return;
            }
            if (!items.getItemMeta().hasDisplayName()) {
                return;
            }

            if (items.getItemMeta().getDisplayName().contains("Back")) {

                PickupGui gui = new PickupGui();
                e.getWhoClicked().closeInventory();
                String lore = items.getItemMeta().getLore().get(0);
                if (lore.contains("Back to page")) {
                int page = Integer.parseInt(lore.replace("§7Back to page ", ""));
                    e.getWhoClicked().closeInventory();
                    gui.spawnerGiveList((Player) e.getWhoClicked(), page);
                } else {
                    e.getWhoClicked().closeInventory();
                    gui.mainSpawnersGui((Player) e.getWhoClicked());
                }


            }


            if (items.getItemMeta().getDisplayName().contains("Next")) {
                PickupGui gui = new PickupGui();
                e.getWhoClicked().closeInventory();
                String lore = items.getItemMeta().getLore().get(0);
                if (lore.contains("Go to page")) {
                    int page = Integer.parseInt(lore.replace("§7Go to page ", ""));
                    e.getWhoClicked().closeInventory();
                    gui.spawnerGiveList((Player) e.getWhoClicked(), page);
                }
            }
            if (items.getItemMeta().getDisplayName().contains("§6§l")) {
                String type = ChatColor.stripColor(items.getItemMeta().getDisplayName().split(" ")[0].toLowerCase());




                if (ps.entities.contains(type)) {
                    player.closeInventory();

                    ItemStack spawner = new ItemStack(me.poma123.spawners.PickupSpawners.material, 1);
                    ItemMeta swmeta = spawner.getItemMeta();
                    // swmeta.setLocalizedName();
                    swmeta.setDisplayName("§e" + type.toLowerCase() + " §7Spawner");

                    spawner.setItemMeta(swmeta);

                    player.getInventory().addItem(spawner);

                    player.sendMessage(Language.getReplacedLocale(player, LocalePath.GIVE, "%count% %type%", 1 + " " + type.toLowerCase()));

                } else {
                    player.sendMessage(me.poma123.spawners.Listener.getLang(player).equals("hu")
                            ? "§cA megadott entitás típus nem létezik."
                            : "§cThis entity type is invalid.");
                }
            }


        }
    }

    /**
     *
     * @param player
     * @param breakerID
     */
    private void sendBreakerItemCommands(Player player, String breakerID) {
        player.sendMessage("§b#------------§6PickupSpawners§b------------#\n\n§cBreaker item commands:\n ");
        player.spigot().sendMessage(getHoverSuggest("§e [*] §6Edit item §8§o(Click here)", "§7/pspawners §bupdateitem " + breakerID, "/pspawners updateitem " + breakerID));
        player.spigot().sendMessage(getHoverSuggest("§e [*] §3Set permission §8§o(Click here)", "§7/pspawners §bsetitempermission " + breakerID + " <permission>", "/pspawners setitempermission " + breakerID + " <permission>"));
        if (sett.getConfig().get("item." + breakerID + ".permission") != null) {
            player.spigot().sendMessage(getHoverSuggest("§c [-] Remove permission §8§o(Click here)", "§7/pspawners §bremoveitempermission " + breakerID , "/pspawners removeitempermission " + breakerID));
        }
        player.spigot().sendMessage(getHoverSuggest("§c [-] Remove item §8§o(Click here)", "§7/pspawners §bremoveitem " + breakerID, "/pspawners removeitem " + breakerID));

                player.sendMessage("\n\n§b#-------------------------------------#\n");
    }


    @EventHandler
    public void onOpJoin(PlayerJoinEvent e) {
        if (sett.getConfig().getBoolean("update-check")) {

            Player p = e.getPlayer();
            if (p.isOp()) {
                if (Updater.version == null) {
                    return;
                }
                if (!Updater.version.equalsIgnoreCase(plugin.getDescription().getVersion())) {
                    p.spigot().sendMessage(getLang(p).equalsIgnoreCase("hu") ? getHoverClick(
                            "§6[PickupSpawners] §7Elérhető egy frissítés a pluginhoz. \n§6[PickupSpawners] §7Jelenlegi §cv"
                                    + plugin.getDescription().getVersion() + "§7, legfrissebb §av" + Updater.version,
                            ChatColor.GREEN + Updater.SPIGOT_DOWNLOAD + Updater.SPIGOT_DOWNLOAD_VERSION
                                    + Updater.downloadID,
                            Updater.SPIGOT_DOWNLOAD + Updater.SPIGOT_DOWNLOAD_VERSION + Updater.downloadID)
                            : getHoverClick(
                            "§6[PickupSpawners] §7There is a new update available. \n§6[PickupSpawners] §7Running §cv"
                                    + plugin.getDescription().getVersion() + "§7, latest §av" + Updater.version,
                            ChatColor.GREEN + Updater.SPIGOT_DOWNLOAD + Updater.SPIGOT_DOWNLOAD_VERSION
                                    + Updater.downloadID,
                            Updater.SPIGOT_DOWNLOAD + Updater.SPIGOT_DOWNLOAD_VERSION + Updater.downloadID));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSpawnerBreak(BlockBreakEvent e) {
        Block s = e.getBlock();
        String lang = getLang(e.getPlayer());
        Object limitcount1 = sett.getConfig().get("daily-broke-limit");
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        try {
            int limitcount = (int) limitcount1;
            if (limitcount > 0) {

                if (s.getType().equals(material) && !e.getPlayer().hasPermission("spawnerlimit.bypass")) {
                    if (limit.containsKey(e.getPlayer().getName())) {
                        if (limit.get(e.getPlayer().getName()) >= limitcount) {

                            e.getPlayer().sendMessage(Language.getReplacedLocale(e.getPlayer(), LocalePath.LIMIT_REACH,
                                    "%limit%", String.valueOf(limitcount)));
                            // e.getPlayer().sendMessage(lang.equals("hu")? "§cElérted a napi kiüthető
                            // spawner limitet (" + limitcount + ").": "§cYou have reached the daily spawner
                            // break limit (" + limitcount + ").");
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        } catch (Exception ex) {

            System.out.println(
                    "§c[PickupSpawners-ERROR] The daily limit is not an integer in the config.yml. Please fix it. Daily limit skipped.");
            ex.printStackTrace();
        }

        boolean isGoodItem = false;

        for (String string : sett.getConfig().getConfigurationSection("item").getKeys(false)) {
            Material mat = Material
                    .matchMaterial(sett.getConfig().getString("item." + string + ".material").toUpperCase());
            List<String> enchants = new ArrayList<String>();
            if (sett.getConfig().getStringList("item." + string + ".enchants") != null) {
                enchants = sett.getConfig().getStringList("item." + string + ".enchants");
            }

            if (enchants.isEmpty()) {
                if (item.getType().equals(mat)) {
                    if (sett.getConfig().get("item." + string + ".permission") != null) {
                        if (e.getPlayer().hasPermission(sett.getConfig().getString("item." + string + ".permission"))) {
                            isGoodItem = true;
                        } else {
                            isGoodItem = false;
                            e.setCancelled(true);
                            e.getPlayer().sendMessage(Language.getLocale(e.getPlayer(), LocalePath.NO_PERM));
                            break;
                        }
                    } else {
                        isGoodItem = true;
                    }
                }
            } else {

                boolean containsAllEnchants = false;

                String enchantments = "";

                for (String ench : sett.getConfig().getStringList("item." + string + ".enchants")) {

                    if (ench.contains(":")) {
                        enchantments = enchantments + "(?=.*" + ench.split(":")[0].toUpperCase() + "]="
                                + ench.split(":")[1] + ")";
                    } else {
                        enchantments = enchantments + "(?=.*" + ench.toUpperCase() + "]=" + ")";
                    }
                }

                Pattern pattern = Pattern.compile(enchantments);
                if (pattern.matcher(item.getEnchantments().toString()).find()) {
                    containsAllEnchants = true;
                }

                if (item.getType().equals(mat) && containsAllEnchants) {
                    if (sett.getConfig().get("item." + string + ".permission") != null) {
                        if (e.getPlayer().hasPermission(sett.getConfig().getString("item." + string + ".permission"))) {
                            isGoodItem = true;
                        } else {
                            isGoodItem = false;
                            e.setCancelled(true);
                            e.getPlayer().sendMessage(Language.getLocale(e.getPlayer(), LocalePath.NO_PERM));
                            break;
                        }
                    } else {
                        isGoodItem = true;
                    }
                }

            }

            if (isGoodItem == true) {
                break;

            }
        }

        // if (item.getType().equals(Material.DIAMOND_PICKAXE)&&
        // item.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
        if (isGoodItem) {
            if (s.getType().equals(material)) {

                CreatureSpawner cs = (CreatureSpawner) s.getState();

                ItemStack spawner = new ItemStack(material, 1);
                ItemMeta swmeta = spawner.getItemMeta();
                // swmeta.setLocalizedName();
                swmeta.setDisplayName("§e" + cs.getSpawnedType().name().toLowerCase() + " §7Spawner");

                e.setExpToDrop(0);
                spawner.setItemMeta(swmeta);

                s.getWorld().dropItemNaturally(s.getLocation(), spawner);
                e.getPlayer().sendMessage(Language.getReplacedLocale(e.getPlayer(), LocalePath.BREAK, "%type%",
                        cs.getSpawnedType().name().toLowerCase()));

                breakedSpawners++;
                // e.getPlayer().sendMessage(lang.equals("hu")? "§7Kiütöttél egy §e" +
                // cs.getSpawnedType().name().toLowerCase() + " §7spawnert!" : "§7You have
                // broken out one §e" + cs.getSpawnedType().name().toLowerCase() + "§7
                // spawner.");
                if (!e.getPlayer().hasPermission("spawnerlimit.bypass")) {
                    if (limit.containsKey(e.getPlayer().getName())) {
                        Integer value = limit.get(e.getPlayer().getName()) + 1;
                        limit.remove(e.getPlayer().getName());
                        limit.put(e.getPlayer().getName(), value);
                    } else {
                        limit.put(e.getPlayer().getName(), 1);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        ItemStack stack = event.getItemInHand();
        String lang = getLang(event.getPlayer());
        if (block.getState() instanceof CreatureSpawner && stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();

            if (meta.getDisplayName().contains("§7Spawner")) {
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                String name = meta.getDisplayName();

                if (!name.isEmpty()) {
                    String spawnerName = ChatColor.stripColor(name.toUpperCase());
                    spawnerName = spawnerName.replaceAll("SPAWNER", "");
                    spawnerName = spawnerName.replaceAll(" ", "");
                    try {
                        EntityType.valueOf(spawnerName.toUpperCase());
                    } catch (Exception e) {

                        return;

                    }

                    // spawner.setCreatureTypeByName(spawnerName);
                    try {
                        spawner.setSpawnedType(EntityType.valueOf(spawnerName));
                        spawner.update(true, true);

                        event.getPlayer().sendMessage(Language.getReplacedLocale(event.getPlayer(), LocalePath.PLACE,
                                "%type%", spawnerName.toLowerCase()));
                        // event.getPlayer().sendMessage(lang.equals("hu")? "§7Letettél egy §e" +
                        // spawnerName.toLowerCase() + " §7spawnert!" : "§7You have placen one §e" +
                        // spawnerName.toLowerCase() + "§7 spawner.");
                    } catch (IllegalArgumentException e) {
                        spawner.setSpawnedType(EntityType.valueOf("PIG"));
                        spawner.update(true, true);

                        event.getPlayer().sendMessage(
                                Language.getReplacedLocale(event.getPlayer(), LocalePath.PLACE, "%type%", "pig"));
                        // event.getPlayer().sendMessage(lang.equals("hu") ? "§7Letettél egy §epig
                        // §7spawnert!": "§7You have placen one §epig §7spawner.");
                    }

                    return;
                }
            }
            if (meta.getDisplayName().contains(
                    ((CreatureSpawner) block.getState()).getSpawnedType().name().toLowerCase() + " Spawner")) {
                CreatureSpawner spawner = (CreatureSpawner) block.getState();

                String name = meta.getDisplayName();
                if (!name.isEmpty()) {
                    String spawnerName = ChatColor.stripColor(name.toUpperCase());
                    spawnerName = spawnerName.replaceAll("SPAWNER", "");
                    spawnerName = spawnerName.replaceAll(" ", "");
                    try {
                        EntityType.valueOf(spawnerName.toUpperCase());
                    } catch (Exception e) {

                        return;

                    }
                    spawner.setSpawnedType(EntityType.valueOf(spawnerName));
                    spawner.update(true, true);
                    event.getPlayer().sendMessage(
                            lang.equals("hu") ? "§7Letettél egy §e" + spawnerName.toLowerCase() + " §7spawnert!"
                                    : "§7You have placen one §e" + spawnerName.toLowerCase() + "§7 spawner.");
                    return;
                }
            }

        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        if (e.getAction().equals(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)) {
            if (p.hasPermission("pickupspawners.signshop.use")) {
                if (b.getType().toString().contains("SIGN")) {

                    Sign s = (Sign) b.getState();
                    if (s.getLine(0).equalsIgnoreCase("§1[PickupSpawners]")) {
                        if (s.getLine(1).startsWith("B ")) {
                            String spawnedType = s.getLine(2).toUpperCase();
                            Integer count = 1;
                            double price = 0;
                            if (s.getLine(3) != null && PickupSpawners.isInteger(s.getLine(3))) {
                                count = Integer.parseInt(s.getLine(3));
                            }

                            if (PickupSpawners.isDouble(s.getLine(1).split(" ")[1])) {
                                price = Double.parseDouble(s.getLine(1).split(" ")[1]);
                            }

                            if (me.poma123.spawners.PickupSpawners.entities.contains(spawnedType.toLowerCase())) {

                                if (p.getInventory().firstEmpty() == -1) {
                                    p.sendMessage(Language.getLocale(p, LocalePath.NO_ENOUGH_SPACE_INV));
                                    return;
                                }
                                EntityType.valueOf(spawnedType);
                                ItemStack spawner = new ItemStack(me.poma123.spawners.PickupSpawners.material, count);
                                ItemMeta swmeta = spawner.getItemMeta();
                                // swmeta.setLocalizedName();
                                swmeta.setDisplayName("§e" + spawnedType.toLowerCase() + " §7Spawner");

                                spawner.setItemMeta(swmeta);
                                try {
                                    Economy economy = PickupSpawners.vault.getEconomy();

                                    if (economy.getBalance(p) >= price) {
                                        economy.withdrawPlayer(p, price);
                                        p.getInventory().addItem(spawner);
                                        p.sendMessage(Language.getReplacedLocale(p, LocalePath.GIVE, "%count% %type%", count + " " + spawnedType.toLowerCase()));
                                    } else {
                                        p.sendMessage(Language.getLocale(p, LocalePath.NO_ENOUGH_MONEY));
                                    }

                                } catch (Exception ex) {
                                    plugin.getLogger().warning(
                                            "§cThere was an error when attempt to buy a spawner. Vault is not installed.");
                                    p.sendMessage(
                                            "§c[PickupSpawners] There was an error when attempt to buy a spawner. Please contact an administrator and tell them to look at the Console.");
                                    return;
                                }

                            } else {
                                p.sendMessage(me.poma123.spawners.Listener.getLang(p).equals("hu")
                                        ? "§cA megadott entitás típus nem létezik."
                                        : "§cThis entity type is invalid.");
                            }
                        }
                    }
                }
            } else {


                p.sendMessage(Language.getLocale(p, LocalePath.NO_PERM));
            }
        }
    }


    @EventHandler
    public void onSignChange(SignChangeEvent e) {

        Player p = e.getPlayer();
        Block b = e.getBlock();

        // TODO is shop enabled in config
        if (p.hasPermission("pickupspawners.signshop.create")) {
            Sign sign = (Sign) b.getState();
            if (e.getLines()[0].equalsIgnoreCase("[PickupSpawners]") || e.getLines()[0].equalsIgnoreCase("[Spawners]")
                    || e.getLines()[0].equalsIgnoreCase("[ps]")) {
                // if
                // (s.getConfig().getStringList("shop-sign-prefixes").contains(e.getLines()[0]))
                // {
                if (e.getLine(1).startsWith("B") || e.getLine(1).startsWith("b")) {
                    if (e.getLine(1).contains(" ") && PickupSpawners.isInteger(e.getLine(1).split(" ")[1])) {
                        sign.setLine(0, "§1[PickupSpawners]");
                        sign.setLine(1, e.getLine(1).replace("b", "B"));
                        if (PickupSpawners.entities.contains(e.getLine(2).toLowerCase())) {
                            sign.setLine(2, e.getLine(2).toLowerCase());
                            if (PickupSpawners.isInteger(e.getLine(3))) {
                                sign.setLine(3, e.getLine(3));

                            } else {
                                sign.setLine(3, "1");
                            }


                            p.sendMessage("§e[PickupSpawners] §aBuy sign succesfully created!");

                        } else {

                            sign.setLine(0, "§4[PickupSpawners]");
                            sign.setLine(1, "");
                            sign.setLine(2, "§cINVALID");
                            p.sendMessage("§e[PickupSpawners] §cInvalid entity name.");

                            /*
                             * final Player receipient =p; final ComponentBuilder message = new
                             * ComponentBuilder("§e[PickupSpawners] §cInvalid entity name. ");
                             *
                             * String entity = ""; int size = entities.size() / 2; int second =
                             * entities.size() - size -1;
                             *
                             * for (int i = 0; i < size; i++) {
                             *
                             * entity = entity + "\n§e- §7" + entities.get(i) + " §e- §7" +
                             * entities.get(second); second++; }
                             *
                             *
                             * message.append(ChatColor.GRAY +
                             * " §7[Valid entities] §o(Hover with cursor!)"); // message.event(new
                             * ClickEvent(ClickEvent.Action.RUN_COMMAND, "")); message.event(new
                             * HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(entity)));
                             *
                             * receipient.spigot().sendMessage(message.create());
                             */
                        }

                    } else {
                        sign.setLine(0, "§4[PickupSpawners]");
                        sign.setLine(2, "§cINVALID");
                        p.sendMessage(
                                "§e[PickupSpawners] §cInvalid price line. §7(Valid price line: §oB <price>§r§7. Example: §oB 500§r§7)");
                    }
                } else {
                    sign.setLine(0, "§4[PickupSpawners]");
                    sign.setLine(2, "§cINVALID");
                    p.sendMessage(
                            "§e[PickupSpawners] §cInvalid price line. §7(Valid price line: §oB <price>§r§7. Example: §oB 500§r§7)");
                }
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {

                        sign.update();
                    }
                }, 2);

            }

        }

    }

}
