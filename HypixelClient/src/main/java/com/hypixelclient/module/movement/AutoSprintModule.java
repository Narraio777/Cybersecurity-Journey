package com.hypixelclient.module.movement;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class AutoSprintModule extends Module {
    public AutoSprintModule() {
        super("AutoSprint", "Automatically sprints without holding the sprint key", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
        setEnabled(true);
    }
}
