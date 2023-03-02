package com.caiomgt.sabotage.events;

import com.caiomgt.sabotage.GameManager;
import com.caiomgt.sabotage.teams;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        Player plr = event.getPlayer();
        Team plrTeam = plr.getScoreboard().getEntityTeam(plr);
        event.renderer(new ChatRenderer() {
            @Override
            public @NotNull Component render(@NotNull Player player, @NotNull Component component, @NotNull Component message, @NotNull Audience audience) {
                NamedTextColor colorText;
                if (manager.gameStarted && !manager.gracePeriod) {
                    if (audience instanceof Player) {
                        Player audiencePlr = (Player) audience;
                        if (audiencePlr.getScoreboard().getEntityTeam(audiencePlr).equals(Teams.sabs)) {
                            if (plrTeam.equals(Teams.sabs)) {
                                colorText = NamedTextColor.RED;
                            } else if (plrTeam.equals(Teams.dets)) {
                                colorText = NamedTextColor.DARK_BLUE;
                            } else {
                                colorText = NamedTextColor.GREEN;
                            }
                        } else {
                            colorText = NamedTextColor.YELLOW;
                        }
                        plugin.getServer().getConsoleSender().sendMessage(audiencePlr.getName() + " has received message with color " + colorText + " with sender team " + plrTeam.getName() + ", receiver team is " + audiencePlr.getScoreboard().getEntityTeam(audiencePlr).getName());
                        return Component.text()
                                .color(colorText)
                                .content("<" + player.getName() + "> ")
                                .append(event.message().color(NamedTextColor.WHITE))
                                .build();
                    }
                }
                return Component.text()
                        .content("<" + player.getName() + "> ")
                        .append(event.message())
                        .build();
            }
        });
    }
}
