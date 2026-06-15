package com.hypixelclient.module.visual;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class FullBrightModule extends Module {
    private float previousGamma = 1.0f;
    private boolean pendingApply = false;

    public FullBrightModule() {
        super("FullBright", "Sets gamma to maximum so you can see in the dark", Category.VISUAL, GLFW.GLFW_KEY_B);
    }

    @Override
    public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            // Options not ready yet (startup config restore) — apply on first tick.
            pendingApply = true;
            return;
        }
        applyGamma(client);
    }

    @Override
    public void onDisable() {
        pendingApply = false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;
        client.options.getGamma().setValue((double) previousGamma);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || !pendingApply) return;
        if (client.options == null) return;
        applyGamma(client);
        pendingApply = false;
    }

    private void applyGamma(MinecraftClient client) {
        previousGamma = client.options.getGamma().getValue().floatValue();
        client.options.getGamma().setValue(16.0);
    }
}
