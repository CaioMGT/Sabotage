package com.caiomgt.sabotage.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player plr = event.getPlayer();
        Team plrTeam = plr.getScoreboard().getPlayerTeam(plr);
        if (!(plrTeam == null)) {
            plr.getScoreboard().getPlayerTeam(plr).removePlayer(plr);
        }
        plr.getScoreboardTags().clear();
    }
}
