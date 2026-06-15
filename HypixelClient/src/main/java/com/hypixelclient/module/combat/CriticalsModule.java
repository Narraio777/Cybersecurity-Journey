package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.util.Humanizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

public class CriticalsModule extends Module {
    // Per-hop delay sampled once per landing (1-3 ticks) so jumps aren't frame-perfect.
    private int hopDelay = 0;

    public CriticalsModule() {
        super("Criticals", "Mini-hops while attacking so hits land as crits on the way down", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;
        if (!client.options.attackKey.isPressed()) { hopDelay = 0; return; }

        Entity target = client.targetedEntity;
        if (target == null || target == client.player) return;

        if (!client.player.isOnGround() || client.player.isSneaking()
                || client.player.isTouchingWater() || client.player.isInLava()
                || client.player.isClimbing()) {
            hopDelay = 0;
            return;
        }

        // 25% of the time skip the hop entirely — humans don't crit on every hit.
        if (Humanizer.chance(0.25)) { hopDelay = 0; return; }

        // Wait the sampled delay before hopping (0 = immediate, 1-2 = slight lag).
        if (hopDelay > 0) { hopDelay--; return; }

        client.player.setJumping(true);
        // Resample delay for the next landing.
        hopDelay = Humanizer.gaussianInt(0, 2);
    }
}
