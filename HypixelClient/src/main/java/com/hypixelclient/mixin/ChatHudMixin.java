package com.hypixelclient.mixin;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.misc.AutoGGModule;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At("HEAD"), require = 0)
    private void onAddMessage(Text message, Object signature, Object indicator, CallbackInfo ci) {
        if (HypixelClient.getInstance() == null) return;
        AutoGGModule gg = HypixelClient.getInstance().getModuleManager().get(AutoGGModule.class);
        if (gg == null) return;

        String text = message.getString().toLowerCase();
        // Hypixel end-of-game messages
        if (text.contains("winner") || text.contains("game over") || text.contains("you won")
         || text.contains("victory") || text.contains("defeat") || text.contains("eliminated")) {
            gg.onGameEnd();
        }
    }
}
