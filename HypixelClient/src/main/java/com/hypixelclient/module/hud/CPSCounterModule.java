package com.hypixelclient.module.hud;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayDeque;
import java.util.Deque;

public class CPSCounterModule extends Module {
    private final Deque<Long> leftClicks = new ArrayDeque<>();
    private final Deque<Long> rightClicks = new ArrayDeque<>();

    public CPSCounterModule() {
        super("CPSCounter", "Shows your left and right clicks per second", Category.HUD);
        setEnabled(true);
    }

    public void registerLeftClick() {
        leftClicks.addLast(System.currentTimeMillis());
    }

    public void registerRightClick() {
        rightClicks.addLast(System.currentTimeMillis());
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        leftClicks.removeIf(t -> now - t > 1000);
        rightClicks.removeIf(t -> now - t > 1000);
        String text = "CPS: " + leftClicks.size() + " | " + rightClicks.size();
        int x = client.getWindow().getScaledWidth() - client.textRenderer.getWidth(text) - 2;
        int y = client.getWindow().getScaledHeight() - 20;
        context.drawText(client.textRenderer, text, x, y, 0xFFFFFFFF, false);
    }
}
