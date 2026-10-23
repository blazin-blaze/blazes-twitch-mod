package net.blazinblaze.blazetwitchmod.networking;

import com.mojang.serialization.Codec;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record FollowNotifyS2CPayload(String message) implements CustomPacketPayload {
    public static final Identifier FOLLOW_NOTIFY_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "follow_notify_payload");
    public static final CustomPacketPayload.Type<FollowNotifyS2CPayload> ID = new CustomPacketPayload.Type<>(FOLLOW_NOTIFY_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, FollowNotifyS2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, FollowNotifyS2CPayload::message, FollowNotifyS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
