package com.caiomgt.sabotage.events;


import com.caiomgt.sabotage.Data;
import com.caiomgt.sabotage.EndType;
import com.caiomgt.sabotage.GameManager;
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
    GameManager gameManager;
    public PlayerJoin(Plugin plugin, GameManager gm, SaveManager sm) {
        this.plugin = plugin;
        this.saveManager = sm;
        this.gameManager = gm;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Data data = saveManager.load(event.getPlayer());
        if (!data.success) {
            event.getPlayer().kickPlayer("Could not get data, sorry.");
        } else {
            event.getPlayer().setLevel(Math.max(data.karma, 0));
        }
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player plr = event.getPlayer();
        Team plrTeam = plr.getScoreboard().getPlayerTeam(plr);
        if (!(plrTeam == null)) {
            plr.getScoreboard().getPlayerTeam(plr).removePlayer(plr);
            // Check if the game will end after player leaves
            if (gameManager.checkEnd() != EndType.NONE) {
                gameManager.End(plr.getWorld());
            }
        }
        plr.getScoreboardTags().clear();
        saveManager.saveAndUnload(plr);
    }
}
