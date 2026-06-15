package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.util.Humanizer;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class BunnyHopModule extends Module {
    // 0-2 extra ticks before the next jump — humans have variable reaction to landing.
    private int jumpDelay = 0;

    public BunnyHopModule() {
        super("BunnyHop", "Automatically jumps while moving for continuous hops", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;

        boolean moving = client.options.forwardKey.isPressed()
                      || client.options.backKey.isPressed()
                      || client.options.leftKey.isPressed()
                      || client.options.rightKey.isPressed();

        if (!moving || client.player.isSneaking()) { jumpDelay = 0; return; }

        if (client.player.isOnGround()) {
            if (jumpDelay > 0) { jumpDelay--; return; }
            client.player.setJumping(true);
            // Resample delay for the next landing (0 ticks ~50% of the time, 1-2 the rest).
            jumpDelay = Humanizer.gaussianInt(0, 2);
        }
    }
}
