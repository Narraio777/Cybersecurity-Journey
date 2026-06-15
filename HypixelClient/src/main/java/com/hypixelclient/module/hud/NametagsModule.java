package com.hypixelclient.module.hud;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

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

    // Projects a world-space position to screen pixel coordinates.
    // Returns null if the point is behind the camera.
    private int[] worldToScreen(double wx, double wy, double wz, MinecraftClient client) {
        try {
            net.minecraft.client.render.Camera cam = client.gameRenderer.getCamera();
            Vec3d camPos = cam.getPos();

            // Relative to camera
            double rx = wx - camPos.x;
            double ry = wy - camPos.y;
            double rz = wz - camPos.z;

            Matrix4f proj = client.gameRenderer.getBasicProjectionMatrix(
                client.options.getFov().getValue());
            Matrix4f view = new Matrix4f();

            // Build view matrix from camera rotation
            float yaw   = (float) Math.toRadians(cam.getYaw());
            float pitch = (float) Math.toRadians(cam.getPitch());
            view.rotateX(pitch).rotateY((float) Math.PI + yaw);

            Vector4f clip = new Vector4f((float) rx, (float) ry, (float) rz, 1.0f);
            clip.mul(view).mul(proj);

            if (clip.w <= 0) return null; // behind camera

            float ndcX = clip.x / clip.w;
            float ndcY = clip.y / clip.w;

            int sw = client.getWindow().getScaledWidth();
            int sh = client.getWindow().getScaledHeight();

            int sx = (int) ((ndcX + 1f) / 2f * sw);
            int sy = (int) ((1f - ndcY) / 2f * sh);

            return new int[]{sx, sy};
        } catch (Exception e) {
            return null;
        }
    }
}
