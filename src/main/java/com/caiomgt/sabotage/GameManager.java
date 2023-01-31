package com.caiomgt.sabotage;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.List;

public class GameManager {
    public boolean gameStarted = false;
    public JavaPlugin plugin;
    //Player lists
    public List<Player> sabs;
    public List<Player> innos;
    public List<Player> dets;
    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public boolean Start(World world) {
        if (world.getPlayerCount() >= 2) {
            List<Player> plrs = world.getPlayers();
            //remove force-picked players from generating roles
            plrs.removeAll(sabs);
            plrs.removeAll(innos);
            plrs.removeAll(dets);
            Iterator<Player> iter = plrs.iterator();
            int sabCount = plrs.size() / 3;
            while (iter.hasNext()) {
                Player plr = iter.next();
                //how should I pick roles?
            }
            gameStarted = true;
            return true;
        }
        return false;
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
        if (plr.getWorld().getPlayerCount() <= 6) {
            return false;
        }
        dets.add(plr);
        return true;
    }
}
