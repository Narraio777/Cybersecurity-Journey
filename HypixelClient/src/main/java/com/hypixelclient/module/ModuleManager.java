package com.hypixelclient.module;

import com.hypixelclient.module.api.HypixelAPIModule;
import com.hypixelclient.module.hud.ArmorHudModule;
import com.hypixelclient.module.hud.KeystrokeHudModule;
import com.hypixelclient.module.hud.PotionHudModule;
import com.hypixelclient.module.movement.AutoBridgeModule;
import com.hypixelclient.module.movement.SafeWalkModule;
import com.hypixelclient.module.visual.CustomCrosshairModule;
import com.hypixelclient.module.visual.FullBrightModule;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        register(new ArmorHudModule());
        register(new PotionHudModule());
        register(new KeystrokeHudModule());
        register(new HypixelAPIModule());
        register(new AutoBridgeModule());
        register(new SafeWalkModule());
        register(new FullBrightModule());
        register(new CustomCrosshairModule());
    }

    public void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() { return modules; }

    public List<Module> getByCategory(Category category) {
        return modules.stream().filter(m -> m.getCategory() == category).toList();
    }

    public Optional<Module> getByName(String name) {
        return modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst();
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> clazz) {
        return (T) modules.stream().filter(m -> m.getClass() == clazz).findFirst().orElse(null);
    }
}
