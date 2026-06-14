package com.hypixelclient.module.hud;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

public class PotionHudModule extends Module {
    public PotionHudModule() {
        super("PotionHUD", "Shows active potion effects with remaining duration", Category.HUD);
        setEnabled(true);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;
        int x = 2;
        int y = 2;
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            String name = effect.getEffectType().value().getName().getString();
            int duration = effect.getDuration() / 20;
            String text = name + " " + (duration > 0 ? duration + "s" : "**");
            context.drawText(client.textRenderer, text, x, y, 0xFFFFFFFF, true);
            y += 11;
        }
    }
}
