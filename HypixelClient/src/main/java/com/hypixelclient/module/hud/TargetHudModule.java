package com.hypixelclient.module.hud;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class TargetHudModule extends Module {
    private PlayerEntity lastTarget = null;
    private int fadeTimer = 0;

    public TargetHudModule() {
        super("TargetHUD", "Shows your current target's name, HP bar, and armor", Category.HUD);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;

        PlayerEntity target = null;
        if (client.targetedEntity instanceof PlayerEntity p && p != client.player) {
            target = p;
            lastTarget = p;
            fadeTimer = 80;
        } else if (fadeTimer > 0 && lastTarget != null && lastTarget.isAlive()) {
            target = lastTarget;
            fadeTimer--;
        } else {
            lastTarget = null;
            return;
        }

        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        int panelW = 160;
        int panelH = 52;
        int x = 4;
        int y = sh - panelH - 4;

        float hp    = target.getHealth();
        float maxHp = target.getMaxHealth();
        float pct   = maxHp > 0 ? hp / maxHp : 0f;
        int fillColor = pct > 0.6f ? 0xFF55FF55 : pct > 0.3f ? 0xFFFFFF55 : 0xFFFF5555;

        // Background panel
        context.fill(x, y, x + panelW, y + panelH, 0xBB000000);
        // Left accent bar (color = HP color)
        context.fill(x, y, x + 3, y + panelH, fillColor);

        // Name
        String name = target.getName().getString();
        context.drawText(client.textRenderer, name, x + 8, y + 5, 0xFFFFFFFF, false);

        // Distance (top-right)
        int dist = (int) client.player.distanceTo(target);
        String distStr = dist + "m";
        int distW = client.textRenderer.getWidth(distStr);
        context.drawText(client.textRenderer, distStr, x + panelW - distW - 4, y + 5, 0xFFAAAAAA, false);

        // HP bar
        int barX = x + 8;
        int barY = y + 18;
        int barW = panelW - 16;
        int barH = 6;
        context.fill(barX, barY, barX + barW, barY + barH, 0xFF333333);
        context.fill(barX, barY, barX + (int)(barW * pct), barY + barH, fillColor);

        // HP text
        String hpStr = String.format("%.1f / %.1f", hp, maxHp);
        context.drawText(client.textRenderer, hpStr, barX, barY + barH + 3, 0xFFCCCCCC, false);

        // Armor row
        EquipmentSlot[] armorSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        int iconX = x + 8;
        int iconY = y + panelH - 18;
        for (EquipmentSlot slot : armorSlots) {
            ItemStack stack = target.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                context.drawItem(stack, iconX, iconY);
                iconX += 18;
            }
        }
    }
}
