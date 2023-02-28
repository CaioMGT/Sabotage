package com.caiomgt.sabotage.events;

import com.caiomgt.sabotage.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

public class PlayerDamage implements Listener {
    Plugin plugin;
    GameManager manager;
    public PlayerDamage(Plugin plugin, GameManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }
    @EventHandler
    public void OnDamage(EntityDamageEvent event) {
        event.setCancelled(manager.gracePeriod);
    }
}
