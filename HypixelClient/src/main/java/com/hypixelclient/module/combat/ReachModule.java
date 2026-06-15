package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import org.lwjgl.glfw.GLFW;

public class ReachModule extends Module {
    // Vanilla = 3.0. Hypixel AC typically flags above ~3.4. Stay safe.
    private final Setting reach = addSetting(new Setting("Reach", 3, 3, 6, 0));

    public ReachModule() {
        super("Reach", "Extends your attack reach (server-side limit still applies)", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void onTick(MinecraftClient client) {
        if (client.player == null) return;
        var attr = client.player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
        if (attr == null) return;
        double target = isEnabled() ? reach.getValue() : 3.0;
        if (attr.getBaseValue() != target) attr.setBaseValue(target);
    }

    @Override
    public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        var attr = client.player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
        if (attr != null) attr.setBaseValue(3.0);
    }
}
