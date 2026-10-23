package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;

public record PlaySoundS2CPayload(SoundEvent sound, float volume, float pitch) implements CustomPacketPayload {
    public static final Identifier PLAY_SOUND_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "play_sound_payload");
    public static final CustomPacketPayload.Type<PlaySoundS2CPayload> ID = new CustomPacketPayload.Type<>(PLAY_SOUND_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PlaySoundS2CPayload> CODEC = StreamCodec.composite(SoundEvent.DIRECT_STREAM_CODEC, PlaySoundS2CPayload::sound,
            ByteBufCodecs.FLOAT, PlaySoundS2CPayload::volume,
            ByteBufCodecs.FLOAT, PlaySoundS2CPayload::pitch,
            PlaySoundS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
