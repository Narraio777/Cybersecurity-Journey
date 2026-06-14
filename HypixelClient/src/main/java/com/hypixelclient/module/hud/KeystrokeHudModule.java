package com.hypixelclient.module.hud;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;

public class KeystrokeHudModule extends Module {
    public KeystrokeHudModule() {
        super("KeystrokeHUD", "Shows WASD and mouse button keystrokes on screen", Category.HUD);
        setEnabled(true);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;
        GameOptions opts = client.options;
        int bx = client.getWindow().getScaledWidth() - 55;
        int by = client.getWindow().getScaledHeight() - 60;

        drawKey(context, client, "W", bx + 17, by,      opts.forwardKey.isPressed());
        drawKey(context, client, "A", bx,      by + 14, opts.leftKey.isPressed());
        drawKey(context, client, "S", bx + 17, by + 14, opts.backKey.isPressed());
        drawKey(context, client, "D", bx + 34, by + 14, opts.rightKey.isPressed());
        drawKey(context, client, "LMB", bx,    by + 28, opts.attackKey.isPressed());
        drawKey(context, client, "RMB", bx+22, by + 28, opts.useKey.isPressed());
    }

    private void drawKey(DrawContext ctx, MinecraftClient client, String label, int x, int y, boolean pressed) {
        int bg = pressed ? 0xAA00AA00 : 0xAA333333;
        int fg = pressed ? 0xFFFFFFFF : 0xFFAAAAAA;
        ctx.fill(x, y, x + 15, y + 12, bg);
        ctx.drawText(client.textRenderer, label, x + 2, y + 2, fg, false);
    }
}
