package com.caiomgt.sabotage.events;

import com.caiomgt.sabotage.teams;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

import java.util.Iterator;
import java.util.Set;


public class PlayerChat implements Listener {
    Plugin plugin;
    teams Teams;
    public PlayerChat(Plugin plugin, teams teams){
        this.plugin = plugin;
        this.Teams = teams;
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player plr = event.getPlayer();
        Team plrTeam = plr.getScoreboard().getEntityTeam(plr);
        if (plrTeam.equals(Teams.dets)) {
            event.setFormat(ChatColor.BLUE + "<%s> " + ChatColor.RESET + "%s");
        } else {
            Set<OfflinePlayer> sabs = Teams.sabs.getPlayers();
            event.setFormat(ChatColor.YELLOW + "<%s> " + ChatColor.RESET + "%s");
            event.getRecipients().removeAll(sabs);
            Iterator<OfflinePlayer> iter = sabs.iterator();
            while (iter.hasNext()) {
                ChatColor color;
                if (plrTeam.equals(Teams.innos)){
                    color = ChatColor.GREEN;
                } else if(plrTeam.equals(Teams.sabs)) {
                    color = ChatColor.RED;
                } else {
                    color = ChatColor.YELLOW;
                }
                iter.next().getPlayer().sendMessage(color + "<" + plr.getName() + "> " + ChatColor.RESET + event.getMessage());
            }
        }
    }
}
