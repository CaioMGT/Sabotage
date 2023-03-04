package com.caiomgt.sabotage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
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
        sidebar = board.registerNewObjective("side", Criteria.DUMMY, Component.text("Information"));
        sabs = board.getTeam("Saboteurs");
        dets = board.getTeam("Detectives");
        innos = board.getTeam("Innocents");
        sabs = board.registerNewTeam("Saboteurs");
        dets = board.registerNewTeam("Detectives");
        innos = board.registerNewTeam("Innocents");
        dets.color(NamedTextColor.BLUE);
    }
    public void cleanup(){
        sidebar.unregister();
        sabs.unregister();
        dets.unregister();
        innos.unregister();
    }
}
