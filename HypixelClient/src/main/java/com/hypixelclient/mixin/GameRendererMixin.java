package com.hypixelclient.mixin;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.visual.ZoomModule;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // getFov returns float in 1.21.4 — must use CallbackInfoReturnable<Float>.
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true, require = 0)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        if (HypixelClient.getInstance() == null) return;
        ZoomModule zoom = HypixelClient.getInstance().getModuleManager().get(ZoomModule.class);
        if (zoom == null || !zoom.isEnabled()) return;
        long win = net.minecraft.client.MinecraftClient.getInstance().getWindow().getHandle();
        if (GLFW.glfwGetKey(win, GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS) {
            cir.setReturnValue((float) zoom.getZoomFov());
        }
    }
}
