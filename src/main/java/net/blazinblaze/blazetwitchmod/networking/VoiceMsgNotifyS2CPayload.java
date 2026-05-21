package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record VoiceMsgNotifyS2CPayload(String message) implements CustomPayload {
    public static final Identifier VOICE_NOTIFY_PAYLOAD_ID = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "voice_notify_payload");
    public static final Id<VoiceMsgNotifyS2CPayload> ID = new Id<>(VOICE_NOTIFY_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, VoiceMsgNotifyS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, VoiceMsgNotifyS2CPayload::message, VoiceMsgNotifyS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
