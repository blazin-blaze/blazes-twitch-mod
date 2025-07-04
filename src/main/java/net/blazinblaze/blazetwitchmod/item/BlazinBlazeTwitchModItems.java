package net.blazinblaze.blazetwitchmod.item;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.item.custom.BlazeSword;
import net.blazinblaze.blazetwitchmod.item.custom.ImmunityShieldItem;
import net.blazinblaze.blazetwitchmod.item.custom.MailItem;
import net.blazinblaze.blazetwitchmod.item.custom.VoiceMessageItem;
import net.minecraft.item.Item;
//import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class BlazinBlazeTwitchModItems {

    public static final TagKey<Item> BLAZIN_TOOLS_TAG = TagKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("blazin_tools"));

    public static final ToolMaterial BLAZIN_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            3500,
            13.0F,
            4.0F,
            25,
            BLAZIN_TOOLS_TAG
    );

    public static final Item MAIL_ITEM = register("mail_item", MailItem::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "default").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_STARRY = register("mail_item_starry", MailItem::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "starry").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_EPIC = register("mail_item_epic", MailItem::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "epic").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_MINECRAFT = register("mail_item_minecraft", MailItem::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "minecraft").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_ERROR = register("mail_item_error", MailItem::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "error").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_SINISTER = register("mail_item_sinister", MailItem::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "sinister").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item MAIL_ITEM_BLAZIN = register("mail_item_blazin", MailItem::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC).component(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, "test").component(BlazinBlazeTwitchModComponents.MAIL_TYPE, "blazin").component(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, "Farewell, "));
    public static final Item VOICE_MESSAGE_ITEM = register("voice_message_item", VoiceMessageItem::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE).component(BlazinBlazeTwitchModComponents.VOICE_MESSAGE, "test").component(BlazinBlazeTwitchModComponents.VOICE_AUTHOR, "test"));
    public static final Item IMMUNITY_SHIELD_ITEM = register("immunity_shield_item", ImmunityShieldItem::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    //public static final Item BLAZE_SWORD = register("blaze_sword", settings -> new BlazeSword(BLAZIN_TOOL_MATERIAL, 3.5F, 4.0F, settings), new Item.Settings().useCooldown(0.5F).fireproof().maxCount(1).rarity(Rarity.EPIC));

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(BlazinBlazeTwitchMod.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
    }
}
