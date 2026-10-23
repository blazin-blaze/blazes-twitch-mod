package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record MailNotifyS2CPayload(String message) implements CustomPacketPayload {
    public static final Identifier MAIL_NOTIFY_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "mail_notify_payload");
    public static final Type<MailNotifyS2CPayload> ID = new Type<>(MAIL_NOTIFY_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, MailNotifyS2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, MailNotifyS2CPayload::message, MailNotifyS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
