package com.hypixelclient.module;

import org.lwjgl.glfw.GLFW;

public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private int keybind;

    public Module(String name, String description, Category category, int defaultKey) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keybind = defaultKey;
        this.enabled = false;
    }

    public Module(String name, String description, Category category) {
        this(name, description, category, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onEnable() {}
    public void onDisable() {}

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKeybind() { return keybind; }
    public void setKeybind(int key) { this.keybind = key; }
}
