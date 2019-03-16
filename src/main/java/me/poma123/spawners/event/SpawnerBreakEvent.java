package me.poma123.spawners.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class SpawnerBreakEvent extends Event implements Cancellable {
    public static HandlerList handlerList = new HandlerList();
    private ItemStack breakerItemStack;
    private Block spawner;
    private Player player;
    private boolean cancelled;


    public SpawnerBreakEvent(Player player, Block b, ItemStack breakerItemStack) {
        this.spawner = b;
        this.player = player;
        this.breakerItemStack = breakerItemStack;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    public void setCancelled(boolean paramBoolean) {
        this.cancelled = paramBoolean;
    }

    /**
     * @return Player who broke the spawner
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }


    /**
     * @return Block what the player has broken
     */
    public Block getSpawner() {
        return spawner;
    }


    /**
     * @return ItemStack what the player has broken with
     */
    public ItemStack getBreakerItem() {
        return breakerItemStack;
    }
}
