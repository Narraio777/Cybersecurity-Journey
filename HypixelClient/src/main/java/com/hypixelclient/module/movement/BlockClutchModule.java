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

public class BlockClutchModule extends Module {
    private final Setting fallSpeed = addSetting(new Setting("FallSpeed", 15, 5, 40, 5));

    private static final Direction[] SIDE_DIRS = {
        Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    private boolean triggered = false;

    public BlockClutchModule() {
        super("BlockClutch", "Auto-places a block on a nearby wall when falling to prevent death", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;

        if (client.player.isOnGround()) { triggered = false; return; }

        double vy        = client.player.getVelocity().y;
        double threshold = -fallSpeed.getValue() / 10.0;
        if (vy > threshold) return;
        if (triggered) return;
        if (!(client.player.getMainHandStack().getItem() instanceof BlockItem)) return;

        BlockPos pos = client.player.getBlockPos();

        // Scan current Y, one below, one above
        for (int dy = 0; dy <= 1; dy++) {
            BlockPos check = pos.add(0, -dy, 0);
            for (Direction dir : SIDE_DIRS) {
                BlockPos neighbor = check.offset(dir);
                if (!client.world.getBlockState(neighbor).isAir()) {
                    Direction face = dir.getOpposite(); // face of neighbor pointing toward player
                    Vec3d hitVec = Vec3d.ofCenter(neighbor).add(Vec3d.of(face.getVector()).multiply(0.5));
                    BlockHitResult hit = new BlockHitResult(hitVec, face, neighbor, false);

                    Vec3d eye  = client.player.getEyePos();
                    double dx  = hitVec.x - eye.x;
                    double ddy = hitVec.y - eye.y;
                    double ddz = hitVec.z - eye.z;
                    double h   = Math.sqrt(dx * dx + ddz * ddz);
                    float wYaw   = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(-dx, ddz)));
                    float wPitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(ddy, h)), -90f, 90f);

                    float savedYaw   = client.player.getYaw();
                    float savedPitch = client.player.getPitch();

                    client.player.setYaw(wYaw);
                    client.player.setPitch(wPitch);

                    try {
                        client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hit);
                        client.player.swingHand(Hand.MAIN_HAND);
                        triggered = true;
                    } catch (Exception ignored) {}

                    client.player.setYaw(savedYaw);
                    client.player.setPitch(savedPitch);

                    if (triggered) return;
                }
            }
        }
    }
}
