package com.hypixelclient.mixin;

import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels Minecraft's menu background blur. The blur is applied inside
 * Screen.applyBlur() whenever a screen is open over the world, independent
 * of whether the screen draws its own background — so removing the
 * renderBackground() call alone does not stop it.
 *
 * require = 0 so that if the method is absent/renamed in a given mappings
 * version, the mod still loads instead of crashing.
 */
@Mixin(Screen.class)
public class ScreenBlurMixin {

    @Inject(method = "applyBlur", at = @At("HEAD"), cancellable = true, require = 0)
    private void hypixelclient$noBlur(CallbackInfo ci) {
        ci.cancel();
    }
}
