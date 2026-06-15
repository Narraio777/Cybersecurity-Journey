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

    public AimAssistModule() {
        super("AimAssist", "Smoothly pulls your aim towards the nearest player", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (!client.options.attackKey.isPressed()) return;

        // 6% "lazy frame" — human attention wanders briefly, rotation barely moves.
        if (Humanizer.chance(0.06)) return;

        double r = range.getValue();
        PlayerEntity nearest = null;
        double nearestDist = r * r;

        for (PlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;
            double dist = client.player.squaredDistanceTo(player);
            if (dist < nearestDist) { nearestDist = dist; nearest = player; }
        }
        if (nearest == null) return;

        // Aim slightly off-centre: humans don't target the exact eye position every frame.
        double offX = Humanizer.jitter(0f, 0.25) * nearest.getWidth();
        double offY = Humanizer.jitter(0f, 0.20) * nearest.getHeight();

        double dx = (nearest.getX() + offX) - client.player.getX();
        double dy = (nearest.getEyeY() + offY) - client.player.getEyeY();
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
        // Add slight gaussian noise to strength each frame — no two frames are identical.
        s = Humanizer.jitter(s, 0.12f);

        float maxStep = Humanizer.jitter((float) speed.getValue(), 0.10f);
        float yawStep   = Math.max(-maxStep, Math.min(maxStep, yawDiff   * s));
        float pitchStep = Math.max(-maxStep, Math.min(maxStep, pitchDiff * s));

        // 8% overshoot — humans correct past the target before snapping back.
        if (Humanizer.chance(0.08)) {
            yawStep   *= 1.0f + (float)(Math.random() * 0.35);
            pitchStep *= 1.0f + (float)(Math.random() * 0.25);
        }

        client.player.setYaw(currentYaw + yawStep);
        client.player.setPitch(currentPitch + pitchStep);
    }
}
