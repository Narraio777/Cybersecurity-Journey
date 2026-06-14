package com.hypixelclient.mixin;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.movement.AutoBridgeModule;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (HypixelClient.getInstance() == null) return;
        AutoBridgeModule autoBridge = HypixelClient.getInstance().getModuleManager().get(AutoBridgeModule.class);
        if (autoBridge != null) autoBridge.onTick(client);
    }
}
