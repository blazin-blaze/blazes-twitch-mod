package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record VoiceMsgNotifyS2CPayload(String message) implements CustomPacketPayload {
    public static final Identifier VOICE_NOTIFY_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "voice_notify_payload");
    public static final Type<VoiceMsgNotifyS2CPayload> ID = new Type<>(VOICE_NOTIFY_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, VoiceMsgNotifyS2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, VoiceMsgNotifyS2CPayload::message, VoiceMsgNotifyS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
