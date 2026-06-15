package com.hypixelclient;

import com.hypixelclient.config.Config;
import com.hypixelclient.keybind.KeybindManager;
import com.hypixelclient.module.ModuleManager;
import com.hypixelclient.module.api.HypixelAPIModule;
import com.hypixelclient.module.hud.*;
import com.hypixelclient.module.visual.CustomCrosshairModule;
import com.hypixelclient.module.visual.HitColorModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HypixelClient implements ClientModInitializer {
    public static final String MOD_ID = "hypixelclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static HypixelClient instance;

    private ModuleManager moduleManager;
    private KeybindManager keybindManager;
    private Config config;

    @Override
    public void onInitializeClient() {
        instance = this;
        config = new Config();
        moduleManager = new ModuleManager();
        config.applyToModules(moduleManager); // restore saved states
        keybindManager = new KeybindManager(moduleManager);

        ClientTickEvents.END_CLIENT_TICK.register(client -> keybindManager.onTick(client));
        // Save config periodically (every 200 ticks = 10 seconds).
        final int[] saveTicker = {0};
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (++saveTicker[0] >= 200) { saveTicker[0] = 0; config.save(moduleManager); }
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;
            moduleManager.get(ArmorHudModule.class).render(context, client);
            moduleManager.get(PotionHudModule.class).render(context, client);
            moduleManager.get(KeystrokeHudModule.class).render(context, client);
            moduleManager.get(CoordDisplayModule.class).render(context, client);
            moduleManager.get(FPSDisplayModule.class).render(context, client);
            moduleManager.get(PingDisplayModule.class).render(context, client);
            moduleManager.get(CPSCounterModule.class).render(context, client);
            moduleManager.get(ReachDisplayModule.class).render(context, client);
            moduleManager.get(NametagsModule.class).render(context, client);
            moduleManager.get(HitColorModule.class).render(context, client);
            moduleManager.get(HypixelAPIModule.class).render(context, client);
            moduleManager.get(CustomCrosshairModule.class).render(context, client);
        });

        LOGGER.info("HypixelClient initialized!");
    }

    public static HypixelClient getInstance() { return instance; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public KeybindManager getKeybindManager() { return keybindManager; }
    public Config getConfig() { return config; }
}
