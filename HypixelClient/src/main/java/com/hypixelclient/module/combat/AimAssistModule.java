package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import com.hypixelclient.util.Humanizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class AimAssistModule extends Module {
    private final Setting strength = addSetting(new Setting("Strength", 30, 0, 100, 5));
    private final Setting range    = addSetting(new Setting("Range",    6,  1, 10,  1));
    private final Setting speed    = addSetting(new Setting("Speed",    4,  1, 20,  1));

    // Stable body-offset so the aim target doesn't jump every frame (main jitter cause).
    private PlayerEntity lastTarget = null;
    private double       cachedOffX = 0;
    private double       cachedOffY = 0;
    private int          offTimer   = 0;

    public AimAssistModule() {
        super("AimAssist", "Smoothly pulls your aim towards the nearest player", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (!client.options.attackKey.isPressed()) return;

        // 4% lazy frame — skip entirely so not every tick moves the camera.
        if (Humanizer.chance(0.04)) return;

        double r = range.getValue();
        PlayerEntity nearest = null;
        double nearestDist = r * r;

        for (PlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;
            double dist = client.player.squaredDistanceTo(player);
            if (dist < nearestDist) { nearestDist = dist; nearest = player; }
        }
        if (nearest == null) return;

        // Only refresh the body-offset when the target changes or every 15-25 ticks.
        // Recalculating it every frame was the main cause of the jittery movement.
        if (nearest != lastTarget) {
            lastTarget = nearest;
            offTimer   = 0;
        }
        if (--offTimer <= 0) {
            cachedOffX = Humanizer.jitter(0f, 0.15) * nearest.getWidth();
            cachedOffY = Humanizer.jitter(0f, 0.12) * nearest.getHeight();
            offTimer   = Humanizer.gaussianInt(15, 25);
        }

        double dx = (nearest.getX() + cachedOffX) - client.player.getX();
        double dy = (nearest.getEyeY() + cachedOffY) - client.player.getEyeY();
        double dz = nearest.getZ() - client.player.getZ();
        double horizDist = Math.sqrt(dx * dx + dz * dz);

        float targetYaw   = (float)  Math.toDegrees(Math.atan2(-dx, dz));
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horizDist));

        float currentYaw   = client.player.getYaw();
        float currentPitch = client.player.getPitch();

        float yawDiff = targetYaw - currentYaw;
        while (yawDiff >  180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;
        float pitchDiff = targetPitch - currentPitch;

        float s = (float)(strength.getValue() / 100.0);
        // Tiny per-frame noise — just enough to avoid machine-perfect frames.
        s = Humanizer.jitter(s, 0.03f);

        float maxStep = Humanizer.jitter((float) speed.getValue(), 0.05f);
        float yawStep   = Math.max(-maxStep, Math.min(maxStep, yawDiff   * s));
        float pitchStep = Math.max(-maxStep, Math.min(maxStep, pitchDiff * s));

        client.player.setYaw(currentYaw + yawStep);
        client.player.setPitch(currentPitch + pitchStep);
    }
}
