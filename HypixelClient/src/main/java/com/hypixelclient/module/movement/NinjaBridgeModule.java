package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import com.hypixelclient.util.Humanizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import java.util.Random;

public class NinjaBridgeModule extends Module {
    // Delay between placements in ms. Randomised ±30% each time to avoid pattern AC.
    private final Setting delay = addSetting(new Setting("Delay", 4, 1, 10, 1));

    private long lastPlace = 0;
    private final Random rng = new Random();

    public NinjaBridgeModule() {
        super("NinjaBridge", "Scaffold that rotates into a legit angle before placing (AC-safer)", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;

        // Require movement input.
        boolean moving = client.options.forwardKey.isPressed()
                      || client.options.backKey.isPressed()
                      || client.options.leftKey.isPressed()
                      || client.options.rightKey.isPressed();
        if (!moving) return;

        // Only bridge over air.
        BlockPos target = client.player.getBlockPos().down();
        if (!client.world.getBlockState(target).isAir()) return;

        // Need a block in hand.
        if (!(client.player.getMainHandStack().getItem() instanceof BlockItem)) return;

        // Variable cooldown based on Delay ticks setting.
        long tickMs = (long)(delay.getValue()) * 50L;
        long variance = (long)(tickMs * 0.3);
        long needed = tickMs - variance + (long)(rng.nextFloat() * variance * 2);
        long now = System.currentTimeMillis();
        if (now - lastPlace < needed) return;

        // Find an adjacent solid face to place against — same priority as Scaffold.
        Direction[] dirs = {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP};
        for (Direction dir : dirs) {
            BlockPos neighbor = target.offset(dir);
            if (client.world.getBlockState(neighbor).isAir()) continue;

            // Calculate the exact face-centre we'd hit if we looked at the block normally.
            Direction face = dir.getOpposite();
            Vec3d hitVec = Vec3d.ofCenter(neighbor).add(Vec3d.of(face.getVector()).multiply(0.5));

            // Compute legit yaw / pitch toward hitVec from eye position.
            Vec3d eye = client.player.getEyePos();
            double dx = hitVec.x - eye.x;
            double dy = hitVec.y - eye.y;
            double dz = hitVec.z - eye.z;
            double horizDist = Math.sqrt(dx * dx + dz * dz);
            float wantedYaw   = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(-dx, dz)));
            float wantedPitch = (float) -Math.toDegrees(Math.atan2(dy, horizDist));

            // Save rotation, rotate, place, restore.
            float savedYaw   = client.player.getYaw();
            float savedPitch = client.player.getPitch();

            // Slight angle jitter (±2°) — humans never aim the geometric centre exactly.
            float jYaw   = wantedYaw   + Humanizer.jitter(0f, 0.08f) * 2f;
            float jPitch = MathHelper.clamp(wantedPitch + Humanizer.jitter(0f, 0.06f) * 2f, -90f, 90f);
            client.player.setYaw(jYaw);
            client.player.setPitch(jPitch);

            BlockHitResult hit = new BlockHitResult(hitVec, face, neighbor, false);
            try {
                client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hit);
                client.player.swingHand(Hand.MAIN_HAND);
                lastPlace = now;
            } catch (Exception ignored) {}

            // Restore camera instantly — player only sees a brief snap, server sees legit angle.
            client.player.setYaw(savedYaw);
            client.player.setPitch(savedPitch);
            return;
        }
    }
}
