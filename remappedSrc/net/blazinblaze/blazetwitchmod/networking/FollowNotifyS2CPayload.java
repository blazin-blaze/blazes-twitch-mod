package net.blazinblaze.blazetwitchmod.networking;

import com.mojang.serialization.Codec;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record FollowNotifyS2CPayload(String message) implements CustomPayload {
    public static final Identifier FOLLOW_NOTIFY_PAYLOAD_ID = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "follow_notify_payload");
    public static final CustomPayload.Id<FollowNotifyS2CPayload> ID = new CustomPayload.Id<>(FOLLOW_NOTIFY_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, FollowNotifyS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, FollowNotifyS2CPayload::message, FollowNotifyS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
