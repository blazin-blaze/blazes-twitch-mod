package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record MailScreenS2CPayload(String message, String mailType, String author, String farewell) implements CustomPacketPayload {
    public static final Identifier MAIL_SCREEN_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "new_blaze_mail_screen");
    public static final Type<MailScreenS2CPayload> ID = new Type<>(MAIL_SCREEN_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, MailScreenS2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, MailScreenS2CPayload::message,
            ByteBufCodecs.STRING_UTF8, MailScreenS2CPayload::mailType,
            ByteBufCodecs.STRING_UTF8, MailScreenS2CPayload::author,
            ByteBufCodecs.STRING_UTF8, MailScreenS2CPayload::farewell,
            MailScreenS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
