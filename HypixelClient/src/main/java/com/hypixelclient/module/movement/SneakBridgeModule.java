package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import java.util.Random;

/**
 * Speedbridge without sneak:
 * Instead of sending sneak/unsneak packets (trivially flagged by AC),
 * this module briefly rotates the camera to a natural downward placement
 * angle, places the block, then restores your look direction instantly.
 * No sneak packets ever sent — indistinguishable from a fast human
 * who times their block placements while running.
 */
public class SneakBridgeModule extends Module {
    // How far down to look when placing (legit speedbridgers look ~75-80°).
    private final Setting pitchAngle = addSetting(new Setting("Pitch", 78, 60, 89, 1));
    // Minimum ticks between placements (jitter adds ±1 tick randomly).
    private final Setting speed      = addSetting(new Setting("Speed", 2, 1, 6, 1));

    private static final Direction[] DIRS = {
        Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP
    };

    private int cooldown = 0;
    private final Random rng = new Random();

    public SneakBridgeModule() {
        super("SneakBridge", "Speedbridge without sneak packets: rotates down to place, instantly restores (AC-safe)", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;

        if (cooldown > 0) { cooldown--; return; }

        boolean moving = client.options.forwardKey.isPressed()
                      || client.options.backKey.isPressed()
                      || client.options.leftKey.isPressed()
                      || client.options.rightKey.isPressed();
        if (!moving) return;

        BlockPos below = client.player.getBlockPos().down();
        if (!client.world.getBlockState(below).isAir()) return;
        if (!(client.player.getMainHandStack().getItem() instanceof BlockItem)) return;

        for (Direction dir : DIRS) {
            BlockPos neighbor = below.offset(dir);
            if (client.world.getBlockState(neighbor).isAir()) continue;

            Direction face = dir.getOpposite();

            // Hit the centre of the face we're placing against.
            Vec3d hitVec = Vec3d.ofCenter(neighbor)
                .add(Vec3d.of(face.getVector()).multiply(0.5));

            // Compute a natural yaw/pitch toward that face from the player's eye.
            Vec3d eye = client.player.getEyePos();
            double dx = hitVec.x - eye.x;
            double dy = hitVec.y - eye.y;
            double dz = hitVec.z - eye.z;
            double h   = Math.sqrt(dx * dx + dz * dz);
            float  wYaw   = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(-dx, dz)));
            // Force pitch into the configured "looking down" range regardless of
            // the raw angle — this is what a real speedbridger does.
            float  wPitch = MathHelper.clamp(
                (float) -Math.toDegrees(Math.atan2(dy, h)),
                (float) pitchAngle.getValue() - 5f,
                (float) pitchAngle.getValue()
            );

            float savedYaw   = client.player.getYaw();
            float savedPitch = client.player.getPitch();

            client.player.setYaw(wYaw);
            client.player.setPitch(wPitch);

            BlockHitResult hit = new BlockHitResult(hitVec, face, neighbor, false);
            try {
                client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hit);
                client.player.swingHand(Hand.MAIN_HAND);
            } catch (Exception ignored) {}

            // Restore instantly — server sees a legit downward angle, client sees no camera movement.
            client.player.setYaw(savedYaw);
            client.player.setPitch(savedPitch);

            // Variable cooldown: base speed ± 1 tick of jitter.
            cooldown = (int) speed.getValue() + rng.nextInt(2);
            return;
        }
    }
}
