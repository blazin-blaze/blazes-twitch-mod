package net.blazinblaze.blazetwitchmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record TimerAttachmentData(Integer integer) {

    public static Codec<TimerAttachmentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("integer").forGetter(TimerAttachmentData::integer)
    ).apply(instance, TimerAttachmentData::new));

    public static PacketCodec<ByteBuf, TimerAttachmentData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public static TimerAttachmentData DEFAULT = new TimerAttachmentData(0);

    public TimerAttachmentData addInt(int value) {
        return new TimerAttachmentData(integer + value);
    }

    public TimerAttachmentData clear() {
        return DEFAULT;
    }
}
