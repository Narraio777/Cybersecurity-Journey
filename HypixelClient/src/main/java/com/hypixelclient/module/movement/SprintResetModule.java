package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class SprintResetModule extends Module {
    private static final int RESET_TICKS = 2;
    private int resetTicks = 0;

    public SprintResetModule() {
        super("SprintReset", "Briefly drops sprint after each hit (W-Tap) for stronger knockback", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
    }

    // Called from the attack hook whenever we land a hit on an entity.
    public void onHit() {
        if (isEnabled()) resetTicks = RESET_TICKS;
    }

    public void onTick(MinecraftClient client) {
        if (client.player == null) return;
        if (!isEnabled()) {
            resetTicks = 0;
            return;
        }
        if (resetTicks > 0) {
            client.player.setSprinting(false);
            resetTicks--;
        }
    }
}
