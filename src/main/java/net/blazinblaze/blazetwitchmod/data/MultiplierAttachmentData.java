package net.blazinblaze.blazetwitchmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;

public record MultiplierAttachmentData(Integer integer) {

    public static Codec<MultiplierAttachmentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("integer").forGetter(MultiplierAttachmentData::integer)
    ).apply(instance, MultiplierAttachmentData::new));

    public static StreamCodec<ByteBuf, MultiplierAttachmentData> PACKET_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static MultiplierAttachmentData DEFAULT = new MultiplierAttachmentData(1);

    public MultiplierAttachmentData addInt(int value) {
        return new MultiplierAttachmentData(integer + value);
    }

    public MultiplierAttachmentData clear() {
        return DEFAULT;
    }
}
