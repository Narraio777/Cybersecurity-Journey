package com.hypixelclient.module;

import com.hypixelclient.module.api.HypixelAPIModule;
import com.hypixelclient.module.combat.AimAssistModule;
import com.hypixelclient.module.combat.AntiBotModule;
import com.hypixelclient.module.combat.AutoClickerModule;
import com.hypixelclient.module.combat.CriticalsModule;
import com.hypixelclient.module.combat.FastPlaceModule;
import com.hypixelclient.module.combat.ReachModule;
import com.hypixelclient.module.combat.TriggerBotModule;
import com.hypixelclient.module.combat.VelocityModule;
import com.hypixelclient.module.hud.*;
import com.hypixelclient.module.misc.AntiAfkModule;
import com.hypixelclient.module.misc.AutoGGModule;
import com.hypixelclient.module.movement.AutoBridgeModule;
import com.hypixelclient.module.movement.AutoSprintModule;
import com.hypixelclient.module.movement.BlockClutchModule;
import com.hypixelclient.module.movement.BunnyHopModule;
import com.hypixelclient.module.movement.SafeWalkModule;
import com.hypixelclient.module.movement.ScaffoldModule;
import com.hypixelclient.module.movement.SpeedModule;
import com.hypixelclient.module.movement.NinjaBridgeModule;
import com.hypixelclient.module.movement.SneakBridgeModule;
import com.hypixelclient.module.movement.SprintResetModule;
import com.hypixelclient.module.movement.TowerModule;
import com.hypixelclient.module.visual.CustomCrosshairModule;
import com.hypixelclient.module.visual.ESPModule;
import com.hypixelclient.module.visual.FullBrightModule;
import com.hypixelclient.module.visual.HitColorModule;
import com.hypixelclient.module.visual.NoFireModule;
import com.hypixelclient.module.visual.NoWeatherModule;
import com.hypixelclient.module.visual.ZoomModule;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // HUD
        register(new ArmorHudModule());
        register(new PotionHudModule());
        register(new KeystrokeHudModule());
        register(new CoordDisplayModule());
        register(new FPSDisplayModule());
        register(new PingDisplayModule());
        register(new CPSCounterModule());
        register(new ReachDisplayModule());
        register(new NametagsModule());
        register(new TargetHudModule());
        // Combat
        register(new AutoClickerModule());
        register(new AimAssistModule());
        register(new FastPlaceModule());
        register(new TriggerBotModule());
        register(new CriticalsModule());
        register(new VelocityModule());
        register(new ReachModule());
        register(new AntiBotModule());
        // Movement
        register(new AutoSprintModule());
        register(new AutoBridgeModule());
        register(new NinjaBridgeModule());
        register(new SneakBridgeModule());
        register(new SafeWalkModule());
        register(new SprintResetModule());
        register(new BunnyHopModule());
        register(new SpeedModule());
        register(new TowerModule());
        register(new ScaffoldModule());
        register(new BlockClutchModule());
        // Visual
        register(new FullBrightModule());
        register(new CustomCrosshairModule());
        register(new NoWeatherModule());
        register(new ESPModule());
        register(new ZoomModule());
        register(new NoFireModule());
        register(new HitColorModule());
        // Misc
        register(new AntiAfkModule());
        register(new AutoGGModule());
        // API
        register(new HypixelAPIModule());
    }

    public void register(Module module) { modules.add(module); }
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
