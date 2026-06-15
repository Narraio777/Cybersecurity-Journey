package com.hypixelclient.module.misc;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class AutoGGModule extends Module {
    private final Setting delayTicks = addSetting(new Setting("Delay", 40, 10, 100, 10));

    private int countdown = -1;

    public AutoGGModule() {
        super("AutoGG", "Automatically types 'gg' in chat when a Hypixel game ends", Category.MISC, GLFW.GLFW_KEY_UNKNOWN);
    }

    // Called from the chat mixin when a game-end message is detected.
    public void onGameEnd() {
        if (isEnabled() && countdown < 0) countdown = (int) delayTicks.getValue();
    }

    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || countdown < 0) return;
        countdown--;
        if (countdown <= 0) {
            client.player.networkHandler.sendChatMessage("gg");
            countdown = -1;
        }
    }
}
