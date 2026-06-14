package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class BunnyHopModule extends Module {
    public BunnyHopModule() {
        super("BunnyHop", "Automatically jumps while moving for continuous hops", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;

        boolean moving = client.options.forwardKey.isPressed()
                      || client.options.backKey.isPressed()
                      || client.options.leftKey.isPressed()
                      || client.options.rightKey.isPressed();

        if (moving && client.player.isOnGround() && !client.player.isSneaking()) {
            client.player.setJumping(true);
        }
    }
}
