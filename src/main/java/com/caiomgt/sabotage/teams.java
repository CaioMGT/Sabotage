package com.caiomgt.sabotage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class teams {
    public Team sabs;
    public Team dets;
    public Team innos;
    public Objective sidebar;
    public void create(){
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        sidebar = board.registerNewObjective("side", "dummy");
        if (!(board.getTeam("Saboteurs") == null)) {
            sabs = board.getTeam("Saboteurs");
            dets = board.getTeam("Detectives");
            innos = board.getTeam("Innocents");
        } else {
            sabs = board.registerNewTeam("Saboteurs");
            dets = board.registerNewTeam("Detectives");
            innos = board.registerNewTeam("Innocents");
            dets.setColor(ChatColor.BLUE);
        }
    }
    public void cleanup(){
        sabs.unregister();
        dets.unregister();
        innos.unregister();
        sidebar.unregister();
    }
}
