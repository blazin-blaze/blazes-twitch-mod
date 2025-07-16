package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record CloseS2CPayload(String message) implements CustomPayload {
    public static final Identifier CLOSE_PAYLOAD_ID = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "close_payload");
    public static final Id<CloseS2CPayload> ID = new Id<>(CLOSE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, CloseS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, CloseS2CPayload::message,
            CloseS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
