package me.poma123.spawners.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpawnerPlaceEvent extends Event implements Cancellable {
    public static HandlerList handlerList = new HandlerList();
    private String spawnedType;
    private Player player;
    private boolean cancelled;


    public SpawnerPlaceEvent(Player player, String type) {
        this.spawnedType = type;
        this.player = player;
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
     * @return Player who placed the spawner
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    /**
     * @return String entitytype what the spawner will spawn
     */
    public String getSpawnedType() {
        return spawnedType;
    }

}
