package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record EmoteScreenS2CPayload(String emoteID, String imageType) implements CustomPayload {
    public static final Identifier EMOTE_SCREEN_PAYLOAD_ID = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "emote_screen_payload_id");
    public static final CustomPayload.Id<EmoteScreenS2CPayload> ID = new CustomPayload.Id<>(EMOTE_SCREEN_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, EmoteScreenS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, EmoteScreenS2CPayload::emoteID,
            PacketCodecs.STRING, EmoteScreenS2CPayload::imageType,
            EmoteScreenS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
