package me.poma123.spawners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.poma123.spawners.language.Language;
import me.poma123.spawners.language.Language.LocalePath;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Listener implements org.bukkit.event.Listener {
	Map<String, Integer> limit = new TreeMap<String, Integer>();
	SettingsManager sett = SettingsManager.getInstance();
	private Plugin plugin = Command.getPlugin(Command.class);
	public static int breakedSpawners = 0;
	
	private Material material = Command.material;

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

	public static TextComponent getHoverClickcmd(String message, String hover, String click) {
		TextComponent text = new TextComponent(message);
		text.setClickEvent(
				new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, click));
		text.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(hover).create()));
		return text;
	}

	@EventHandler
	public void onOpJoin(PlayerJoinEvent e) {
		if (sett.getConfig().getBoolean("update-check")) {

			Player p = e.getPlayer();
			if (p.isOp()) {

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

							e.getPlayer().sendMessage(Language.getReplacedLocale(e.getPlayer(), LocalePath.LIMIT_REACH, "%limit%", String.valueOf(limitcount)));
							//e.getPlayer().sendMessage(lang.equals("hu")? "§cElérted a napi kiüthető spawner limitet (" + limitcount + ").": "§cYou have reached the daily spawner break limit (" + limitcount + ").");
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
					isGoodItem = true;
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
					isGoodItem = true;
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
				e.getPlayer().sendMessage(Language.getReplacedLocale(e.getPlayer(), LocalePath.BREAK, "%type%", cs.getSpawnedType().name().toLowerCase()));
				
				breakedSpawners++;
			//	e.getPlayer().sendMessage(lang.equals("hu")? "§7Kiütöttél egy §e" + cs.getSpawnedType().name().toLowerCase() + " §7spawnert!"						: "§7You have broken out one §e" + cs.getSpawnedType().name().toLowerCase() + "§7 spawner.");
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

						event.getPlayer().sendMessage(Language.getReplacedLocale(event.getPlayer(), LocalePath.PLACE, "%type%", spawnerName.toLowerCase()));
						//event.getPlayer().sendMessage(lang.equals("hu")? "§7Letettél egy §e" + spawnerName.toLowerCase() + " §7spawnert!"							: "§7You have placen one §e" + spawnerName.toLowerCase() + "§7 spawner.");
					} catch (IllegalArgumentException e) {
						spawner.setSpawnedType(EntityType.valueOf("PIG"));
						spawner.update(true, true);

						event.getPlayer().sendMessage(Language.getReplacedLocale(event.getPlayer(), LocalePath.PLACE, "%type%", "pig"));
						//event.getPlayer().sendMessage(lang.equals("hu") ? "§7Letettél egy §epig §7spawnert!": "§7You have placen one §epig §7spawner.");
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
}
