package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import com.hypixelclient.util.Humanizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class TowerModule extends Module {
    private final Setting speed = addSetting(new Setting("Speed", 1, 1, 4, 1));

    private static final Direction[] SIDE_DIRS = {
        Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    private int cooldown = 0;

    public TowerModule() {
        super("Tower", "Places blocks under you while holding jump to build straight up", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;
        if (!client.options.jumpKey.isPressed()) return;
        if (!(client.player.getMainHandStack().getItem() instanceof BlockItem)) return;

        if (cooldown > 0) { cooldown--; return; }

        BlockPos feet    = client.player.getBlockPos();
        BlockPos toFill  = feet.down();

        if (!client.world.getBlockState(toFill).isAir()) return;

        // Prefer placing on top of block directly below the gap
        BlockPos support = toFill.down();
        if (!client.world.getBlockState(support).isAir()) {
            place(client, support, Direction.UP);
            cooldown = (int) speed.getValue() + Humanizer.gaussianInt(0, 1);
            return;
        }

        // Fallback: place against any solid side neighbor of the gap
        for (Direction dir : SIDE_DIRS) {
            BlockPos neighbor = toFill.offset(dir);
            if (!client.world.getBlockState(neighbor).isAir()) {
                place(client, neighbor, dir.getOpposite());
                cooldown = (int) speed.getValue() + Humanizer.gaussianInt(0, 1);
                return;
            }
        }
    }

    private void place(MinecraftClient client, BlockPos supportPos, Direction face) {
        Vec3d hitVec = Vec3d.ofCenter(supportPos).add(Vec3d.of(face.getVector()).multiply(0.5));
        BlockHitResult hit = new BlockHitResult(hitVec, face, supportPos, false);

        float savedYaw   = client.player.getYaw();
        float savedPitch = client.player.getPitch();

        float targetPitch = face == Direction.UP ? 87f : 65f;
        client.player.setPitch(MathHelper.clamp(
            targetPitch + Humanizer.jitter(0f, 0.03f) * 2f, -90f, 90f
        ));

        try {
            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hit);
            client.player.swingHand(Hand.MAIN_HAND);
        } catch (Exception ignored) {}

        client.player.setYaw(savedYaw);
        client.player.setPitch(savedPitch);
    }
}
