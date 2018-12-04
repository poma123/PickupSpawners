package me.poma123.spawners.gui;

import me.poma123.spawners.PickupSpawners;
import me.poma123.spawners.SettingsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PickupGui implements Listener {


    PickupSpawners ps = PickupSpawners.getInstance();
    SettingsManager s = SettingsManager.getInstance();

    public void mainSpawnersGui(Player p) {


        Inventory inv = ps.getServer().createInventory(null, 27, "§1PickupSpawners Main Page");


        ItemStack give = GuiItem.getGuiItem(getSpawnegg("CREEPER").getType(), 1, "§6§lGive spawners",
                Arrays.asList("§7You can easily get", "§7a spawner by clicking to a", "§7spawn egg of a mob!"));

        ItemStack breaker = GuiItem.getGuiItem(Material.DIAMOND_PICKAXE, 1, "§c§lBreaker items",
                Arrays.asList("§7Check, edit and remove spawner breaker items!"));


        inv.setItem(14, breaker);
        inv.setItem(12, give);
        p.openInventory(inv);

    }


    public void breakerItems(Player p, int page) {


        Inventory inv = ps.getServer().createInventory(null, 36, "§1PickupSpawners > Breaker Items");

        List<ItemStack> arr = new ArrayList<ItemStack>();

        for (String path : s.getConfig().getConfigurationSection("item").getKeys(false)) {
            Material matt;
            ItemStack localItemStack;
            boolean isPerm = SettingsManager.getInstance().getConfig().get("item." + path + ".permission") != null;
            try {

                matt = Material.getMaterial(SettingsManager.getInstance().getConfig().getString("item." + path + ".material"));
                localItemStack = new ItemStack(matt, 1);

                if (s.getConfig().get("item." + path + ".enchants") != null) {
                    ItemMeta meta = localItemStack.getItemMeta();
                    List<String> lore = new ArrayList<String>();

                    for (String str : s.getConfig().getStringList("item." + path + ".enchants")) {
                        str = str.toLowerCase();
                        String output = str.replaceFirst("" + str.charAt(0), "" + Character.toUpperCase(str.charAt(0))).replace("_", " ").replace(":", " ");
                        lore.add("§7" + output);


                    }
                    lore.add("");
                    if (isPerm) {
                        lore.add("§3Permission: " + SettingsManager.getInstance().getConfig().get("item." + path + ".permission"));

                    }

                    lore.add("§eBreakerID: " + path);
                    meta.setLore(lore);
                    meta.addEnchant(Enchantment.getByName("SILK_TOUCH"), 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS);

                    //   meta.setLore(Arrays.asList(s.getConfig().getStringList("item." + path + ".enchants") + "\n§eBreakerID: " + path));

                    localItemStack.setItemMeta(meta);


                } else {
                    ItemMeta meta = localItemStack.getItemMeta();
                    List<String> lore = new ArrayList<String>();

                    lore.add("");
                    if (isPerm) {
                        lore.add("§3Permission: " + SettingsManager.getInstance().getConfig().get("item." + path + ".permission"));

                    }
                    lore.add("§eBreakerID: " + path);

                    //   meta.setLore(Arrays.asList(s.getConfig().getStringList("item." + path + ".enchants") + "\n§eBreakerID: " + path));

                    meta.setLore(lore);
                    localItemStack.setItemMeta(meta);

                }


            } catch (Exception exc) {
                matt = Material.PAPER;

                localItemStack = new ItemStack(matt, 1);


                if (s.getConfig().get("item." + path + ".enchants") != null) {
                    ItemMeta meta = localItemStack.getItemMeta();
                    List<String> lore = new ArrayList<String>();

                    lore.add("§cInvalid material!");


                    for (String str : s.getConfig().getStringList("item." + path + ".enchants")) {
                        str.replace("_", " ").replace(":", " ");
                        lore.add("§7" + str.replaceFirst("" + str.charAt(0), "" + Character.toUpperCase(str.charAt(0))));


                    }
                    meta.addEnchant(Enchantment.getByName("SILK_TOUCH"), 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS);
                    lore.add("");
                    if (isPerm) {
                        lore.add("§3Permission: " + SettingsManager.getInstance().getConfig().get("item." + path + ".permission"));

                    }

                    lore.add("§eBreakerID: " + path);

                    localItemStack.setItemMeta(meta);


                } else {
                    ItemMeta meta = localItemStack.getItemMeta();
                    List<String> lore = new ArrayList<String>();
                    lore.add("");
                    if (isPerm) {
                        lore.add("§3Permission: " + SettingsManager.getInstance().getConfig().get("item." + path + ".permission"));

                    }
                    lore.add("§cInvalid material!");
                    lore.add("§eBreakerID: " + path);

                    meta.setLore(lore);
                    localItemStack.setItemMeta(meta);
                }

            }

            arr.add(localItemStack);


        }

        final int n = 27;

        int rows = 1 + arr.size() / n;
        List<List<ItemStack>> sublists = IntStream.range(0, rows)
                .mapToObj(i -> arr.subList(n * i, Math.min(n + (n * i), arr.size())))
                .collect(Collectors.toList());


        String backPageButtonAction = "";


        if (page == 1) {
            backPageButtonAction = "backToMainMenu";

        } else if (page == sublists.size() && page > 1) {
            backPageButtonAction = "pageBack_";
        }


        List<ItemStack> listByPage = sublists.get(page - 1);

        for (ItemStack stack : listByPage) {
            inv.setItem(listByPage.indexOf(stack), stack);
        }

        if (ps.getVersion().contains("1_13_R")) {
            ItemStack back = GuiItem.getGuiSkullItem("MHF_ArrowLeft", "§aBack", Arrays.asList(backPageButtonAction.contains("pageBack_") ?
                    "§7Back to page " + (page - 1) : "§7Back to the main menu..."));
            inv.setItem(27, back);
            if (page < sublists.size()) {
                ItemStack next = GuiItem.getGuiSkullItem("MHF_ArrowRight", "§aNext",
                        Arrays.asList(
                                "§7Go to page " + (page + 1)));
                inv.setItem(35, next);
            }
        } else {
            ItemStack back = GuiItem.getGuiItem(Material.ARROW, 1, "§aBack", Arrays.asList(backPageButtonAction.contains("pageBack_") ?
                    "§7Back to page " + (page - 1) : "§7Back to the main menu..."));
            inv.setItem(27, back);
            if (page < sublists.size()) {
                ItemStack next = GuiItem.getGuiItem(Material.ARROW, 1, "§aNext",
                        Arrays.asList(
                                "§7Go to page " + (page + 1)));
                inv.setItem(35, next);
            }
        }

        p.openInventory(inv);
    }


    public void spawnerGiveList(Player p, int page) {
        Inventory inv = ps.getServer().createInventory(null, 54, "§1PickupSpawners > Give");


        final int n = 45;
        List<String> arr = ps.entities;
        int rows = 1 + arr.size() / n;
        List<List<String>> sublists = IntStream.range(0, rows)
                .mapToObj(i -> arr.subList(n * i, Math.min(n + (n * i), arr.size())))
                .collect(Collectors.toList());


        String backPageButtonAction = "";


        if (page == 1) {
            backPageButtonAction = "backToMainMenu";

        } else if (page == sublists.size() && page > 1) {
            backPageButtonAction = "pageBack_";
        }


        List<String> listByPage = sublists.get(page - 1);

        if (ps.getVersion().contains("1_13_R")) {
            for (String s : listByPage) {
                if (s.equalsIgnoreCase("pig_zombie")) {
                    ItemStack item = new ItemStack(Material.ZOMBIE_PIGMAN_SPAWN_EGG);
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName("§6§l" + "Zombie_pigman");
                    item.setItemMeta(itemMeta);
                    inv.setItem(listByPage.indexOf(s), item);
                } else {
                    ItemStack item = new ItemStack(Material.getMaterial(s.toUpperCase() + "_SPAWN_EGG"));
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName("§6§l" + s.replaceFirst("" + s.charAt(0), "" + Character.toUpperCase(s.charAt(0))));
                    item.setItemMeta(itemMeta);
                    inv.setItem(listByPage.indexOf(s), item);
                }
            }
        } else {

            for (String s : listByPage) {
                ItemStack item = new ItemStack(Material.getMaterial("MONSTER_EGG"), 1, EntityType.valueOf(s.toUpperCase()).getTypeId());
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName("§6§l" + s.replaceFirst("" + s.charAt(0), "" + Character.toUpperCase(s.charAt(0))));
                item.setItemMeta(itemMeta);
                inv.setItem(listByPage.indexOf(s), item);

                //   System.out.println(item.getData() + " " + EntityType.valueOf(s.toUpperCase()).getTypeId());
            }
        }

        if (ps.getVersion().contains("1_13_R")) {
            ItemStack back = GuiItem.getGuiSkullItem("MHF_ArrowLeft", "§aBack", Arrays.asList(backPageButtonAction.contains("pageBack_") ?
                    "§7Back to page " + (page - 1) : "§7Back to the main menu..."));
            inv.setItem(45, back);
            if (page < sublists.size()) {
                ItemStack next = GuiItem.getGuiSkullItem("MHF_ArrowRight", "§aNext",
                        Arrays.asList(
                                "§7Go to page " + (page + 1)));
                inv.setItem(53, next);
            }
        } else {
            ItemStack back = GuiItem.getGuiItem(Material.ARROW, 1, "§aBack", Arrays.asList(backPageButtonAction.contains("pageBack_") ?
                    "§7Back to page " + (page - 1) : "§7Back to the main menu..."));
            inv.setItem(45, back);
            if (page < sublists.size()) {
                ItemStack next = GuiItem.getGuiItem(Material.ARROW, 1, "§aNext",
                        Arrays.asList(
                                "§7Go to page " + (page + 1)));
                inv.setItem(53, next);
            }
        }

        p.openInventory(inv);

    }


    public ItemStack getSpawnegg(String type) {

        if (ps.isOnePointThirteen) {
            if (type.equalsIgnoreCase("PIG_ZOMBIE")) {
                return new ItemStack(Material.ZOMBIE_PIGMAN_SPAWN_EGG, 1);
            } else {
                return new ItemStack(Material.getMaterial(type.toUpperCase() + "_SPAWN_EGG"));
            }
        } else {

            return new ItemStack(Material.getMaterial("MONSTER_EGG"), 1,
                    EntityType.valueOf(type.toUpperCase()).getTypeId()
            );
        }


    }
}
