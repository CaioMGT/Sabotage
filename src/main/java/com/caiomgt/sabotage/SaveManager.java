package com.caiomgt.sabotage;

import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SaveManager {
    Plugin plugin;
    Gson gson = new Gson();
    final Path dataFolder;

    public final Map<UUID, Data> plrData = new HashMap<>();

    public SaveManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder().toPath().resolve("sabData");
        try {
            Files.createDirectories(dataFolder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    public void saveAndUnload(Player plr) {
        UUID uuid = plr.getUniqueId();
        Data data = plrData.remove(uuid);
        if (data == null) return; // Shouldn't happen
        try {
            Path tmp = Files.createTempFile(dataFolder, uuid.toString(), ".tmp");
            try (BufferedWriter w = Files.newBufferedWriter(tmp)) {
                gson.toJson(data, w);
            } catch (IOException e) {
                try {
                    Files.delete(tmp);
                } catch (IOException e0) {
                    //noop
                }
                throw new UncheckedIOException(e);
            }
            Files.move(tmp, dataFolder.resolve(uuid.toString()), StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    public void setKarma(Player plr, int karma) {
        UUID uuid = plr.getUniqueId();
        plrData.get(uuid).karma = karma;
        plr.setLevel(Math.min(karma, 0));
    }
    public int getKarma(Player plr) {
        UUID uuid = plr.getUniqueId();
        return plrData.get(uuid).karma;
    }
    public void addKarma(Player plr, int karma) {
        setKarma(plr, getKarma(plr) + karma);
    }
    public Data load(Player plr) {
        UUID uuid = plr.getUniqueId();
        return plrData.computeIfAbsent(uuid, u -> {
            Path f = dataFolder.resolve(uuid.toString());
            Data d = null;
            if (Files.exists(f)) {
                try (BufferedReader r = Files.newBufferedReader(f)) {
                    d = gson.fromJson(r, Data.class);
                } catch (IOException e) {
                    //Data err = new Data();
                    //err.success = false;
                    //return err;
                    throw new UncheckedIOException(e); // Old code is above if you don't want an exception
                }
            }
            return d != null ? d : new Data(uuid, 1000);
        });
    }
}
