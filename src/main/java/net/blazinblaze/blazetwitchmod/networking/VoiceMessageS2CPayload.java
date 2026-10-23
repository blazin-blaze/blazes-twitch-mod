package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;

public record VoiceMessageS2CPayload(String message, String author) implements CustomPacketPayload {
    public static final Identifier VOICE_MESSAGE_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "voice_message_payload");
    public static final CustomPacketPayload.Type<VoiceMessageS2CPayload> ID = new CustomPacketPayload.Type<>(VOICE_MESSAGE_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, VoiceMessageS2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, VoiceMessageS2CPayload::message,
            ByteBufCodecs.STRING_UTF8, VoiceMessageS2CPayload::author,
            VoiceMessageS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
