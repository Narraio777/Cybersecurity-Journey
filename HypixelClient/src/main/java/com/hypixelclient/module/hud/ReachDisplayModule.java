package com.hypixelclient.module.hud;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ReachDisplayModule extends Module {
    private double sessionMax = 0;
    private long lastSeen = 0;
    private double lastReach = 0;

    public ReachDisplayModule() {
        super("ReachDisplay", "Shows the real reach in blocks to the entity you target", Category.HUD);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;

        Entity target = client.targetedEntity;
        long now = System.currentTimeMillis();
        if (target != null && target != client.player) {
            lastReach = reachTo(client, target);
            if (lastReach > sessionMax) sessionMax = lastReach;
            lastSeen = now;
        }

        // Keep the last value briefly on screen after the target leaves the crosshair.
        if (now - lastSeen > 1500) return;

        String text = String.format("Reach: %.2f  (max %.2f)", lastReach, sessionMax);
        int x = client.getWindow().getScaledWidth() - client.textRenderer.getWidth(text) - 2;
        int y = client.getWindow().getScaledHeight() - 30;
        context.drawText(client.textRenderer, text, x, y, 0xFFFFFFFF, false);
    }

    // Eye-to-nearest-point of the target's hitbox = the actual reach needed to hit it.
    private double reachTo(MinecraftClient client, Entity target) {
        Vec3d eye = client.player.getEyePos();
        Box box = target.getBoundingBox();
        double dx = Math.max(Math.max(box.minX - eye.x, 0), eye.x - box.maxX);
        double dy = Math.max(Math.max(box.minY - eye.y, 0), eye.y - box.maxY);
        double dz = Math.max(Math.max(box.minZ - eye.z, 0), eye.z - box.maxZ);
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
