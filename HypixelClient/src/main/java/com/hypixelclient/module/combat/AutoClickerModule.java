package com.hypixelclient.module.combat;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import com.hypixelclient.util.Humanizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class AutoClickerModule extends Module {
    private final Setting minCps = addSetting(new Setting("MinCPS", 10, 1, 20, 1));
    private final Setting maxCps = addSetting(new Setting("MaxCPS", 14, 1, 20, 1));

    private long lastClick = 0;
    // After a burst of clicks, simulate a brief stutter (humans aren't machines).
    private int clickStreak = 0;
    private long stutterUntil = 0;

    public AutoClickerModule() {
        super("AutoClicker", "Automatically left-clicks at a humanized CPS while holding attack", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.interactionManager == null) return;
        if (!client.options.attackKey.isPressed()) { clickStreak = 0; return; }

        long now = System.currentTimeMillis();

        // Simulate a stutter every 8–18 clicks (humans shift grip, blink, etc.).
        if (now < stutterUntil) return;
        if (clickStreak > Humanizer.gaussianInt(8, 18)) {
            stutterUntil = now + Humanizer.gaussianDelay(60, 200);
            clickStreak = 0;
            return;
        }

        // Gaussian delay between clicks (not uniform — uniform is a bot fingerprint).
        int lo = (int) minCps.getValue();
        int hi = (int) maxCps.getValue();
        double cps   = Humanizer.gaussianInt(Math.min(lo, hi), Math.max(lo, hi));
        long   delay = (long)(1000.0 / Math.max(1, cps));
        if (now - lastClick < delay) return;

        // 4% random miss — humans don't hit every single time.
        if (Humanizer.chance(0.04)) { lastClick = now; return; }

        Entity target = client.targetedEntity;
        if (target == null) return;
        if (target instanceof PlayerEntity p) {
            AntiBotModule antiBot = HypixelClient.getInstance().getModuleManager().get(AntiBotModule.class);
            if (antiBot != null && antiBot.isBot(p)) return;
        }

        client.interactionManager.attackEntity(client.player, target);
        client.player.swingHand(Hand.MAIN_HAND);
        lastClick = now;
        clickStreak++;
    }
}
