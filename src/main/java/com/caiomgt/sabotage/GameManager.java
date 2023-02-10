package com.caiomgt.sabotage;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameManager {
    public boolean gameStarted = false;
    public JavaPlugin plugin;
    public teams teams;
    public SaveManager saves;
    //Player lists
    public List<Player> sabs = new ArrayList<>();
    public List<Player> innos = new ArrayList<>();
    public List<Player> dets = new ArrayList<>();
    public GameManager(JavaPlugin plugin, teams teams, SaveManager saves) {
        this.plugin = plugin;
        this.teams = teams;
        this.saves = saves;
    }
    public boolean Start(World world) {
        if (world.getPlayerCount() >= 2) {
            List<Player> plrs = world.getPlayers();
            //remove force-picked players from generating roles
            plrs.removeAll(sabs);
            plrs.removeAll(innos);
            plrs.removeAll(dets);
            Collections.shuffle(plrs);
            int sabCount = plrs.size() / 3;
            if (sabCount < 1) {
                sabCount = 1;
            }
            int detCount = plrs.size() / 8;
            for (Player plr : plrs) {
                if (detCount >= 1) {
                    detCount--;
                    AddDet(plr);
                } else if (sabCount >= 1) {
                    sabCount--;
                    AddSab(plr);
                } else {
                    AddInno(plr);
                }
            }
            gameStarted = true;
            return true;
        }
        return false;
    }

    public boolean AddSab(Player plr) {
        teams.sabs.addPlayer(plr);
        sabs.add(plr);
        return true;
    }
    public void AddInno(Player plr) {
        teams.innos.addPlayer(plr);
        innos.add(plr);
    }
    public boolean AddDet(Player plr) {
        if (plr.getWorld().getPlayerCount() <= 8) {
            return false;
        }
        teams.dets.addPlayer(plr);
        dets.add(plr);
        return true;
    }
    public void cleanup() {
        teams.cleanup();
        for (Player plr : plugin.getServer().getOnlinePlayers()) {
            saves.saveAndUnload(plr);
        }
    }
}
