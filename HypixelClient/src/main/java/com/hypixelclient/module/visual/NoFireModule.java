package com.hypixelclient.module.visual;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class NoFireModule extends Module {
    public NoFireModule() {
        super("NoFire", "Hides the fire and pumpkin overlays from your screen", Category.VISUAL, GLFW.GLFW_KEY_UNKNOWN);
    }
}
