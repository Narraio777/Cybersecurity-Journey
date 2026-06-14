package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class FastPlaceModule extends Module {
    public FastPlaceModule() {
        super("FastPlace", "Removes the cooldown between block placements", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }
}
