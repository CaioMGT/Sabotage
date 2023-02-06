package com.caiomgt.sabotage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;


public class SaveManager {
    Plugin plugin;
    Gson gson = new Gson();

    public SaveManager(Plugin plugin) {
        this.plugin = plugin;
    }
    public boolean save(UUID uuid, Object data) {
        try {
            gson.toJson(data, new FileWriter(plugin.getDataFolder().getPath() + "\\" + uuid));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void addKarma(UUID uuid, int Karma) {
        //setKarma(uuid, );
    }
    public Object load(UUID uuid) {
        return gson.fromJson(plugin.getDataFolder().getPath() + "\\" + uuid, Object.class);
    }
}
