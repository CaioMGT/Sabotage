package com.caiomgt.sabotage.events;


import com.caiomgt.sabotage.Data;
import com.caiomgt.sabotage.SaveManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

public class PlayerJoin implements Listener {
    Plugin plugin;
    SaveManager saveManager;
    public PlayerJoin(Plugin plugin, SaveManager sm) {
        this.plugin = plugin;
        saveManager = sm;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Data data = saveManager.load(event.getPlayer().getUniqueId());
        if (!data.success) {
            event.getPlayer().kickPlayer("Could not get data, sorry.");
        } else {
            event.getPlayer().setLevel(data.karma);
        }
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player plr = event.getPlayer();
        Team plrTeam = plr.getScoreboard().getPlayerTeam(plr);
        if (!(plrTeam == null)) {
            plr.getScoreboard().getPlayerTeam(plr).removePlayer(plr);
        }
        plr.getScoreboardTags().clear();
        saveManager.save(event.getPlayer().getUniqueId());
    }
}
