package com.caiomgt.sabotage;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
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
    Server server;
    public GameManager(JavaPlugin plugin, teams teams, SaveManager saves) {
        this.plugin = plugin;
        this.teams = teams;
        this.saves = saves;
        this.server = plugin.getServer();
    }
    public boolean Start(World world) {
        if (world.getPlayerCount() >= 2) {
            List<Player> plrs = world.getPlayers();
            // Remove force-picked players from generating roles
            plrs.removeAll(sabs);
            plrs.removeAll(innos);
            plrs.removeAll(dets);
            Collections.shuffle(plrs);
            int sabCount = plrs.size() / 3;
            if (sabCount < 1 && sabs.size() < 1) {
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
    public EndType checkEnd() {
        if (teams.sabs.getSize() < 1) {
            return EndType.INNOCENTS;
        } else if (teams.innos.getSize() < 1 && teams.dets.getSize() < 1) {
            return EndType.SABOTEURS;
        }
        return EndType.NONE;
    }
    String getPlayerNamesInList(List<Player> list){
        String result = "";
        for (Player plr : list){
            result = result + plr.getName() + " ";
        }
        return result;
    }
    public void End(World world) {
        EndType endType = checkEnd();
        if (!(endType == EndType.NONE) && gameStarted) {
            server.broadcastMessage("The game has ended! The following players were the saboteurs: " + ChatColor.RED + getPlayerNamesInList(sabs));
            if (endType == EndType.INNOCENTS) {
                // Award karma to surviving innocents
                server.broadcastMessage("Awarding 20 Karma to surviving " + ChatColor.GREEN + "Innocents" + ChatColor.RESET + "and 50 Karma to surviving " + ChatColor.BLUE + "Detectives");
                for (OfflinePlayer plr : teams.innos.getPlayers()) {
                    saves.addKarma((Player) plr, 20);
                }
                for (OfflinePlayer plr : teams.dets.getPlayers()) {
                    saves.addKarma((Player) plr, 50);
                }
            } else {
                // Award karma to surviving saboteurs
                server.broadcastMessage("Awarding 20 Karma to surviving " + ChatColor.RED + "Saboteurs");
                for (OfflinePlayer plr : teams.sabs.getPlayers()) {
                    saves.addKarma((Player) plr, 20);
                }
            }
            // Remove all players from teams.
            for (OfflinePlayer plr : teams.innos.getPlayers()) {
                teams.innos.removePlayer(plr);
            }
            for (OfflinePlayer plr : teams.dets.getPlayers()) {
                teams.dets.removePlayer(plr);
            }
            for (OfflinePlayer plr : teams.sabs.getPlayers()) {
                teams.sabs.removePlayer(plr);
            }
            sabs.clear();
            dets.clear();
            innos.clear();
            gameStarted = false;
        }
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
        for (Player plr : server.getOnlinePlayers()) {
            saves.saveAndUnload(plr);
        }
    }
}
