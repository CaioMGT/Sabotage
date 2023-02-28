package com.caiomgt.sabotage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameManager {
    public boolean gameStarted = false;
    public boolean gracePeriod = false;
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
            gracePeriod = true;
            gameStarted = true;
            server.broadcastMessage("The game has started! You have 30 seconds to collect items, gear, or hide. Your roles will be selected after the grace period.");
            Objective sidebar = teams.sidebar;
            sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
            BukkitScheduler scheduler = Bukkit.getScheduler();
            sidebar.getScore("Time left: 30").setScore(1);
            // Timer
            for (int i = 1; i <= 30; i++) {
                final int j = i;
                scheduler.runTaskLater(plugin, () -> {
                    sidebar.getScore("Time left: " + j).setScore(1);
                    sidebar.getScore("Time left: " + (j + 1)).resetScore();
                }, 20 * (30 - i));
            }
            // Only pick roles after the grace period
            scheduler.runTaskLater(plugin, () -> {
                gracePeriod = false;
                sidebar.getScore("Time left: 1").resetScore();
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
            }, 20 * 30);
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
                for (String plr : teams.innos.getEntries()) {
                    saves.addKarma(server.getPlayer(plr), 20);
                }
                for (String plr : teams.dets.getEntries()) {
                    saves.addKarma(server.getPlayer(plr), 50);
                }
            } else {
                // Award karma to surviving saboteurs
                server.broadcastMessage("Awarding 20 Karma to surviving " + ChatColor.RED + "Saboteurs");
                for (String plr : teams.sabs.getEntries()) {
                    saves.addKarma(server.getPlayer(plr), 20);
                }
            }
            // Remove all players from teams.
            for (String plr : teams.innos.getEntries()) {
                teams.innos.removePlayer(server.getPlayer(plr));
            }
            for (String plr : teams.dets.getEntries()) {
                teams.dets.removePlayer(server.getPlayer(plr));
            }
            for (String plr : teams.sabs.getEntries()) {
                teams.sabs.removePlayer(server.getPlayer(plr));
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
