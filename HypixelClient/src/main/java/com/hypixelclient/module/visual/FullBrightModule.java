package com.hypixelclient.module.visual;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class FullBrightModule extends Module {
    private float previousGamma = 1.0f;

    public FullBrightModule() {
        super("FullBright", "Sets gamma to maximum so you can see in the dark", Category.VISUAL, GLFW.GLFW_KEY_B);
    }

    @Override
    public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        previousGamma = client.options.getGamma().getValue().floatValue();
        client.options.getGamma().setValue(16.0);
    }

    @Override
    public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.options.getGamma().setValue((double) previousGamma);
    }
}
