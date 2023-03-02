package com.caiomgt.sabotage.events;

import com.caiomgt.sabotage.GameManager;
import com.caiomgt.sabotage.teams;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;


import java.util.Set;


public class PlayerChat implements Listener {
    Plugin plugin;
    teams Teams;
    GameManager manager;
    public PlayerChat(Plugin plugin, teams teams, GameManager manager){
        this.plugin = plugin;
        this.Teams = teams;
        this.manager = manager;
    }
    @EventHandler
    public void onChat(AsyncChatEvent event) {
        // I'll leave this for a different pr, requires some restructuring to transition to components
        Player plr = event.getPlayer();
        Team plrTeam = plr.getScoreboard().getEntityTeam(plr);
        event.renderer(new ChatRenderer() {
            @Override
            public @NotNull Component render(@NotNull Player player, @NotNull Component component, @NotNull Component message, @NotNull Audience audience) {
                if (audience.getClass() == Player.class) {
                    Player audiencePlr = (Player) audience;
                    if (audiencePlr.getScoreboard().getEntityTeam(audiencePlr) == Teams.sabs) {
                        event.message().
                    }
                }
                return message;
            }
        });
        if (manager.gameStarted && !manager.gracePeriod){
            if (!(plrTeam == null) && plrTeam.equals(Teams.dets)) {
                event.setFormat(ChatColor.BLUE + "<%s> " + ChatColor.RESET + "%s");
            } else {
                Set<Player> sabs = manager.getPlayersInTeam(Teams.sabs);
                event.setFormat(ChatColor.YELLOW + "<%s> " + ChatColor.RESET + "%s");
                event.getRecipients().removeAll(sabs);
                for (Player sab : sabs) {
                    ChatColor color;
                    if (plrTeam.equals(Teams.innos)){
                        color = ChatColor.GREEN;
                    } else if(plrTeam.equals(Teams.sabs)) {
                        color = ChatColor.RED;
                    } else {
                        color = ChatColor.YELLOW;
                    }
                    sab.getPlayer().sendMessage(color + "<" + plr.getName() + "> " + ChatColor.RESET + event.getMessage());
                }
            }
        }
    }
}
