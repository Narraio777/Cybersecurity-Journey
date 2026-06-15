package com.hypixelclient.mixin;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.visual.ESPModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return; // already glowing, keep it
        if (HypixelClient.getInstance() == null) return;
        Entity self = (Entity) (Object) this;
        if (!(self instanceof PlayerEntity player)) return;
        ESPModule esp = HypixelClient.getInstance().getModuleManager().get(ESPModule.class);
        if (esp != null && esp.shouldGlow(player)) cir.setReturnValue(true);
    }
}
