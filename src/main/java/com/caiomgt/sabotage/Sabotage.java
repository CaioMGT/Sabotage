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
    public GameManager GameManager = new GameManager(this, teams);
    @Override
    public void onEnable() {
        // Plugin startup logic
        teams.create();
        console.sendMessage("[SABOTAGE] Starting Sabotage");
        // Register Events
        CommandHandler exec = new CommandHandler(this, GameManager);
        getCommand("saboteur").setExecutor(exec);
        getCommand("innocent").setExecutor(exec);
        getCommand("detective").setExecutor(exec);
        getCommand("forcestart").setExecutor(exec);
        manager.registerEvents(new PlayerJoin(), this);
        manager.registerEvents(new PlayerChat(this, teams, GameManager), this);
    }

    @Override
    public void onDisable() {
        // Clean up teams
        console.sendMessage("[SABOTAGE] Stopping Sabotage");
        GameManager.cleanup();
    }
}
