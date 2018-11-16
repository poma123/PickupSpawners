package me.poma123.spawners;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Listener implements org.bukkit.event.Listener {
	Map<String, Integer> limit = new TreeMap<String, Integer>();
	SettingsManager sett = SettingsManager.getInstance();

	public String getLang(Player p) {
		String[] s = StringUtils.split(p.spigot().getLocale(), '_');
		return s[0];
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSpawnerBreak(BlockBreakEvent e) {
		Block s = e.getBlock();
		String lang = getLang(e.getPlayer());
		Object limitcount1 = sett.getConfig().get("daily-broke-limit");
		
		try {
			int limitcount = (int) limitcount1;
			if (limitcount > 0) {
				if (s.getType().equals(Material.SPAWNER) && !e.getPlayer().hasPermission("spawnerlimit.bypass")) {
					if (limit.containsKey(e.getPlayer().getName())) {
						if (limit.get(e.getPlayer().getName()) >= limitcount) {
						
							e.getPlayer()
									.sendMessage(lang.equals("hu") ? "�cEl�rted a napi ki�thet� spawner limitet (" + limitcount+")."
											: "�cYou have reached the daily spawner break limit (" + limitcount+").");
							e.setCancelled(true);
							return;
						}
					}
				}
			}
		} catch (Exception ex) {
			
			System.out.println("�c[PickupSpawners-ERROR] The daily limit is not an integer in the config.yml. Please fix it. Daily limit skipped.");
		}

		if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE) && e.getPlayer()
				.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
			if (s.getType().equals(Material.SPAWNER)) {

				CreatureSpawner cs = (CreatureSpawner) s.getState();

				ItemStack spawner = new ItemStack(Material.SPAWNER, 1);
				ItemMeta swmeta = spawner.getItemMeta();
				swmeta.setLocalizedName("�e" + cs.getSpawnedType().name().toLowerCase() + " �7Spawner");
				swmeta.setDisplayName(swmeta.getLocalizedName());

				e.setExpToDrop(0);
				spawner.setItemMeta(swmeta);

				s.getWorld().dropItemNaturally(s.getLocation(), spawner);

				e.getPlayer().sendMessage(lang.equals("hu") ?"�7Ki�t�tt�l egy �e" + cs.getSpawnedType().name().toLowerCase() + " �7spawnert!"
						: "�7You have broken out one �e"+ cs.getSpawnedType().name().toLowerCase()+ "�7 spawner.");
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

			if (meta.getDisplayName().contains("�7Spawner")) {
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
					spawner.setSpawnedType(EntityType.valueOf(spawnerName));
					spawner.update(true, true);
					
					event.getPlayer().sendMessage(lang.equals("hu") ?"�7Letett�l egy �e" + spawnerName.toLowerCase() + " �7spawnert!"
							: "�7You have placen one �e"+ spawnerName.toLowerCase()+ "�7 spawner.");
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
					event.getPlayer().sendMessage(lang.equals("hu") ?"�7Letett�l egy �e" + spawnerName.toLowerCase() + " �7spawnert!"
							: "�7You have placen one �e"+ spawnerName.toLowerCase()+ "�7 spawner.");
					return;
				}
			}
		

		}
	}
}


