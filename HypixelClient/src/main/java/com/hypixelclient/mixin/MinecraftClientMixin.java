package com.hypixelclient.mixin;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.combat.AimAssistModule;
import com.hypixelclient.module.combat.AutoClickerModule;
import com.hypixelclient.module.combat.CriticalsModule;
import com.hypixelclient.module.combat.FastPlaceModule;
import com.hypixelclient.module.combat.ReachModule;
import com.hypixelclient.module.combat.TriggerBotModule;
import com.hypixelclient.module.visual.FullBrightModule;
import com.hypixelclient.module.visual.HitColorModule;
import com.hypixelclient.module.hud.CPSCounterModule;
import com.hypixelclient.module.misc.AntiAfkModule;
import com.hypixelclient.module.misc.AutoGGModule;
import com.hypixelclient.module.movement.AutoBridgeModule;
import com.hypixelclient.module.movement.AutoSprintModule;
import com.hypixelclient.module.movement.NinjaBridgeModule;
import com.hypixelclient.module.movement.SneakBridgeModule;
import com.hypixelclient.module.movement.BunnyHopModule;
import com.hypixelclient.module.movement.SafeWalkModule;
import com.hypixelclient.module.movement.SprintResetModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.lang.reflect.Field;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (HypixelClient.getInstance() == null || client.player == null) return;

        AutoBridgeModule autoBridge = HypixelClient.getInstance().getModuleManager().get(AutoBridgeModule.class);
        if (autoBridge != null) autoBridge.onTick(client);

        NinjaBridgeModule ninjaBridge = HypixelClient.getInstance().getModuleManager().get(NinjaBridgeModule.class);
        if (ninjaBridge != null) ninjaBridge.onTick(client);

        SneakBridgeModule sneakBridge = HypixelClient.getInstance().getModuleManager().get(SneakBridgeModule.class);
        if (sneakBridge != null) sneakBridge.onTick(client);

        AutoSprintModule autoSprint = HypixelClient.getInstance().getModuleManager().get(AutoSprintModule.class);
        if (autoSprint != null && autoSprint.isEnabled() && !client.player.isSneaking()) {
            client.player.setSprinting(true);
        }

        // SprintReset must run after AutoSprint so it can override sprint during the W-Tap window.
        SprintResetModule sprintReset = HypixelClient.getInstance().getModuleManager().get(SprintResetModule.class);
        if (sprintReset != null) sprintReset.onTick(client);

        SafeWalkModule safeWalk = HypixelClient.getInstance().getModuleManager().get(SafeWalkModule.class);
        if (safeWalk != null) safeWalk.onTick(client);

        BunnyHopModule bunnyHop = HypixelClient.getInstance().getModuleManager().get(BunnyHopModule.class);
        if (bunnyHop != null) bunnyHop.onTick(client);

        FullBrightModule fullBright = HypixelClient.getInstance().getModuleManager().get(FullBrightModule.class);
        if (fullBright != null) fullBright.onTick(client);

        AutoClickerModule autoClicker = HypixelClient.getInstance().getModuleManager().get(AutoClickerModule.class);
        if (autoClicker != null) autoClicker.onTick(client);

        TriggerBotModule triggerBot = HypixelClient.getInstance().getModuleManager().get(TriggerBotModule.class);
        if (triggerBot != null) triggerBot.onTick(client);

        CriticalsModule criticals = HypixelClient.getInstance().getModuleManager().get(CriticalsModule.class);
        if (criticals != null) criticals.onTick(client);

        AimAssistModule aimAssist = HypixelClient.getInstance().getModuleManager().get(AimAssistModule.class);
        if (aimAssist != null) aimAssist.onTick(client);

        ReachModule reach = HypixelClient.getInstance().getModuleManager().get(ReachModule.class);
        if (reach != null) reach.onTick(client);

        AntiAfkModule antiAfk = HypixelClient.getInstance().getModuleManager().get(AntiAfkModule.class);
        if (antiAfk != null) antiAfk.onTick(client);

        AutoGGModule autoGG = HypixelClient.getInstance().getModuleManager().get(AutoGGModule.class);
        if (autoGG != null) autoGG.onTick(client);
    }

    @Inject(at = @At("RETURN"), method = "doItemUse")
    private void onItemUse(CallbackInfo ci) {
        if (HypixelClient.getInstance() == null) return;

        CPSCounterModule cps = HypixelClient.getInstance().getModuleManager().get(CPSCounterModule.class);
        if (cps != null) cps.registerRightClick();

        FastPlaceModule fp = HypixelClient.getInstance().getModuleManager().get(FastPlaceModule.class);
        if (fp != null && fp.isEnabled()) {
            resetItemCooldown();
        }
    }

    @Inject(at = @At("HEAD"), method = "doAttack")
    private void onLeftClick(CallbackInfoReturnable<Boolean> ci) {
        if (HypixelClient.getInstance() == null) return;
        CPSCounterModule cps = HypixelClient.getInstance().getModuleManager().get(CPSCounterModule.class);
        if (cps != null) cps.registerLeftClick();

        MinecraftClient client = (MinecraftClient) (Object) this;
        Entity target = client.targetedEntity;
        if (target instanceof LivingEntity && target != client.player) {
            SprintResetModule sprintReset = HypixelClient.getInstance().getModuleManager().get(SprintResetModule.class);
            if (sprintReset != null) sprintReset.onHit();

            HitColorModule hitColor = HypixelClient.getInstance().getModuleManager().get(HitColorModule.class);
            if (hitColor != null) hitColor.registerHit();
        }
    }

    private void resetItemCooldown() {
        // Try known Yarn field names across MC versions; silently skip if not found.
        for (String name : new String[]{"itemUseCooldown", "attackCooldown", "rightClickDelay"}) {
            try {
                Field f = MinecraftClient.class.getDeclaredField(name);
                f.setAccessible(true);
                f.setInt(this, 0);
                return;
            } catch (Exception ignored) {}
        }
    }
}
