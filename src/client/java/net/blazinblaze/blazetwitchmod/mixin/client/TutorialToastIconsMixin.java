package net.blazinblaze.blazetwitchmod.mixin.client;

import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TutorialToast.Icons.class)
enum TutorialToastIconsMixin {
    BLAZE_TWITCH_MOD_MAIL(Identifier.fromNamespaceAndPath("blaze-twitch-mod", "toast/mail_toast_icon")),
    BLAZE_TWITCH_MOD_VOICE_MSG(Identifier.fromNamespaceAndPath("blaze-twitch-mod", "toast/mail_toast_icon"));

    @Shadow
    TutorialToastIconsMixin(Identifier sprite) {
    }
}
