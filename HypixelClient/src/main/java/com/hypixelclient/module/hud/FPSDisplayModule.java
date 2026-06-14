package com.hypixelclient.module.hud;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class FPSDisplayModule extends Module {
    public FPSDisplayModule() {
        super("FPSDisplay", "Shows your current FPS on screen", Category.HUD);
        setEnabled(true);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled()) return;
        int fps = client.getCurrentFps();
        int color = fps >= 60 ? 0xFF00FF00 : fps >= 30 ? 0xFFFFFF00 : 0xFFFF0000;
        context.drawText(client.textRenderer, fps + " FPS", 2, client.getWindow().getScaledHeight() - 30, color, false);
    }
}
