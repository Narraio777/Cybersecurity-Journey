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
    // Minimum ticks between placements (jitter adds ±1 tick randomly).
    private final Setting speed = addSetting(new Setting("Speed", 1, 1, 6, 1));

    // Fallback scan order if we can't infer a support block from movement.
    private static final Direction[] DIRS = {
        Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP
    };

    private int cooldown = 0;
    private final Random rng = new Random();

    public SneakBridgeModule() {
        super("SneakBridge", "Speedbridge without sneak packets: auto-aims the perfect angle, places, instantly restores (AC-safe)", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
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

        // Pick the block to place against. Preferred = the support block BEHIND your
        // movement (so the bridge extends along your path). Fall back to any solid face.
        Direction placeDir = chooseSupport(client, below);
        if (placeDir == null) return;

        BlockPos neighbor = below.offset(placeDir);
        Direction face = placeDir.getOpposite();

        // Hit the exact centre of the face we place against.
        Vec3d hitVec = Vec3d.ofCenter(neighbor).add(Vec3d.of(face.getVector()).multiply(0.5));

        // Compute the EXACT yaw/pitch from the eye to that point = the perfect angle.
        Vec3d eye = client.player.getEyePos();
        double dx = hitVec.x - eye.x;
        double dy = hitVec.y - eye.y;
        double dz = hitVec.z - eye.z;
        double h  = Math.sqrt(dx * dx + dz * dz);
        float wYaw   = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(-dx, dz)));
        float wPitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(dy, h)), -90f, 90f);

        float savedYaw   = client.player.getYaw();
        float savedPitch = client.player.getPitch();

        // Aim perfectly, place, restore — all in one tick (invisible to the player).
        client.player.setYaw(wYaw);
        client.player.setPitch(wPitch);

        BlockHitResult hit = new BlockHitResult(hitVec, face, neighbor, false);
        try {
            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hit);
            client.player.swingHand(Hand.MAIN_HAND);
        } catch (Exception ignored) {}

        client.player.setYaw(savedYaw);
        client.player.setPitch(savedPitch);

        cooldown = (int) speed.getValue() + rng.nextInt(2);
    }

    // Returns the direction from the gap toward a solid support block, preferring
    // the block behind the player's horizontal movement so the bridge follows your path.
    private Direction chooseSupport(MinecraftClient client, BlockPos gap) {
        Vec3d vel = client.player.getVelocity();
        double speedSq = vel.x * vel.x + vel.z * vel.z;

        if (speedSq > 0.0025) { // moving fast enough to infer a direction
            Direction behind;
            if (Math.abs(vel.x) > Math.abs(vel.z)) {
                behind = vel.x > 0 ? Direction.WEST : Direction.EAST;
            } else {
                behind = vel.z > 0 ? Direction.NORTH : Direction.SOUTH;
            }
            if (!client.world.getBlockState(gap.offset(behind)).isAir()) return behind;
        }

        // Fallback: first solid neighbour (block directly below, then any side).
        for (Direction dir : DIRS) {
            if (!client.world.getBlockState(gap.offset(dir)).isAir()) return dir;
        }
        return null;
    }
}
