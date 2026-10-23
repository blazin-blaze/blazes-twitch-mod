package net.blazinblaze.blazetwitchmod;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.common.util.BucketUtils;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Stream;
import io.github.iprodigy.bttv.common.Emote;
import net.blazinblaze.blazetwitchmod.block.entity.MailboxBlockEntity;
import net.blazinblaze.blazetwitchmod.block.entity.RadioBlockEntity;
import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.data.BlazinBlazeAttachmentTypes;
import net.blazinblaze.blazetwitchmod.data.LootCrateAttachmentData;
import net.blazinblaze.blazetwitchmod.data.MailboxAttachmentData;
import net.blazinblaze.blazetwitchmod.data.RadioAttachmentData;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.entity.custom.*;
import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.blazinblaze.blazetwitchmod.mixin.CatAccess;
import net.blazinblaze.blazetwitchmod.mixin.ParrotAccess;
import net.blazinblaze.blazetwitchmod.mixin.WolfAccess;
import net.blazinblaze.blazetwitchmod.networking.*;
import net.blazinblaze.blazetwitchmod.reference.BlazinBlazeProbabilities;
import net.blazinblaze.blazetwitchmod.sound.BlazinBlazeTwitchModSounds;
import net.blazinblaze.blazetwitchmod.util.MultiplierTick;
import net.blazinblaze.blazetwitchmod.util.SoundMessageTimer;
import net.blazinblaze.blazetwitchmod.util.TeleportTimer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatSoundVariants;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.feline.CatVariants;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariants;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import io.github.bucket4j.Bucket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.apache.commons.lang.WordUtils;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import java.time.Duration;

public class BlazinBlazeTwitchModEvent {

    private final Map<String, SpawnableMob> SPAWNABLE_MOBS = new HashMap<>();
    private static final Map<String, String> POTION_COLORS = Map.ofEntries(
            Map.entry("poison", "§2§l"),
            Map.entry("weakness", "§7§l"),
            Map.entry("slowness", "§8§l"),
            Map.entry("levitation", "§b§l"),
            Map.entry("jump boost", "§a§l"),
            Map.entry("invisibility", "§1§l"),
            Map.entry("absorption", "§d§l"),
            Map.entry("resistance", "§5§l"),
            Map.entry("fire resistance", "§4§l"),
            Map.entry("regeneration", "§c§l"),
            Map.entry("speed", "§9§l"),
            Map.entry("strength", "§e§l"),
            Map.entry("health boost", "§2§l"));
    private static final Map<String, String> POTION_COLORS2 = Map.ofEntries(
            Map.entry("poison", "§2§l"),
            Map.entry("weakness", "§7§l"),
            Map.entry("slowness", "§8§l"),
            Map.entry("levitation", "§b§l"),
            Map.entry("jump boost", "§a§l"),
            Map.entry("invisibility", "§1§l"),
            Map.entry("absorption", "§d§l"),
            Map.entry("resistance", "§5§l"),
            Map.entry("fire resistance", "§4§l"),
            Map.entry("regeneration", "§c§l"),
            Map.entry("speed", "§9§l"),
            Map.entry("strength", "§e§l"),
            Map.entry("blindness", "§0§l"),
            Map.entry("nausea", "§2§l"),
            Map.entry("wither", "§0§l"),
            Map.entry("bad luck", "§c§l"),
            Map.entry("luck", "§a§l"),
            Map.entry("slow falling", "§7§l"),
            Map.entry("mining fatigue", "§8§l"),
            Map.entry("health boost", "§2§l"));
    private final Map<String, Double> MAIL_RARITIES = new HashMap<>();
    private static final Map<String, ItemStack> MAIL_TYPES = Map.ofEntries(
            Map.entry("default", BlazinBlazeTwitchModItems.MAIL_ITEM.asItem().getDefaultInstance()),
            Map.entry("starry", BlazinBlazeTwitchModItems.MAIL_ITEM_STARRY.asItem().getDefaultInstance()),
            Map.entry("blazin", BlazinBlazeTwitchModItems.MAIL_ITEM_BLAZIN.asItem().getDefaultInstance()),
            Map.entry("epic", BlazinBlazeTwitchModItems.MAIL_ITEM_EPIC.asItem().getDefaultInstance()),
            Map.entry("minecraft", BlazinBlazeTwitchModItems.MAIL_ITEM_MINECRAFT.asItem().getDefaultInstance()),
            Map.entry("error", BlazinBlazeTwitchModItems.MAIL_ITEM_ERROR.asItem().getDefaultInstance()),
            Map.entry("sinister", BlazinBlazeTwitchModItems.MAIL_ITEM_SINISTER.asItem().getDefaultInstance()));
    private static final Map<String, String> ITEM_ALIAS = Map.ofEntries(
            Map.entry("steak", "cooked_beef"),
            Map.entry("eye_of_ender", "ender_eye"),
            Map.entry("totem", "totem_of_undying"),
            Map.entry("yellow_flower", "dandelion"),
            Map.entry("red_flower", "poppy"),
            Map.entry("mob_spawner", "spawner"),
            Map.entry("wooden_door", "oak_door"),
            Map.entry("redstone_repeater", "repeater"),
            Map.entry("redstone_comparator", "comparator"),
            Map.entry("boat", "oak_boat"),
            Map.entry("door", "oak_door"),
            Map.entry("planks", "oak_planks"),
            Map.entry("fences", "oak_fence"),
            Map.entry("fence", "oak_fence"),
            Map.entry("fence_gate", "oak_fence_gate"),
            Map.entry("bed", "red_bed"),
            Map.entry("wool", "white_wool"),
            Map.entry("banner", "white_banner"),
            Map.entry("spawn_egg", "blaze_spawn_egg"),
            Map.entry("skull", "player_head_item"),
            Map.entry("head", "player_head_item"),
            Map.entry("fireworks", "firework_rocket"),
            Map.entry("firework", "firework_rocket"),
            Map.entry("chorus_fruit_popped", "popped_chorus_fruit"),
            Map.entry("music_disc", "music_disc_chirp"),
            Map.entry("record", "music_disc_chirp"),
            Map.entry("sign", "oak_sign"),
            Map.entry("stairs", "oak_stairs"),
            Map.entry("stair", "oak_stair"),
            Map.entry("slab", "oak_slab"),
            Map.entry("hoe", "wooden_hoe"),
            Map.entry("pickaxe", "wooden_pickaxe"),
            Map.entry("axe", "wooden_axe"),
            Map.entry("sword", "wooden_sword"),
            Map.entry("shovel", "wooden_shovel"),
            Map.entry("armor", "leather_armor"),
            Map.entry("horse_armor", "leather_horse_armor")
    );
    private final Bucket commandBucket = BucketUtils.createBucket(BucketUtils.simple(5, Duration.ofSeconds(10)));
    private final Bucket chatterBucket = BucketUtils.createBucket(BucketUtils.simple(1, Duration.ofSeconds(60)));
    private final ArrayList<DyeColor> dyeColors = new ArrayList<DyeColor>();
    private final ArrayList<ResourceKey<WolfVariant>> wolfVariants = new ArrayList<ResourceKey<WolfVariant>>();
    private final ArrayList<ResourceKey<CatVariant>> catVariants = new ArrayList<ResourceKey<CatVariant>>();
    private final ArrayList<Parrot.Variant> parrotVariants = new ArrayList<Parrot.Variant>();
    private final ArrayList<Holder<MobEffect>> statusEffects = new ArrayList<Holder<MobEffect>>();
    private final ArrayList<Holder<MobEffect>> statusEffects2 = new ArrayList<Holder<MobEffect>>();
    private final ArrayList<String> mailKeys = new ArrayList<String>();
    private final ArrayList<String> authorFarewells = new ArrayList<String>();
    private final ArrayList<SoundEvent> soundEffects = new ArrayList<SoundEvent>();
    private final ArrayList<EntityType<?>> skeletonTypes = new ArrayList<EntityType<?>>();
    private final ArrayList<EntityType<?>> villagerTypes = new ArrayList<EntityType<?>>();
    private final ArrayList<ItemStack> harnessTypes = new ArrayList<ItemStack>();
    private static final List<String> commandNames = List.of("!spawn",
            "!dinventory",
            "!ditem",
            "!fling",
            "!void",
            "!beddy",
            "!lootcrate",
            "!loot",
            "!nuke",
            "!meganuke",
            "voicemessage",
            "!potion",
            "!teleport",
            "!tp",
            "!cbomb",
            "!sound",
            "!fullscreen",
            "!close",
            "!dtimer",
            "!hold");

    public BlazinBlazeTwitchModEvent() {
        SPAWNABLE_MOBS.put("zombie", new SpawnableMob(EntityType.ZOMBIE, getProbabilityOrDefault(BlazinBlazeProbabilities.ZOMBIE_DEFAULT)));
        SPAWNABLE_MOBS.put("blaze", new SpawnableMob(EntityType.BLAZE, getProbabilityOrDefault(BlazinBlazeProbabilities.BLAZE_DEFAULT)));
        SPAWNABLE_MOBS.put("dragon", new SpawnableMob(EntityType.ENDER_DRAGON, getProbabilityOrDefault(BlazinBlazeProbabilities.DRAGON_DEFAULT)));
        SPAWNABLE_MOBS.put("wither", new SpawnableMob(EntityType.WITHER, getProbabilityOrDefault(BlazinBlazeProbabilities.WITHER_DEFAULT)));
        SPAWNABLE_MOBS.put("creeper", new SpawnableMob(EntityType.CREEPER, getProbabilityOrDefault(BlazinBlazeProbabilities.CREEPER_DEFAULT)));
        SPAWNABLE_MOBS.put("tnt", new SpawnableMob(EntityType.TNT, getProbabilityOrDefault(BlazinBlazeProbabilities.TNT_DEFAULT)));
        SPAWNABLE_MOBS.put("skeleton", new SpawnableMob(EntityType.SKELETON, getProbabilityOrDefault(BlazinBlazeProbabilities.SKELETON_DEFAULT)));
        SPAWNABLE_MOBS.put("warden", new SpawnableMob(EntityType.WARDEN, getProbabilityOrDefault(BlazinBlazeProbabilities.WARDEN_DEFAULT)));
        SPAWNABLE_MOBS.put("brute", new SpawnableMob(EntityType.PIGLIN_BRUTE, getProbabilityOrDefault(BlazinBlazeProbabilities.BRUTE_DEFAULT)));
        SPAWNABLE_MOBS.put("guardian", new SpawnableMob(EntityType.GUARDIAN, getProbabilityOrDefault(BlazinBlazeProbabilities.GUARDIAN_DEFAULT)));
        SPAWNABLE_MOBS.put("ghast", new SpawnableMob(EntityType.GHAST, getProbabilityOrDefault(BlazinBlazeProbabilities.GHAST_DEFAULT)));
        SPAWNABLE_MOBS.put("lightning bolt", new SpawnableMob(EntityType.LIGHTNING_BOLT, getProbabilityOrDefault(BlazinBlazeProbabilities.LIGHTNING_BOLT_DEFAULT)));
        SPAWNABLE_MOBS.put("spider", new SpawnableMob(EntityType.SPIDER, getProbabilityOrDefault(BlazinBlazeProbabilities.SPIDER_DEFAULT)));
        SPAWNABLE_MOBS.put("vex", new SpawnableMob(EntityType.VEX, getProbabilityOrDefault(BlazinBlazeProbabilities.VEX_DEFAULT)));
        SPAWNABLE_MOBS.put("evoker", new SpawnableMob(EntityType.EVOKER, getProbabilityOrDefault(BlazinBlazeProbabilities.EVOKER_DEFAULT)));
        SPAWNABLE_MOBS.put("pillager", new SpawnableMob(EntityType.PILLAGER, getProbabilityOrDefault(BlazinBlazeProbabilities.PILLAGER_DEFAULT)));
        SPAWNABLE_MOBS.put("breeze", new SpawnableMob(EntityType.BREEZE, getProbabilityOrDefault(BlazinBlazeProbabilities.BREEZE_DEFAULT)));
        SPAWNABLE_MOBS.put("hoglin", new SpawnableMob(EntityType.HOGLIN, getProbabilityOrDefault(BlazinBlazeProbabilities.HOGLIN_DEFAULT)));
        SPAWNABLE_MOBS.put("pigln", new SpawnableMob(EntityType.PIGLIN, getProbabilityOrDefault(BlazinBlazeProbabilities.PIGLIN_DEFAULT)));
        SPAWNABLE_MOBS.put("illusioner", new SpawnableMob(EntityType.ILLUSIONER, getProbabilityOrDefault(BlazinBlazeProbabilities.ILLUSIONER_DEFAULT)));
        SPAWNABLE_MOBS.put("slime", new SpawnableMob(EntityType.SLIME, getProbabilityOrDefault(BlazinBlazeProbabilities.SLIME_DEFAULT)));
        SPAWNABLE_MOBS.put("phantom", new SpawnableMob(EntityType.PHANTOM, getProbabilityOrDefault(BlazinBlazeProbabilities.PHANTOM_DEFAULT)));
        SPAWNABLE_MOBS.put("ravager", new SpawnableMob(EntityType.RAVAGER, getProbabilityOrDefault(BlazinBlazeProbabilities.RAVAGER_DEFAULT)));
        SPAWNABLE_MOBS.put("shulker", new SpawnableMob(EntityType.SHULKER, getProbabilityOrDefault(BlazinBlazeProbabilities.SHULKER_DEFAULT)));
        SPAWNABLE_MOBS.put("silverfish", new SpawnableMob(EntityType.SILVERFISH, getProbabilityOrDefault(BlazinBlazeProbabilities.SILVERFISH_DEFAULT)));
        SPAWNABLE_MOBS.put("vindicator", new SpawnableMob(EntityType.VINDICATOR, getProbabilityOrDefault(BlazinBlazeProbabilities.VINDICATOR_DEFAULT)));
        SPAWNABLE_MOBS.put("villager", new SpawnableMob(EntityType.VILLAGER, getProbabilityOrDefault(BlazinBlazeProbabilities.VILLAGER_DEFAULT)));
        SPAWNABLE_MOBS.put("fish", new SpawnableMob(EntityType.SALMON, getProbabilityOrDefault(BlazinBlazeProbabilities.FISH_DEFAULT)));
        SPAWNABLE_MOBS.put("iron golem", new SpawnableMob(EntityType.IRON_GOLEM, getProbabilityOrDefault(BlazinBlazeProbabilities.IRON_GOLEM_DEFAULT)));

        MAIL_RARITIES.put("default", getProbabilityOrDefault(BlazinBlazeProbabilities.LETTER_DEFAULT));
        MAIL_RARITIES.put("starry", getProbabilityOrDefault(BlazinBlazeProbabilities.LETTER_STARRY));
        MAIL_RARITIES.put("blazin", getProbabilityOrDefault(BlazinBlazeProbabilities.LETTER_BLAZIN));
        MAIL_RARITIES.put("epic", getProbabilityOrDefault(BlazinBlazeProbabilities.LETTER_EPIC));
        MAIL_RARITIES.put("minecraft", getProbabilityOrDefault(BlazinBlazeProbabilities.LETTER_MINECRAFT));
        MAIL_RARITIES.put("error", getProbabilityOrDefault(BlazinBlazeProbabilities.LETTER_ERROR));
        MAIL_RARITIES.put("sinister", getProbabilityOrDefault(BlazinBlazeProbabilities.LETTER_SINISTER));

        dyeColors.add(DyeColor.BLUE);
        dyeColors.add(DyeColor.BROWN);
        dyeColors.add(DyeColor.LIME);
        dyeColors.add(DyeColor.ORANGE);
        dyeColors.add(DyeColor.PURPLE);
        dyeColors.add(DyeColor.WHITE);
        dyeColors.add(DyeColor.LIGHT_BLUE);
        dyeColors.add(DyeColor.BLACK);
        dyeColors.add(DyeColor.CYAN);
        dyeColors.add(DyeColor.GRAY);
        dyeColors.add(DyeColor.GREEN);
        dyeColors.add(DyeColor.LIGHT_GRAY);
        dyeColors.add(DyeColor.MAGENTA);
        dyeColors.add(DyeColor.PINK);
        dyeColors.add(DyeColor.RED);
        dyeColors.add(DyeColor.YELLOW);

        wolfVariants.add(WolfVariants.PALE);
        wolfVariants.add(WolfVariants.BLACK);
        wolfVariants.add(WolfVariants.ASHEN);
        wolfVariants.add(WolfVariants.CHESTNUT);
        wolfVariants.add(WolfVariants.RUSTY);
        wolfVariants.add(WolfVariants.SNOWY);
        wolfVariants.add(WolfVariants.SPOTTED);
        wolfVariants.add(WolfVariants.STRIPED);
        wolfVariants.add(WolfVariants.WOODS);

        statusEffects.add(MobEffects.HEALTH_BOOST);
        statusEffects.add(MobEffects.STRENGTH);
        statusEffects.add(MobEffects.SPEED);
        statusEffects.add(MobEffects.REGENERATION);
        statusEffects.add(MobEffects.FIRE_RESISTANCE);
        statusEffects.add(MobEffects.RESISTANCE);
        statusEffects.add(MobEffects.ABSORPTION);
        statusEffects.add(MobEffects.INVISIBILITY);
        statusEffects.add(MobEffects.JUMP_BOOST);
        statusEffects.add(MobEffects.LEVITATION);
        statusEffects.add(MobEffects.SLOWNESS);
        statusEffects.add(MobEffects.WEAKNESS);
        statusEffects.add(MobEffects.POISON);

        statusEffects2.add(MobEffects.HEALTH_BOOST);
        statusEffects2.add(MobEffects.STRENGTH);
        statusEffects2.add(MobEffects.SPEED);
        statusEffects2.add(MobEffects.REGENERATION);
        statusEffects2.add(MobEffects.FIRE_RESISTANCE);
        statusEffects2.add(MobEffects.RESISTANCE);
        statusEffects2.add(MobEffects.ABSORPTION);
        statusEffects2.add(MobEffects.INVISIBILITY);
        statusEffects2.add(MobEffects.JUMP_BOOST);
        statusEffects2.add(MobEffects.LEVITATION);
        statusEffects2.add(MobEffects.SLOWNESS);
        statusEffects2.add(MobEffects.WEAKNESS);
        statusEffects2.add(MobEffects.POISON);
        statusEffects2.add(MobEffects.BLINDNESS);
        statusEffects2.add(MobEffects.NAUSEA);
        statusEffects2.add(MobEffects.WITHER);
        statusEffects2.add(MobEffects.UNLUCK);
        statusEffects2.add(MobEffects.LUCK);
        statusEffects2.add(MobEffects.SLOW_FALLING);
        statusEffects2.add(MobEffects.MINING_FATIGUE);

        catVariants.add(CatVariants.JELLIE);
        catVariants.add(CatVariants.RED);
        catVariants.add(CatVariants.WHITE);
        catVariants.add(CatVariants.BLACK);
        catVariants.add(CatVariants.SIAMESE);
        catVariants.add(CatVariants.ALL_BLACK);
        catVariants.add(CatVariants.BRITISH_SHORTHAIR);
        catVariants.add(CatVariants.CALICO);
        catVariants.add(CatVariants.PERSIAN);
        catVariants.add(CatVariants.RAGDOLL);
        catVariants.add(CatVariants.TABBY);

        parrotVariants.add(Parrot.Variant.BLUE);
        parrotVariants.add(Parrot.Variant.RED_BLUE);
        parrotVariants.add(Parrot.Variant.GRAY);
        parrotVariants.add(Parrot.Variant.YELLOW_BLUE);
        parrotVariants.add(Parrot.Variant.GREEN);

        mailKeys.add("default");
        mailKeys.add("blazin");
        mailKeys.add("epic");
        mailKeys.add("error");
        mailKeys.add("sinister");
        mailKeys.add("minecraft");
        mailKeys.add("starry");

        authorFarewells.add("Best wishes - ");
        authorFarewells.add("Sincerely - ");
        authorFarewells.add("You stink - ");
        authorFarewells.add("Your mom's a diamond hoe - ");
        authorFarewells.add("I wish you well - ");
        authorFarewells.add("Have a nice day - ");
        authorFarewells.add("BLAZIN - ");
        authorFarewells.add("Touch grass pls - ");
        authorFarewells.add("You're bad - ");
        authorFarewells.add("Get gud - ");
        authorFarewells.add("Bonne journée - ");
        authorFarewells.add("hehehah - ");
        authorFarewells.add("See you - ");
        authorFarewells.add("Have a good one - ");
        authorFarewells.add("Take care - ");
        authorFarewells.add("Take it easy - ");
        authorFarewells.add("BYE - ");
        authorFarewells.add("Farewell - ");
        authorFarewells.add("Suck an end rod - ");
        authorFarewells.add("You're the best - ");
        authorFarewells.add("Commit fall in lava - ");
        authorFarewells.add("i smell - ");
        authorFarewells.add("i'm a fetus - ");
        authorFarewells.add("You look easy to draw - ");
        authorFarewells.add("i'm a d1 glazer - ");
        authorFarewells.add("discord mod ahh - ");
        authorFarewells.add("shut it lil bro - ");
        authorFarewells.add("stupid ahh - ");
        authorFarewells.add("i lack brain cells - ");
        authorFarewells.add("big forehead ahh - ");

        soundEffects.add(SoundEvents.CREEPER_PRIMED);
        soundEffects.add(SoundEvents.AMBIENT_CAVE.value());
        soundEffects.add(SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR);
        soundEffects.add(SoundEvents.ZOMBIE_AMBIENT);
        soundEffects.add(SoundEvents.ZOMBIE_AMBIENT);
        soundEffects.add(SoundEvents.WARDEN_EMERGE);
        soundEffects.add(SoundEvents.WARDEN_NEARBY_CLOSE);
        soundEffects.add(SoundEvents.WARDEN_HEARTBEAT);
        soundEffects.add(SoundEvents.WARDEN_DIG);
        soundEffects.add(SoundEvents.WARDEN_AMBIENT);
        soundEffects.add(SoundEvents.SKELETON_AMBIENT);
        soundEffects.add(SoundEvents.CROSSBOW_SHOOT);
        soundEffects.add(SoundEvents.ARROW_HIT_PLAYER);
        soundEffects.add(SoundEvents.PLAYER_HURT);
        soundEffects.add(SoundEvents.DEEPSLATE_BREAK);
        soundEffects.add(SoundEvents.DEEPSLATE_PLACE);
        soundEffects.add(SoundEvents.STONE_BREAK);
        soundEffects.add(SoundEvents.STONE_PLACE);
        soundEffects.add(SoundEvents.WOOD_PLACE);
        soundEffects.add(SoundEvents.WOOD_BREAK);
        soundEffects.add(SoundEvents.WOODEN_DOOR_OPEN);
        soundEffects.add(SoundEvents.WOODEN_DOOR_CLOSE);
        soundEffects.add(SoundEvents.COPPER_DOOR_OPEN);
        soundEffects.add(SoundEvents.COPPER_DOOR_CLOSE);
        soundEffects.add(SoundEvents.IRON_DOOR_OPEN);
        soundEffects.add(SoundEvents.IRON_DOOR_CLOSE);
        soundEffects.add(SoundEvents.CHEST_OPEN);
        soundEffects.add(SoundEvents.CHEST_CLOSE);
        soundEffects.add(SoundEvents.SPIDER_AMBIENT);
        soundEffects.add(SoundEvents.PLAYER_BURP);
        soundEffects.add(SoundEvents.PLAYER_BIG_FALL);
        soundEffects.add(SoundEvents.PLAYER_SMALL_FALL);
        soundEffects.add(SoundEvents.PLAYER_ATTACK_CRIT);
        soundEffects.add(SoundEvents.PLAYER_ATTACK_SWEEP);
        soundEffects.add(SoundEvents.PLAYER_SWIM);
        soundEffects.add(SoundEvents.ENDERMAN_SCREAM);
        soundEffects.add(SoundEvents.ENDERMAN_AMBIENT);
        soundEffects.add(SoundEvents.ENDERMAN_STARE);
        soundEffects.add(SoundEvents.ENDERMAN_TELEPORT);
        soundEffects.add(SoundEvents.DROWNED_AMBIENT_WATER);
        soundEffects.add(SoundEvents.DROWNED_AMBIENT);
        soundEffects.add(SoundEvents.LIGHTNING_BOLT_IMPACT);
        soundEffects.add(SoundEvents.LIGHTNING_BOLT_THUNDER);
        soundEffects.add(SoundEvents.GENERIC_EXPLODE.value());
        soundEffects.add(SoundEvents.DEEPSLATE_STEP);
        soundEffects.add(SoundEvents.GRASS_STEP);
        soundEffects.add(SoundEvents.GRAVEL_STEP);
        soundEffects.add(SoundEvents.GRAVEL_BREAK);
        soundEffects.add(SoundEvents.GRAVEL_PLACE);
        soundEffects.add(SoundEvents.STONE_STEP);
        soundEffects.add(SoundEvents.WOOD_STEP);
        soundEffects.add(SoundEvents.GRASS_BREAK);
        soundEffects.add(SoundEvents.AMETHYST_BLOCK_CHIME);
        soundEffects.add(SoundEvents.AMETHYST_BLOCK_PLACE);
        soundEffects.add(SoundEvents.GHAST_AMBIENT);
        soundEffects.add(SoundEvents.GHAST_SCREAM);
        soundEffects.add(SoundEvents.GHAST_SHOOT);

        skeletonTypes.add(EntityType.BOGGED);
        skeletonTypes.add(EntityType.WITHER_SKELETON);
        skeletonTypes.add(EntityType.STRAY);

        villagerTypes.add(EntityType.ZOMBIE_VILLAGER);
        villagerTypes.add(EntityType.WITCH);

        harnessTypes.add(Items.BLACK_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.BLUE_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.RED_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.LIME_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.PURPLE_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.MAGENTA_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.GREEN_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.LIGHT_BLUE_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.YELLOW_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.WHITE_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.LIGHT_GRAY_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.GRAY_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.CYAN_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.BROWN_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.ORANGE_HARNESS.getDefaultInstance());
        harnessTypes.add(Items.PINK_HARNESS.getDefaultInstance());
    }

    @EventSubscriber
    public void onStreamUp(ChannelGoLiveEvent event) {
        Stream stream = event.getStream();
        MinecraftServer server = BlazinBlazeTwitchMod.serverRef;
        if(server != null) {
            for(ServerPlayer playerEntity : server.getPlayerList().getPlayers()) {
                broadcast(String.format("§5§l[BTM]§r %s has gone live for %d viewers: %s", stream.getUserName(), stream.getViewerCount(), stream.getTitle()), playerEntity);
            }
        }
    }

    @EventSubscriber
    public void onStreamDown(ChannelGoOfflineEvent event) {
        MinecraftServer server = BlazinBlazeTwitchMod.serverRef;
        if(server != null) {
            for(ServerPlayer playerEntity : server.getPlayerList().getPlayers()) {
                broadcast(String.format("§5§l[BTM]§r %s has stopped streaming.", event.getChannel().getName()), playerEntity);
            }
        }
    }

    @EventSubscriber
    public void onFollow(FollowEvent event) {
        MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

        if(server != null) {
            FollowNotifyS2CPayload payload = new FollowNotifyS2CPayload(event.getUser().getName());
            PlaySoundS2CPayload payload2 = new PlaySoundS2CPayload(BlazinBlazeTwitchModSounds.TADA_SOUND_EFFECT, 0.5F, 1.0F);

            for (ServerPlayer player : PlayerLookup.all(server)) {
                ServerPlayNetworking.send(player, payload);
                ServerPlayNetworking.send(player, payload2);
                broadcast(String.format("§5§l[BLAZIN] %s just followed you!", event.getUser().getName()), player);
            }
        }
    }

    @EventSubscriber
    public void onChat(ChannelMessageEvent event) {
        String username = event.getUser().getName();
        String message = event.getMessage();

        int multiplier = MultiplierTick.MultiplierTimer.getMultiplier();
        if(message.toLowerCase().startsWith("!spawn ")) {
            String lowerNpcName = message.substring("!spawn ".length()).toLowerCase();
            SpawnableMob mob = SPAWNABLE_MOBS.get(lowerNpcName);

            if (mob != null && ThreadLocalRandom.current().nextDouble() < (mob.threshold() * multiplier) && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {

                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos spawnLocation = serverPlayer.blockPosition();
                            ServerLevel world = serverPlayer.level();

                            if (world.areEntitiesLoaded(serverPlayer.chunkPosition().pack())) {
                                Entity entity;
                                String lowerNpcName2 = message.substring("!spawn ".length()).toLowerCase();
                                if(lowerNpcName2.equals("creeper")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.RARE_MUTATION_DEFAULT) * multiplier) {
                                        entity = BlazinBlazeTwitchModEntities.MEGA_CREEPER.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6F, 1.0F);
                                        lowerNpcName2 = "mega creeper";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("skeleton")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        Random random = new Random();
                                        EntityType<?> rndmSkeleton = skeletonTypes.get(random.nextInt(skeletonTypes.size()));
                                        entity = rndmSkeleton.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6F, 1.0F);
                                        if(rndmSkeleton == EntityType.BOGGED) {
                                            lowerNpcName2 = "bogged";
                                        }else if(rndmSkeleton == EntityType.WITHER_SKELETON) {
                                            lowerNpcName2 = "wither skeleton";
                                        }else {
                                            lowerNpcName2 = "stray";
                                        }
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("zombie")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.RARE_MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.GIANT.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6F, 1.0F);
                                        lowerNpcName2 = "giant";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    }else if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.HUSK.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6F, 1.0F);
                                        lowerNpcName2 = "husk";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("spider")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.CAVE_SPIDER.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6F, 1.0F);
                                        lowerNpcName2 = "cave spider";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("villager")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        Random random = new Random();
                                        EntityType<?> rndmVillager = villagerTypes.get(random.nextInt(villagerTypes.size()));
                                        entity = rndmVillager.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6F, 1.0F);
                                        if(rndmVillager == EntityType.WITCH) {
                                            lowerNpcName2 = "witch";
                                        }else {
                                            lowerNpcName2 = "zombie villager";
                                        }
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("slime")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.MAGMA_CUBE.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6F, 1.0F);
                                        lowerNpcName2 = "magma cube";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("ghast")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.HAPPY_GHAST.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                        Random random = new Random();
                                        ItemStack rndmElem = harnessTypes.get(random.nextInt(harnessTypes.size()));
                                        ((HappyGhast)entity).setItemSlot(EquipmentSlot.BODY, rndmElem.copy());
                                        serverPlayer.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6F, 1.0F);
                                        lowerNpcName2 = "happy ghast";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("silverfish")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.ENDERMITE.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6F, 1.0F);
                                        lowerNpcName2 = "endermite";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("guardian")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.ELDER_GUARDIAN.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6F, 1.0F);
                                        lowerNpcName2 = "elder guardian";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("fish")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.RARE_MUTATION_DEFAULT) * multiplier) {
                                        entity = BlazinBlazeTwitchModEntities.LIFE_LINKED_FISH_ENTITY.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                        ((LifeLinkedFishEntity)entity).setOwnerUUID(serverPlayer.getStringUUID());
                                        serverPlayer.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6F, 1.0F);
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                    }
                                }else {
                                    entity = mob.type().spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                }
                                if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.POTION_MUTATION_DEFAULT) * multiplier) {
                                    if(!lowerNpcName2.equals("lightning bolt") && !lowerNpcName2.equals("tnt")) {
                                        Random random = new Random();
                                        Holder<MobEffect> rndmPotion = statusEffects.get(random.nextInt(statusEffects.size()));
                                        String potionName = rndmPotion.value().getDisplayName().getString().toUpperCase();
                                        ((LivingEntity) entity).addEffect(new MobEffectInstance(rndmPotion, 50000, 2, true, true, true));
                                        String potionColor = POTION_COLORS.get(potionName.toLowerCase());
                                        if(entity instanceof LifeLinkedFishEntity || entity instanceof LifeLinkedCatEntity || entity instanceof LifeLinkedWolfEntity || entity instanceof LifeLinkedParrotEntity) {
                                            entity.setCustomName(Component.literal(String.format("%s§o[%s]§r §4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's %s", potionColor, potionName, username, WordUtils.capitalize(lowerNpcName2))));
                                        }else {
                                            entity.setCustomName(Component.literal(String.format("%s§o[%s]§r %s's %s", potionColor, potionName, username, WordUtils.capitalize(lowerNpcName2))));
                                        }
                                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload(potionColor + "ENTITY SPAWNED WITH " + potionName + "!");
                                        ServerPlayNetworking.send(serverPlayer, payload);
                                        serverPlayer.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 0.5F);
                                        broadcast(String.format("§5§l[BTM]§r %s%s just spawned in a %s!", potionColor, username, lowerNpcName2), serverPlayer);
                                    }
                                }else {
                                    if(entity instanceof LifeLinkedFishEntity || entity instanceof LifeLinkedCatEntity || entity instanceof LifeLinkedWolfEntity || entity instanceof LifeLinkedParrotEntity) {
                                        entity.setCustomName(Component.literal(String.format("§4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's %s", username, WordUtils.capitalize(lowerNpcName2))));
                                    }else {
                                        entity.setCustomName(Component.literal(String.format("%s's %s", username, WordUtils.capitalize(lowerNpcName2))));
                                    }
                                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§f§lENTITY SPAWNED!");
                                    ServerPlayNetworking.send(serverPlayer, payload);
                                    serverPlayer.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                                    broadcast(String.format("§5§l[BTM]§r %s just spawned in a %s!", username, lowerNpcName2), serverPlayer);
                                }
                            }
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!fling")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.FLING_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            serverPlayer.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20*5, 155));

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§b§lFLUNG!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(BlazinBlazeTwitchModSounds.WEE_SOUND_EFFECT, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r %s just flung you!", username), serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!nuke")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.NUKE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos spawnLocation = serverPlayer.blockPosition();
                            ServerLevel world = serverPlayer.level();

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lNUKE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(SoundEvents.WITHER_DEATH, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §4§l%s just nuked you!", username), serverPlayer);

                            for(int i = 0; i < 25; i++) {
                                if (world.areEntitiesLoaded(serverPlayer.chunkPosition().pack())) {
                                    EntityType.TNT.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED).setFuse(20);
                                }
                            }
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!meganuke")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MEGA_NUKE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos spawnLocation = serverPlayer.blockPosition();
                            ServerLevel world = serverPlayer.level();

                            BlazinBlazeTwitchModEntities.NUCLEAR_TNT_ENTITY.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED).setFuse(80);

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lMEGA NUKE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);
                            broadcast(String.format("§5§l[BTM]§r §4§l%s just MEGA nuked you!", username), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!teleport") || message.toLowerCase().startsWith("!tp")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.TELEPORT_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            Random random = new Random();

                            double randomNumberX = random.nextDouble(10000000 - (-10000000) + 1) + (-10000000);
                            double randomNumberZ = random.nextDouble(10000000 - (-10000000) + 1) + (-10000000);

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§d§lTELEPORTED!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(SoundEvents.ENDERMAN_TELEPORT,  1.0F, 1.0F);

                            broadcast(String.format("§5§l[BTM]§r §d§l%s just teleported you!", username), serverPlayer);
                            serverPlayer.teleportTo(randomNumberX, 1000, randomNumberZ);
                            serverPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 10000000, 1, false, false));
                            TeleportTimer.INSTANCE.setLocation(serverPlayer.level(), (int)randomNumberX, (int)randomNumberZ);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!lootcrate") || message.toLowerCase().startsWith("!loot")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.LOOT_CRATE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos spawnLocation = serverPlayer.blockPosition();
                            ServerLevel world = serverPlayer.level();

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§9§lLOOT CRATE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(SoundEvents.CHEST_OPEN, 0.5F, 1.0F);

                            broadcast(String.format("§5§l[BTM]§r §9§l%s just dropped you a loot crate!", username), serverPlayer);

                            BlockPos topY = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, spawnLocation);

                            RegistryAccess registryManager = world.registryAccess();

                            if(spawnLocation.getY() < topY.getY()) {
                                BlockPos newLocation = new BlockPos(spawnLocation.getX(), (spawnLocation.getY() + 5), spawnLocation.getZ());
                                if(world.isEmptyBlock(newLocation)) {
                                    BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(world, newLocation, EntitySpawnReason.MOB_SUMMONED);
                                }else{
                                    if(Objects.equals(world.dimensionTypeRegistration(), registryManager.lookupOrThrow(Registries.DIMENSION_TYPE).get(BuiltinDimensionTypes.NETHER.identifier()).get())) {
                                        LootCrateAttachmentData data = serverPlayer.getAttachedOrCreate(BlazinBlazeAttachmentTypes.LOOT_CRATE_ATTACHMENT_TYPE, () -> LootCrateAttachmentData.DEFAULT);
                                        serverPlayer.setAttached(BlazinBlazeAttachmentTypes.LOOT_CRATE_ATTACHMENT_TYPE, data.addInt(1));

                                        broadcast("§5§l[BTM]§r §oYou will receive your loot crate when you leave the nether!", serverPlayer);
                                    }else {
                                        BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(world, new BlockPos((int) serverPlayer.getX(), world.getHeight(Heightmap.Types.WORLD_SURFACE, (int) serverPlayer.getX(), (int) serverPlayer.getZ()), (int) serverPlayer.getZ()), EntitySpawnReason.MOB_SUMMONED);
                                        broadcast("§5§l[BTM]§r §oSpawned loot crate at the surface of current position!", serverPlayer);
                                    }
                                }
                            }else {
                                BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(world, new BlockPos(spawnLocation.getX(), (spawnLocation.getY() + 100), spawnLocation.getZ()), EntitySpawnReason.MOB_SUMMONED);
                            }
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }

            }
        }
        if(message.toLowerCase().startsWith("!pet")) {
            String lowerPet = message.substring("!pet ".length());
            if(lowerPet.toLowerCase().contains("wolf")) {
                if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.WOLF_DEFAULT) * multiplier && checkCooldown()) {
                    MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                    if(server != null) {
                        for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                            if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                                BlockPos spawnLocation = serverPlayer.blockPosition();
                                ServerLevel world = serverPlayer.level();

                                if (world.areEntitiesLoaded(serverPlayer.chunkPosition().pack())) {
                                    if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.LIFE_LINKED_DEFAULT) * multiplier) {
                                        LifeLinkedWolfEntity wolf = BlazinBlazeTwitchModEntities.LIFE_LINKED_WOLF_ENTITY.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);

                                        if(wolf != null) {
                                            Random random = new Random();

                                            wolf.setTame(true, true);
                                            wolf.setOwner(serverPlayer);
                                            ((WolfAccess)wolf).callSetCollarColor(DyeColor.RED);

                                            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.WOLF_ARMORED_DEFAULT) * multiplier) {
                                                wolf.setBodyArmorItem(Items.WOLF_ARMOR.getDefaultInstance());
                                                wolf.setCustomName(Component.literal(String.format("§6§l§o[ARMORED]§r §4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Wolf", username)));
                                            }else {
                                                wolf.setCustomName(Component.literal(String.format("§4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Wolf", username)));
                                            }

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§7§lWOLF SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSound(SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.SoundSet.CLASSIC).adultSounds().ambientSound().value(), 1.0F, 0.5F);

                                            broadcast(String.format("§5§l[BTM]§r §4§l%s just gave you a LIFE-LINKED§r§4❤§r§4§l Wolf!", username), serverPlayer);
                                        }
                                    }else {
                                        Wolf wolf = EntityType.WOLF.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);

                                        if(wolf != null) {
                                            Random random = new Random();

                                            DyeColor rndmElem = dyeColors.get(random.nextInt(dyeColors.size()));
                                            ResourceKey<WolfVariant> rndmElem2 = wolfVariants.get(random.nextInt(wolfVariants.size()));

                                            wolf.setTame(true, true);
                                            wolf.setOwner(serverPlayer);
                                            ((WolfAccess)wolf).callSetCollarColor(rndmElem);

                                            RegistryAccess registryManager = world.registryAccess();
                                            Optional<Holder.Reference<WolfVariant>> wolfVariant = registryManager.lookupOrThrow(Registries.WOLF_VARIANT).get(rndmElem2.identifier());

                                            wolfVariant.ifPresentOrElse(value -> ((WolfAccess)wolf).callSetVariant(wolfVariant.get()),
                                                    () -> BlazinBlazeTwitchMod.LOGGER.info("WolfVariant not found!"));

                                            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.WOLF_ARMORED_DEFAULT) * multiplier) {
                                                wolf.setBodyArmorItem(Items.WOLF_ARMOR.getDefaultInstance());
                                                wolf.setCustomName(Component.literal(String.format("§6§l§o[ARMORED]§r %s's Wolf", username)));
                                            }else {
                                                wolf.setCustomName(Component.literal(String.format("%s's Wolf", username)));
                                            }

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§7§lWOLF SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSound(SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.SoundSet.CLASSIC).adultSounds().ambientSound().value(), 1.0F, 1.0F);

                                            broadcast(String.format("§5§l[BTM]§r %s just gave you a Wolf!", username), serverPlayer);
                                        }
                                    }
                                }
                                shouldGiveImmunity(serverPlayer);
                            }else {
                                broadcastBlock(serverPlayer, username);
                            }
                        }
                    }
                }
            }else if(lowerPet.toLowerCase().contains("cat")) {
                if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.CAT_DEFAULT) * multiplier && checkCooldown()) {
                    MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                    if(server != null) {
                        for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                            if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                                BlockPos spawnLocation = serverPlayer.blockPosition();
                                ServerLevel world = serverPlayer.level();

                                if (world.areEntitiesLoaded(serverPlayer.chunkPosition().pack())) {
                                    if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.LIFE_LINKED_DEFAULT) * multiplier) {
                                        LifeLinkedCatEntity cat = BlazinBlazeTwitchModEntities.LIFE_LINKED_CAT_ENTITY.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);

                                        if(cat != null) {

                                            cat.setTame(true, true);
                                            cat.setOwner(serverPlayer);
                                            ((CatAccess)cat).callSetCollarColor(DyeColor.RED);

                                            cat.setCustomName(Component.literal(String.format("§4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Cat", username)));

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§e§lCAT SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSound(SoundEvents.CAT_SOUNDS.get(CatSoundVariants.SoundSet.CLASSIC).adultSounds().ambientSound().value(), 1.0F, 0.5F);

                                            broadcast(String.format("§5§l[BTM]§r §4§l%s just gave you a LIFE-LINKED§r§4❤§r§4§l Cat!", username), serverPlayer);
                                        }
                                    }else {
                                        Cat cat = EntityType.CAT.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);

                                        if(cat != null) {
                                            Random random = new Random();

                                            DyeColor rndmElem = dyeColors.get(random.nextInt(dyeColors.size()));
                                            ResourceKey<CatVariant> rndmElem2 = catVariants.get(random.nextInt(catVariants.size()));

                                            cat.setTame(true, true);
                                            cat.setOwner(serverPlayer);
                                            ((CatAccess)cat).callSetCollarColor(rndmElem);

                                            RegistryAccess registryManager = world.registryAccess();
                                            Optional<Holder.Reference<CatVariant>> catVariant = registryManager.lookupOrThrow(Registries.CAT_VARIANT).get(rndmElem2.identifier());

                                            catVariant.ifPresentOrElse(value -> ((CatAccess)cat).callSetVariant(catVariant.get()),
                                                    () -> BlazinBlazeTwitchMod.LOGGER.info("Cat Variant not found!"));

                                            cat.setCustomName(Component.literal(String.format("%s's Cat", username)));

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§e§lCAT SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSound(SoundEvents.CAT_SOUNDS.get(CatSoundVariants.SoundSet.CLASSIC).adultSounds().ambientSound().value(), 1.0F, 1.0F);

                                            broadcast(String.format("§5§l[BTM]§r %s just gave you a Cat!", username), serverPlayer);
                                        }
                                    }
                                }
                                shouldGiveImmunity(serverPlayer);
                            }else {
                                broadcastBlock(serverPlayer, username);
                            }
                        }
                    }
                }
            }else if(lowerPet.toLowerCase().contains("parrot")) {
                if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.PARROT_DEFAULT) * multiplier && checkCooldown()) {
                    MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                    if(server != null) {
                        for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                            if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                                BlockPos spawnLocation = serverPlayer.blockPosition();
                                ServerLevel world = serverPlayer.level();

                                if (world.areEntitiesLoaded(serverPlayer.chunkPosition().pack())) {
                                    if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.LIFE_LINKED_DEFAULT) * multiplier) {
                                        LifeLinkedParrotEntity parrot = BlazinBlazeTwitchModEntities.LIFE_LINKED_PARROT_ENTITY.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);

                                        if(parrot != null) {
                                            parrot.setTame(true, true);
                                            parrot.setOwner(serverPlayer);

                                            parrot.setCustomName(Component.literal(String.format("§4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Parrot", username)));

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§1§lPARROT SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSound(SoundEvents.PARROT_AMBIENT, 1.0F, 0.5F);

                                            broadcast(String.format("§5§l[BTM]§r §4§l%s just gave you a LIFE-LINKED§r§4❤§r§4§l Parrot!", username), serverPlayer);
                                        }
                                    }else {
                                        Parrot parrot = EntityType.PARROT.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);

                                        if(parrot != null) {
                                            Random random = new Random();

                                            Parrot.Variant rndmElem = parrotVariants.get(random.nextInt(parrotVariants.size()));

                                            parrot.setTame(true, true);
                                            parrot.setOwner(serverPlayer);
                                            ((ParrotAccess)parrot).callSetVariant(rndmElem);

                                            parrot.setCustomName(Component.literal(String.format("%s's Parrot", username)));

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§1§lPARROT SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSound(SoundEvents.PARROT_AMBIENT, 1.0F, 1.0F);

                                            broadcast(String.format("§5§l[BTM]§r %s just gave you a Parrot!", username), serverPlayer);
                                        }
                                    }
                                }
                                shouldGiveImmunity(serverPlayer);
                            }else {
                                broadcastBlock(serverPlayer, username);
                            }
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!potion")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.POTION_EFFECT_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            Random random = new Random();
                            Holder<MobEffect> rndmPotion = statusEffects2.get(random.nextInt(statusEffects2.size()));
                            String potionName = rndmPotion.value().getDisplayName().getString().toLowerCase();

                            serverPlayer.addEffect(new MobEffectInstance(rndmPotion, 1200, 2));

                            String potionColor2 = POTION_COLORS2.get(potionName.toLowerCase());

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload(potionColor2 + "POTION EFFECT!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(SoundEvents.SPLASH_POTION_BREAK, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r %s%s just gave you the %s effect!", potionColor2, username, potionName), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!cbomb")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.CHICKEN_BOMB_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos spawnLocation = serverPlayer.blockPosition();
                            ServerLevel world = serverPlayer.level();
                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§f§lCHICKEN BOMB!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §f§l%s just Chicken Bombed you!", username), serverPlayer);

                            for(int i = 0; i < 500; i++) {
                                if (world.areEntitiesLoaded(serverPlayer.chunkPosition().pack())) {
                                    Chicken c = EntityType.CHICKEN.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                                    c.setCustomName(Component.nullToEmpty(String.format("§4§l%s's CBOMB", username)));
                                }
                            }
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!message") || message.toLowerCase().startsWith("!msg")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MESSAGE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            ServerLevel world = serverPlayer.level();
                            MailboxAttachmentData data = serverPlayer.getAttachedOrCreate(BlazinBlazeAttachmentTypes.MAILBOX_ATTACHMENT_TYPE, () -> MailboxAttachmentData.DEFAULT);

                            String messageText = message.substring("!message ".length());
                            String limitedText = messageText.substring(0, Math.min(messageText.length(), 500));

                            String chosenType;

                            while(true) {
                                Random random = new Random();
                                String rndmKey = mailKeys.get(random.nextInt(mailKeys.size()));
                                Double rndmRarity = MAIL_RARITIES.get(rndmKey);

                                if(rndmKey == null) {
                                    chosenType = "default";
                                    break;
                                }

                                if(ThreadLocalRandom.current().nextDouble() < rndmRarity) {
                                    chosenType = rndmKey;
                                    break;
                                }
                            }

                            Random random = new Random();
                            String rndmFarewell = authorFarewells.get(random.nextInt(authorFarewells.size()));

                            ItemStack stack;
                            stack = MAIL_TYPES.get(chosenType).copy();
                            stack.set(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, limitedText);
                            stack.set(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, username);
                            stack.set(BlazinBlazeTwitchModComponents.MAIL_TYPE, chosenType);
                            stack.set(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, rndmFarewell);

                            server.execute(() -> {
                                try {
                                    BlockEntity assignedMailbox = world.getBlockEntity(data.blockPos());
                                    if(assignedMailbox instanceof MailboxBlockEntity mailboxBlockEntity) {
                                        NonNullList<ItemStack> inventory = mailboxBlockEntity.getItems();

                                        boolean inserted = false;

                                        for(int i = 0; i < inventory.size(); i++)  {
                                            ItemStack tempStack = inventory.get(i);
                                            if(tempStack.isEmpty()) {
                                                inventory.set(i, stack);
                                                inserted = true;
                                                break;
                                            }
                                        }

                                        if(inserted) {
                                            mailboxBlockEntity.setItems(inventory);
                                            MailNotifyS2CPayload payload = new MailNotifyS2CPayload(username);
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSound(SoundEvents.NETHER_WOOD_DOOR_OPEN, 1.0F, 1.0F);
                                            broadcast(String.format("§5§l[BTM]§r §7§l%s just sent you a message!", username), serverPlayer);
                                            shouldGiveImmunity(serverPlayer);
                                        }else {
                                            serverPlayer.sendSystemMessage(Component.literal("§5§l[BTM]§r §c§lYour mailbox is full!"));
                                        }
                                    }else {
                                        serverPlayer.sendSystemMessage(Component.literal("§5§l[BTM]§r §cYou don't have a mailbox to accept mail."));
                                    }

                                }catch(Exception e) {
                                    BlazinBlazeTwitchMod.LOGGER.error("Failed to get assigned mailbox", e);
                                }
                            });
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!voicemessage") || message.toLowerCase().startsWith("!vm")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.VOICE_MESSAGE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            ServerLevel world = serverPlayer.level();
                            RadioAttachmentData data = serverPlayer.getAttachedOrCreate(BlazinBlazeAttachmentTypes.RADIO_ATTACHMENT_TYPE, () -> RadioAttachmentData.DEFAULT);
                            String messageText = message.toLowerCase().substring("!voicemessage ".length()); //said sage cuz of MesSAGE... bruh
                            String limitedText = messageText.substring(0, Math.min(messageText.length(), 250));

                            ItemStack stack = BlazinBlazeTwitchModItems.VOICE_MESSAGE_ITEM.getDefaultInstance().copy();
                            stack.set(BlazinBlazeTwitchModComponents.VOICE_MESSAGE, limitedText);
                            stack.set(BlazinBlazeTwitchModComponents.VOICE_AUTHOR, username);

                            server.execute(() -> {
                                try {
                                    BlockEntity assignedMailbox = world.getBlockEntity(data.blockPos());
                                    if(assignedMailbox instanceof RadioBlockEntity radioBlockEntity) {
                                        NonNullList<ItemStack> inventory = radioBlockEntity.getItems();

                                        boolean inserted = false;

                                        for(int i = 0; i < inventory.size(); i++)  {
                                            ItemStack tempStack = inventory.get(i);
                                            if(tempStack.isEmpty()) {
                                                inventory.set(i, stack);
                                                inserted = true;
                                                break;
                                            }
                                        }

                                        if(inserted) {
                                            radioBlockEntity.setItems(inventory);
                                            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.AUTOMATIC_VOICE_MESSAGE_DEFAULT) * multiplier) {
                                                VoiceMessageS2CPayload payload = new VoiceMessageS2CPayload(limitedText, username);
                                                VoiceMsgNotifyS2CPayload payload2 = new VoiceMsgNotifyS2CPayload(username);
                                                ServerPlayNetworking.send(serverPlayer, payload);
                                                ServerPlayNetworking.send(serverPlayer, payload2);
                                                serverPlayer.playSound(BlazinBlazeTwitchModSounds.VOICE_MESSAGE_EFFECT, 1.0F, 1.0F);
                                                serverPlayer.playSound(SoundEvents.VILLAGER_AMBIENT, 1.0F, 1.0F);
                                                broadcast(String.format("§5§l[BTM]§r §9§l%s just sent you an AUTOMATIC voice message!", username), serverPlayer);
                                                shouldGiveImmunity(serverPlayer);
                                            }else {
                                                VoiceMsgNotifyS2CPayload payload = new VoiceMsgNotifyS2CPayload(username);
                                                ServerPlayNetworking.send(serverPlayer, payload);
                                                serverPlayer.playSound(SoundEvents.VILLAGER_AMBIENT, 1.0F, 1.0F);
                                                broadcast(String.format("§5§l[BTM]§r §9§l%s just sent you a voice message!", username), serverPlayer);
                                                shouldGiveImmunity(serverPlayer);
                                            }
                                        }else {
                                            serverPlayer.sendSystemMessage(Component.literal("§5§l[BTM]§r §c§lYour mailbox is full!"));
                                        }
                                    }else {
                                        serverPlayer.sendSystemMessage(Component.literal("§5§l[BTM]§r §cYou don't have a radio to accept voice messages."));
                                    }
                                }catch(Exception e) {
                                    BlazinBlazeTwitchMod.LOGGER.error("Failed to get assigned radio", e);
                                }
                            });
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!ditem")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.DELETE_ITEM_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            ItemStack activeItem = serverPlayer.getItemInHand(serverPlayer.getUsedItemHand());
                            if(activeItem != null) {
                                activeItem.shrink(activeItem.getCount());
                            }

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§c§lITEM DELETE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r %s just deleted your item!", username), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!dinventory")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.DELETE_INVENTORY_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            Inventory inventory = serverPlayer.getInventory();
                            inventory.clearContent();

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§c§lINVENTORY DELETE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §4§l%s just deleted your ENTIRE INVENTORY!", username), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!void")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.VOID_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos location = serverPlayer.blockPosition();
                            serverPlayer.teleportTo(location.getX(), -65, location.getZ());

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§8§lVOID...");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(SoundEvents.AMBIENT_CAVE.value(), 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §8§l%s just threw you into the VOID!", username), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!beddy")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.BEDDY_DEFAULT) * multiplier && checkCooldown()) {

                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos location = serverPlayer.blockPosition();
                            ServerLevel world = serverPlayer.level();
                            BeddyEntity beddy = BlazinBlazeTwitchModEntities.BEDDY_ENTITY.spawn(world, location, EntitySpawnReason.MOB_SUMMONED);
                            beddy.setLeashedTo(serverPlayer, true);
                            beddy.setUser(username);
                            beddy.setUUID(serverPlayer.getStringUUID());

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§8§lBEDDY...");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(SoundEvents.STONE_BREAK, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §8§l%s just attached you to Beddy!", username), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!sound")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.SOUND_DEFAULT) * multiplier && checkCooldown()) {

                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            Random random = new Random();
                            SoundEvent rndmSound = soundEffects.get(random.nextInt(soundEffects.size()));
                            serverPlayer.playSound(rndmSound, 1.0F, 1.0F);

                            SoundMessageTimer.INSTANCE.setTimerAndUser(100L, username);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!fullscreen")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.FULLSCREEN_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {

                            FullscreenS2CPayload payload = new FullscreenS2CPayload("§6§lFULLSCREEN TOGGLED!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSound(SoundEvents.PISTON_EXTEND, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §8§l%s just toggled fullscreen!", username), serverPlayer);

                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!close")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.CLOSE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            CloseS2CPayload payload = new CloseS2CPayload(username);
                            ServerPlayNetworking.send(serverPlayer, payload);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!dtimer")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.DECREASE_TIMER_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {

                            MultiplierTick.MultiplierTimer.eventBoost(30);
                            serverPlayer.playSound(SoundEvents.WOLF_ARMOR_CRACK, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §c§l%s just decreased the multiplier timer!", username), serverPlayer);

                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, username);
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!hold")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.CHATTER_HOLD) * multiplier) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;
                String lowerItem = message.substring("!hold ".length());

                if(ITEM_ALIAS.containsKey(lowerItem)) {
                    lowerItem = ITEM_ALIAS.get(lowerItem);
                }

                if(server != null) {
                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                        ServerLevel serverWorld = serverPlayer.level();
                        List<? extends ChatterEntity> entities = getCurrentChattersForId(serverWorld, serverPlayer, event.getUser().getId());

                        if(lowerItem.matches("player_head_item")) {
                            ItemStack playerHead = Items.PLAYER_HEAD.getDefaultInstance().copy();
                            playerHead.set(DataComponents.PROFILE, ResolvableProfile.createResolved(serverPlayer.getGameProfile()));
                            for(var entity : entities) {
                                entity.setHeldItem(playerHead);
                            }
                        }else {
                            Identifier id = Identifier.withDefaultNamespace(lowerItem);

                            Item chattedItem = BuiltInRegistries.ITEM.getValue(id);
                            Item defaultItem = BuiltInRegistries.ITEM.getValue(BuiltInRegistries.ITEM.getDefaultKey());
                            if(entities.isEmpty() || chattedItem.equals(defaultItem)) {
                                //add response code perhaps
                            }else {
                                ItemStack itemStack = chattedItem.getDefaultInstance();
                                for(var entity : entities) {
                                    entity.setHeldItem(itemStack);
                                }
                            }
                        }
                    }
                }
            }
        }

        final Collection<Emote> globalEmotes = BlazinBlazeTwitchMod.globalEmotes;
        final Map<String, List<Emote>> channelEmotes = BlazinBlazeTwitchMod.channelEmotes;
        final Map<String, List<Emote>> sharedEmotes = BlazinBlazeTwitchMod.sharedEmotes;

        if(globalEmotes != null) {
            int max = 0;
            for (Emote emote : globalEmotes) {
                if(max < 3) {
                    String[] messageWords = message.split(" ");
                    for(String word : messageWords) {
                        if(word.matches(emote.code())) {
                            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.EMOTE_DISPLAY_DEFAULT)) {
                                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                                if(server != null) {
                                    for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                                        EmoteScreenS2CPayload payload = new EmoteScreenS2CPayload(emote.id(), emote.imageType());
                                        ServerPlayNetworking.send(serverPlayer, payload);
                                    }
                                }
                                max++;
                            }
                        }
                    }
                }else {
                    break;
                }
            }
        }

        if(channelEmotes != null && !channelEmotes.isEmpty()) {
            List<Emote> emotes = channelEmotes.get(event.getChannel().getId());
            if(emotes != null) {
                int max = 0;
                for (Emote emote : emotes) {
                    if(max < 3) {
                        String[] messageWords = message.split(" ");
                        for(String word : messageWords) {
                            if(word.matches(emote.code())) {
                                if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.EMOTE_DISPLAY_DEFAULT)) {
                                    MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                                    if(server != null) {
                                        for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                                            EmoteScreenS2CPayload payload = new EmoteScreenS2CPayload(emote.id(), emote.imageType());
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                        }
                                    }
                                    max++;
                                }
                            }
                        }
                    }else {
                        break;
                    }
                }
            }
        }

        if(sharedEmotes != null && !sharedEmotes.isEmpty()) {
            List<Emote> emotes = sharedEmotes.get(event.getChannel().getId());
            if(emotes != null) {
                int max = 0;
                for (Emote emote : emotes) {
                    if(max < 3) {
                        String[] messageWords = message.split(" ");
                        for(String word : messageWords) {
                            if(word.matches(emote.code())) {
                                if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.EMOTE_DISPLAY_DEFAULT)) {

                                    MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                                    if(server != null) {
                                        for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                                            EmoteScreenS2CPayload payload = new EmoteScreenS2CPayload(emote.id(), emote.imageType());
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                        }
                                    }
                                    max++;
                                }
                            }
                        }
                    }else {
                        break;
                    }
                }
            }
        }

        MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

        if(server != null) {
            for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                ServerLevel world = serverPlayer.level();
                BlockPos spawnLocation = serverPlayer.blockPosition();

                List<? extends ChatterEntity> entities = getCurrentChattersForId(world, serverPlayer, event.getUser().getId());

                if (entities.isEmpty()) {
                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.CHATTER_SPAWN) * multiplier) {
                        if (chatterBucket.tryConsume(1L)) {
                            ChatterEntity chatter = BlazinBlazeTwitchModEntities.CHATTER_ENTITY.spawn(world, spawnLocation, EntitySpawnReason.MOB_SUMMONED);
                            chatter.setUserId(event.getUser().getId());

                            chatter.setTame(true, true);
                            chatter.setOwner(serverPlayer);
                            chatter.setUsername(username);

                            serverPlayer.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §d%s just spawned in as a chatter!", username), serverPlayer);
                        }
                    }
                } else {
                    String limitedText = message.substring(0, Math.min(message.length(), 250));

                    boolean containsCommand = false;
                    for(String command : commandNames) {
                        if(limitedText.contains(command)) {
                            containsCommand = true;
                            break;
                        }
                    }

                    if(!containsCommand) {
                        for (var entity : entities) {
                            entity.setChat(limitedText);
                        }
                    }
                }
            }
        }
    }

    private List<? extends ChatterEntity> getCurrentChattersForId(ServerLevel serverWorld, ServerPlayer serverPlayer, String id) {
        return serverWorld.getEntities(EntityTypeTest.forClass(ChatterEntity.class), (entity) -> entity.getOwner() == serverPlayer && entity.getUserId().matches(id));
    }

    private boolean checkCooldown() {
        return commandBucket.tryConsume(1L);
    }

    private double getProbabilityOrDefault(BlazinBlazeProbabilities probability) {
        if(BlazinBlazeTwitchMod.config != null) {
            Double configProb = BlazinBlazeTwitchMod.config.getProbability(probability.getKey());
            if(configProb != null) {
                if(configProb > 1) {
                    return 1;
                }
                if(configProb < 0) {
                    return 0;
                }
                return configProb;
            }else {
                return probability.getProbabilty();
            }
        }else {
            return probability.getProbabilty();
        }
    }

    private void shouldGiveImmunity(ServerPlayer serverPlayer) {
        if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.IMMUNITY_DEFAULT)) {
            serverPlayer.addItem(BlazinBlazeTwitchModItems.IMMUNITY_SHIELD_ITEM.getDefaultInstance().copy());
            serverPlayer.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            broadcast("§5§l[BTM]§r §2§lYou just received an immunity shield!", serverPlayer);
        }
    }

    private void broadcastBlock(ServerPlayer serverPlayer, String username) {
        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lENTITY SPAWN BLOCKED!");
        ServerPlayNetworking.send(serverPlayer, payload);
        serverPlayer.playSound(SoundEvents.SHIELD_BLOCK.value(), 1.0F, 1.0F);
        broadcast(String.format("§5§l[BTM]§r §c%s's event just got blocked!", username), serverPlayer);
    }

    private void broadcast(String message, ServerPlayer serverPlayer) {
        serverPlayer.sendSystemMessage(Component.literal(message), false);
    }

    record SpawnableMob(EntityType type, double threshold) {}
}
