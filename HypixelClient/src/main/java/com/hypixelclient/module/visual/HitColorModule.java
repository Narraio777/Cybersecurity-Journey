package com.hypixelclient.module.visual;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class HitColorModule extends Module {
    private int flashTicks = 0;
    private static final int FLASH_DURATION = 3;

    public HitColorModule() {
        super("HitColor", "Flashes the screen red when your hit lands on an enemy", Category.VISUAL, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void registerHit() {
        if (isEnabled()) flashTicks = FLASH_DURATION;
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled() || flashTicks <= 0) return;
        // Red vignette that fades over FLASH_DURATION ticks.
        int alpha = (int) (160 * ((double) flashTicks / FLASH_DURATION));
        context.fill(0, 0, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(),
            (alpha << 24) | 0x00CC0000);
        flashTicks--;
    }
}
