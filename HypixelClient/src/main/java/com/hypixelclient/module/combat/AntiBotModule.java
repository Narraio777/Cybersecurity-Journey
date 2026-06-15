package com.hypixelclient.module.combat;

import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.entity.player.PlayerEntity;
import java.util.regex.Pattern;

public class AntiBotModule extends Module {
    // Common bot name patterns (lots of numbers, or typical bot prefixes).
    private static final Pattern BOT_PATTERN = Pattern.compile(
        "(?i)(bot|npc|fake|dummy)|(\\d{4,})"
    );

    public AntiBotModule() {
        super("AntiBot", "Prevents AutoClicker and TriggerBot from targeting bot players", Category.COMBAT);
    }

    public boolean isBot(PlayerEntity player) {
        if (!isEnabled()) return false;
        String name = player.getName().getString();
        if (BOT_PATTERN.matcher(name).find()) return true;
        // Offline-mode UUIDs are version 3 (name-based) — real Hypixel players always have version 4.
        return player.getUuid().version() == 3;
    }
}
