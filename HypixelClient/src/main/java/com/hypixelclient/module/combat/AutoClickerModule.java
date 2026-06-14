package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import java.util.Random;

public class AutoClickerModule extends Module {
    private final Setting minCps = addSetting(new Setting("MinCPS", 10, 1, 20, 1));
    private final Setting maxCps = addSetting(new Setting("MaxCPS", 14, 1, 20, 1));

    private long lastClick = 0;
    private final Random random = new Random();

    public AutoClickerModule() {
        super("AutoClicker", "Automatically left-clicks at a set CPS while holding attack", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.interactionManager == null) return;
        if (!client.options.attackKey.isPressed()) return;

        int lo = (int) minCps.getValue();
        int hi = (int) maxCps.getValue();
        if (lo > hi) hi = lo;
        int cps = lo + (hi > lo ? random.nextInt(hi - lo + 1) : 0);
        long delay = 1000L / Math.max(1, cps);
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
