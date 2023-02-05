package com.caiomgt.sabotage.events;

import com.caiomgt.sabotage.GameManager;
import com.caiomgt.sabotage.teams;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
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
    public PlayerDie(Plugin plugin, teams teams, GameManager manager) {
        this.plugin = plugin;
        this.teams = teams;
        this.manager = manager;
    }
    @EventHandler
    public void onDie(PlayerDeathEvent event) {
        //TODO: USE KARMA SYSTEM
        //TODO: WIN / LOSE CONDITION
        Player plr = event.getPlayer();
        EntityDamageEvent.DamageCause cause = plr.getLastDamageCause().getCause();
        Team team = plr.getScoreboard().getPlayerTeam(plr);
        if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            LivingEntity killerEntity = plr.getKiller();
            if (killerEntity instanceof Player) {
                Player killer = (Player) killerEntity;
                Team killerTeam = killer.getScoreboard().getPlayerTeam(killer);
                if (killerTeam == teams.sabs) {
                    if (team == teams.dets) {
                        //is det, award more karma
                    } else if (team == teams.innos) {
                        //is inno, award karma
                    } else {
                        //is sab, remove karma
                    }
                } else {
                    //is not sab
                    if (team == teams.sabs) {
                        //award karma
                    } else if (team == teams.dets) {
                        //is det, kill killer
                    } else {
                        //is inno, remove karma
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

        } else if (sabs < 1) {
            //innos win

        }
        Bukkit.getServer().broadcastMessage(plr.getName() + " has died. " + this.teams.innos.getEntries().size() + " players remain");
        plr.setGameMode(GameMode.SPECTATOR);
        event.setCancelled(true);
    }
}
