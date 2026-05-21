package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MailNotifyS2CPayload(String message) implements CustomPayload {
    public static final Identifier MAIL_NOTIFY_PAYLOAD_ID = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "mail_notify_payload");
    public static final Id<MailNotifyS2CPayload> ID = new Id<>(MAIL_NOTIFY_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, MailNotifyS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, MailNotifyS2CPayload::message, MailNotifyS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
