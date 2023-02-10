package com.caiomgt.sabotage.events;

import com.caiomgt.sabotage.GameManager;
import com.caiomgt.sabotage.SaveManager;
import com.caiomgt.sabotage.teams;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

public class PlayerDie implements Listener {
    Plugin plugin;
    teams teams;
    GameManager manager;
    SaveManager saves;
    public PlayerDie(Plugin plugin, teams teams, GameManager manager, SaveManager saves) {
        this.plugin = plugin;
        this.teams = teams;
        this.manager = manager;
        this.saves = saves;
    }
    @EventHandler
    public void onDie(PlayerDeathEvent event) {
        //TODO: USE KARMA SYSTEM
        //TODO: WIN / LOSE CONDITION
        Player plr = event.getPlayer();
        EntityDamageEvent.DamageCause cause = plr.getLastDamageCause().getCause();
        Team team = plr.getScoreboard().getPlayerTeam(plr);
        if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            Player killer = plr.getKiller();
            Bukkit.getServer().getConsoleSender().sendMessage(killer.getName() + " killed someone");
            if (killer != null) {
                Team killerTeam = killer.getScoreboard().getPlayerTeam(killer);
                if (killerTeam.equals(teams.sabs)) {
                    if (team.equals(teams.dets)) {
                        //is det, award more karma
                        saves.addKarma(killer, 100);
                    } else if (team.equals(teams.innos)) {
                        //is inno, award karma
                        saves.addKarma(killer, 20);
                    } else {
                        //is sab, remove karma
                        saves.addKarma(killer, -100);
                    }
                } else {
                    //is not sab
                    if (team.equals(teams.sabs)) {
                        //award karma
                        saves.addKarma(killer, 40);
                    } else if (team.equals(teams.dets)) {
                        //is det, kill killer
                        killer.setHealth(0);
                    } else {
                        //is inno, remove karma
                        saves.addKarma(killer, -20);
                    }
                }
                plugin.getServer().getConsoleSender().sendMessage(plr.getName() + " was killed by " + killer.getName());
            }
        }
        if (!(team == null)) {
            team.removePlayer(plr);
        }
        int innos = this.teams.innos.getEntries().size();
        int dets = this.teams.dets.getEntries().size();
        int sabs = this.teams.dets.getEntries().size();
        if (innos < 1 && dets < 1) {
            //sabs win
            Bukkit.getServer().getConsoleSender().sendMessage("sabs win");
        } else if (sabs < 1) {
            //innos win
            Bukkit.getServer().getConsoleSender().sendMessage("innos win");
        } else {
            Bukkit.getServer().broadcastMessage(plr.getName() + " has died. " + this.teams.innos.getEntries().size() + " players remain");
        }
        plr.setGameMode(GameMode.SPECTATOR);
        event.setCancelled(true);
    }
}
