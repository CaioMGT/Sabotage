package com.caiomgt.sabotage;

import com.google.gson.Gson;
import org.bukkit.plugin.Plugin;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

class Data {
    public UUID uuid;
    public int karma;

}
public class SaveManager {
    Plugin plugin;
    Gson gson = new Gson();

    ArrayList<Data> plrData = new ArrayList<>();

    public SaveManager(Plugin plugin) {
        this.plugin = plugin;
    }
    public boolean save(UUID uuid) {
        return save(uuid, load(uuid));
    }
    public boolean save(UUID uuid, Data data) {
        try {
            gson.toJson(data, new FileWriter(plugin.getDataFolder().getPath() + "\\sabData\\" + uuid));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void setKarma(UUID uuid, int karma) {
        load(uuid).karma = karma;
    }
    public int getKarma(UUID uuid) {
        return load(uuid).karma;
    }
    public void addKarma(UUID uuid, int karma) {
        setKarma(uuid, getKarma(uuid) + karma);
    }
    public Data load(UUID uuid) {
        for (Data data : plrData) {
            if (data.uuid == uuid) {
                return data;
            }
        }
        Data data = gson.fromJson(plugin.getDataFolder().getPath() + "\\sabData\\" + uuid, Data.class);
        plrData.add(data);
        return data;
    }
}
