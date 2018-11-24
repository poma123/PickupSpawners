package me.poma123.spawners;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultAPI {

    private Plugin plugin;

    private Permission permission = null;
    private Economy economy = null;
    private Chat chat = null;

    public VaultAPI(Plugin plugin) {
        this.plugin = plugin;
        if (!setupEconomy()) {
            plugin.getPluginLoader().disablePlugin(plugin);
            plugin.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", plugin.getDescription().getName()));
        }
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public Economy getEconomy() {
        return economy;
    }

    public Chat getChat() {
        return chat;
    }

    public Permission getPermission() {
        return permission;
    }
}
