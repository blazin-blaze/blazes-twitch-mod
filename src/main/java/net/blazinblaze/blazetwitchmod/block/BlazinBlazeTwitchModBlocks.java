package net.blazinblaze.blazetwitchmod.block;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.block.custom.MailboxBlock;
import net.blazinblaze.blazetwitchmod.block.custom.NuclearTntBlock;
import net.blazinblaze.blazetwitchmod.block.custom.RadioBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class BlazinBlazeTwitchModBlocks {
    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        // Create a registry key for the blocks
        RegistryKey<Block> blockKey = keyOfBlock(name);
        // Create the blocks instance
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        // Sometimes, you may not want to register an item for the blocks.
        // Eg: if it's a technical blocks like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            RegistryKey<Item> itemKey = keyOfItem(name);

            if(name.matches("mailbox") || name.matches("radio_block")) {
                BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey).maxCount(1));
                Registry.register(Registries.ITEM, itemKey, blockItem);
            }else {
                BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
                Registry.register(Registries.ITEM, itemKey, blockItem);
            }
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(BlazinBlazeTwitchMod.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(BlazinBlazeTwitchMod.MOD_ID, name));
    }

    public static final Block NUCLEAR_TNT = register(
            "nuclear_tnt",
            NuclearTntBlock::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRASS),
            true
    );

    public static final Block MAILBOX_BLOCK = register(
            "mailbox",
            MailboxBlock::new,
            AbstractBlock.Settings.create().solid().sounds(BlockSoundGroup.IRON).requiresTool().strength(2.0F, 6.0F).nonOpaque(),
            true
    );

    public static final Block RADIO_BLOCK = register(
            "radio_block",
            RadioBlock::new,
            AbstractBlock.Settings.create().solid().sounds(BlockSoundGroup.IRON).requiresTool().strength(2.0F, 6.0F).nonOpaque(),
            true
    );

    public static final Block SILICON_ORE = register(
            "silicon_ore",
            Block::new,
            AbstractBlock.Settings.create().solid().sounds(BlockSoundGroup.STONE).mapColor(MapColor.STONE_GRAY).requiresTool().strength(3.0F, 3.0F),
            true
    );

    public static final Block DEEPSLATE_SILICON_ORE = register(
            "deepslate_silicon_ore",
            Block::new,
            AbstractBlock.Settings.create().solid().sounds(BlockSoundGroup.DEEPSLATE).mapColor(MapColor.DEEPSLATE_GRAY).requiresTool().strength(4.5F, 3.0F),
            true
    );

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(BlazinBlazeTwitchModBlocks.MAILBOX_BLOCK);
            entries.add(BlazinBlazeTwitchModBlocks.RADIO_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.add(BlazinBlazeTwitchModBlocks.SILICON_ORE);
            entries.add(BlazinBlazeTwitchModBlocks.DEEPSLATE_SILICON_ORE);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.add(BlazinBlazeTwitchModBlocks.NUCLEAR_TNT);
        });
    }
}
