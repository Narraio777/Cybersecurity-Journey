package com.hypixelclient.mixin;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.movement.SafeWalkModule;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(at = @At("RETURN"), method = "isAutoJumpEnabled()Z", cancellable = true)
    private void onIsAutoJumpEnabled(CallbackInfoReturnable<Boolean> cir) {
        if (HypixelClient.getInstance() == null) return;
        SafeWalkModule safeWalk = HypixelClient.getInstance().getModuleManager().get(SafeWalkModule.class);
        if (safeWalk != null && safeWalk.isEnabled()) {
            cir.setReturnValue(false);
        }
    }
    // isSneaking injection removed — SafeWalk now uses velocity clamping, no sneak packets.
}
