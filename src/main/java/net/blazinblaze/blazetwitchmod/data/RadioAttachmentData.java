package net.blazinblaze.blazetwitchmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.core.BlockPos;

public record RadioAttachmentData(BlockPos blockPos) {

    public static Codec<RadioAttachmentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("blockpos").forGetter(RadioAttachmentData::blockPos)
    ).apply(instance, RadioAttachmentData::new));

    public static StreamCodec<ByteBuf, RadioAttachmentData> PACKET_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static RadioAttachmentData DEFAULT = new RadioAttachmentData(new BlockPos(0,0,0));

    public RadioAttachmentData setBlockPos(BlockPos blockPos) {
        return new RadioAttachmentData(blockPos);
    }

    public RadioAttachmentData clear() {
        return DEFAULT;
    }
}
