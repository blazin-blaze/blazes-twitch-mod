package net.blazinblaze.blazetwitchmod.item;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.block.BlazinBlazeTwitchModBlocks;
import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.item.custom.*;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.Item;
//import net.minecraft.item.SwordItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class BlazinBlazeTwitchModItems {

    public static final TagKey<Item> BLAZIN_TOOLS_TAG = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("blazin_tools"));

    public static final ToolMaterial BLAZIN_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            3500,
            13.0F,
            4.0F,
            25,
            BLAZIN_TOOLS_TAG
    );

    public static final Item MAIL_ITEM = register("mail_item", MailItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "default").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_STARRY = register("mail_item_starry", MailItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "starry").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_EPIC = register("mail_item_epic", MailItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "epic").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_MINECRAFT = register("mail_item_minecraft", MailItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "minecraft").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_ERROR = register("mail_item_error", MailItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "error").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_SINISTER = register("mail_item_sinister", MailItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "sinister").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_BLAZIN = register("mail_item_blazin", MailItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "blazin").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item VOICE_MESSAGE_ITEM = register("voice_message_item", VoiceMessageItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(BlazinBlazeTwitchModComponents.VOICE_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.VOICE_AUTHOR, "test"));
    public static final Item IMMUNITY_SHIELD_ITEM = register("immunity_shield_item", ImmunityShieldItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    public static final Item FAKE_EYE_OF_ENDER = register("fake_eye_of_ender", FakeEyeOfEnderItem::new, new Item.Properties().component(BlazinBlazeTwitchModComponents.FAKE_EYE_USER, "test"));
    public static final Item RAW_SILICON = register("silicon", Item::new, new Item.Properties());
    public static final Item SMELTED_SILICON = register("smelted_silicon", Item::new, new Item.Properties());
    public static final Item SEMICONDUCTOR = register("semiconductor", Item::new, new Item.Properties().rarity(Rarity.RARE));
    //public static final Item BLAZE_SWORD = register("blaze_sword", settings -> new BlazeSword(BLAZIN_TOOL_MATERIAL, 3.5F, 4.0F, settings), new Item.Settings().useCooldown(0.5F).fireproof().maxCount(1).rarity(Rarity.EPIC));

    public static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.accept(BlazinBlazeTwitchModItems.RAW_SILICON);
            entries.accept(BlazinBlazeTwitchModItems.SMELTED_SILICON);
            entries.accept(BlazinBlazeTwitchModItems.SEMICONDUCTOR);
        });
    }
}
