package com.hypixelclient.module.hud;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
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

        double r = range.getValue();
        for (PlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;
            if (client.player.squaredDistanceTo(player) > r * r) continue;

            int[] screen = worldToScreen(player.getX(), player.getY() + player.getHeight() + 0.25, player.getZ(), client);
            if (screen == null) continue;

            int sx = screen[0];
            int sy = screen[1];

            float hp  = player.getHealth();
            float maxHp = player.getMaxHealth();
            int dist = (int) client.player.distanceTo(player);

            int hpColor = hp > maxHp * 0.6f ? 0xFF00FF00 : hp > maxHp * 0.3f ? 0xFFFFFF00 : 0xFFFF4444;

            String nameTag = player.getName().getString();
            String hpTag   = String.format("%.1f❤", hp);
            String distTag = dist + "m";

            int nameW = client.textRenderer.getWidth(nameTag);
            int hpW   = client.textRenderer.getWidth(hpTag);
            int distW = client.textRenderer.getWidth(distTag);

            context.drawText(client.textRenderer, nameTag, sx - nameW / 2, sy,      0xFFFFFFFF, false);
            context.drawText(client.textRenderer, hpTag,   sx - hpW  / 2, sy + 10, hpColor,    false);
            context.drawText(client.textRenderer, distTag, sx - distW / 2, sy + 20, 0xFFAAAAAA, false);
        }
    }

    // Projects a world-space position to screen pixel coordinates using
    // yaw/pitch rotation — no getBasicProjectionMatrix needed.
    // Returns null if the point is behind the camera.
    private int[] worldToScreen(double wx, double wy, double wz, MinecraftClient client) {
        try {
            net.minecraft.client.render.Camera cam = client.gameRenderer.getCamera();
            Vec3d eye = cam.getPos();

            double dx = wx - eye.x;
            double dy = wy - eye.y;
            double dz = wz - eye.z;

            // Yaw rotation (Y-axis, horizontal)
            double yawRad = Math.toRadians(cam.getYaw() + 180);
            double cosY   = Math.cos(yawRad), sinY = Math.sin(yawRad);
            double rx =  dx * cosY + dz * sinY;
            double rz = -dx * sinY + dz * cosY;

            if (rz <= 0) return null; // behind camera

            // Pitch rotation (X-axis, vertical)
            double pitchRad = Math.toRadians(cam.getPitch());
            double cosP = Math.cos(pitchRad), sinP = Math.sin(pitchRad);
            double ry2 = dy * cosP - rz * sinP;
            double rz2 = dy * sinP + rz * cosP;

            if (rz2 <= 0) return null;

            double fov   = client.options.getFov().getValue();
            double sh    = client.getWindow().getScaledHeight();
            double scale = (sh / 2.0) / Math.tan(Math.toRadians(fov / 2.0));

            int sx = (int) (client.getWindow().getScaledWidth()  / 2.0 + rx  / rz2 * scale);
            int sy = (int) (sh / 2.0                             - ry2 / rz2 * scale);
            return new int[]{sx, sy};
        } catch (Exception e) {
            return null;
        }
    }
}
