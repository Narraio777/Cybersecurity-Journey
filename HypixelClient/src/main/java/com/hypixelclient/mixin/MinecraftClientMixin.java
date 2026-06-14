package com.hypixelclient.mixin;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.combat.AimAssistModule;
import com.hypixelclient.module.combat.AutoClickerModule;
import com.hypixelclient.module.combat.FastPlaceModule;
import com.hypixelclient.module.hud.CPSCounterModule;
import com.hypixelclient.module.movement.AutoBridgeModule;
import com.hypixelclient.module.movement.AutoSprintModule;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow private int itemUseCooldown;

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (HypixelClient.getInstance() == null || client.player == null) return;

        AutoBridgeModule autoBridge = HypixelClient.getInstance().getModuleManager().get(AutoBridgeModule.class);
        if (autoBridge != null) autoBridge.onTick(client);

        AutoSprintModule autoSprint = HypixelClient.getInstance().getModuleManager().get(AutoSprintModule.class);
        if (autoSprint != null && autoSprint.isEnabled() && !client.player.isSneaking()) {
            client.player.setSprinting(true);
        }

        AutoClickerModule autoClicker = HypixelClient.getInstance().getModuleManager().get(AutoClickerModule.class);
        if (autoClicker != null) autoClicker.onTick(client);

        AimAssistModule aimAssist = HypixelClient.getInstance().getModuleManager().get(AimAssistModule.class);
        if (aimAssist != null) aimAssist.onTick(client);

        FastPlaceModule fastPlace = HypixelClient.getInstance().getModuleManager().get(FastPlaceModule.class);
        if (fastPlace != null && fastPlace.isEnabled() && itemUseCooldown > 0) {
            itemUseCooldown = 0;
        }
    }

    @Inject(at = @At("HEAD"), method = "doAttack")
    private void onLeftClick(CallbackInfo ci) {
        if (HypixelClient.getInstance() == null) return;
        CPSCounterModule cps = HypixelClient.getInstance().getModuleManager().get(CPSCounterModule.class);
        if (cps != null) cps.registerLeftClick();
    }

    @Inject(at = @At("RETURN"), method = "doItemUse")
    private void onRightClick(CallbackInfo ci) {
        if (HypixelClient.getInstance() == null) return;
        CPSCounterModule cps = HypixelClient.getInstance().getModuleManager().get(CPSCounterModule.class);
        if (cps != null) cps.registerRightClick();
    }
}
