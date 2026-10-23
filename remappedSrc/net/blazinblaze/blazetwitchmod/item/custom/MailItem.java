package net.blazinblaze.blazetwitchmod.item.custom;

import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.networking.FollowNotifyS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.MailScreenS2CPayload;
import net.blazinblaze.blazetwitchmod.sound.BlazinBlazeTwitchModSounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

import java.util.List;

public class MailItem extends Item {

    public MailItem(net.minecraft.item.Item.Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        String author = stack.getOrDefault(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test");
        tooltip.add(Text.translatable("item.blaze-twitch-mod.mail_author.info", author).formatted(Formatting.GOLD));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {

        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient) {
            return ActionResult.PASS;
        }

        MailScreenS2CPayload payload = new MailScreenS2CPayload(stack.getOrDefault(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test"), stack.getOrDefault(BlazinBlazeTwitchModComponents.MAIL_TYPE, "default"),stack.getOrDefault(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test"),stack.getOrDefault(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
        ServerPlayNetworking.send((ServerPlayerEntity) user, payload);
        user.playSoundToPlayer(BlazinBlazeTwitchModSounds.MAIL_ITEM_USE, SoundCategory.NEUTRAL, 1.0F, 1.0F);

        return ActionResult.SUCCESS;
    }
}
