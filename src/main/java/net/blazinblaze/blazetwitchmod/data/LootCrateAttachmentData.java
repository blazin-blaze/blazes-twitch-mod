package net.blazinblaze.blazetwitchmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;

public record LootCrateAttachmentData(Integer integer) {

    public static Codec<LootCrateAttachmentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("integer").forGetter(LootCrateAttachmentData::integer)
    ).apply(instance, LootCrateAttachmentData::new));

    public static PacketCodec<ByteBuf, LootCrateAttachmentData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public static LootCrateAttachmentData DEFAULT = new LootCrateAttachmentData(0);

    public LootCrateAttachmentData addInt(int value) {
        return new LootCrateAttachmentData(integer + value);
    }

    public LootCrateAttachmentData clear() {
        return DEFAULT;
    }
}
