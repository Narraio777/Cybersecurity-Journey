package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
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

        double r = range.getValue();
        PlayerEntity nearest = null;
        double nearestDist = r * r;

        for (PlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;
            double dist = client.player.squaredDistanceTo(player);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = player;
            }
        }

        if (nearest == null) return;

        double dx = nearest.getX() - client.player.getX();
        double dy = nearest.getEyeY() - client.player.getEyeY();
        double dz = nearest.getZ() - client.player.getZ();
        double horizDist = Math.sqrt(dx * dx + dz * dz);

        float targetYaw   = (float)  Math.toDegrees(Math.atan2(-dx, dz));
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horizDist));

        float currentYaw   = client.player.getYaw();
        float currentPitch = client.player.getPitch();

        // Normalize yaw difference to [-180, 180]
        float yawDiff = targetYaw - currentYaw;
        while (yawDiff >  180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        float pitchDiff = targetPitch - currentPitch;

        // Scale step by strength percentage
        float s = (float)(strength.getValue() / 100.0);
        float yawStep   = yawDiff   * s;
        float pitchStep = pitchDiff * s;

        // Clamp to max degrees per tick for smooth motion
        float maxStep = (float) speed.getValue();
        yawStep   = Math.max(-maxStep, Math.min(maxStep, yawStep));
        pitchStep = Math.max(-maxStep, Math.min(maxStep, pitchStep));

        client.player.setYaw(currentYaw + yawStep);
        client.player.setPitch(currentPitch + pitchStep);
    }
}
