package net.blazinblaze.blazetwitchmod.item.custom;

import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.networking.FollowNotifyS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.MailScreenS2CPayload;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public class MailItem extends Item {

    public MailItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        String author = stack.getOrDefault(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test");
        displayComponent.shows(BlazinBlazeTwitchModComponents.MAIL_AUTHOR);
        textConsumer.accept(Component.translatable("item.blaze-twitch-mod.mail_author.info", author).withStyle(ChatFormatting.GOLD));
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

        user.playSound(BlazinBlazeTwitchModSounds.MAIL_ITEM_USE, 1.0F, 1.0F);
        MailScreenS2CPayload payload = new MailScreenS2CPayload(stack.getOrDefault(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test"), stack.getOrDefault(BlazinBlazeTwitchModComponents.MAIL_TYPE, "default"),stack.getOrDefault(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test"),stack.getOrDefault(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
        ServerPlayNetworking.send((ServerPlayer) user, payload);

        return InteractionResult.SUCCESS;
    }
}
