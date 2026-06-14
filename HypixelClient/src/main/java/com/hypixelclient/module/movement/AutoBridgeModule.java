package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class AutoBridgeModule extends Module {
    public AutoBridgeModule() {
        super("AutoBridge", "Automatically places blocks below you while bridging backwards", Category.MOVEMENT, GLFW.GLFW_KEY_V);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (!client.options.sneakKey.isPressed()) return;

        BlockPos below = client.player.getBlockPos().down();
        if (!client.world.getBlockState(below).isAir()) return;

        if (client.player.getMainHandStack().isEmpty()) return;

        Vec3d pos = client.player.getPos();
        BlockPos bridgePos = new BlockPos((int) Math.floor(pos.x), (int) Math.floor(pos.y) - 1, (int) Math.floor(pos.z));
        HitResult hit = client.crosshairTarget;
        if (hit instanceof BlockHitResult blockHit) {
            client.interactionManager.interactBlock(client.player, client.player.preferredHand, blockHit);
        }
    }
}
