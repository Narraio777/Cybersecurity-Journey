package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

public class CriticalsModule extends Module {
    public CriticalsModule() {
        super("Criticals", "Mini-hops while attacking so hits land as crits on the way down", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;
        if (!client.options.attackKey.isPressed()) return;

        Entity target = client.targetedEntity;
        if (target == null || target == client.player) return;

        // Only hop when grounded; the descent that follows turns the next click into a crit.
        if (client.player.isOnGround() && !client.player.isSneaking()
                && !client.player.isTouchingWater() && !client.player.isInLava()
                && !client.player.isClimbing()) {
            client.player.setJumping(true);
        }
    }
}
