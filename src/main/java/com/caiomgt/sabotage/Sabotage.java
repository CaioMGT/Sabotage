package com.caiomgt.sabotage;

import com.caiomgt.sabotage.events.PlayerChat;
import com.caiomgt.sabotage.events.PlayerDie;
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
    public SaveManager SaveManager = new SaveManager(this);
    public GameManager GameManager = new GameManager(this, teams, SaveManager);

    @Override
    public void onEnable() {
        // Plugin startup logic
        teams.create();
        console.sendMessage("[SABOTAGE] Starting Sabotage");
        // Register Events
        manager.registerEvents(new PlayerJoin(this, SaveManager), this);
        manager.registerEvents(new PlayerChat(this, teams, GameManager), this);
        manager.registerEvents(new PlayerDie(this, teams, GameManager, SaveManager), this);
        // Register Commands
        CommandHandler exec = new CommandHandler(this, GameManager, SaveManager);
        getCommand("saboteur").setExecutor(exec);
        getCommand("innocent").setExecutor(exec);
        getCommand("detective").setExecutor(exec);
        getCommand("forcestart").setExecutor(exec);
        getCommand("getkarma").setExecutor(exec);
        getCommand("setkarma").setExecutor(exec);
    }

    @Override
    public void onDisable() {
        // Clean up teams
        console.sendMessage("[SABOTAGE] Stopping Sabotage");
        GameManager.cleanup();
    }
}
