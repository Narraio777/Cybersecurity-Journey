package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class SafeWalkModule extends Module {
    public SafeWalkModule() {
        super("SafeWalk", "Prevents you from walking off edges", Category.MOVEMENT, GLFW.GLFW_KEY_G);
    }
}
