package com.hypixelclient.module.hud;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import com.hypixelclient.module.combat.AntiBotModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class NametagsModule extends Module {
    private final Setting range = addSetting(new Setting("Range", 32, 8, 64, 4));

    public NametagsModule() {
        super("Nametags+", "Shows name, health and distance above every nearby player", Category.HUD);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (client.player.networkHandler == null) return;

        AntiBotModule antiBot = HypixelClient.getInstance().getModuleManager().get(AntiBotModule.class);

        // Build a set of UUIDs that are actually in the tab list (real players only).
        java.util.Set<java.util.UUID> realPlayers = new java.util.HashSet<>();
        for (var entry : client.player.networkHandler.getPlayerList()) {
            realPlayers.add(entry.getProfile().getId());
        }

        double r = range.getValue();
        for (PlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;
            if (client.player.squaredDistanceTo(player) > r * r) continue;

            // Skip anyone not in the real tab list (server NPCs, practice dummies, etc.)
            if (!realPlayers.contains(player.getUuid())) continue;

            // Also skip known bots / npc-prefixed names as a secondary filter
            if (antiBot != null && antiBot.isBot(player)) continue;
            String rawName = player.getName().getString().toLowerCase();
            if (rawName.startsWith("npc-") || rawName.startsWith("npc_")) continue;

            int[] screen = worldToScreen(player.getX(), player.getY() + player.getHeight() + 0.3, player.getZ(), client);
            if (screen == null) continue;

            int sx = screen[0];
            int sy = screen[1];

            float hp    = player.getHealth();
            float maxHp = player.getMaxHealth();
            int   dist  = (int) client.player.distanceTo(player);

            int hpColor = hp > maxHp * 0.6f ? 0xFF55FF55 : hp > maxHp * 0.3f ? 0xFFFFFF55 : 0xFFFF5555;

            String nameTag = player.getName().getString();
            String hpTag   = String.format("%.1f ❤", hp);
            String distTag = dist + "m";

            int nameW = client.textRenderer.getWidth(nameTag);
            int hpW   = client.textRenderer.getWidth(hpTag);
            int distW = client.textRenderer.getWidth(distTag);
            int panelW = Math.max(nameW, Math.max(hpW, distW)) + 6;
            int panelH = 30;

            // Dark background panel for readability
            int bx = sx - panelW / 2;
            context.fill(bx, sy - 2, bx + panelW, sy + panelH, 0xAA000000);

            context.drawText(client.textRenderer, nameTag, sx - nameW / 2, sy,      0xFFFFFFFF, false);
            context.drawText(client.textRenderer, hpTag,   sx - hpW  / 2, sy + 10, hpColor,    false);
            context.drawText(client.textRenderer, distTag, sx - distW / 2, sy + 20, 0xFFAAAAAA, false);
        }
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
