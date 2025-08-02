package net.blazinblaze.blazetwitchmod;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.common.util.BucketUtils;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Stream;
import io.github.iprodigy.bttv.common.Emote;
import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.data.BlazinBlazeAttachmentTypes;
import net.blazinblaze.blazetwitchmod.data.LootCrateAttachmentData;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.entity.custom.*;
import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.blazinblaze.blazetwitchmod.mixin.CatEntityAccess;
import net.blazinblaze.blazetwitchmod.mixin.ParrotEntityAccess;
import net.blazinblaze.blazetwitchmod.mixin.WolfEntityAccess;
import net.blazinblaze.blazetwitchmod.networking.*;
import net.blazinblaze.blazetwitchmod.reference.BlazinBlazeProbabilities;
import net.blazinblaze.blazetwitchmod.sound.BlazinBlazeTwitchModSounds;
import net.blazinblaze.blazetwitchmod.util.MultiplierTick;
import net.blazinblaze.blazetwitchmod.util.SoundMessageTimer;
import net.blazinblaze.blazetwitchmod.util.TeleportTimer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.*;
import io.github.bucket4j.Bucket;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.dimension.DimensionTypes;
import org.apache.commons.lang.WordUtils;
import org.apache.logging.log4j.core.jmx.Server;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import java.time.Duration;

public class BlazinBlazeTwitchModEvent {

    private static final Map<String, SpawnableMob> SPAWNABLE_MOBS = new HashMap<>();
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
    private static final Map<String, Double> MAIL_RARITIES = new HashMap<>();
    private static final Map<String, ItemStack> MAIL_TYPES = Map.ofEntries(
            Map.entry("default", BlazinBlazeTwitchModItems.MAIL_ITEM.asItem().getDefaultStack()),
            Map.entry("starry", BlazinBlazeTwitchModItems.MAIL_ITEM_STARRY.asItem().getDefaultStack()),
            Map.entry("blazin", BlazinBlazeTwitchModItems.MAIL_ITEM_BLAZIN.asItem().getDefaultStack()),
            Map.entry("epic", BlazinBlazeTwitchModItems.MAIL_ITEM_EPIC.asItem().getDefaultStack()),
            Map.entry("minecraft", BlazinBlazeTwitchModItems.MAIL_ITEM_MINECRAFT.asItem().getDefaultStack()),
            Map.entry("error", BlazinBlazeTwitchModItems.MAIL_ITEM_ERROR.asItem().getDefaultStack()),
            Map.entry("sinister", BlazinBlazeTwitchModItems.MAIL_ITEM_SINISTER.asItem().getDefaultStack()));
    private static final Map<String, String> ITEM_ALIAS = Map.ofEntries(
            Map.entry("steak", "cooked_beef")
    );
    private final Bucket commandBucket = BucketUtils.createBucket(BucketUtils.simple(5, Duration.ofSeconds(10)));
    private final Bucket chatterBucket = BucketUtils.createBucket(BucketUtils.simple(1, Duration.ofSeconds(60)));
    private static final ArrayList<DyeColor> dyeColors = new ArrayList<DyeColor>();
    private static final ArrayList<RegistryKey<WolfVariant>> wolfVariants = new ArrayList<RegistryKey<WolfVariant>>();
    private static final ArrayList<RegistryKey<CatVariant>> catVariants = new ArrayList<RegistryKey<CatVariant>>();
    private static final ArrayList<ParrotEntity.Variant> parrotVariants = new ArrayList<ParrotEntity.Variant>();
    private static final ArrayList<RegistryEntry<StatusEffect>> statusEffects = new ArrayList<RegistryEntry<StatusEffect>>();
    private static final ArrayList<RegistryEntry<StatusEffect>> statusEffects2 = new ArrayList<RegistryEntry<StatusEffect>>();
    private static final ArrayList<String> mailKeys = new ArrayList<String>();
    private static final ArrayList<String> authorFarewells = new ArrayList<String>();
    private static final ArrayList<SoundEvent> soundEffects = new ArrayList<SoundEvent>();
    private static final ArrayList<EntityType<?>> skeletonTypes = new ArrayList<EntityType<?>>();
    private static final ArrayList<EntityType<?>> villagerTypes = new ArrayList<EntityType<?>>();
    private static final ArrayList<ItemStack> harnessTypes = new ArrayList<ItemStack>();
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

        statusEffects.add(StatusEffects.HEALTH_BOOST);
        statusEffects.add(StatusEffects.STRENGTH);
        statusEffects.add(StatusEffects.SPEED);
        statusEffects.add(StatusEffects.REGENERATION);
        statusEffects.add(StatusEffects.FIRE_RESISTANCE);
        statusEffects.add(StatusEffects.RESISTANCE);
        statusEffects.add(StatusEffects.ABSORPTION);
        statusEffects.add(StatusEffects.INVISIBILITY);
        statusEffects.add(StatusEffects.JUMP_BOOST);
        statusEffects.add(StatusEffects.LEVITATION);
        statusEffects.add(StatusEffects.SLOWNESS);
        statusEffects.add(StatusEffects.WEAKNESS);
        statusEffects.add(StatusEffects.POISON);

        statusEffects2.add(StatusEffects.HEALTH_BOOST);
        statusEffects2.add(StatusEffects.STRENGTH);
        statusEffects2.add(StatusEffects.SPEED);
        statusEffects2.add(StatusEffects.REGENERATION);
        statusEffects2.add(StatusEffects.FIRE_RESISTANCE);
        statusEffects2.add(StatusEffects.RESISTANCE);
        statusEffects2.add(StatusEffects.ABSORPTION);
        statusEffects2.add(StatusEffects.INVISIBILITY);
        statusEffects2.add(StatusEffects.JUMP_BOOST);
        statusEffects2.add(StatusEffects.LEVITATION);
        statusEffects2.add(StatusEffects.SLOWNESS);
        statusEffects2.add(StatusEffects.WEAKNESS);
        statusEffects2.add(StatusEffects.POISON);
        statusEffects2.add(StatusEffects.BLINDNESS);
        statusEffects2.add(StatusEffects.NAUSEA);
        statusEffects2.add(StatusEffects.WITHER);
        statusEffects2.add(StatusEffects.UNLUCK);
        statusEffects2.add(StatusEffects.LUCK);
        statusEffects2.add(StatusEffects.SLOW_FALLING);
        statusEffects2.add(StatusEffects.MINING_FATIGUE);

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

        parrotVariants.add(ParrotEntity.Variant.BLUE);
        parrotVariants.add(ParrotEntity.Variant.RED_BLUE);
        parrotVariants.add(ParrotEntity.Variant.GRAY);
        parrotVariants.add(ParrotEntity.Variant.YELLOW_BLUE);
        parrotVariants.add(ParrotEntity.Variant.GREEN);

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

        soundEffects.add(SoundEvents.ENTITY_CREEPER_PRIMED);
        soundEffects.add(SoundEvents.AMBIENT_CAVE.value());
        soundEffects.add(SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR);
        soundEffects.add(SoundEvents.ENTITY_ZOMBIE_AMBIENT);
        soundEffects.add(SoundEvents.ENTITY_ZOMBIE_AMBIENT);
        soundEffects.add(SoundEvents.ENTITY_WARDEN_EMERGE);
        soundEffects.add(SoundEvents.ENTITY_WARDEN_NEARBY_CLOSE);
        soundEffects.add(SoundEvents.ENTITY_WARDEN_HEARTBEAT);
        soundEffects.add(SoundEvents.ENTITY_WARDEN_DIG);
        soundEffects.add(SoundEvents.ENTITY_WARDEN_AMBIENT);
        soundEffects.add(SoundEvents.ENTITY_SKELETON_AMBIENT);
        soundEffects.add(SoundEvents.ITEM_CROSSBOW_SHOOT);
        soundEffects.add(SoundEvents.ENTITY_ARROW_HIT_PLAYER);
        soundEffects.add(SoundEvents.ENTITY_PLAYER_HURT);
        soundEffects.add(SoundEvents.BLOCK_DEEPSLATE_BREAK);
        soundEffects.add(SoundEvents.BLOCK_DEEPSLATE_PLACE);
        soundEffects.add(SoundEvents.BLOCK_STONE_BREAK);
        soundEffects.add(SoundEvents.BLOCK_STONE_PLACE);
        soundEffects.add(SoundEvents.BLOCK_WOOD_PLACE);
        soundEffects.add(SoundEvents.BLOCK_WOOD_BREAK);
        soundEffects.add(SoundEvents.BLOCK_WOODEN_DOOR_OPEN);
        soundEffects.add(SoundEvents.BLOCK_WOODEN_DOOR_CLOSE);
        soundEffects.add(SoundEvents.BLOCK_COPPER_DOOR_OPEN);
        soundEffects.add(SoundEvents.BLOCK_COPPER_DOOR_CLOSE);
        soundEffects.add(SoundEvents.BLOCK_IRON_DOOR_OPEN);
        soundEffects.add(SoundEvents.BLOCK_IRON_DOOR_CLOSE);
        soundEffects.add(SoundEvents.BLOCK_CHEST_OPEN);
        soundEffects.add(SoundEvents.BLOCK_CHEST_CLOSE);
        soundEffects.add(SoundEvents.ENTITY_SPIDER_AMBIENT);
        soundEffects.add(SoundEvents.ENTITY_PLAYER_BURP);
        soundEffects.add(SoundEvents.ENTITY_PLAYER_BIG_FALL);
        soundEffects.add(SoundEvents.ENTITY_PLAYER_SMALL_FALL);
        soundEffects.add(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT);
        soundEffects.add(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP);
        soundEffects.add(SoundEvents.ENTITY_PLAYER_SWIM);
        soundEffects.add(SoundEvents.ENTITY_ENDERMAN_SCREAM);
        soundEffects.add(SoundEvents.ENTITY_ENDERMAN_AMBIENT);
        soundEffects.add(SoundEvents.ENTITY_ENDERMAN_STARE);
        soundEffects.add(SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        soundEffects.add(SoundEvents.ENTITY_DROWNED_AMBIENT_WATER);
        soundEffects.add(SoundEvents.ENTITY_DROWNED_AMBIENT);
        soundEffects.add(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT);
        soundEffects.add(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
        soundEffects.add(SoundEvents.ENTITY_GENERIC_EXPLODE.value());
        soundEffects.add(SoundEvents.BLOCK_DEEPSLATE_STEP);
        soundEffects.add(SoundEvents.BLOCK_GRASS_STEP);
        soundEffects.add(SoundEvents.BLOCK_GRAVEL_STEP);
        soundEffects.add(SoundEvents.BLOCK_GRAVEL_BREAK);
        soundEffects.add(SoundEvents.BLOCK_GRAVEL_PLACE);
        soundEffects.add(SoundEvents.BLOCK_STONE_STEP);
        soundEffects.add(SoundEvents.BLOCK_WOOD_STEP);
        soundEffects.add(SoundEvents.BLOCK_GRASS_BREAK);
        soundEffects.add(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME);
        soundEffects.add(SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE);
        soundEffects.add(SoundEvents.ENTITY_GHAST_AMBIENT);
        soundEffects.add(SoundEvents.ENTITY_GHAST_SCREAM);
        soundEffects.add(SoundEvents.ENTITY_GHAST_SHOOT);

        skeletonTypes.add(EntityType.BOGGED);
        skeletonTypes.add(EntityType.WITHER_SKELETON);
        skeletonTypes.add(EntityType.STRAY);

        villagerTypes.add(EntityType.ZOMBIE_VILLAGER);
        villagerTypes.add(EntityType.WITCH);

        harnessTypes.add(Items.BLACK_HARNESS.getDefaultStack());
        harnessTypes.add(Items.BLUE_HARNESS.getDefaultStack());
        harnessTypes.add(Items.RED_HARNESS.getDefaultStack());
        harnessTypes.add(Items.LIME_HARNESS.getDefaultStack());
        harnessTypes.add(Items.PURPLE_HARNESS.getDefaultStack());
        harnessTypes.add(Items.MAGENTA_HARNESS.getDefaultStack());
        harnessTypes.add(Items.GREEN_HARNESS.getDefaultStack());
        harnessTypes.add(Items.LIGHT_BLUE_HARNESS.getDefaultStack());
        harnessTypes.add(Items.YELLOW_HARNESS.getDefaultStack());
        harnessTypes.add(Items.WHITE_HARNESS.getDefaultStack());
        harnessTypes.add(Items.LIGHT_GRAY_HARNESS.getDefaultStack());
        harnessTypes.add(Items.GRAY_HARNESS.getDefaultStack());
        harnessTypes.add(Items.CYAN_HARNESS.getDefaultStack());
        harnessTypes.add(Items.BROWN_HARNESS.getDefaultStack());
        harnessTypes.add(Items.ORANGE_HARNESS.getDefaultStack());
        harnessTypes.add(Items.PINK_HARNESS.getDefaultStack());
    }

    @EventSubscriber
    public void onStreamUp(ChannelGoLiveEvent event) {
        Stream stream = event.getStream();
        //broadcast(String.format("§o[Twitch] %s has gone live for %d viewers: %s", stream.getUserName(), stream.getViewerCount(), stream.getTitle()), serverPlayer);
    }

    @EventSubscriber
    public void onStreamDown(ChannelGoOfflineEvent event) {
        //broadcast(String.format("§o[Twitch] %s has stopped streaming.", event.getChannel().getName()), serverPlayer);
    }

    @EventSubscriber
    public void onFollow(FollowEvent event) {
        MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

        if(server != null) {
            FollowNotifyS2CPayload payload = new FollowNotifyS2CPayload(event.getUser().getName());
            PlaySoundS2CPayload payload2 = new PlaySoundS2CPayload(BlazinBlazeTwitchModSounds.TADA_SOUND_EFFECT, 0.5F, 1.0F);

            for (ServerPlayerEntity player : PlayerLookup.all(server)) {
                ServerPlayNetworking.send(player, payload);
                ServerPlayNetworking.send(player, payload2);
                broadcast(String.format("§5§l[BLAZIN] %s just followed you!", event.getUser().getName()), player);
            }
        }
    }

    @EventSubscriber
    public void onCheer(CheerEvent event) {
        //if (event.getBits() >= 500)
            //broadcast(String.format("[Twitch] %s just cheered %d bits for %s!", event.getUser().getName(), event.getBits(), event.getChannel().getName()));
    }

    @EventSubscriber
    public void onSub(SubscriptionEvent event) {
        //if (!event.getGifted())
            //broadcast(String.format("[Twitch] %s just subscribed to %s for %d months", event.getUser().getName(), event.getChannel().getName(), event.getMonths()));
    }

    @EventSubscriber
    public void onChat(ChannelMessageEvent event) {
        String message = event.getMessage();
        String username = event.getUser().getName();
        int multiplier = MultiplierTick.MultiplierTimer.getMultiplier();
        if(message.toLowerCase().startsWith("!spawn ")) {
            String lowerNpcName = message.substring("!spawn ".length()).toLowerCase();
            SpawnableMob mob = SPAWNABLE_MOBS.get(lowerNpcName);

            //Old Version
            //String[] fullMessage = message.split("\\s+");
            //String command = fullMessage[0];
            // lowerNpcName = fullMessage[1];

            if (mob != null && ThreadLocalRandom.current().nextDouble() < (mob.threshold() * multiplier) && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    //ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(TARGET_PLAYER);

                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();
                            ServerWorld world = serverPlayer.getWorld();

                            if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                Entity entity;
                                String lowerNpcName2 = message.substring("!spawn ".length()).toLowerCase();
                                if(lowerNpcName2.equals("creeper")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.RARE_MUTATION_DEFAULT) * multiplier) {
                                        entity = BlazinBlazeTwitchModEntities.MEGA_CREEPER.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                        lowerNpcName2 = "mega creeper";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("skeleton")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        Random random = new Random();
                                        EntityType<?> rndmSkeleton = skeletonTypes.get(random.nextInt(skeletonTypes.size()));
                                        entity = rndmSkeleton.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                        if(rndmSkeleton == EntityType.BOGGED) {
                                            lowerNpcName2 = "bogged";
                                        }else if(rndmSkeleton == EntityType.WITHER_SKELETON) {
                                            lowerNpcName2 = "wither skeleton";
                                        }else {
                                            lowerNpcName2 = "stray";
                                        }
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("zombie")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.RARE_MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.GIANT.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                        lowerNpcName2 = "giant";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    }else if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.HUSK.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                        lowerNpcName2 = "husk";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("spider")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.CAVE_SPIDER.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                        lowerNpcName2 = "cave spider";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("villager")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        Random random = new Random();
                                        EntityType<?> rndmVillager = villagerTypes.get(random.nextInt(villagerTypes.size()));
                                        entity = rndmVillager.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                        if(rndmVillager == EntityType.WITCH) {
                                            lowerNpcName2 = "witch";
                                        }else {
                                            lowerNpcName2 = "zombie villager";
                                        }
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("slime")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.MAGMA_CUBE.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                        lowerNpcName2 = "magma cube";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("ghast")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.HAPPY_GHAST.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                        Random random = new Random();
                                        ItemStack rndmElem = harnessTypes.get(random.nextInt(harnessTypes.size()));
                                        ((HappyGhastEntity)entity).equipStack(EquipmentSlot.BODY, rndmElem.copy());
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                        lowerNpcName2 = "happy ghast";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("silverfish")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.ENDERMITE.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                        lowerNpcName2 = "endermite";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("guardian")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MUTATION_DEFAULT) * multiplier) {
                                        entity = EntityType.ELDER_GUARDIAN.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                        lowerNpcName2 = "elder guardian";
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    }
                                }else if(lowerNpcName2.equals("fish")) {
                                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.RARE_MUTATION_DEFAULT) * multiplier) {
                                        entity = BlazinBlazeTwitchModEntities.LIFE_LINKED_FISH_ENTITY.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                        ((LifeLinkedFishEntity)entity).setOwnerUUID(serverPlayer.getUuidAsString());
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                        broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                    } else {
                                        entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    }
                                }else {
                                    entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                }
                                if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.POTION_MUTATION_DEFAULT) * multiplier) {
                                    if(!lowerNpcName2.equals("lightning bolt") && !lowerNpcName2.equals("tnt")) {
                                        Random random = new Random();
                                        RegistryEntry<StatusEffect> rndmPotion = statusEffects.get(random.nextInt(statusEffects.size()));
                                        String potionName = rndmPotion.value().getName().getString().toUpperCase();
                                        ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(rndmPotion, 50000, 2, true, true, true));
                                        String potionColor = POTION_COLORS.get(potionName.toLowerCase());
                                        if(entity instanceof LifeLinkedFishEntity || entity instanceof LifeLinkedCatEntity || entity instanceof LifeLinkedWolfEntity || entity instanceof LifeLinkedParrotEntity) {
                                            entity.setCustomName(Text.literal(String.format("%s§o[%s]§r §4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's %s", potionColor, potionName, username, WordUtils.capitalize(lowerNpcName2))));
                                        }else {
                                            entity.setCustomName(Text.literal(String.format("%s§o[%s]§r %s's %s", potionColor, potionName, username, WordUtils.capitalize(lowerNpcName2))));
                                        }
                                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload(potionColor + "ENTITY SPAWNED WITH " + potionName + "!");
                                        ServerPlayNetworking.send(serverPlayer, payload);
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, 0.5F);
                                        broadcast(String.format("§5§l[BTM]§r %s%s just spawned in a %s!", potionColor, event.getUser().getName(), lowerNpcName2), serverPlayer);
                                    }
                                }else {
                                    if(entity instanceof LifeLinkedFishEntity || entity instanceof LifeLinkedCatEntity || entity instanceof LifeLinkedWolfEntity || entity instanceof LifeLinkedParrotEntity) {
                                        entity.setCustomName(Text.literal(String.format("§4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's %s", username, WordUtils.capitalize(lowerNpcName2))));
                                    }else {
                                        entity.setCustomName(Text.literal(String.format("%s's %s", username, WordUtils.capitalize(lowerNpcName2))));
                                    }
                                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§f§lENTITY SPAWNED!");
                                    ServerPlayNetworking.send(serverPlayer, payload);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                                    broadcast(String.format("§5§l[BTM]§r %s just spawned in a %s!", event.getUser().getName(), lowerNpcName2), serverPlayer);
                                }
                            }
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!fling")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.FLING_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20*5, 155));

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§b§lFLUNG!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(BlazinBlazeTwitchModSounds.WEE_SOUND_EFFECT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r %s just flung you!", event.getUser().getName()), serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!nuke")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.NUKE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();
                            ServerWorld world = serverPlayer.getWorld();

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lNUKE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §4§l%s just nuked you!", event.getUser().getName()), serverPlayer);

                            for(int i = 0; i < 25; i++) {
                                if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                    EntityType.TNT.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED).setFuse(20);
                                }
                            }
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!meganuke")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MEGA_NUKE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();
                            ServerWorld world = serverPlayer.getWorld();

                            BlazinBlazeTwitchModEntities.NUCLEAR_TNT_ENTITY.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED).setFuse(80);

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lMEGA NUKE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.NEUTRAL, 1.0F, 0.5F);
                            broadcast(String.format("§5§l[BTM]§r §4§l%s just MEGA nuked you!", event.getUser().getName()), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!teleport") || message.toLowerCase().startsWith("!tp")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.TELEPORT_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            Random random = new Random();

                            double randomNumberX = random.nextDouble(10000000 - (-10000000) + 1) + (-10000000);
                            double randomNumberZ = random.nextDouble(10000000 - (-10000000) + 1) + (-10000000);
                            //ChunkLoader.create(world.getChunkManager().chunkLoadingManager, ChunkStatus.FULL, ChunkPos.fromRegion((int) randomNumberX, (int) randomNumberZ));
                            //BlazinBlazeTwitchMod.LOGGER.info(String.valueOf(randomNumberX));
                            //BlazinBlazeTwitchMod.LOGGER.info(String.valueOf(randomNumberZ));

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§d§lTELEPORTED!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                            broadcast(String.format("§5§l[BTM]§r §d§l%s just teleported you!", event.getUser().getName()), serverPlayer);
                            //BlazinBlazeEventHandler.addToList(player);
                            serverPlayer.requestTeleport(randomNumberX, 1000, randomNumberZ);
                            serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 10000000, 1, false, false));
                            TeleportTimer.INSTANCE.setLocation(serverPlayer.getWorld(), (int)randomNumberX, (int)randomNumberZ);
                            //double randomNumberY = world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) randomNumberX, (int) randomNumberZ);
                            //ServerPlayer.requestTeleport(randomNumberX, randomNumberY, randomNumberZ);
                            //BlazinBlazeTwitchMod.LOGGER.info(String.valueOf(randomNumberY));
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!lootcrate") || message.toLowerCase().startsWith("!loot")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.LOOT_CRATE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();
                            ServerWorld world = serverPlayer.getWorld();

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§9§lLOOT CRATE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.NEUTRAL, 0.5F, 1.0F);

                            broadcast(String.format("§5§l[BTM]§r §9§l%s just dropped you a loot crate!", event.getUser().getName()), serverPlayer);

                            BlockPos topY = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, spawnLocation);

                            DynamicRegistryManager registryManager = world.getRegistryManager();

                            if(spawnLocation.getY() < topY.getY()) {
                                BlockPos newLocation = new BlockPos(spawnLocation.getX(), (spawnLocation.getY() + 5), spawnLocation.getZ());
                                if(world.isAir(newLocation)) {
                                    BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(world, newLocation, SpawnReason.MOB_SUMMONED);
                                }else{
                                    if(Objects.equals(world.getDimensionEntry(), registryManager.getOrThrow(RegistryKeys.DIMENSION_TYPE).getEntry(DimensionTypes.THE_NETHER.getValue()).get())) {
                                        LootCrateAttachmentData data = serverPlayer.getAttachedOrCreate(BlazinBlazeAttachmentTypes.LOOT_CRATE_ATTACHMENT_TYPE, () -> LootCrateAttachmentData.DEFAULT);
                                        serverPlayer.setAttached(BlazinBlazeAttachmentTypes.LOOT_CRATE_ATTACHMENT_TYPE, data.addInt(1));

                                        broadcast("§5§l[BTM]§r §oYou will receive your loot crate when you leave the nether!", serverPlayer);
                                    }else {
                                        BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(world, new BlockPos((int) serverPlayer.getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) serverPlayer.getX(), (int) serverPlayer.getZ()), (int) serverPlayer.getZ()), SpawnReason.MOB_SUMMONED);
                                        broadcast("§5§l[BTM]§r §oSpawned loot crate at the surface of current position!", serverPlayer);
                                    }
                                }
                            }else {
                                BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(world, new BlockPos(spawnLocation.getX(), (spawnLocation.getY() + 100), spawnLocation.getZ()), SpawnReason.MOB_SUMMONED);
                            }
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
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
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                            if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                                BlockPos spawnLocation = serverPlayer.getBlockPos();
                                ServerWorld world = serverPlayer.getWorld();

                                if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                    if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.LIFE_LINKED_DEFAULT) * multiplier) {
                                        LifeLinkedWolfEntity wolf = BlazinBlazeTwitchModEntities.LIFE_LINKED_WOLF_ENTITY.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);

                                        if(wolf != null) {
                                            //BlazinBlazeTwitchMod.LOGGER.info(String.valueOf("Wolf is not null!"));
                                            Random random = new Random();

                                            wolf.setTamed(true, true);
                                            wolf.setOwner(serverPlayer);
                                            ((WolfEntityAccess)wolf).callSetCollarColor(DyeColor.RED);

                                            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.WOLF_ARMORED_DEFAULT) * multiplier) {
                                                wolf.equipBodyArmor(Items.WOLF_ARMOR.getDefaultStack());
                                                wolf.setCustomName(Text.literal(String.format("§6§l§o[ARMORED]§r §4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Wolf", username)));
                                            }else {
                                                wolf.setCustomName(Text.literal(String.format("§4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Wolf", username)));
                                            }

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§7§lWOLF SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSoundToPlayer(SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.Type.CLASSIC).ambientSound().value(), SoundCategory.NEUTRAL, 1.0F, 0.5F);

                                            broadcast(String.format("§5§l[BTM]§r §4§l%s just gave you a LIFE-LINKED§r§4❤§r§4§l Wolf!", event.getUser().getName()), serverPlayer);
                                        }
                                    }else {
                                        WolfEntity wolf = EntityType.WOLF.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);

                                        if(wolf != null) {
                                            //BlazinBlazeTwitchMod.LOGGER.info(String.valueOf("Wolf is not null!"));
                                            Random random = new Random();

                                            DyeColor rndmElem = dyeColors.get(random.nextInt(dyeColors.size()));
                                            RegistryKey<WolfVariant> rndmElem2 = wolfVariants.get(random.nextInt(wolfVariants.size()));

                                            wolf.setTamed(true, true);
                                            wolf.setOwner(serverPlayer);
                                            ((WolfEntityAccess)wolf).callSetCollarColor(rndmElem);

                                            DynamicRegistryManager registryManager = world.getRegistryManager();
                                            Optional<RegistryEntry.Reference<WolfVariant>> wolfVariant = registryManager.getOrThrow(RegistryKeys.WOLF_VARIANT).getEntry(rndmElem2.getValue());

                                            wolfVariant.ifPresentOrElse(value -> ((WolfEntityAccess)wolf).callSetVariant(wolfVariant.get()),
                                                    () -> BlazinBlazeTwitchMod.LOGGER.info("WolfVariant not found!"));

                                            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.WOLF_ARMORED_DEFAULT) * multiplier) {
                                                wolf.equipBodyArmor(Items.WOLF_ARMOR.getDefaultStack());
                                                wolf.setCustomName(Text.literal(String.format("§6§l§o[ARMORED]§r %s's Wolf", username)));
                                            }else {
                                                wolf.setCustomName(Text.literal(String.format("%s's Wolf", username)));
                                            }

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§7§lWOLF SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSoundToPlayer(SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.Type.CLASSIC).ambientSound().value(), SoundCategory.NEUTRAL, 1.0F, 1.0F);

                                            broadcast(String.format("§5§l[BTM]§r %s just gave you a Wolf!", event.getUser().getName()), serverPlayer);
                                        }
                                    }
                                }
                                shouldGiveImmunity(serverPlayer);
                            }else {
                                broadcastBlock(serverPlayer, event.getUser().getName());
                            }
                        }
                    }
                }
            }else if(lowerPet.toLowerCase().contains("cat")) {
                if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.CAT_DEFAULT) * multiplier && checkCooldown()) {
                    MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                    if(server != null) {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                            if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                                BlockPos spawnLocation = serverPlayer.getBlockPos();
                                ServerWorld world = serverPlayer.getWorld();

                                if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                    if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.LIFE_LINKED_DEFAULT) * multiplier) {
                                        LifeLinkedCatEntity cat = BlazinBlazeTwitchModEntities.LIFE_LINKED_CAT_ENTITY.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);

                                        if(cat != null) {

                                            cat.setTamed(true, true);
                                            cat.setOwner(serverPlayer);
                                            ((CatEntityAccess)cat).callSetCollarColor(DyeColor.RED);

                                            cat.setCustomName(Text.literal(String.format("§4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Cat", username)));

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§e§lCAT SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_CAT_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 0.5F);

                                            broadcast(String.format("§5§l[BTM]§r §4§l%s just gave you a LIFE-LINKED§r§4❤§r§4§l Cat!", event.getUser().getName()), serverPlayer);
                                        }
                                    }else {
                                        CatEntity cat = EntityType.CAT.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);

                                        if(cat != null) {
                                            Random random = new Random();

                                            DyeColor rndmElem = dyeColors.get(random.nextInt(dyeColors.size()));
                                            RegistryKey<CatVariant> rndmElem2 = catVariants.get(random.nextInt(catVariants.size()));

                                            cat.setTamed(true, true);
                                            cat.setOwner(serverPlayer);
                                            ((CatEntityAccess)cat).callSetCollarColor(rndmElem);

                                            DynamicRegistryManager registryManager = world.getRegistryManager();
                                            Optional<RegistryEntry.Reference<CatVariant>> catVariant = registryManager.getOrThrow(RegistryKeys.CAT_VARIANT).getEntry(rndmElem2.getValue());

                                            catVariant.ifPresentOrElse(value -> ((CatEntityAccess)cat).callSetVariant(catVariant.get()),
                                                    () -> BlazinBlazeTwitchMod.LOGGER.info("Cat Variant not found!"));

                                            cat.setCustomName(Text.literal(String.format("%s's Cat", username)));

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§e§lCAT SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_CAT_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                                            broadcast(String.format("§5§l[BTM]§r %s just gave you a Cat!", event.getUser().getName()), serverPlayer);
                                        }
                                    }
                                }
                                shouldGiveImmunity(serverPlayer);
                            }else {
                                broadcastBlock(serverPlayer, event.getUser().getName());
                            }
                        }
                    }
                }
            }else if(lowerPet.toLowerCase().contains("parrot")) {
                if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.PARROT_DEFAULT) * multiplier && checkCooldown()) {
                    MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                    if(server != null) {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                            if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                                BlockPos spawnLocation = serverPlayer.getBlockPos();
                                ServerWorld world = serverPlayer.getWorld();

                                if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                    if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.LIFE_LINKED_DEFAULT) * multiplier) {
                                        LifeLinkedParrotEntity parrot = BlazinBlazeTwitchModEntities.LIFE_LINKED_PARROT_ENTITY.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);

                                        if(parrot != null) {
                                            parrot.setTamed(true, true);
                                            parrot.setOwner(serverPlayer);

                                            parrot.setCustomName(Text.literal(String.format("§4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Parrot", username)));

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§1§lPARROT SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_PARROT_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 0.5F);

                                            broadcast(String.format("§5§l[BTM]§r §4§l%s just gave you a LIFE-LINKED§r§4❤§r§4§l Parrot!", event.getUser().getName()), serverPlayer);
                                        }
                                    }else {
                                        ParrotEntity parrot = EntityType.PARROT.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);

                                        if(parrot != null) {
                                            Random random = new Random();

                                            ParrotEntity.Variant rndmElem = parrotVariants.get(random.nextInt(parrotVariants.size()));

                                            parrot.setTamed(true, true);
                                            parrot.setOwner(serverPlayer);
                                            ((ParrotEntityAccess)parrot).callSetVariant(rndmElem);

                                            parrot.setCustomName(Text.literal(String.format("%s's Parrot", username)));

                                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§1§lPARROT SPAWNED!");
                                            ServerPlayNetworking.send(serverPlayer, payload);
                                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_PARROT_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                                            broadcast(String.format("§5§l[BTM]§r %s just gave you a Parrot!", event.getUser().getName()), serverPlayer);
                                        }
                                    }
                                }
                                shouldGiveImmunity(serverPlayer);
                            }else {
                                broadcastBlock(serverPlayer, event.getUser().getName());
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
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            Random random = new Random();
                            RegistryEntry<StatusEffect> rndmPotion = statusEffects2.get(random.nextInt(statusEffects2.size()));
                            String potionName = rndmPotion.value().getName().getString().toLowerCase();

                            serverPlayer.addStatusEffect(new StatusEffectInstance(rndmPotion, 1200, 2));

                            String potionColor2 = POTION_COLORS2.get(potionName.toLowerCase());

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload(potionColor2 + "POTION EFFECT!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r %s%s just gave you the %s effect!", potionColor2, event.getUser().getName(), potionName), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!cbomb")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.CHICKEN_BOMB_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();
                            ServerWorld world = serverPlayer.getWorld();
                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§f§lCHICKEN BOMB!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §f§l%s just Chicken Bombed you!", event.getUser().getName()), serverPlayer);

                            for(int i = 0; i < 500; i++) {
                                if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                    ChickenEntity c = EntityType.CHICKEN.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    c.setCustomName(Text.of(String.format("§4§l%s's CBOMB", username)));
                                }
                            }
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!message") || message.toLowerCase().startsWith("!msg")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.MESSAGE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            String messageText = message.substring("!message ".length());
                            String limitedText = messageText.substring(0, Math.min(messageText.length(), 500));

                            //Text[] messages = {Text.of(username), Text.of(limitedText), Text.of(""), Text.of("")};
                            //Text[] filteredMessages = {};

                            //SignText text = new SignText(messages, filteredMessages, DyeColor.BLACK, false);

                            //SignBlockEntity sign = BlockEntityType.SIGN.instantiate(spawnLocation, Blocks.OAK_SIGN.getDefaultState());
                            //sign.setWorld(world);
                            //sign.setText(text, true);
                            //broadcast(String.format("%s just sent Blaze a message!", event.getUser().getName()));

                            String chosenType;

                            while(true) {
                                Random random = new Random();
                                String rndmKey = mailKeys.get(random.nextInt(mailKeys.size()));
                                Double rndmRarity = MAIL_RARITIES.get(rndmKey); //ThreadLocalRandom.current().nextDouble() < mob.threshold()

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

                            if(chosenType != null) {
                                ItemStack stack = MAIL_TYPES.get(chosenType).copy();
                                stack.set(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, limitedText);
                                stack.set(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, event.getUser().getName());
                                stack.set(BlazinBlazeTwitchModComponents.MAIL_TYPE, chosenType);
                                stack.set(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, rndmFarewell);
                                serverPlayer.giveItemStack(stack);
                            }else {
                                ItemStack stack = BlazinBlazeTwitchModItems.MAIL_ITEM.asItem().getDefaultStack().copy();
                                stack.set(BlazinBlazeTwitchModComponents.MAIL_MESSAGE, limitedText);
                                stack.set(BlazinBlazeTwitchModComponents.MAIL_AUTHOR, event.getUser().getName());
                                stack.set(BlazinBlazeTwitchModComponents.MAIL_TYPE, "default");
                                stack.set(BlazinBlazeTwitchModComponents.MAIL_FAREWELL, rndmFarewell);
                                serverPlayer.giveItemStack(stack);
                            }

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§7§lYOU'VE GOT MAIL!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §7§l%s just sent you a message!", event.getUser().getName()), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!voicemessage") || message.toLowerCase().startsWith("!vm")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.VOICE_MESSAGE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            String messageText = message.toLowerCase().substring("!voicemessage ".length()); //said sage cuz of MesSAGE... bruh
                            String limitedText = messageText.substring(0, Math.min(messageText.length(), 250));

                            ItemStack stack = BlazinBlazeTwitchModItems.VOICE_MESSAGE_ITEM.getDefaultStack().copy();
                            stack.set(BlazinBlazeTwitchModComponents.VOICE_MESSAGE, limitedText);
                            stack.set(BlazinBlazeTwitchModComponents.VOICE_AUTHOR, event.getUser().getName());
                            serverPlayer.giveItemStack(stack);
                            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.AUTOMATIC_VOICE_MESSAGE_DEFAULT) * multiplier) {
                                VoiceMessageS2CPayload payload = new VoiceMessageS2CPayload(limitedText, event.getUser().getName());
                                ServerPlayNetworking.send(serverPlayer, payload);
                                serverPlayer.playSoundToPlayer(BlazinBlazeTwitchModSounds.VOICE_MESSAGE_EFFECT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                                serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_VILLAGER_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                                broadcast(String.format("§5§l[BTM]§r §9§l%s just sent you an AUTOMATIC voice message!", event.getUser().getName()), serverPlayer);
                                shouldGiveImmunity(serverPlayer);
                            }else {
                                OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§9§lYOU'VE GOT VOICEMAIL!");
                                ServerPlayNetworking.send(serverPlayer, payload);
                                serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_VILLAGER_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                                broadcast(String.format("§5§l[BTM]§r §9§l%s just sent you a voice message!", event.getUser().getName()), serverPlayer);
                                shouldGiveImmunity(serverPlayer);
                            }
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!ditem")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.DELETE_ITEM_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            ItemStack activeItem = serverPlayer.getStackInHand(serverPlayer.getActiveHand());
                            if(activeItem != null) {
                                activeItem.decrement(activeItem.getCount());
                            }

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§c§lITEM DELETE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r %s just deleted your item!", event.getUser().getName()), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!dinventory")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.DELETE_INVENTORY_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            PlayerInventory inventory = serverPlayer.getInventory();
                            inventory.clear();

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§c§lINVENTORY DELETE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §4§l%s just deleted your ENTIRE INVENTORY!", event.getUser().getName()), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!void")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.VOID_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos location = serverPlayer.getBlockPos();
                            serverPlayer.requestTeleport(location.getX(), -65, location.getZ());

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§8§lVOID...");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.AMBIENT_CAVE.value(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §8§l%s just threw you into the VOID!", event.getUser().getName()), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!beddy")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.BEDDY_DEFAULT) * multiplier && checkCooldown()) {

                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            BlockPos location = serverPlayer.getBlockPos();
                            ServerWorld world = serverPlayer.getWorld();
                            BeddyEntity beddy = BlazinBlazeTwitchModEntities.BEDDY_ENTITY.spawn(world, location, SpawnReason.MOB_SUMMONED);
                            beddy.attachLeash(serverPlayer, true);
                            beddy.setUser(event.getUser().getName());
                            beddy.setUUID(serverPlayer.getUuidAsString());
                            //beddy.setCustomName(Text.of(String.format("§8§l%s's Beddy", event.getUser().getName())));

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§8§lBEDDY...");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_STONE_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §8§l%s just attached you to Beddy!", event.getUser().getName()), serverPlayer);
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!sound")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.SOUND_DEFAULT) * multiplier && checkCooldown()) {

                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            Random random = new Random();
                            SoundEvent rndmSound = soundEffects.get(random.nextInt(soundEffects.size()));
                            serverPlayer.playSoundToPlayer(rndmSound, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                            SoundMessageTimer.INSTANCE.setTimerAndUser(100L, event.getUser().getName());
                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!fullscreen")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.FULLSCREEN_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {

                            FullscreenS2CPayload payload = new FullscreenS2CPayload("§6§lFULLSCREEN TOGGLED!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §8§l%s just toggled fullscreen!", event.getUser().getName()), serverPlayer);

                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!close")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.CLOSE_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {
                            CloseS2CPayload payload = new CloseS2CPayload(event.getUser().getName());
                            ServerPlayNetworking.send(serverPlayer, payload);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
                        }
                    }
                }
            }
        }
        if(message.toLowerCase().startsWith("!dtimer")) {
            if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.DECREASE_TIMER_DEFAULT) * multiplier && checkCooldown()) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        if(!serverPlayer.hasStatusEffect(BlazinBlazeTwitchMod.IMMUNITY_SHIELD_EFFECT)) {

                            MultiplierTick.MultiplierTimer.eventBoost(30);
                            serverPlayer.playSoundToPlayer(SoundEvents.ITEM_WOLF_ARMOR_CRACK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §c§l%s just decreased the multiplier timer!", event.getUser().getName()), serverPlayer);

                            shouldGiveImmunity(serverPlayer);
                        }else {
                            broadcastBlock(serverPlayer, event.getUser().getName());
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
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        ServerWorld serverWorld = serverPlayer.getWorld();
                        var entities = getCurrentChattersForId(serverWorld, serverPlayer, event.getUser().getId());

                        Identifier id = Identifier.ofVanilla(lowerItem);

                        if(id != null) {
                            Item chattedItem = Registries.ITEM.get(id);
                            Item defaultItem = Registries.ITEM.get(Registries.ITEM.getDefaultId());
                            if(entities.isEmpty() || chattedItem.equals(defaultItem)) {
                                //add response code perhaps
                            }else {
                                ItemStack itemStack = chattedItem.getDefaultStack();
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
                                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
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
                                        for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
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
                                        for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
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
            for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                ServerWorld world = serverPlayer.getWorld();
                BlockPos spawnLocation = serverPlayer.getBlockPos();

                var entities = getCurrentChattersForId(world, serverPlayer, event.getUser().getId());

                if (entities.isEmpty()) {
                    if (ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.CHATTER_SPAWN) * multiplier) {
                        if (chatterBucket.tryConsume(1L)) {
                            ChatterEntity chatter = BlazinBlazeTwitchModEntities.CHATTER_ENTITY.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                            chatter.setUserId(event.getUser().getId());
                            chatter.setTamed(true, true);
                            chatter.setOwner(serverPlayer);
                            chatter.setUsername(event.getUser().getName());

                            serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§5§l[BTM]§r §d%s just spawned in as a chatter!", event.getUser().getName()), serverPlayer);
                        }
                    }
                } else {
                    String limitedText = event.getMessage().substring(0, Math.min(event.getMessage().length(), 250));

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

    @EventSubscriber
    public void onSubMysteryGift(GiftSubscriptionsEvent event) {
        //broadcast(String.format("[Twitch] Thank you %s for gifting %d subs to %s", event.getUser().getName(), event.getCount(), event.getChannel().getName()));

        // Create a firework at spawn for a large gift sub event
        // Note: these EventSubscriber's operate on a separate thread so we need to use Bukkit's scheduler to run this on the main thread
    }

    private List<? extends ChatterEntity> getCurrentChattersForId(ServerWorld serverWorld, ServerPlayerEntity serverPlayer, String id) {
        return serverWorld.getEntitiesByType(TypeFilter.instanceOf(ChatterEntity.class), (entity) -> entity.getOwner() == serverPlayer && entity.getUserId().matches(id));
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

    private void shouldGiveImmunity(ServerPlayerEntity serverPlayer) {
        if(ThreadLocalRandom.current().nextDouble() < getProbabilityOrDefault(BlazinBlazeProbabilities.IMMUNITY_DEFAULT)) {
            serverPlayer.giveItemStack(BlazinBlazeTwitchModItems.IMMUNITY_SHIELD_ITEM.getDefaultStack().copy());
            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            broadcast("§5§l[BTM]§r §2§lYou just received an immunity shield!", serverPlayer);
        }
    }

    private void broadcastBlock(ServerPlayerEntity serverPlayer, String username) {
        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lENTITY SPAWN BLOCKED!");
        ServerPlayNetworking.send(serverPlayer, payload);
        serverPlayer.playSoundToPlayer(SoundEvents.ITEM_SHIELD_BLOCK.value(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
        broadcast(String.format("§5§l[BTM]§r §c%s's event just got blocked!", username), serverPlayer);
    }

    private void broadcast(String message, ServerPlayerEntity serverPlayer) {
        serverPlayer.sendMessage(Text.literal(message), false);
    }

    record SpawnableMob(EntityType type, double threshold) {}
}
