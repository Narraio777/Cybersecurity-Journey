package com.hypixelclient.module.combat;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class TriggerBotModule extends Module {
    private final Setting delay = addSetting(new Setting("Delay", 100, 0, 500, 50));

    private long lastAttack = 0;

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

        long now = System.currentTimeMillis();
        if (now - lastAttack < (long) delay.getValue()) return;

        client.interactionManager.attackEntity(client.player, target);
        client.player.swingHand(Hand.MAIN_HAND);
        lastAttack = now;
    }
}
