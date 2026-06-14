package com.hypixelclient.mixin;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.combat.VelocityModule;
import com.hypixelclient.module.movement.SneakBridgeModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(at = @At("RETURN"), method = "takeKnockback")
    private void onTakeKnockback(double strength, double x, double z, CallbackInfo ci) {
        if (HypixelClient.getInstance() == null) return;

        LivingEntity self = (LivingEntity) (Object) this;
        MinecraftClient client = MinecraftClient.getInstance();
        if (self != client.player) return;

        VelocityModule velocity = HypixelClient.getInstance().getModuleManager().get(VelocityModule.class);
        if (velocity != null && velocity.isEnabled()) {
            Vec3d v = self.getVelocity();
            self.setVelocity(v.x * velocity.getHorizontal(), v.y * velocity.getVertical(), v.z * velocity.getHorizontal());
            return;
        }

        // Auto-correct knockback while bridging so a hit can't knock you off the bridge.
        SneakBridgeModule bridge = HypixelClient.getInstance().getModuleManager().get(SneakBridgeModule.class);
        if (bridge != null && bridge.isCorrecting(client)) {
            double keep = bridge.getKeepFactor();
            Vec3d v = self.getVelocity();
            self.setVelocity(v.x * keep, v.y, v.z * keep);
        }
    }
}
