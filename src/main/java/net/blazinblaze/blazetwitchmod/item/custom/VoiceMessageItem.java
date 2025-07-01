package net.blazinblaze.blazetwitchmod.item.custom;

import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.networking.MailScreenS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.VoiceMessageS2CPayload;
import net.blazinblaze.blazetwitchmod.sound.BlazinBlazeTwitchModSounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class VoiceMessageItem extends Item {
    public VoiceMessageItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        String author = stack.getOrDefault(BlazinBlazeTwitchModComponents.VOICE_AUTHOR, "test");
        displayComponent.shouldDisplay(BlazinBlazeTwitchModComponents.VOICE_AUTHOR);
        textConsumer.accept(Text.translatable("item.blaze-twitch-mod.voice_author.info", author).formatted(Formatting.BLUE));
        //tooltip.add(Text.translatable("item.blaze-twitch-mod.mail_author.info", author).formatted(Formatting.GOLD));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {

        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient) {
            return ActionResult.PASS;
        }

        VoiceMessageS2CPayload payload = new VoiceMessageS2CPayload(stack.getOrDefault(BlazinBlazeTwitchModComponents.VOICE_MESSAGE, "test"), stack.getOrDefault(BlazinBlazeTwitchModComponents.VOICE_AUTHOR, "test"));
        ServerPlayNetworking.send((ServerPlayerEntity) user, payload);
        user.playSoundToPlayer(BlazinBlazeTwitchModSounds.VOICE_MESSAGE_EFFECT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

        OverlayMessageS2CPayload payload2 = new OverlayMessageS2CPayload(String.format("§l[%s's VC]§r ", stack.getOrDefault(BlazinBlazeTwitchModComponents.VOICE_AUTHOR, "test")) + stack.getOrDefault(BlazinBlazeTwitchModComponents.VOICE_MESSAGE, "test"));
        ServerPlayNetworking.send((ServerPlayerEntity) user, payload2);

        return ActionResult.SUCCESS;
    }
}
