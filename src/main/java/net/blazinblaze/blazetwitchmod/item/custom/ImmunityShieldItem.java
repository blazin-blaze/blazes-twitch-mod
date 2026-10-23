package net.blazinblaze.blazetwitchmod.item.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.effect.MobEffectInstance;
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

import java.util.function.Consumer;

public class ImmunityShieldItem extends Item {
    public ImmunityShieldItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(Component.literal("Gives you 3 minutes of immunity to all events.").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if(!stack.isEmpty() && user instanceof ServerPlayer serverPlayerEntity) {
            if(serverPlayerEntity.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                int currentDuration = serverPlayerEntity.getEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT).getDuration();
                serverPlayerEntity.removeEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT);
                serverPlayerEntity.addEffect(new MobEffectInstance(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT, currentDuration + 20*180, 0, true, true, true));
                OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§a§lEVENTS BLOCKED FOR ANOTHER 3 MINUTES!");
                ServerPlayNetworking.send(serverPlayerEntity, payload);
                serverPlayerEntity.sendSystemMessage(Component.literal("§5§l[BTM]§r §aEvents cannot impact you for an added 3 minutes!"), false);
            }else {
                serverPlayerEntity.addEffect(new MobEffectInstance(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT, 20*180, 0, true, true, true));
                OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§a§lEVENTS BLOCKED FOR 3 MINUTES!");
                ServerPlayNetworking.send(serverPlayerEntity, payload);
                serverPlayerEntity.sendSystemMessage(Component.literal("§5§l[BTM]§r §aEvents cannot impact you for the next 3 minutes!"), false);
            }
            serverPlayerEntity.playSound(SoundEvents.WANDERING_TRADER_DRINK_POTION, 1.0F, 1.0F);
            stack.consume(1, user);
            return InteractionResult.CONSUME;
        }else {
            return InteractionResult.FAIL;
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
