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
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class SneakBridgeModule extends Module {
    // Sneak for N ticks (stay on edge), then release for M ticks (move forward), repeat.
    private final Setting sneakTicks = addSetting(new Setting("SneakTicks", 3, 1, 10, 1));
    private final Setting moveTicks  = addSetting(new Setting("MoveTicks",  2, 1, 10, 1));

    private static final Direction[] DIRS = {
        Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP
    };

    private int phase = 0;
    private boolean drivingSneak = false;

    public SneakBridgeModule() {
        super("SneakBridge", "Bridges by toggling sneak in a rhythm (sneak/unsneak/sneak)", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    public void onDisable() {
        releaseSneak(MinecraftClient.getInstance());
        phase = 0;
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) {
            releaseSneak(client);
            return;
        }
        if (client.interactionManager == null) return;

        // Only act while moving and over air with a block in hand.
        boolean moving = client.options.forwardKey.isPressed()
                      || client.options.backKey.isPressed()
                      || client.options.leftKey.isPressed()
                      || client.options.rightKey.isPressed();
        BlockPos target = client.player.getBlockPos().down();
        boolean overAir = client.world.getBlockState(target).isAir();
        boolean hasBlock = client.player.getMainHandStack().getItem() instanceof BlockItem;

        if (!moving || !overAir || !hasBlock) {
            releaseSneak(client);
            phase = 0;
            return;
        }

        // Sneak rhythm: hold sneak for SneakTicks, release for MoveTicks, loop.
        int cycle = (int) (sneakTicks.getValue() + moveTicks.getValue());
        int pos = phase % cycle;
        boolean sneakNow = pos < (int) sneakTicks.getValue();

        client.options.sneakKey.setPressed(sneakNow);
        drivingSneak = true;
        phase++;

        // Place a block below against any adjacent solid face.
        for (Direction dir : DIRS) {
            BlockPos neighbor = target.offset(dir);
            if (client.world.getBlockState(neighbor).isAir()) continue;

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

    // Hand sneak control back to the player when we are no longer bridging.
    private void releaseSneak(MinecraftClient client) {
        if (drivingSneak && client != null && client.options != null) {
            client.options.sneakKey.setPressed(false);
            drivingSneak = false;
        }
    }
}
