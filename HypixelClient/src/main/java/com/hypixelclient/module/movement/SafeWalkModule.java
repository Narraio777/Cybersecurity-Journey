package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

/**
 * Prevents walking off edges without sending sneak packets.
 * Works by checking whether each horizontal velocity component would take
 * the player's bounding box over air, and cancelling that component if so.
 */
public class SafeWalkModule extends Module {

    public SafeWalkModule() {
        super("SafeWalk", "Prevents you from walking off edges (no sneak packets)", Category.MOVEMENT, GLFW.GLFW_KEY_G);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (!client.player.isOnGround()) return;

        Vec3d vel = client.player.getVelocity();
        double vx = vel.x, vz = vel.z;
        if (vx == 0 && vz == 0) return;

        double cx  = client.player.getX();
        double cz  = client.player.getZ();
        // Check just below the player's feet for the block they're standing on.
        double checkY = client.player.getY() - 0.05;
        // Inset slightly from the actual BB width to avoid false positives from adjacent walls.
        double hw = client.player.getWidth() / 2.0 - 0.01;

        // If moving in X would leave no ground under any corner, cancel X.
        if (vx != 0 && !hasGround(client, cx + vx, checkY, cz, hw)) vx = 0;
        // If moving in Z would leave no ground under any corner, cancel Z.
        if (vz != 0 && !hasGround(client, cx, checkY, cz + vz, hw)) vz = 0;

        if (vx != vel.x || vz != vel.z) {
            client.player.setVelocity(vx, vel.y, vz);
        }
    }

    // Returns true if at least one of the 4 bottom corners of the player's
    // bounding box (centred at cx, cz) has a non-air block below it.
    private boolean hasGround(MinecraftClient client, double cx, double y, double cz, double hw) {
        return solid(client, cx - hw, y, cz - hw)
            || solid(client, cx + hw, y, cz - hw)
            || solid(client, cx - hw, y, cz + hw)
            || solid(client, cx + hw, y, cz + hw);
    }

    private boolean solid(MinecraftClient client, double x, double y, double z) {
        return !client.world.getBlockState(BlockPos.ofFloored(x, y, z)).isAir();
    }
}
