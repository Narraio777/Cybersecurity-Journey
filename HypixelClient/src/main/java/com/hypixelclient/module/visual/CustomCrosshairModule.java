package com.hypixelclient.module.visual;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class CustomCrosshairModule extends Module {
    public CustomCrosshairModule() {
        super("CustomCrosshair", "Replaces the default crosshair with a custom one", Category.VISUAL, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled()) return;
        int cx = client.getWindow().getScaledWidth() / 2;
        int cy = client.getWindow().getScaledHeight() / 2;
        int color = 0xFFFFFFFF;
        int size = 5;
        int thickness = 1;
        // Horizontal line
        context.fill(cx - size, cy - thickness, cx + size + 1, cy + thickness + 1, color);
        // Vertical line
        context.fill(cx - thickness, cy - size, cx + thickness + 1, cy + size + 1, color);
    }
}
