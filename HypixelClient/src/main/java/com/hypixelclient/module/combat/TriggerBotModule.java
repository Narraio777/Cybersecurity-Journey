package com.hypixelclient.module.combat;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import com.hypixelclient.util.Humanizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class TriggerBotModule extends Module {
    private final Setting delay = addSetting(new Setting("Delay", 100, 0, 500, 50));

    private long lastAttack = 0;
    // Reaction time sampled fresh each time a new target enters the crosshair.
    private long nextDelay = 0;
    private Entity lastTarget = null;

    public TriggerBotModule() {
        super("TriggerBot", "Automatically attacks the entity under your crosshair", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.interactionManager == null) return;

        Entity target = client.targetedEntity;
        if (!(target instanceof LivingEntity)) return;
        if (target == client.player) return;
        if (target instanceof PlayerEntity p) {
            AntiBotModule antiBot = HypixelClient.getInstance().getModuleManager().get(AntiBotModule.class);
            if (antiBot != null && antiBot.isBot(p)) return;
        }

        // Re-sample a gaussian reaction delay whenever a new target enters the crosshair.
        if (target != lastTarget) {
            lastTarget = target;
            long base = (long) delay.getValue();
            // Gaussian spread ±20% around the configured delay — no two reactions are the same.
            nextDelay = Humanizer.gaussianDelay(Math.max(0, base - base / 5), base + base / 5);
        }

        long now = System.currentTimeMillis();
        if (now - lastAttack < nextDelay) return;

        // 5% random miss — trigger fingers aren't perfect.
        if (Humanizer.chance(0.05)) { lastAttack = now; return; }

        client.interactionManager.attackEntity(client.player, target);
        client.player.swingHand(Hand.MAIN_HAND);
        lastAttack = now;
        // Resample delay for the next attack window.
        long base = (long) delay.getValue();
        nextDelay = Humanizer.gaussianDelay(Math.max(0, base - base / 5), base + base / 5);
    }
}
