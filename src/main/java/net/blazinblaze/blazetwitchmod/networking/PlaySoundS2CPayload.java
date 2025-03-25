package net.blazinblaze.blazetwitchmod.networking;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public record PlaySoundS2CPayload(SoundEvent sound, float volume, float pitch) implements CustomPayload {
    public static final Identifier PLAY_SOUND_PAYLOAD_ID = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "play_sound_payload");
    public static final CustomPayload.Id<PlaySoundS2CPayload> ID = new CustomPayload.Id<>(PLAY_SOUND_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, PlaySoundS2CPayload> CODEC = PacketCodec.tuple(SoundEvent.PACKET_CODEC, PlaySoundS2CPayload::sound,
            PacketCodecs.FLOAT, PlaySoundS2CPayload::volume,
            PacketCodecs.FLOAT, PlaySoundS2CPayload::pitch,
            PlaySoundS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
