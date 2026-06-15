package com.hypixelclient.module.visual;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import org.lwjgl.glfw.GLFW;

public class ZoomModule extends Module {
    private final Setting fov = addSetting(new Setting("FOV", 15, 5, 45, 5));

    public ZoomModule() {
        super("Zoom", "Hold C to zoom in like a spyglass (no item needed)", Category.VISUAL, GLFW.GLFW_KEY_C);
    }

    public double getZoomFov() { return fov.getValue(); }
}
