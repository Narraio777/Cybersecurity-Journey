package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import java.util.Random;

public class AutoClickerModule extends Module {
    private long lastClick = 0;
    private final Random random = new Random();
    private final int minCps = 10;
    private final int maxCps = 14;

    public AutoClickerModule() {
        super("AutoClicker", "Automatically left-clicks at 10-14 CPS while holding attack", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.interactionManager == null) return;
        if (!client.options.attackKey.isPressed()) return;

        int cps = minCps + random.nextInt(maxCps - minCps + 1);
        long delay = 1000L / cps;
        long now = System.currentTimeMillis();
        if (now - lastClick < delay) return;

        Entity target = client.targetedEntity;
        if (target != null) {
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(Hand.MAIN_HAND);
            lastClick = now;
        }
    }
}
