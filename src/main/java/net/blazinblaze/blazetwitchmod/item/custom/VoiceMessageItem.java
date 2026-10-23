package net.blazinblaze.blazetwitchmod.item.custom;

import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.networking.MailScreenS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.VoiceMessageS2CPayload;
import net.blazinblaze.blazetwitchmod.sound.BlazinBlazeTwitchModSounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class VoiceMessageItem extends Item {
    public VoiceMessageItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        String author = stack.getOrDefault(BlazinBlazeTwitchModComponents.VOICE_AUTHOR, "test");
        displayComponent.shows(BlazinBlazeTwitchModComponents.VOICE_AUTHOR);
        textConsumer.accept(Component.translatable("item.blaze-twitch-mod.voice_author.info", author).withStyle(ChatFormatting.BLUE));
        //tooltip.add(Text.translatable("item.blaze-twitch-mod.mail_author.info", author).formatted(Formatting.GOLD));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {

        ItemStack stack = user.getItemInHand(hand);

        if (world.isClientSide()) {
            return InteractionResult.PASS;
        }

        VoiceMessageS2CPayload payload = new VoiceMessageS2CPayload(stack.getOrDefault(BlazinBlazeTwitchModComponents.VOICE_MESSAGE, "test"), stack.getOrDefault(BlazinBlazeTwitchModComponents.VOICE_AUTHOR, "test"));
        ServerPlayNetworking.send((ServerPlayer) user, payload);
        user.playSound(BlazinBlazeTwitchModSounds.VOICE_MESSAGE_EFFECT, 1.0F, 1.0F);

        OverlayMessageS2CPayload payload2 = new OverlayMessageS2CPayload(String.format("§l[%s's VC]§r ", stack.getOrDefault(BlazinBlazeTwitchModComponents.VOICE_AUTHOR, "test")) + stack.getOrDefault(BlazinBlazeTwitchModComponents.VOICE_MESSAGE, "test"));
        ServerPlayNetworking.send((ServerPlayer) user, payload2);

        return InteractionResult.SUCCESS;
    }
}
