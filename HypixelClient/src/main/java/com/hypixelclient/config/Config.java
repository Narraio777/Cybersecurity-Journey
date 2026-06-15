package com.hypixelclient.config;

import com.google.gson.*;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.ModuleManager;
import com.hypixelclient.module.Setting;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path configPath;
    private ConfigData data;

    public Config() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve("hypixelclient.json");
        data = new ConfigData();
        loadBase();
    }

    // Load API key and other primitive settings (called at startup before modules exist).
    private void loadBase() {
        if (!configPath.toFile().exists()) return;
        try (Reader r = new FileReader(configPath.toFile())) {
            ConfigData loaded = GSON.fromJson(r, ConfigData.class);
            if (loaded != null) data = loaded;
        } catch (IOException ignored) {}
        if (data == null) data = new ConfigData();
    }

    // Called after ModuleManager is ready — restores module states and settings.
    public void applyToModules(ModuleManager manager) {
        if (data.modules == null) return;
        for (Module m : manager.getModules()) {
            JsonObject entry = data.modules.getAsJsonObject(m.getName());
            if (entry == null) continue;
            if (entry.has("enabled")) m.setEnabled(entry.get("enabled").getAsBoolean());
            if (entry.has("settings") && !m.getSettings().isEmpty()) {
                JsonObject settings = entry.getAsJsonObject("settings");
                for (Setting s : m.getSettings()) {
                    if (settings.has(s.getName())) {
                        double saved = settings.get(s.getName()).getAsDouble();
                        double step  = (s.getMax() - s.getMin()) / Math.max(1, (s.getMax() - s.getMin()) / s.getStep());
                        // Walk from min toward saved value to land on a valid step.
                        while (s.getValue() < saved - 0.0001) s.increment();
                        while (s.getValue() > saved + 0.0001) s.decrement();
                    }
                }
            }
        }
    }

    // Serialize all module states and settings back to disk.
    public void save(ModuleManager manager) {
        if (data.modules == null) data.modules = new JsonObject();
        for (Module m : manager.getModules()) {
            JsonObject entry = new JsonObject();
            entry.addProperty("enabled", m.isEnabled());
            if (!m.getSettings().isEmpty()) {
                JsonObject settings = new JsonObject();
                for (Setting s : m.getSettings()) settings.addProperty(s.getName(), s.getValue());
                entry.add("settings", settings);
            }
            data.modules.add(m.getName(), entry);
        }
        try (Writer w = new FileWriter(configPath.toFile())) {
            GSON.toJson(data, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigData getData() { return data; }

    public static class ConfigData {
        public String hypixelApiKey = "";
        public JsonObject modules = new JsonObject();

        // Helper getters so existing code that reads these booleans keeps compiling.
        public double getStep() { return 1; }
    }
}
