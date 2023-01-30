package com.caiomgt.sabotage;

import com.caiomgt.sabotage.events.PlayerChat;
import com.caiomgt.sabotage.events.PlayerJoin;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sabotage extends JavaPlugin {
    Server server = getServer();
    ConsoleCommandSender console = server.getConsoleSender();
    PluginManager manager = server.getPluginManager();
    public teams teams = new teams();
    @Override
    public void onEnable() {
        // Plugin startup logic
        teams.create();
        console.sendMessage("[SABOTAGE] Starting Sabotage");
        manager.registerEvents(new PlayerJoin(), this);
        manager.registerEvents(new PlayerChat(this, teams), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        console.sendMessage("[SABOTAGE] Stopping Sabotage");
    }
}
