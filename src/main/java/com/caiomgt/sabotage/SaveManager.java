package com.caiomgt.sabotage;

import com.google.gson.Gson;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;


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
        Data data;
        File folder = plugin.getDataFolder();
        if (!folder.exists()) {
            folder.mkdir();
        }
        File datafolder = new File(folder.getPath() + "\\sabData\\");
        if (!datafolder.exists()) {
            datafolder.mkdir();
        }
        File dataFile = new File(datafolder.getPath() + "\\" + uuid);
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getServer().getConsoleSender().sendMessage("pain 3");
                Data err = new Data();
                err.success = false;
                return err;
            }
        }
        try {
            data = gson.fromJson(new FileReader(dataFile), Data.class);
        } catch (FileNotFoundException e) {
            if (save(uuid, new Data(uuid, 1000))) {
                return load(uuid);
            }
            Data err = new Data();
            err.success = false;
            return err;
        }
        plrData.add(data);
        data.success = true;
        return data;
    }
}
