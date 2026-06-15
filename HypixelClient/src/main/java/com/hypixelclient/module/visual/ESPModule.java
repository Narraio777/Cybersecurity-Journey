package com.hypixelclient.module.visual;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class ESPModule extends Module {
    private final Setting range = addSetting(new Setting("Range", 64, 16, 128, 8));

    public ESPModule() {
        super("ESP", "Draws an outline around nearby players through walls", Category.VISUAL, GLFW.GLFW_KEY_UNKNOWN);
    }

    // Called from EntityMixin.isGlowing() for every entity each frame.
    public boolean shouldGlow(PlayerEntity player) {
        if (!isEnabled()) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;
        if (player == client.player) return false;
        return client.player.squaredDistanceTo(player) <= range.getValue() * range.getValue();
    }
}
