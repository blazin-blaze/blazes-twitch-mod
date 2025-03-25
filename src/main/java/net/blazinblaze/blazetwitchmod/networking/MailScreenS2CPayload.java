package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record MailScreenS2CPayload(String message, String type, String author, String farewell) implements CustomPayload {
    public static final Identifier MAIL_SCREEN_PAYLOAD_ID = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "new_blaze_mail_screen");
    public static final CustomPayload.Id<MailScreenS2CPayload> ID = new CustomPayload.Id<>(MAIL_SCREEN_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, MailScreenS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, MailScreenS2CPayload::message,
            PacketCodecs.STRING, MailScreenS2CPayload::type,
            PacketCodecs.STRING, MailScreenS2CPayload::author,
            PacketCodecs.STRING, MailScreenS2CPayload::farewell,
            MailScreenS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
