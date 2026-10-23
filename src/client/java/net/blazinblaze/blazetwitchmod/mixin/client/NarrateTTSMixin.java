package net.blazinblaze.blazetwitchmod.mixin.client;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.GameNarrator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.w3c.dom.Text;

@Mixin(GameNarrator.class)
public abstract class NarrateTTSMixin {
    @Shadow protected abstract NarratorStatus getStatus();

    @Shadow @Final private Narrator narrator;

    @Shadow protected abstract void narrateMessage(String text, boolean interrupt);

    @Inject(method = "saySystemChatQueued(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
    public void narrateWithoutActive(net.minecraft.network.chat.Component message, CallbackInfo ci) {
        if(this.getStatus().shouldNarrateSystem() && !message.getString().isEmpty() && this.narrator.active()) {
            ci.cancel();
        }else {
            if(message.getString().startsWith("[TTS]")) {
                this.narrator.clear();
                this.narrateMessage(message.getString().substring("[TTS]".length()), true);
            }else {
                ci.cancel();
            }
        }
    }
}
