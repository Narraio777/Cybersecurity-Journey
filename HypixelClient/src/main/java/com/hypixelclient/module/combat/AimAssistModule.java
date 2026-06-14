package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class AimAssistModule extends Module {
    private final float range = 6.0f;
    private final float strength = 0.3f;

    public AimAssistModule() {
        super("AimAssist", "Smoothly pulls your aim towards the nearest player", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (!client.options.attackKey.isPressed()) return;

        PlayerEntity nearest = null;
        double nearestDist = range * range;

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

        float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horizDist));

        float currentYaw = client.player.getYaw();
        float currentPitch = client.player.getPitch();

        // Normalize yaw difference to [-180, 180]
        float yawDiff = targetYaw - currentYaw;
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        client.player.setYaw(currentYaw + yawDiff * strength);
        client.player.setPitch(currentPitch + (targetPitch - currentPitch) * strength);
    }
}
