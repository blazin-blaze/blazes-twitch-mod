package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record CloseS2CPayload(String user) implements CustomPacketPayload {
    public static final Identifier CLOSE_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "close_payload");
    public static final Type<CloseS2CPayload> ID = new Type<>(CLOSE_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, CloseS2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, CloseS2CPayload::user,
            CloseS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
