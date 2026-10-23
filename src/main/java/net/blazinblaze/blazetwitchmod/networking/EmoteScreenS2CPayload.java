package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record EmoteScreenS2CPayload(String emoteID, String imageType) implements CustomPacketPayload {
    public static final Identifier EMOTE_SCREEN_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "emote_screen_payload_id");
    public static final CustomPacketPayload.Type<EmoteScreenS2CPayload> ID = new CustomPacketPayload.Type<>(EMOTE_SCREEN_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, EmoteScreenS2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, EmoteScreenS2CPayload::emoteID,
            ByteBufCodecs.STRING_UTF8, EmoteScreenS2CPayload::imageType,
            EmoteScreenS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
