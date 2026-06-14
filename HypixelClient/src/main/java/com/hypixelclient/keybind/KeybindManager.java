package com.hypixelclient.keybind;

import com.hypixelclient.gui.ModuleScreen;
import com.hypixelclient.module.ModuleManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {
    private final ModuleManager moduleManager;
    private final KeyBinding openGuiKey;

    public KeybindManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hypixelclient.opengui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.hypixelclient"
        ));
    }

    public void onTick(MinecraftClient client) {
        while (openGuiKey.wasPressed()) {
            if (client.currentScreen == null) {
                client.setScreen(new ModuleScreen());
            }
        }
    }
}
