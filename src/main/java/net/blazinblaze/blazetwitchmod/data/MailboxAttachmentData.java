package net.blazinblaze.blazetwitchmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;

public record MailboxAttachmentData(BlockPos blockPos) {

    public static Codec<MailboxAttachmentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("blockpos").forGetter(MailboxAttachmentData::blockPos)
    ).apply(instance, MailboxAttachmentData::new));

    public static PacketCodec<ByteBuf, MailboxAttachmentData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public static MailboxAttachmentData DEFAULT = new MailboxAttachmentData(new BlockPos(0,0,0));

    public MailboxAttachmentData setBlockPos(BlockPos blockPos) {
        return new MailboxAttachmentData(blockPos);
    }

    public MailboxAttachmentData clear() {
        return DEFAULT;
    }
}
