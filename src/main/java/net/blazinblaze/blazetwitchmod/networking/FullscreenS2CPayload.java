package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record FullscreenS2CPayload(String message) implements CustomPayload {
    public static final Identifier FULLSCREEN_PAYLOAD_ID = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "fullscreen_payload");
    public static final Id<FullscreenS2CPayload> ID = new Id<>(FULLSCREEN_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, FullscreenS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, FullscreenS2CPayload::message,
            FullscreenS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
