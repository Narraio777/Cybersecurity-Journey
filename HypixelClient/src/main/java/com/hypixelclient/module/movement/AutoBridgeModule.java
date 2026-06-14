package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class AutoBridgeModule extends Module {
    public AutoBridgeModule() {
        super("AutoBridge", "Automatically places blocks below you while bridging backwards", Category.MOVEMENT, GLFW.GLFW_KEY_V);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;
        if (!client.options.sneakKey.isPressed()) return;

        // Only trigger when there is no block below the player
        BlockPos below = client.player.getBlockPos().down();
        BlockState belowState = client.world.getBlockState(below);
        if (!belowState.isAir()) return;

        // Only place if holding a block item
        if (!(client.player.getMainHandStack().getItem() instanceof BlockItem)) return;

        // Require a valid block face to place against
        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;
        if (!(hit instanceof BlockHitResult blockHit)) return;

        try {
            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHit);
            client.player.swingHand(Hand.MAIN_HAND);
        } catch (Exception ignored) {}
    }
}
