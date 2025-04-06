package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public record VoiceMessageS2CPayload(String message, String author) implements CustomPayload {
    public static final Identifier VOICE_MESSAGE_PAYLOAD_ID = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "voice_message_payload");
    public static final CustomPayload.Id<VoiceMessageS2CPayload> ID = new CustomPayload.Id<>(VOICE_MESSAGE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, VoiceMessageS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, VoiceMessageS2CPayload::message,
            PacketCodecs.STRING, VoiceMessageS2CPayload::author,
            VoiceMessageS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
