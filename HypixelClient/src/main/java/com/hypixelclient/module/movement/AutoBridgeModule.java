package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class AutoBridgeModule extends Module {
    // Check directions in this order: block below target first, then sides
    private static final Direction[] DIRS = {
        Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP
    };

    public AutoBridgeModule() {
        super("Scaffold", "Places blocks below your feet automatically while walking", Category.MOVEMENT, GLFW.GLFW_KEY_V);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;

        // Require movement input
        boolean moving = client.options.forwardKey.isPressed()
                      || client.options.backKey.isPressed()
                      || client.options.leftKey.isPressed()
                      || client.options.rightKey.isPressed();
        if (!moving) return;

        // Only scaffold if air is below
        BlockPos target = client.player.getBlockPos().down();
        if (!client.world.getBlockState(target).isAir()) return;

        // Need a block item in hand
        if (!(client.player.getMainHandStack().getItem() instanceof BlockItem)) return;

        // Find an adjacent solid block to place against
        for (Direction dir : DIRS) {
            BlockPos neighbor = target.offset(dir);
            if (client.world.getBlockState(neighbor).isAir()) continue;

            // Create a synthetic hit result targeting this neighbor's face
            Direction face = dir.getOpposite();
            Vec3d hitVec = Vec3d.ofCenter(target);
            BlockHitResult hit = new BlockHitResult(hitVec, face, neighbor, false);

            try {
                client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hit);
                client.player.swingHand(Hand.MAIN_HAND);
            } catch (Exception ignored) {}
            return;
        }
    }
}
