package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record FullscreenS2CPayload(String message) implements CustomPacketPayload {
    public static final Identifier FULLSCREEN_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "fullscreen_payload");
    public static final Type<FullscreenS2CPayload> ID = new Type<>(FULLSCREEN_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, FullscreenS2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, FullscreenS2CPayload::message,
            FullscreenS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
