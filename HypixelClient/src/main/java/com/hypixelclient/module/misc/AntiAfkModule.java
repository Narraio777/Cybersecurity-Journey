package com.hypixelclient.module.misc;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import java.util.Random;

public class AntiAfkModule extends Module {
    // Interval in seconds between micro-movements (randomised ±20%).
    private final Setting interval = addSetting(new Setting("Interval", 90, 30, 180, 10));

    private long nextAction = 0;
    private final Random rng = new Random();

    public AntiAfkModule() {
        super("AntiAFK", "Sends random micro-rotations to prevent Hypixel's AFK kick", Category.MISC, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;
        long now = System.currentTimeMillis();
        if (nextAction == 0) scheduleNext(now);
        if (now < nextAction) return;

        // Tiny yaw nudge — completely invisible, indistinguishable from mouse drift.
        float nudge = (rng.nextFloat() - 0.5f) * 2f;
        client.player.setYaw(client.player.getYaw() + nudge);

        scheduleNext(now);
    }

    private void scheduleNext(long now) {
        long base = (long) interval.getValue() * 1000L;
        long jitter = (long) (base * 0.2 * (rng.nextDouble() - 0.5) * 2);
        nextAction = now + base + jitter;
    }
}
