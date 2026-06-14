package com.hypixelclient.module.hud;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public class PingDisplayModule extends Module {
    public PingDisplayModule() {
        super("PingDisplay", "Shows your current ping to the server", Category.HUD);
        setEnabled(true);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.getNetworkHandler() == null) return;
        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
        if (entry == null) return;
        int ping = entry.getLatency();
        int color = ping < 80 ? 0xFF00FF00 : ping < 150 ? 0xFFFFFF00 : 0xFFFF0000;
        context.drawText(client.textRenderer, ping + "ms", 2, client.getWindow().getScaledHeight() - 40, color, false);
    }
}
