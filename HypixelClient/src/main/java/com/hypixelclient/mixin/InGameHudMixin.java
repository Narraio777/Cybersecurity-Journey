package com.hypixelclient.mixin;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.visual.NoFireModule;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    // Cancel the fire overlay render. require=0 so a wrong Yarn name won't crash.
    @Inject(method = "renderFireOverlay(Lnet/minecraft/client/gui/DrawContext;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void noFire(CallbackInfo ci) {
        if (shouldCancel()) ci.cancel();
    }

    // Pumpkin/helmet item overlay (shown when wearing a carved pumpkin).
    @Inject(method = "renderMiscOverlays", at = @At("HEAD"), cancellable = true, require = 0)
    private void noMiscOverlay(CallbackInfo ci) {
        if (shouldCancel()) ci.cancel();
    }

    private boolean shouldCancel() {
        if (HypixelClient.getInstance() == null) return false;
        NoFireModule m = HypixelClient.getInstance().getModuleManager().get(NoFireModule.class);
        return m != null && m.isEnabled();
    }
}
