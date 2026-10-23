package net.blazinblaze.blazetwitchmod.block;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.block.custom.MailboxBlock;
import net.blazinblaze.blazetwitchmod.block.custom.NuclearTntBlock;
import net.blazinblaze.blazetwitchmod.block.custom.RadioBlock;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class BlazinBlazeTwitchModBlocks {
    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
        // Create a registry key for the blocks
        ResourceKey<Block> blockKey = keyOfBlock(name);
        // Create the blocks instance
        Block block = blockFactory.apply(settings.setId(blockKey));

        // Sometimes, you may not want to register an item for the blocks.
        // Eg: if it's a technical blocks like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            ResourceKey<Item> itemKey = keyOfItem(name);

            if(name.matches("mailbox") || name.matches("radio_block")) {
                BlockItem blockItem = new BlockItem(block, new net.minecraft.world.item.Item.Properties().setId(itemKey).stacksTo(1));
                Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
            }else {
                BlockItem blockItem = new BlockItem(block, new net.minecraft.world.item.Item.Properties().setId(itemKey));
                Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
            }
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, name));
    }

    public static final Block NUCLEAR_TNT = register(
            "nuclear_tnt",
            NuclearTntBlock::new,
            BlockBehaviour.Properties.of().sound(SoundType.GRASS),
            true
    );

    public static final Block MAILBOX_BLOCK = register(
            "mailbox",
            MailboxBlock::new,
            BlockBehaviour.Properties.of().forceSolidOn().sound(SoundType.IRON).requiresCorrectToolForDrops().strength(2.0F, 6.0F).noOcclusion(),
            true
    );

    public static final Block RADIO_BLOCK = register(
            "radio_block",
            RadioBlock::new,
            BlockBehaviour.Properties.of().forceSolidOn().sound(SoundType.IRON).requiresCorrectToolForDrops().strength(2.0F, 6.0F).noOcclusion(),
            true
    );

    public static final Block SILICON_ORE = register(
            "silicon_ore",
            Block::new,
            BlockBehaviour.Properties.of().forceSolidOn().sound(SoundType.STONE).mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F),
            true
    );

    public static final Block DEEPSLATE_SILICON_ORE = register(
            "deepslate_silicon_ore",
            Block::new,
            BlockBehaviour.Properties.of().forceSolidOn().sound(SoundType.DEEPSLATE).mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F),
            true
    );

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.accept(BlazinBlazeTwitchModBlocks.MAILBOX_BLOCK);
            entries.accept(BlazinBlazeTwitchModBlocks.RADIO_BLOCK);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.accept(BlazinBlazeTwitchModBlocks.SILICON_ORE);
            entries.accept(BlazinBlazeTwitchModBlocks.DEEPSLATE_SILICON_ORE);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> {
            entries.accept(BlazinBlazeTwitchModBlocks.NUCLEAR_TNT);
        });
    }
}
