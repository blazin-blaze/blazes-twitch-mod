package net.blazinblaze.blazetwitchmod.item.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class ImmunityShieldItem extends Item {
    public ImmunityShieldItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.literal("Gives you 3 minutes of immunity to all events.").formatted(Formatting.GOLD));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(!stack.isEmpty() && user instanceof ServerPlayerEntity serverPlayerEntity) {
            if(serverPlayerEntity.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                int currentDuration = serverPlayerEntity.getStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT).getDuration();
                serverPlayerEntity.removeStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT);
                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT, currentDuration + 20*180, 0, true, true, true));
                OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§a§lEVENTS BLOCKED FOR ANOTHER 3 MINUTES!");
                ServerPlayNetworking.send(serverPlayerEntity, payload);
                serverPlayerEntity.sendMessage(Text.literal("§5§l[BTM]§r §aEvents cannot impact you for an added 3 minutes!"), false);
            }else {
                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT, 20*180, 0, true, true, true));
                OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§a§lEVENTS BLOCKED FOR 3 MINUTES!");
                ServerPlayNetworking.send(serverPlayerEntity, payload);
                serverPlayerEntity.sendMessage(Text.literal("§5§l[BTM]§r §aEvents cannot impact you for the next 3 minutes!"), false);
            }
            serverPlayerEntity.playSoundToPlayer(SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            stack.decrementUnlessCreative(1, user);
            return ActionResult.CONSUME;
        }else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
