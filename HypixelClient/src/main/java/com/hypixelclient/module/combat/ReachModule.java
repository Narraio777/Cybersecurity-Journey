package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ReachModule extends Module {
    // Vanilla = 3.0. Hypixel AC typically flags above ~3.4. Stay safe.
    private final Setting reach = addSetting(new Setting("Reach", 3, 3, 4, 1));

    public ReachModule() {
        super("Reach", "Extends your attack reach (server-side limit still applies)", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    private EntityAttributeInstance getAttribute(MinecraftClient client) {
        if (client.player == null) return null;
        return Registries.ATTRIBUTE.getEntry(Identifier.of("minecraft", "player.entity_interaction_range"))
            .map(e -> client.player.getAttributeInstance(e))
            .orElse(null);
    }

    public void onTick(MinecraftClient client) {
        var attr = getAttribute(client);
        if (attr == null) return;
        double target = isEnabled() ? reach.getValue() : 3.0;
        if (attr.getBaseValue() != target) attr.setBaseValue(target);
    }

    @Override
    public void onDisable() {
        var attr = getAttribute(MinecraftClient.getInstance());
        if (attr != null) attr.setBaseValue(3.0);
    }
}
