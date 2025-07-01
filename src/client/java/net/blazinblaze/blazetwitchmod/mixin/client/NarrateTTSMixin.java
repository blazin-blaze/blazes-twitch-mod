package net.blazinblaze.blazetwitchmod.mixin.client;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.w3c.dom.Text;

@Mixin(NarratorManager.class)
public abstract class NarrateTTSMixin {
    @Shadow protected abstract NarratorMode getNarratorMode();

    @Shadow @Final private Narrator narrator;

    @Shadow protected abstract void say(String text, boolean interrupt);

    @Inject(method = "narrate(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    public void narrateWithoutActive(net.minecraft.text.Text message, CallbackInfo ci) {
        if(this.getNarratorMode().shouldNarrateSystem() && !message.getString().isEmpty() && this.narrator.active()) {
            ci.cancel();
        }else {
            if(message.getString().startsWith("[TTS]")) {
                this.narrator.clear();
                this.say(message.getString().substring("[TTS]".length()), true);
            }else {
                ci.cancel();
            }
        }
    }
}
