package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class SpeedModule extends Module {
    // 100 = vanilla, 200 = double horizontal speed
    private final Setting strength = addSetting(new Setting("Strength", 130, 100, 200, 10));

    public SpeedModule() {
        super("Speed", "Boosts ground movement speed (very detectable)", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;
        if (!client.player.isOnGround()) return;

        boolean moving = client.options.forwardKey.isPressed()
                      || client.options.backKey.isPressed()
                      || client.options.leftKey.isPressed()
                      || client.options.rightKey.isPressed();
        if (!moving) return;

        double mult = strength.getValue() / 100.0;
        Vec3d v = client.player.getVelocity();
        client.player.setVelocity(v.x * mult, v.y, v.z * mult);
    }
}
