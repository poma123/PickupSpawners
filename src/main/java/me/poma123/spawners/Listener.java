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

import me.poma123.spawners.event.SpawnerBreakEvent;
import me.poma123.spawners.event.SpawnerPlaceEvent;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

public class Listener implements org.bukkit.event.Listener {
    public static int breakedSpawners = 0;
    PickupSpawners ps = PickupSpawners.getInstance();
    SettingsManager sett = SettingsManager.getInstance();
    private Plugin plugin = PickupSpawners.getPlugin(PickupSpawners.class);
    private Material material = PickupSpawners.material;

    public static String getLang(Player p) {
            String locale;
            try {
                Method getLocale = Class.forName("org.bukkit.entity.Player.Spigot").getMethod("getLocale");
                locale = (String) getLocale.invoke(p.spigot());
            } catch (Exception e) {
                locale = p.getLocale();
            }

            String[] s = StringUtils.split(locale, '_');
            return s[0];
    }

    public static String getLangExact(Player p) {

        String locale;
        try {
            Method getLocale = Class.forName("org.bukkit.entity.Player.Spigot").getMethod("getLocale");
            locale = (String) getLocale.invoke(p.spigot());
        } catch (Exception e) {
            locale = p.getLocale();
        }

        return locale;

       /* if (PickupSpawners.getVersion().contains("1_8_")) {
            return p.spigot().getLocale();
        } else {
            return p.getLocale();
        }*/
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

    public static <K, V extends Comparable<V>> NavigableMap<K, V> sortByValues(final Map<K, V> map) {
        final Comparator<K> valueComparator = new Comparator<K>() {
            @Override
            public int compare(final K k1, final K k2) {
                final int compare = map.get(k2).compareTo(map.get(k1));
                if (compare == 0) {
                    return 1;
                }
                return compare;
            }
        };
        final NavigableMap<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll((Map<? extends K, ? extends V>) map);
        return sortedByValues;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        Inventory open = e.getClickedInventory();
        ItemStack items = e.getCurrentItem();

        int slot = e.getSlot();
        if (open == null) {
            return;
        }
        if (e.getView().getTitle().equalsIgnoreCase("§1PickupSpawners Main Page")) {
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
        if (e.getView().getTitle().equalsIgnoreCase("§1PickupSpawners > Breaker Items")) {
            e.setCancelled(true);
            if (e.isShiftClick()) {
                return;
            }
            if ((items == null) || (!items.hasItemMeta())) {
                return;
            }


            if (items.getItemMeta().getLore().toString().contains("BreakerID")) {

                String str = ChatColor.stripColor(items.getItemMeta().getLore().get(items.getItemMeta().getLore().size() - 1)).replace("BreakerID: ", "");
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
        if (e.getView().getTitle().equalsIgnoreCase("§1PickupSpawners > Give")) {
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
     * @param player
     * @param breakerID
     */
    private void sendBreakerItemCommands(Player player, String breakerID) {
        player.sendMessage("§b#------------§6PickupSpawners§b------------#\n\n§cBreaker item commands:\n ");
        player.spigot().sendMessage(getHoverSuggest("§e [*] §6Edit item §8§o(Click here)", "§7/pspawners §bupdateitem " + breakerID, "/pspawners updateitem " + breakerID));
        player.spigot().sendMessage(getHoverSuggest("§e [*] §3Set permission §8§o(Click here)", "§7/pspawners §bsetitempermission " + breakerID + " <permission>", "/pspawners setitempermission " + breakerID + " <permission>"));
        if (sett.getConfig().get("item." + breakerID + ".permission") != null) {
            player.spigot().sendMessage(getHoverSuggest("§c [-] Remove permission §8§o(Click here)", "§7/pspawners §bremoveitempermission " + breakerID, "/pspawners removeitempermission " + breakerID));
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

    private boolean isLimitBlocks(Player p) {
        if (p.hasPermission("spawnerlimit.bypass")) {
            return false;
        }
        String limitPermissionPrefix = "pickupspawners.breaklimit.";
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();

        //    List<String> limits = new ArrayList<>();
        NavigableMap<String, Integer> map = new TreeMap<>();

        for (String str : sett.getConfig().getConfigurationSection("break-limits").getKeys(false)) {
            // limits.add(str + ";" + sett.getConfig().get("break-limits." + str));
            //TODO DEBUG   plugin.getLogger().info(str +": " + sett.getConfig().getInt("break-limits." + str));
            if (str.equals("default")) {
                //    plugin.getLogger().info("VANJOG: "+ limitPermissionPrefix + str);
                map.put(limitPermissionPrefix + str, sett.getConfig().getInt("break-limits." + str));
            } else if (p.hasPermission(limitPermissionPrefix + str)) {
                // plugin.getLogger().info("VANJOG: "+ limitPermissionPrefix + str);
                map.put(limitPermissionPrefix + str, sett.getConfig().getInt("break-limits." + str));
            }
        }

      /*  for (String str : limits) {
            String path = str.split(";")[0];
            int value = Integer.valueOf(str.split(";")[1]);
            if (p.hasPermission(limitPermissionPrefix + path)) {
                map.put(limitPermissionPrefix+path, value);
            }
        }*/

        NavigableMap<String, Integer> sorted = sortByValues(map);

        Map.Entry<String, Integer> lastEntry = sorted.firstEntry();
        int limit = lastEntry.getValue();
        //  System.out.println("LIMIT: " + limit);
        //Object limit = sett.getConfig().get("daily-broke-limit");


        int limit1 = 0;
        try {

            limit1 = limit;

        } catch (Exception e) {
            System.out.println(
                    "§c[PickupSpawners-ERROR] The daily break limit is not an integer in the config.yml. Please fix it. Daily limit skipped.");
            return false;
        }


        if (limit1 > 0) {
            File f = new File(ps.getDataFolder() + File.separator + "daily_limits.yml");


            if (!f.exists()) {

                try {
                    f.createNewFile();
                    FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
                    conf.set("date", year + "_" + month + "_" + day);
                    conf.save(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
            if (!conf.get("date").equals(year + "_" + month + "_" + day)) {
                conf.set("players", null);
                conf.set("date", year + "_" + month + "_" + day);
                try {
                    conf.save(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (conf.get("players." + p.getName()) != null) {


                    int localLimit = conf.getInt("players." + p.getName());
                    if (localLimit >= limit1) {
                        p.sendMessage(Language.getReplacedLocale(p, Language.LocalePath.LIMIT_REACH, "%limit%",
                                String.valueOf(limit1)));
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public boolean isItemStacksGood(ItemStack saved, ItemStack used, String path) {
        if (used == null) {
            System.out.println("§c[PickupSpawners] The spawner breaker item (used) is null. Please create an issue with the following stacktrace on github.com/poma123/PickupSpawners");

        }
        if (saved == null) {
            System.out.println("§c[PickupSpawners] The spawner breaker item (saved) is null. Please create an issue with the following stacktrace on github.com/poma123/PickupSpawners");
        }
        if (used.hasItemMeta() && saved.hasItemMeta()) {
            ItemMeta savedM = saved.getItemMeta();
            ItemMeta usedM = used.getItemMeta();
            boolean disp = true;
            boolean lore = true;
            boolean enchants = true;
            boolean material = true;
            boolean damage = true;
            if (used.getType().equals(saved.getType())) {
                material = true;
            } else {
                material = false;
            }
            if (savedM.hasDisplayName()) {
                if (!usedM.hasDisplayName()) {
                    disp = false;
                } else {
                    if (usedM.getDisplayName().equalsIgnoreCase(savedM.getDisplayName())) {
                        disp = true;
                    } else {
                        disp = false;
                    }
                }
            }
            if (savedM.hasLore()) {
                if (!usedM.hasLore()) {
                    lore = false;
                } else {
                    if (usedM.getLore().equals(savedM.getLore())) {
                        lore = true;
                    } else {
                        lore = false;
                    }
                }
            }

            if (PickupSpawners.getVersion().contains("1_13_") || PickupSpawners.getVersion().contains("1_14_") || PickupSpawners.getVersion().contains("1_15_")) {
                if (savedM instanceof Damageable && ((Damageable) savedM).hasDamage()) {
                    if (usedM instanceof Damageable && ((Damageable) usedM).hasDamage()) {
                        if (((Damageable) usedM).getDamage() == ((Damageable) savedM).getDamage()) {
                            damage = true;
                        } else {
                            damage = false;
                        }
                    } else {
                        damage = false;
                    }
                }
            }
            if (savedM.hasEnchants()) {
                if (!usedM.hasEnchants()) {
                    enchants = false;
                } else {
                    boolean containsAllEnchants = false;
                    String enchantments = "";
                    for (String ench : sett.getConfig().getStringList("item." + path + ".enchants")) {

                        if (ench.contains(":")) {
                            enchantments = enchantments + "(?=.*" + ench.split(":")[0].toUpperCase() + "]="
                                    + ench.split(":")[1] + ")";
                        } else {
                            enchantments = enchantments + "(?=.*" + ench.toUpperCase() + "]=" + ")";
                        }
                    }
               /* for (Enchantment encha : saved.getEnchantments().keySet()) {

                    String ench = encha.getName();
                    Integer value = saved.getEnchantments().get(encha);

                        enchantments = enchantments + "(?=.*" + ench.toUpperCase() + "]="
                                + value + ")";

                    //    enchantments = enchantments + "(?=.*" + ench.toUpperCase() + "]=" + ")";

                }*/

                    Pattern pattern = Pattern.compile(enchantments);
                    if (pattern.matcher(used.getEnchantments().toString()).find()) {
                        containsAllEnchants = true;
                    }

                    if (containsAllEnchants) {
                        enchants = true;
                    } else {
                        enchants = false;
                    }
                }
            }

            if (disp && lore && enchants && material && damage) {
                return true;
            }


        } else {
            if (used.equals(saved)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSpawnerBreak(BlockBreakEvent e) {
        Block s = e.getBlock();
        String lang = getLang(e.getPlayer());

        if (s.getType().equals(material)) {
            if (isLimitBlocks(e.getPlayer())) {

                // e.getPlayer().sendMessage(lang.equals("hu")? "§cElérted a napi kiüthető
                // spawner limitet (" + limitcount + ").": "§cYou have reached the daily spawner
                // break limit (" + limitcount + ").");
                e.setCancelled(true);
                return;
            }


            ItemStack item;

            if (PickupSpawners.getVersion().contains("1_8_")) {
                item = e.getPlayer().getInventory().getItemInHand();

            } else {
                item = e.getPlayer().getInventory().getItemInMainHand();
            }

            boolean isGoodItem = false;

            if (item == null) {
                return;
            }
            for (String string : sett.getConfig().getConfigurationSection("item").getKeys(false)) {
                ItemStack breakerItem = (ItemStack) sett.getConfig().get("item." + string + ".itemstack");

               /* Material mat = Material
                        .matchMaterial(sett.getConfig().getString("item." + string + ".material").toUpperCase());*/
                if (isItemStacksGood(breakerItem, item, string)) {
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
                /*List<String> enchants = new ArrayList<String>();
                if (sett.getConfig().getStringList("item." + string + ".enchants") != null) {
                    enchants = sett.getConfig().getStringList("item." + string + ".enchants");
                }

                if (enchants.isEmpty()) {

                    if (item.getType().equals(mat)) {
                        if (item.equals(breakerItem)) {  // TODO item equals breakeritem
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

                }*/

                if (isGoodItem == true) {
                    break;

                }
            }

            // if (item.getType().equals(Material.DIAMOND_PICKAXE)&&
            // item.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
            if (isGoodItem) {
                if (s.getType().equals(material)) {

                    Object event = new SpawnerBreakEvent(e.getPlayer(), s, item);

                    CreatureSpawner cs = (CreatureSpawner) s.getState();

                    ItemStack spawner = new ItemStack(material, 1);
                    ItemMeta swmeta = spawner.getItemMeta();
                    // swmeta.setLocalizedName();
                    swmeta.setDisplayName("§e" + cs.getSpawnedType().name().toLowerCase() + " §7Spawner");

                    e.setExpToDrop(0);
                    spawner.setItemMeta(swmeta);

                    Bukkit.getPluginManager().callEvent((Event) event);

                    if (((SpawnerBreakEvent) event).isCancelled()) {
                        return;
                    }

                    s.getWorld().dropItemNaturally(s.getLocation(), spawner);
                    e.getPlayer().sendMessage(Language.getReplacedLocale(e.getPlayer(), LocalePath.BREAK, "%type%",
                            cs.getSpawnedType().name().toLowerCase()));

                    breakedSpawners++;
                    // e.getPlayer().sendMessage(lang.equals("hu")? "§7Kiütöttél egy §e" +
                    // cs.getSpawnedType().name().toLowerCase() + " §7spawnert!" : "§7You have
                    // broken out one §e" + cs.getSpawnedType().name().toLowerCase() + "§7
                    // spawner.");
                    if (!e.getPlayer().hasPermission("spawnerlimit.bypass")) {
                        File f = new File(ps.getDataFolder() + File.separator + "daily_limits.yml");

                        FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
                        if (conf.get("players." + e.getPlayer().getName()) != null) {
                            Integer value = conf.getInt("players." + e.getPlayer().getName()) + 1;

                            conf.set("players." + e.getPlayer().getName(), value);
                            try {
                                conf.save(f);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                        } else {
                            conf.set("players." + e.getPlayer().getName(), 1);
                            try {
                                conf.save(f);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                if (!e.getPlayer().hasPermission("pickupspawners.bypasspickupblock")) {
                    e.getPlayer().sendMessage(Language.getLocale(e.getPlayer(), Language.LocalePath.CANNOT_PICKUP));
                    e.setCancelled(true);
                }
            }
        }
    }

    public boolean compareTwoList(List<String> models, String str) {
        return Arrays.asList(str).stream().allMatch(t -> models.stream().anyMatch(t::contains));
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

                    String type = spawnerName;
                    if (EntityType.values().toString().toLowerCase().contains(type.toLowerCase())) {

                    } else {
                        type = "PIG";
                    }
                    Object ev = new SpawnerPlaceEvent(event.getPlayer(), type);
                    Bukkit.getPluginManager().callEvent((Event) ev);

                    if (((SpawnerPlaceEvent) ev).isCancelled()) {
                        return;
                    }

                    // spawner.setCreatureTypeByName(spawnerName);
                    try {
                        spawner.setSpawnedType(EntityType.valueOf(spawnerName));
                        spawner.update(true, true);

                        event.getPlayer().sendMessage(Language.getReplacedLocale(event.getPlayer(), LocalePath.PLACE,
                                "%type%", spawnerName.toLowerCase()));
                        // event.getPlayer().sendMessage(lang.equals("hu")? "§7Letettél egy §e" +
                        // spawnerName.toLowerCase() + " §7spawnert!" : "§7You have placed one §e" +
                        // spawnerName.toLowerCase() + "§7 spawner.");
                    } catch (IllegalArgumentException e) {
                        spawner.setSpawnedType(EntityType.valueOf("PIG"));
                        spawner.update(true, true);

                        event.getPlayer().sendMessage(
                                Language.getReplacedLocale(event.getPlayer(), LocalePath.PLACE, "%type%", "pig"));
                        // event.getPlayer().sendMessage(lang.equals("hu") ? "§7Letettél egy §epig
                        // §7spawnert!": "§7You have placed one §epig §7spawner.");
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
                                    : "§7You have placed one §e" + spawnerName.toLowerCase() + "§7 spawner.");
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
            if (b.getType().toString().contains("SIGN")) {

                Sign s = (Sign) b.getState();
                if (s.getLine(0).equalsIgnoreCase("§1[PickupSpawners]")) {
                    if (p.hasPermission("pickupspawners.signshop.use")) {
                        // BUY SIGN
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
                            // SELL SIGN
                        } else  if (s.getLine(1).startsWith("S ")) {
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

                              /*  if (p.getInventory().firstEmpty() == -1) {
                                    p.sendMessage(Language.getLocale(p, LocalePath.NO_ENOUGH_SPACE_INV));
                                    return;
                                }*/
                                EntityType.valueOf(spawnedType);
                                ItemStack spawner = new ItemStack(me.poma123.spawners.PickupSpawners.material);//, count);
                                ItemMeta swmeta = spawner.getItemMeta();
                                // swmeta.setLocalizedName();
                                swmeta.setDisplayName("§e" + spawnedType.toLowerCase() + " §7Spawner");

                                spawner.setItemMeta(swmeta);
                              //  if (count > 1) {
                                    if (!p.getInventory().containsAtLeast(spawner, count)) {
                                        //TODO LANGUAGE
                                        p.sendMessage(me.poma123.spawners.Listener.getLang(p).equals("hu")
                                                ? "§cNincs elég tárgyad az eladáshoz."
                                                : "§cYou don't have enough items to sell.");
                                        return;
                                    }
                              /*  } else {
                                    if (!p.getInventory().containsAtLeast(spawner, count)) {
                                        //TODO LANGUAGE
                                        p.sendMessage(me.poma123.spawners.Listener.getLang(p).equals("hu")
                                                ? "§cNincs elég tárgyad az eladáshoz."
                                                : "§cYou don't have enough items to sell.");
                                        return;
                                    }
                                }*/

                                try {
                                    Economy economy = PickupSpawners.vault.getEconomy();
                                    spawner.setAmount(count);
                                    p.getInventory().removeItem(spawner);
                                    economy.depositPlayer(p, price);
                                    // TODO LANGUAGE

                                    p.sendMessage(me.poma123.spawners.Listener.getLang(p).equals("hu")
                                            ? "§aEladtál " + count +" §e" + spawnedType.toLowerCase() + "§a spawnert."
                                            : "§aYou have sold " + count +" §e" + spawnedType.toLowerCase() + "§a spawner(s).");
                                } catch (Exception ex) {
                                    plugin.getLogger().warning(
                                            "§cThere was an error when attempt to sell a spawner. Vault is not installed.");
                                    p.sendMessage(
                                            "§c[PickupSpawners] There was an error when attempt to sell a spawner. Please contact an administrator and tell them to look at the Console.");
                                    return;
                                }

                            } else {
                                p.sendMessage(me.poma123.spawners.Listener.getLang(p).equals("hu")
                                        ? "§cA megadott entitás típus nem létezik."
                                        : "§cThis entity type is invalid.");
                            }
                        }
                    } else {


                        p.sendMessage(Language.getLocale(p, LocalePath.NO_PERM));
                    }

                }
            }
        }
    }


    @EventHandler
    public void onSignChange(SignChangeEvent e) {

        Player p = e.getPlayer();
        Block b = e.getBlock();

        // TODO is shop enabled in config
        if (p.hasPermission("pickupspawners.signshop.create")) {
            if (b.getState() instanceof Sign) {
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
                    } else if (e.getLine(1).startsWith("S") || e.getLine(1).startsWith("s")) {
                        if (e.getLine(1).contains(" ") && PickupSpawners.isInteger(e.getLine(1).split(" ")[1])) {
                            sign.setLine(0, "§1[PickupSpawners]");
                            sign.setLine(1, e.getLine(1).replace("s", "S"));
                            if (PickupSpawners.entities.contains(e.getLine(2).toLowerCase())) {
                                sign.setLine(2, e.getLine(2).toLowerCase());
                                if (PickupSpawners.isInteger(e.getLine(3))) {
                                    sign.setLine(3, e.getLine(3));

                                } else {
                                    sign.setLine(3, "1");
                                }


                                p.sendMessage("§e[PickupSpawners] §aSell sign succesfully created!");

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
                                    "§e[PickupSpawners] §cInvalid price line. §7(Valid price line: §oS <price>§r§7. Example: §oS 500§r§7)");
                        }
                    } else {
                        sign.setLine(0, "§4[PickupSpawners]");
                        sign.setLine(2, "§cINVALID");
                        p.sendMessage(
                                "§e[PickupSpawners] §cInvalid price line. §7(Valid price lines are: §oB <price>§r§7, or §oS <price>§r§7. Example: §oB 500§r§7)");
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

}
