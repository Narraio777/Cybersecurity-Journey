package com.hypixelclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path configPath;
    private ConfigData data;

    public Config() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve("hypixelclient.json");
        load();
    }

    public void load() {
        if (configPath.toFile().exists()) {
            try (Reader reader = new FileReader(configPath.toFile())) {
                data = GSON.fromJson(reader, ConfigData.class);
            } catch (IOException e) {
                data = new ConfigData();
            }
        } else {
            data = new ConfigData();
        }
        if (data == null) data = new ConfigData();
    }

    public void save() {
        try (Writer writer = new FileWriter(configPath.toFile())) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigData getData() { return data; }

    public static class ConfigData {
        public String hypixelApiKey = "";
        public boolean armorHudEnabled = true;
        public boolean potionHudEnabled = true;
        public boolean keystrokeHudEnabled = true;
        public boolean fullBrightEnabled = false;
        public boolean customCrosshairEnabled = false;
        public boolean autoBridgeEnabled = false;
        public boolean safeWalkEnabled = false;
        public boolean statsDisplayEnabled = false;
    }
}
