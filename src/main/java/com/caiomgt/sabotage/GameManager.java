package com.caiomgt.sabotage;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class GameManager {
    public boolean gameStarted = false;
    public JavaPlugin plugin;
    public List<Player> sabs;
    public List<Player> innos;
    public List<Player> dets;
    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public boolean Start() {
        gameStarted = true;
        return true;
    }

    public boolean AddSab(Player plr) {
        sabs.add(plr);
        return true;
    }
    public boolean AddInno(Player plr) {
        innos.add(plr);
        return true;
    }
    public boolean AddDet(Player plr) {
        return plr.getWorld().getPlayerCount() <= 6;
    }
}
