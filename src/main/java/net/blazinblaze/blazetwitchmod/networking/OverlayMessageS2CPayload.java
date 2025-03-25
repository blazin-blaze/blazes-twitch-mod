package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public record OverlayMessageS2CPayload(String message) implements CustomPayload {
    public static final Identifier OVERLAY_MESSAGE_PAYLOAD_ID = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "overlay_message_payload");
    public static final CustomPayload.Id<OverlayMessageS2CPayload> ID = new CustomPayload.Id<>(OVERLAY_MESSAGE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, OverlayMessageS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, OverlayMessageS2CPayload::message, OverlayMessageS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
