package net.blazinblaze.blazetwitchmod.block.entity;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.block.BlazinBlazeTwitchModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class BlazinBlazeTwitchModBlockEntities {
    public static final BlockEntityType<MailboxBlockEntity> MAILBOX_BLOCK_ENTITY =
            register("mailbox_entity", MailboxBlockEntity::new, BlazinBlazeTwitchModBlocks.MAILBOX_BLOCK);

    public static final BlockEntityType<RadioBlockEntity> RADIO_BLOCK_ENTITY =
            register("radio_block_entity", RadioBlockEntity::new, BlazinBlazeTwitchModBlocks.RADIO_BLOCK);

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void initialize() {}
}
