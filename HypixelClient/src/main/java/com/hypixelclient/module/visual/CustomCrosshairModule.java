package com.hypixelclient.module.visual;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CustomCrosshairModule extends Module {
    public CustomCrosshairModule() {
        super("CustomCrosshair", "Replaces the default crosshair with a clean custom one", Category.VISUAL);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled()) return;
        int cx = client.getWindow().getScaledWidth() / 2;
        int cy = client.getWindow().getScaledHeight() / 2;
        int color = 0xFFFFFFFF;
        int size = 5;
        // Horizontal bar
        context.fill(cx - size, cy - 1, cx + size + 1, cy + 2, color);
        // Vertical bar
        context.fill(cx - 1, cy - size, cx + 2, cy + size + 1, color);
    }
}
