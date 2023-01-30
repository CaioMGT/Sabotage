package com.caiomgt.sabotage;

import com.caiomgt.sabotage.events.PlayerChat;
import com.caiomgt.sabotage.events.PlayerJoin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sabotage extends JavaPlugin {
    Server server = getServer();
    ConsoleCommandSender console = server.getConsoleSender();
    PluginManager manager = server.getPluginManager();
    public teams teams = new teams();
    ProtocolManager protocolManager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        teams.create();
        console.sendMessage("[SABOTAGE] Starting Sabotage");
        // Register Events
        manager.registerEvents(new PlayerJoin(), this);
        manager.registerEvents(new PlayerChat(this, teams), this);

        // Manage Tablist Packet
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER) {
            @Override
            public void onPacketReceiving(PacketEvent event){
                console.sendMessage("Received packet: " + event.getPacket().getStringArrays().toString());
            }
        });
    }

    @Override
    public void onDisable() {
        // Clean up teams
        console.sendMessage("[SABOTAGE] Stopping Sabotage");
        teams.cleanup();
    }
}
