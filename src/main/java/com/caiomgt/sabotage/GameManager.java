package com.caiomgt.sabotage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.*;

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
            server.getConsoleSender().sendMessage("Starting game");
            gracePeriod = true;
            gameStarted = true;
            server.broadcast(Component.text("The game has started! You have 30 seconds to collect items, gear, or hide. Your roles will be selected after the grace period.", NamedTextColor.YELLOW));
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
                        // Detective
                        plr.sendMessage(Component.text().content("You are a ")
                                        .append(Component.text("Detective.", NamedTextColor.BLUE))
                                        .appendNewline()
                                        .append(Component.text("Test suspicious people with your shears to detect if they're a Saboteur and help your fellow "))
                                        .append(Component.text("Innocents ", NamedTextColor.GREEN))
                                        .append(Component.text("to kill all "))
                                        .append(Component.text("Saboteurs.", NamedTextColor.RED))
                                .build()
                        );
                        plr.showTitle(Title.title(Component.text("You are a Detective", NamedTextColor.BLUE), Component.empty()));
                        plr.playNote(plr.getLocation(), Instrument.PLING, Note.sharp(1, Note.Tone.C));
                        scheduler.runTaskLater(plugin, () -> {
                            plr.playNote(plr.getLocation(), Instrument.PLING, Note.sharp(1, Note.Tone.E));
                        }, 10);
                        detCount--;
                        AddDet(plr);
                    } else if (sabCount >= 1) {
                        // Saboteur
                        plr.sendMessage(Component.text().content("You are a ")
                                        .append(Component.text("Saboteur.", NamedTextColor.RED))
                                        .appendNewline()
                                        .append(Component.text("Kill all "))
                                        .append(Component.text("Innocents ", NamedTextColor.GREEN))
                                        .append(Component.text("and "))
                                        .append(Component.text("Detectives ", NamedTextColor.BLUE))
                                        .append(Component.text("to win. Work as a team with your fellow "))
                                        .append(Component.text("Saboteurs ", NamedTextColor.RED))
                                        .append(Component.text("to arrange traps and kill without being caught"))
                                .build()
                        );
                        plr.showTitle(Title.title(Component.text("You are a Saboteur", NamedTextColor.RED), Component.empty()));
                        plr.playNote(plr.getLocation(), Instrument.PLING, Note.sharp(1, Note.Tone.C));
                        scheduler.runTaskLater(plugin, () -> {
                            plr.playNote(plr.getLocation(), Instrument.PLING, Note.sharp(0, Note.Tone.B));
                        }, 10);
                        sabCount--;
                        AddSab(plr);
                    } else {
                        // Innocent
                        plr.sendMessage(Component.text().content("You are a ")
                                        .append(Component.text("Innocent", NamedTextColor.GREEN))
                                        .appendNewline()
                                        .append(Component.text("Figure out who the "))
                                        .append(Component.text("Saboteurs ", NamedTextColor.RED))
                                        .append(Component.text("are to win. Work with the "))
                                        .append(Component.text("Detective(s) ", NamedTextColor.BLUE))
                                        .append(Component.text("to rule out "))
                                        .append(Component.text("Innocents ", NamedTextColor.GREEN))
                                        .append(Component.text("and eliminate all "))
                                        .append(Component.text("Saboteurs.", NamedTextColor.RED))
                                .build()
                        );
                        plr.showTitle(Title.title(Component.text("You are an Innocent", NamedTextColor.GREEN), Component.empty()));
                        plr.playNote(plr.getLocation(), Instrument.PLING, Note.sharp(1, Note.Tone.C));
                        AddInno(plr);
                    }
                }
                String detectives = getPlayerNamesInList(getPlayersInTeam(teams.dets));
                String saboteurs = getPlayerNamesInList(getPlayersInTeam(teams.sabs));
                if (teams.dets.getSize() == 0) {
                    for (Player plr : getPlayersInTeam(teams.sabs)) {
                        plr.sendMessage(Component.text().content("The ").append(Component.text("Saboteurs ", NamedTextColor.RED)).append(Component.text("are ")).append(Component.text(saboteurs, NamedTextColor.RED)).build());
                    }
                }
                else {
                    for (Player plr : getPlayersInTeam(teams.dets)) {
                        plr.sendMessage(Component.text().content("The ").append(Component.text("Detectives ", NamedTextColor.BLUE)).append(Component.text("are ")).append(Component.text(detectives, NamedTextColor.BLUE)).build());
                    }
                    for (Player plr : getPlayersInTeam(teams.innos)) {
                        plr.sendMessage(Component.text().content("The ").append(Component.text("Detectives ", NamedTextColor.BLUE)).append(Component.text("are ")).append(Component.text(detectives, NamedTextColor.BLUE)).build());
                    }
                    for (Player plr : getPlayersInTeam(teams.sabs)) {
                        plr.sendMessage(Component.text().content("The ").append(Component.text("Saboteurs ", NamedTextColor.RED)).append(Component.text("are ")).append(Component.text(saboteurs, NamedTextColor.RED)).build());
                        plr.sendMessage(Component.text().content("The ").append(Component.text("Detectives ", NamedTextColor.BLUE)).append(Component.text("are ")).append(Component.text(detectives, NamedTextColor.BLUE)).build());
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
    String getPlayerNamesInList(Set<Player> list){
        String result = "";
        for (Player plr : list){
            result = result + plr.getName() + " ";
        }
        return result;
    }
    public Set<Player> getPlayersInTeam(Team team) {
        Set<Player> plrs = new HashSet<>();
        for (String plr : team.getEntries()) {
            plrs.add(server.getPlayer(plr));
        }
        return plrs;
    }
    public void End(World world) {
        EndType endType = checkEnd();
        if (!(endType == EndType.NONE) && gameStarted) {
            server.getConsoleSender().sendMessage("Ending game");
            server.broadcast(Component.text().content("The game has ended! The following players were the saboteurs: ")
                    .append(Component.text(getPlayerNamesInList(sabs), NamedTextColor.RED))
                    .build());
            if (endType == EndType.INNOCENTS) {
                // Award karma to surviving innocents
                server.broadcast(Component.text().content("Awarding 20 Karma to surviving ")
                        .append(Component.text("Innocents ", NamedTextColor.GREEN)
                                .append(Component.text("and 50 Karma to surviving ", NamedTextColor.WHITE)
                                        .append(Component.text("Detectives", NamedTextColor.BLUE))))
                        .build());
                for (Player plr : getPlayersInTeam(teams.innos)) {
                    saves.addKarma(plr, 20);
                }
                for (Player plr : getPlayersInTeam(teams.dets)) {
                    saves.addKarma(plr, 50);
                }
            } else {
                // Award karma to surviving saboteurs
                server.broadcast(Component.text().content("Awarding 20 Karma to surviving ").append(Component.text("Saboteurs", NamedTextColor.RED)).build());
                for (Player plr : getPlayersInTeam(teams.sabs)) {
                    saves.addKarma(plr, 20);
                }
            }
            // Remove all players from teams.
            for (Player plr : getPlayersInTeam(teams.innos)) {
                teams.innos.removePlayer(plr);
            }
            for (Player plr : getPlayersInTeam(teams.dets)) {
                teams.dets.removePlayer(plr);
            }
            for (Player plr : getPlayersInTeam(teams.sabs)) {
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
