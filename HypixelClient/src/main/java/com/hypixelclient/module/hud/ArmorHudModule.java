package com.hypixelclient.module.hud;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ArmorHudModule extends Module {
    public ArmorHudModule() {
        super("ArmorHUD", "Displays your armor durability on screen", Category.HUD);
        setEnabled(true);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;
        int x = 2, y = client.getWindow().getScaledHeight() - 60;
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = client.player.getEquippedStack(slots[i]);
            if (!stack.isEmpty()) {
                context.drawItem(stack, x + (i * 18), y);
                if (stack.isDamageable()) {
                    int durability = stack.getMaxDamage() - stack.getDamage();
                    int max = stack.getMaxDamage();
                    int color = durability < max / 4 ? 0xFFFF0000 : durability < max / 2 ? 0xFFFFFF00 : 0xFF00FF00;
                    context.drawText(client.textRenderer, String.valueOf(durability), x + (i * 18), y + 18, color, true);
                }
            }
        }
    }
}
