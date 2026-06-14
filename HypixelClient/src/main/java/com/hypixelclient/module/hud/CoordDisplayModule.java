package com.hypixelclient.module.hud;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;

public class CoordDisplayModule extends Module {
    public CoordDisplayModule() {
        super("Coordinates", "Shows your current XYZ position on screen", Category.HUD);
        setEnabled(true);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;
        BlockPos pos = client.player.getBlockPos();
        String text = String.format("XYZ: %d / %d / %d", pos.getX(), pos.getY(), pos.getZ());
        int x = 2;
        int y = client.getWindow().getScaledHeight() - 20;
        context.drawText(client.textRenderer, text, x, y, 0xFFFFFFFF, false);
    }
}
