package com.hypixelclient.module.hud;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import com.hypixelclient.module.combat.AntiBotModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class NametagsModule extends Module {
    private final Setting range = addSetting(new Setting("Range", 32, 8, 64, 4));

    public NametagsModule() {
        super("Nametags+", "Shows enhanced nametags with HP bar, armor, and distance", Category.HUD);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (client.player.networkHandler == null) return;

        AntiBotModule antiBot = HypixelClient.getInstance().getModuleManager().get(AntiBotModule.class);

        java.util.Set<java.util.UUID> realPlayers = new java.util.HashSet<>();
        for (var entry : client.player.networkHandler.getPlayerList()) {
            realPlayers.add(entry.getProfile().getId());
        }

        double r = range.getValue();
        for (PlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;
            if (client.player.squaredDistanceTo(player) > r * r) continue;
            if (!realPlayers.contains(player.getUuid())) continue;
            if (antiBot != null && antiBot.isBot(player)) continue;
            String rawName = player.getName().getString().toLowerCase();
            if (rawName.startsWith("npc-") || rawName.startsWith("npc_")) continue;

            int[] screen = worldToScreen(
                player.getX(), player.getY() + player.getHeight() + 0.25, player.getZ(), client
            );
            if (screen == null) continue;

            int sx = screen[0];
            int sy = screen[1];

            float hp    = player.getHealth();
            float maxHp = player.getMaxHealth();
            float pct   = maxHp > 0 ? hp / maxHp : 0f;
            int dist    = (int) client.player.distanceTo(player);

            int fillColor = pct > 0.6f ? 0xFF55FF55 : pct > 0.3f ? 0xFFFFFF55 : 0xFFFF5555;

            // Panel sizing: fixed width, dynamic height based on whether armor is present
            boolean hasArmor = hasAnyArmor(player);
            int panelW = 100;
            int panelH = hasArmor ? 48 : 32;
            int bx = sx - panelW / 2;

            // Background
            context.fill(bx, sy, bx + panelW, sy + panelH, 0xAA0A0A0A);
            // Top accent stripe (HP color)
            context.fill(bx, sy, bx + panelW, sy + 2, fillColor);

            // Name
            String nameTag = player.getName().getString();
            int nameW = client.textRenderer.getWidth(nameTag);
            context.drawText(client.textRenderer, nameTag, sx - nameW / 2, sy + 4, 0xFFFFFFFF, false);

            // HP bar
            int barX = bx + 4;
            int barY = sy + 15;
            int barW = panelW - 8;
            int barH = 5;
            context.fill(barX, barY, barX + barW, barY + barH, 0xFF2A2A2A);
            if (pct > 0) {
                context.fill(barX, barY, barX + (int)(barW * pct), barY + barH, fillColor);
            }

            // HP text (left) and distance text (right) on same line below bar
            String hpStr   = String.format("%.1f", hp);
            String distStr = dist + "m";
            int distW = client.textRenderer.getWidth(distStr);
            context.drawText(client.textRenderer, hpStr,   barX,                   barY + barH + 2, fillColor,    false);
            context.drawText(client.textRenderer, distStr, bx + panelW - distW - 4, barY + barH + 2, 0xFFAAAAAA, false);

            // Armor row
            if (hasArmor) {
                EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
                int iconX = bx + 2;
                int iconY = sy + panelH - 17;
                for (EquipmentSlot slot : slots) {
                    ItemStack stack = player.getEquippedStack(slot);
                    if (!stack.isEmpty()) {
                        context.drawItem(stack, iconX, iconY);
                        iconX += 18;
                    }
                }
            }
        }
    }

    private boolean hasAnyArmor(PlayerEntity player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        }) {
            if (!player.getEquippedStack(slot).isEmpty()) return true;
        }
        return false;
    }

    private int[] worldToScreen(double wx, double wy, double wz, MinecraftClient client) {
        try {
            net.minecraft.client.render.Camera cam = client.gameRenderer.getCamera();
            Vec3d eye = cam.getPos();

            double dx = wx - eye.x;
            double dy = wy - eye.y;
            double dz = wz - eye.z;

            double yawRad = Math.toRadians(cam.getYaw() + 180);
            double cosY   = Math.cos(yawRad), sinY = Math.sin(yawRad);
            double rx =  dx * cosY + dz * sinY;
            double rz = -dx * sinY + dz * cosY;

            if (rz <= 0) return null;

            double pitchRad = Math.toRadians(cam.getPitch());
            double cosP = Math.cos(pitchRad), sinP = Math.sin(pitchRad);
            double ry2 =  dy * cosP - rz * sinP;
            double rz2 =  dy * sinP + rz * cosP;

            if (rz2 <= 0) return null;

            double fov   = client.options.getFov().getValue();
            double sh    = client.getWindow().getScaledHeight();
            double scale = (sh / 2.0) / Math.tan(Math.toRadians(fov / 2.0));

            int sx = (int)(client.getWindow().getScaledWidth() / 2.0 + rx  / rz2 * scale);
            int sy = (int)(sh / 2.0                                  - ry2 / rz2 * scale);
            return new int[]{sx, sy};
        } catch (Exception e) {
            return null;
        }
    }
}
