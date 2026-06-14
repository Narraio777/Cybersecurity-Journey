package com.hypixelclient.mixin;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.visual.CustomCrosshairModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "renderCrosshair")
    private void onRenderCrosshair(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (HypixelClient.getInstance() == null) return;
        CustomCrosshairModule mod = HypixelClient.getInstance().getModuleManager().get(CustomCrosshairModule.class);
        if (mod != null && mod.isEnabled()) {
            // crosshair rendering handled in HUD callback
        }
    }
}
