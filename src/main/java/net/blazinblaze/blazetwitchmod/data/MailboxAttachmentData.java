package net.blazinblaze.blazetwitchmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.core.BlockPos;

public record MailboxAttachmentData(BlockPos blockPos) {

    public static Codec<MailboxAttachmentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("blockpos").forGetter(MailboxAttachmentData::blockPos)
    ).apply(instance, MailboxAttachmentData::new));

    public static StreamCodec<ByteBuf, MailboxAttachmentData> PACKET_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static MailboxAttachmentData DEFAULT = new MailboxAttachmentData(new BlockPos(0,0,0));

    public MailboxAttachmentData setBlockPos(BlockPos blockPos) {
        return new MailboxAttachmentData(blockPos);
    }

    public MailboxAttachmentData clear() {
        return DEFAULT;
    }
}
