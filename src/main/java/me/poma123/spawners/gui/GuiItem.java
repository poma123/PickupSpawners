package me.poma123.spawners.gui;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiItem {

	public static ItemStack getGuiCustomSkullItem(String link, String itemname) {
		ItemStack item = Skull.getCustomSkull(link);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(itemname);

		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack getGuiCustomSkullItem(String link, String itemname, List<String> lore) {
		ItemStack item = Skull.getPlayerSkull(link);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(itemname);
		meta.setLore(lore);

		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * 
	 * @param playername
	 * @param itemname
	 * 
	 * @author poma123
	 * @return ItemStack
	 */

	public static ItemStack getGuiSkullItem(String playername, String itemname) {
		ItemStack item = Skull.getPlayerSkull(playername);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(itemname);

		item.setItemMeta(meta);
		return item;
	}

	/**
	 * 
	 * @param playername
	 * @param itemname
	 * @param lore
	 * @author poma123
	 * @return ItemStack
	 */
	public static ItemStack getGuiSkullItem(String playername, String itemname, List<String> lore) {
		ItemStack item = Skull.getPlayerSkull(playername);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(itemname);
		meta.setLore(lore);

		item.setItemMeta(meta);
		return item;
	}

	/**
	 * 
	 * @param m
	 * @param amount
	 * @param itemname
	 * @author poma123
	 * @return ItemStack
	 */
	public static ItemStack getGuiItem(Material m, int amount, String itemname) {
		ItemStack item = new ItemStack(m, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(itemname);
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * 
	 * @param m
	 * @param amount
	 * @param itemname
	 * @param lore
	 * @author poma123
	 * @return ItemStack
	 */

	public static ItemStack getGuiItem(Material m, int amount, String itemname, List<String> lore) {
		ItemStack item = new ItemStack(m, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(itemname);
		meta.setLore(lore);

		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * @deprecated
	 * @param m
	 * @param amount
	 * @param damage
	 * @param itemname
	 * @param lore
	 * @return
	 */
	public static ItemStack getGuiDamageItem(Material m, int amount, short damage, String itemname, List<String> lore) {
		ItemStack item = new ItemStack(m, amount, damage);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(itemname);
		meta.setLore(lore);

		item.setItemMeta(meta);
		return item;
	}

}
