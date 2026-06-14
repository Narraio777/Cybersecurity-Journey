package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import org.lwjgl.glfw.GLFW;

public class VelocityModule extends Module {
    // 0 = full anti-knockback, 100 = vanilla knockback
    private final Setting horizontal = addSetting(new Setting("Horizontal", 0, 0, 100, 10));
    private final Setting vertical   = addSetting(new Setting("Vertical",   0, 0, 100, 10));

    public VelocityModule() {
        super("Velocity", "Reduces or cancels the knockback you take", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public double getHorizontal() { return horizontal.getValue() / 100.0; }
    public double getVertical()   { return vertical.getValue() / 100.0; }
}
