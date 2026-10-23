package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;

public record OverlayMessageS2CPayload(String message) implements CustomPacketPayload {
    public static final Identifier OVERLAY_MESSAGE_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "overlay_message_payload");
    public static final CustomPacketPayload.Type<OverlayMessageS2CPayload> ID = new CustomPacketPayload.Type<>(OVERLAY_MESSAGE_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OverlayMessageS2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, OverlayMessageS2CPayload::message, OverlayMessageS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
