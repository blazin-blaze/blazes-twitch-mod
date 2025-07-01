package net.blazinblaze.blazetwitchmod;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.common.util.BucketUtils;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Stream;
import io.github.iprodigy.bttv.api.BetterTTV;
import io.github.iprodigy.bttv.api.domain.User;
import io.github.iprodigy.bttv.common.Emote;
import io.github.iprodigy.bttv.common.Provider;
import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.entity.custom.BeddyEntity;
import net.blazinblaze.blazetwitchmod.entity.custom.LifeLinkedCatEntity;
import net.blazinblaze.blazetwitchmod.entity.custom.LifeLinkedParrotEntity;
import net.blazinblaze.blazetwitchmod.entity.custom.LifeLinkedWolfEntity;
import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.blazinblaze.blazetwitchmod.mixin.CatEntityAccess;
import net.blazinblaze.blazetwitchmod.mixin.ParrotEntityAccess;
import net.blazinblaze.blazetwitchmod.mixin.WolfEntityAccess;
import net.blazinblaze.blazetwitchmod.networking.EmoteScreenS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.FollowNotifyS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.PlaySoundS2CPayload;
import net.blazinblaze.blazetwitchmod.sound.BlazinBlazeTwitchModSounds;
import net.blazinblaze.blazetwitchmod.util.SoundMessageTimer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.*;
import io.github.bucket4j.Bucket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.apache.commons.lang.WordUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import java.time.Duration;
import java.util.function.Consumer;

public class BlazinBlazeTwitchModEvent {

    //private final BlazinBlazeTwitchMod plugin;

    private static final Map<String, SpawnableMob> SPAWNABLE_MOBS = Map.ofEntries(
            Map.entry("zombie", new SpawnableMob(EntityType.ZOMBIE, 0.7)),
            Map.entry("blaze", new SpawnableMob(EntityType.BLAZE, 0.7)),
            Map.entry("dragon", new SpawnableMob(EntityType.ENDER_DRAGON, 0.00001)),
            Map.entry("wither", new SpawnableMob(EntityType.WITHER, 0.00001)),
            Map.entry("creeper", new SpawnableMob(EntityType.CREEPER, 0.6)),
            Map.entry("tnt", new SpawnableMob(EntityType.TNT, 0.7)),
            Map.entry("skeleton", new SpawnableMob(EntityType.SKELETON, 0.7)),
            Map.entry("warden", new SpawnableMob(EntityType.WARDEN, 0.00001)),
            Map.entry("brute", new SpawnableMob(EntityType.PIGLIN_BRUTE, 0.1)),
            Map.entry("guardian", new SpawnableMob(EntityType.GUARDIAN, 0.7)),
            Map.entry("ghast", new SpawnableMob(EntityType.GHAST, 0.5)),
            Map.entry("mega creeper", new SpawnableMob(BlazinBlazeTwitchModEntities.MEGA_CREEPER, 0.01)),
            Map.entry("lightning bolt", new SpawnableMob(EntityType.LIGHTNING_BOLT, 0.1)),
            Map.entry("spider", new SpawnableMob(EntityType.SPIDER, 0.7)),
            Map.entry("vex", new SpawnableMob(EntityType.VEX, 0.5)),
            Map.entry("evoker", new SpawnableMob(EntityType.LIGHTNING_BOLT, 0.5)),
            Map.entry("pillager", new SpawnableMob(EntityType.PILLAGER, 0.5)),
            Map.entry("breeze", new SpawnableMob(EntityType.BREEZE, 0.5)),
            Map.entry("hoglin", new SpawnableMob(EntityType.HOGLIN, 0.7)),
            Map.entry("pigln", new SpawnableMob(EntityType.PIGLIN, 0.7)),
            Map.entry("illusioner", new SpawnableMob(EntityType.ILLUSIONER, 0.4)),
            Map.entry("slime", new SpawnableMob(EntityType.SLIME, 0.7)),
            Map.entry("phantom", new SpawnableMob(EntityType.PHANTOM, 0.5)),
            Map.entry("ravager", new SpawnableMob(EntityType.RAVAGER, 0.4)),
            Map.entry("shulker", new SpawnableMob(EntityType.SHULKER, 0.6)),
            Map.entry("silverfish", new SpawnableMob(EntityType.SILVERFISH, 0.7)),
            Map.entry("vindicator", new SpawnableMob(EntityType.VINDICATOR, 0.5)),
            Map.entry("villager", new SpawnableMob(EntityType.VILLAGER, 0.7)));
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
    private static final Map<String, Double> MAIL_RARITIES = Map.ofEntries(
            Map.entry("default", .8D),
            Map.entry("starry", .5D),
            Map.entry("blazin", .3D),
            Map.entry("epic", .3D),
            Map.entry("minecraft", .5D),
            Map.entry("error", .1D),
            Map.entry("sinister", .5D));
    private static final Map<String, ItemStack> MAIL_TYPES = Map.ofEntries(
            Map.entry("default", BlazinBlazeTwitchModItems.MAIL_ITEM.asItem().getDefaultStack()),
            Map.entry("starry", BlazinBlazeTwitchModItems.MAIL_ITEM_STARRY.asItem().getDefaultStack()),
            Map.entry("blazin", BlazinBlazeTwitchModItems.MAIL_ITEM_BLAZIN.asItem().getDefaultStack()),
            Map.entry("epic", BlazinBlazeTwitchModItems.MAIL_ITEM_EPIC.asItem().getDefaultStack()),
            Map.entry("minecraft", BlazinBlazeTwitchModItems.MAIL_ITEM_MINECRAFT.asItem().getDefaultStack()),
            Map.entry("error", BlazinBlazeTwitchModItems.MAIL_ITEM_ERROR.asItem().getDefaultStack()),
            Map.entry("sinister", BlazinBlazeTwitchModItems.MAIL_ITEM_SINISTER.asItem().getDefaultStack()));
    private static final UUID TARGET_PLAYER = UUID.fromString("0906348a-eedf-4e56-8978-1b0e894dda1a");
    private final Bucket mobSpawnBucket = BucketUtils.createBucket(BucketUtils.simple(5, Duration.ofSeconds(5)));
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

    public BlazinBlazeTwitchModEvent() {
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
        if(message.startsWith("!spawn ")) {
            String lowerNpcName = message.substring("!spawn ".length()).toLowerCase();
            SpawnableMob mob = SPAWNABLE_MOBS.get(lowerNpcName);

            //Old Version
            //String[] fullMessage = message.split("\\s+");
            //String command = fullMessage[0];
            // lowerNpcName = fullMessage[1];

            if (mob != null && ThreadLocalRandom.current().nextDouble() < mob.threshold() && mobSpawnBucket.tryConsume(1L)) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    //ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(TARGET_PLAYER);

                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        BlockPos spawnLocation = serverPlayer.getBlockPos();
                        ServerWorld world = serverPlayer.getWorld();

                        if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                            Entity entity;
                            String lowerNpcName2 = message.substring("!spawn ".length()).toLowerCase();
                            if(lowerNpcName2.equals("creeper")) {
                                if (ThreadLocalRandom.current().nextDouble() < .05) {
                                    entity = BlazinBlazeTwitchModEntities.MEGA_CREEPER.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                    lowerNpcName2 = "mega creeper";
                                    broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                } else {
                                    entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                }
                            }else if(lowerNpcName2.equals("skeleton")) {
                                if (ThreadLocalRandom.current().nextDouble() < .2) {
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
                                if (ThreadLocalRandom.current().nextDouble() < .05) {
                                    entity = EntityType.GIANT.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                    lowerNpcName2 = "giant";
                                    broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                }else if (ThreadLocalRandom.current().nextDouble() < .2) {
                                    entity = EntityType.HUSK.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                    lowerNpcName2 = "husk";
                                    broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                } else {
                                    entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                }
                            }else if(lowerNpcName2.equals("spider")) {
                                if (ThreadLocalRandom.current().nextDouble() < .2) {
                                    entity = EntityType.CAVE_SPIDER.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                    lowerNpcName2 = "cave spider";
                                    broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                } else {
                                    entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                }
                            }else if(lowerNpcName2.equals("villager")) {
                                if (ThreadLocalRandom.current().nextDouble() < .2) {
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
                                if (ThreadLocalRandom.current().nextDouble() < .2) {
                                    entity = EntityType.MAGMA_CUBE.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                    lowerNpcName2 = "magma cube";
                                    broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                } else {
                                    entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                }
                            }else if(lowerNpcName2.equals("ghast")) {
                                if (ThreadLocalRandom.current().nextDouble() < .2) {
                                    entity = EntityType.HAPPY_GHAST.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                    lowerNpcName2 = "happy ghast";
                                    broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                } else {
                                    entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                }
                            }else if(lowerNpcName2.equals("silverfish")) {
                                if (ThreadLocalRandom.current().nextDouble() < .2) {
                                    entity = EntityType.ENDERMITE.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                    lowerNpcName2 = "endermite";
                                    broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                } else {
                                    entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                }
                            }else if(lowerNpcName2.equals("guardian")) {
                                if (ThreadLocalRandom.current().nextDouble() < .2) {
                                    entity = EntityType.ELDER_GUARDIAN.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.6F, 1.0F);
                                    lowerNpcName2 = "elder guardian";
                                    broadcast("§5§l[BTM]§r §d§lEntity mutated!", serverPlayer);
                                } else {
                                    entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                }
                            }else {
                                entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                            }
                            if(ThreadLocalRandom.current().nextDouble() < .3) {
                                if(!lowerNpcName2.equals("lightning bolt") && !lowerNpcName2.equals("tnt")) {
                                    Random random = new Random();
                                    RegistryEntry<StatusEffect> rndmPotion = statusEffects.get(random.nextInt(statusEffects.size()));
                                    String potionName = rndmPotion.value().getName().getString().toUpperCase();
                                    ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(rndmPotion, 50000, 2, true, true, true));
                                    String potionColor = POTION_COLORS.get(potionName.toLowerCase());
                                    entity.setCustomName(Text.literal(String.format("%s§o[%s]§r %s's %s", potionColor, potionName, username, WordUtils.capitalize(lowerNpcName2))));
                                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload(potionColor + "ENTITY SPAWNED WITH " + potionName + "!");
                                    ServerPlayNetworking.send(serverPlayer, payload);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, 0.5F);
                                    broadcast(String.format("§5§l[BTM]§r %s%s just spawned in a %s!", potionColor, event.getUser().getName(), lowerNpcName2), serverPlayer);
                                }
                            }else {
                                entity.setCustomName(Text.literal(String.format("%s's %s", username, WordUtils.capitalize(lowerNpcName2))));
                                OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§f§lENTITY SPAWNED!");
                                ServerPlayNetworking.send(serverPlayer, payload);
                                serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                                broadcast(String.format("§5§l[BTM]§r %s just spawned in a %s!", event.getUser().getName(), lowerNpcName2), serverPlayer);
                            }
                        }
                    }
                }
            }
        }
        if(message.startsWith("!fling")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {

                        serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20*5, 155));
                        //((PlayerEntity) serverPlayer).jump();

                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§b§lFLUNG!");
                        ServerPlayNetworking.send(serverPlayer, payload);
                        serverPlayer.playSoundToPlayer(BlazinBlazeTwitchModSounds.WEE_SOUND_EFFECT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        broadcast(String.format("§5§l[BTM]§r %s just flung you!", event.getUser().getName()), serverPlayer);
                    }
                }
            }
        }
        if(message.startsWith("!nuke")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        BlockPos spawnLocation = serverPlayer.getBlockPos();
                        ServerWorld world = serverPlayer.getWorld();

                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lNUKE!");
                        ServerPlayNetworking.send(serverPlayer, payload);
                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        broadcast(String.format("§5§l[BTM]§r §4§l%s just nuked you!", event.getUser().getName()), serverPlayer);

                        for(int i = 0; i < 25; i++) {
                            if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                EntityType.TNT.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED).setFuse(5);
                            }
                        }
                    }
                }
            }
        }
        if(message.startsWith("!megaNuke")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        BlockPos spawnLocation = serverPlayer.getBlockPos();
                        ServerWorld world = serverPlayer.getWorld();

                        BlazinBlazeTwitchModEntities.NUCLEAR_TNT_ENTITY.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED).setFuse(160);

                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lMEGA NUKE!");
                        ServerPlayNetworking.send(serverPlayer, payload);
                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.NEUTRAL, 1.0F, 0.5F);
                        broadcast(String.format("§5§l[BTM]§r §4§l%s just MEGA nuked you!", event.getUser().getName()), serverPlayer);
                    }
                }
            }
        }
        if(message.startsWith("!teleport")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
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
                        var instance = new StatusEffectInstance(BlazinBlazeTwitchMod.RANDOM_TELEPORT, 8*20, 0, false, true, false);
                        serverPlayer.addStatusEffect(instance);
                        //double randomNumberY = world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) randomNumberX, (int) randomNumberZ);
                        //ServerPlayer.requestTeleport(randomNumberX, randomNumberY, randomNumberZ);
                        //BlazinBlazeTwitchMod.LOGGER.info(String.valueOf(randomNumberY));
                    }
                }
            }
        }
        if(message.startsWith("!lootCrate")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
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
                                    //BlazinBlazeLootPlayerData playerState = BlazinBlazeStateSaverAndLoader.getPlayerState(serverPlayer);
                                    //playerState.netherLootCrates += 1;

                                    //PacketByteBuf data = PacketByteBufs.create();
                                    //data.writeInt(playerState.netherLootCrates);
                                    broadcast("§5§l[BTM]§r §oYou will receive your loot crate when you leave the nether!", serverPlayer);
                                }else {
                                    BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(world, new BlockPos((int) serverPlayer.getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) serverPlayer.getX(), (int) serverPlayer.getZ()), (int) serverPlayer.getZ()), SpawnReason.MOB_SUMMONED);
                                    broadcast("§5§l[BTM]§r §oSpawned loot crate at the surface of current position!", serverPlayer);
                                }
                            }
                        }else {
                            BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(world, new BlockPos(spawnLocation.getX(), (spawnLocation.getY() + 100), spawnLocation.getZ()), SpawnReason.MOB_SUMMONED);
                        }
                    }
                }

            }
        }
        if(message.startsWith("!wolf")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        BlockPos spawnLocation = serverPlayer.getBlockPos();
                        ServerWorld world = serverPlayer.getWorld();

                        if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                            if(ThreadLocalRandom.current().nextDouble() < 0.03) { //0.03
                                LifeLinkedWolfEntity wolf = BlazinBlazeTwitchModEntities.LIFE_LINKED_WOLF_ENTITY.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);

                                if(wolf != null) {
                                    //BlazinBlazeTwitchMod.LOGGER.info(String.valueOf("Wolf is not null!"));
                                    Random random = new Random();

                                    wolf.setTamed(true, true);
                                    wolf.setOwner(serverPlayer);
                                    ((WolfEntityAccess)wolf).callSetCollarColor(DyeColor.RED);

                                    if(ThreadLocalRandom.current().nextDouble() < .3) {
                                        wolf.equipBodyArmor(Items.WOLF_ARMOR.getDefaultStack());
                                        wolf.setCustomName(Text.literal(String.format("§6§l§o[ARMORED]§r §4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Wolf", username)));
                                    }else {
                                        wolf.setCustomName(Text.literal(String.format("§4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Wolf", username)));
                                    }

                                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§7§lWOLF SPAWNED!");
                                    ServerPlayNetworking.send(serverPlayer, payload);
                                    serverPlayer.playSoundToPlayer(SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.Type.CLASSIC).ambientSound().value(), SoundCategory.NEUTRAL, 1.0F, 0.5F);

                                    broadcast(String.format("§5§l[BTM]§r §4§l%s just gave a LIFE-LINKED§r§4❤§r§4§l Wolf to you!", event.getUser().getName()), serverPlayer);
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

                                    if(ThreadLocalRandom.current().nextDouble() < .3) {
                                        wolf.equipBodyArmor(Items.WOLF_ARMOR.getDefaultStack());
                                        wolf.setCustomName(Text.literal(String.format("§6§l§o[ARMORED]§r %s's Wolf", username)));
                                    }else {
                                        wolf.setCustomName(Text.literal(String.format("%s's Wolf", username)));
                                    }

                                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§7§lWOLF SPAWNED!");
                                    ServerPlayNetworking.send(serverPlayer, payload);
                                    serverPlayer.playSoundToPlayer(SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.Type.CLASSIC).ambientSound().value(), SoundCategory.NEUTRAL, 1.0F, 1.0F);

                                    broadcast(String.format("§5§l[BTM]§r %s just gave a Wolf to you!", event.getUser().getName()), serverPlayer);
                                }
                            }
                        }
                    }
                }
            }
        }
        if(message.startsWith("!cat")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        BlockPos spawnLocation = serverPlayer.getBlockPos();
                        ServerWorld world = serverPlayer.getWorld();

                        if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                            if(ThreadLocalRandom.current().nextDouble() < 0.03) { //0.03
                                LifeLinkedCatEntity cat = BlazinBlazeTwitchModEntities.LIFE_LINKED_CAT_ENTITY.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);

                                if(cat != null) {
                                    Random random = new Random();

                                    cat.setTamed(true, true);
                                    cat.setOwner(serverPlayer);
                                    ((CatEntityAccess)cat).callSetCollarColor(DyeColor.RED);

                                    cat.setCustomName(Text.literal(String.format("§4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Cat", username)));

                                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§e§lCAT SPAWNED!");
                                    ServerPlayNetworking.send(serverPlayer, payload);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_CAT_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 0.5F);

                                    broadcast(String.format("§5§l[BTM]§r §4§l%s just gave a LIFE-LINKED§r§4❤§r§4§l Cat to you!", event.getUser().getName()), serverPlayer);
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

                                    broadcast(String.format("§5§l[BTM]§r %s just gave a Cat to you!", event.getUser().getName()), serverPlayer);
                                }
                            }
                        }
                    }
                }
            }
        }
        if(message.startsWith("!parrot")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        BlockPos spawnLocation = serverPlayer.getBlockPos();
                        ServerWorld world = serverPlayer.getWorld();

                        if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                            if(ThreadLocalRandom.current().nextDouble() < 0.03) {
                                LifeLinkedParrotEntity parrot = BlazinBlazeTwitchModEntities.LIFE_LINKED_PARROT_ENTITY.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);

                                if(parrot != null) {
                                    parrot.setTamed(true, true);
                                    parrot.setOwner(serverPlayer);

                                    parrot.setCustomName(Text.literal(String.format("§4§l§o[LIFE-LINKED§r§4❤§r§4§l]§r %s's Parrot", username)));

                                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§1§lPARROT SPAWNED!");
                                    ServerPlayNetworking.send(serverPlayer, payload);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_PARROT_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 0.5F);

                                    broadcast(String.format("§5§l[BTM]§r §4§l%s just gave a LIFE-LINKED§r§4❤§r§4§l Parrot to you!", event.getUser().getName()), serverPlayer);
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

                                    broadcast(String.format("§5§l[BTM]§r %s just gave a Parrot to you!", event.getUser().getName()), serverPlayer);
                                }
                            }
                        }
                    }
                }
            }
        }
        if(message.startsWith("!potion")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        Random random = new Random();
                        RegistryEntry<StatusEffect> rndmPotion = statusEffects2.get(random.nextInt(statusEffects2.size()));
                        String potionName = rndmPotion.value().getName().getString().toLowerCase();

                        serverPlayer.addStatusEffect(new StatusEffectInstance(rndmPotion, 1200, 2));

                        String potionColor2 = POTION_COLORS2.get(potionName.toLowerCase());

                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload(potionColor2 + "POTION EFFECT!");
                        ServerPlayNetworking.send(serverPlayer, payload);
                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        broadcast(String.format("§5§l[BTM]§r %s%s just gave the %s effect to you!", potionColor2, event.getUser().getName(), potionName), serverPlayer);
                    }
                }
            }
        }
        if(message.startsWith("!cBomb")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        BlockPos spawnLocation = serverPlayer.getBlockPos();
                        ServerWorld world = serverPlayer.getWorld();
                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§f§lCHICKEN BOMB!");
                        ServerPlayNetworking.send(serverPlayer, payload);
                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        broadcast(String.format("§5§l[BTM]§r §f§l%s just Chicken Bombed you!", event.getUser().getName()), serverPlayer);

                        for(int i = 0; i < 1000; i++) {
                            if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                ChickenEntity c = EntityType.CHICKEN.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                c.setCustomName(Text.of(String.format("§4§l%s's CBOMB", username)));
                            }
                        }
                    }
                }
            }
        }
        if(message.startsWith("!message")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
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
                    }
                }
            }
        }
        if(message.startsWith("!voiceMessage")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        String messageText = message.substring("!voiceMessage ".length()); //said sage cuz of MesSAGE... bruh
                        String limitedText = messageText.substring(0, Math.min(messageText.length(), 250));

                        ItemStack stack = BlazinBlazeTwitchModItems.VOICE_MESSAGE_ITEM.getDefaultStack().copy();
                        stack.set(BlazinBlazeTwitchModComponents.VOICE_MESSAGE, limitedText);
                        stack.set(BlazinBlazeTwitchModComponents.VOICE_AUTHOR, event.getUser().getName());
                        serverPlayer.giveItemStack(stack);

                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§9§lYOU'VE GOT VOICEMAIL!");
                        ServerPlayNetworking.send(serverPlayer, payload);
                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_VILLAGER_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        broadcast(String.format("§5§l[BTM]§r §9§l%s just sent you a voice message!", event.getUser().getName()), serverPlayer);
                    }
                }
            }
        }
        if(message.startsWith("!dItem")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        ItemStack activeItem = serverPlayer.getStackInHand(serverPlayer.getActiveHand());
                        if(activeItem != null) {
                            activeItem.decrement(activeItem.getCount());
                        }

                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§c§lITEM DELETE!");
                        ServerPlayNetworking.send(serverPlayer, payload);
                        serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        broadcast(String.format("§5§l[BTM]§r %s just deleted your item!", event.getUser().getName()), serverPlayer);
                    }
                }
            }
        }
        if(message.startsWith("!dInventory")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        PlayerInventory inventory = serverPlayer.getInventory();
                        inventory.clear();

                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§c§lINVENTORY DELETE!");
                        ServerPlayNetworking.send(serverPlayer, payload);
                        serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        broadcast(String.format("§5§l[BTM]§r §4§l%s just deleted your ENTIRE INVENTORY!", event.getUser().getName()), serverPlayer);
                    }
                }
            }
        }
        if(message.startsWith("!void")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        BlockPos location = serverPlayer.getBlockPos();
                        serverPlayer.requestTeleport(location.getX(), -65, location.getZ());

                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§8§lVOID...");
                        ServerPlayNetworking.send(serverPlayer, payload);
                        serverPlayer.playSoundToPlayer(SoundEvents.AMBIENT_CAVE.value(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        broadcast(String.format("§5§l[BTM]§r §8§l%s just threw you into the VOID!", event.getUser().getName()), serverPlayer);
                    }
                }
            }
        }
        if(message.startsWith("!beddy")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {

                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        BlockPos location = serverPlayer.getBlockPos();
                        ServerWorld world = serverPlayer.getWorld();
                        BeddyEntity beddy = BlazinBlazeTwitchModEntities.BEDDY_ENTITY.spawn(world, location, SpawnReason.MOB_SUMMONED);
                        beddy.attachLeash(serverPlayer, true);
                        beddy.setUser(event.getUser().getName());
                        //beddy.setCustomName(Text.of(String.format("§8§l%s's Beddy", event.getUser().getName())));

                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§8§lBEDDY...");
                        ServerPlayNetworking.send(serverPlayer, payload);
                        serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_STONE_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        broadcast(String.format("§5§l[BTM]§r §8§l%s just attached you to Beddy!", event.getUser().getName()), serverPlayer);
                    }
                }
            }
        }
        if(message.startsWith("!sound")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {

                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                        BlockPos location = serverPlayer.getBlockPos();
                        Random random = new Random();
                        SoundEvent rndmSound = soundEffects.get(random.nextInt(soundEffects.size()));
                        serverPlayer.playSoundToPlayer(rndmSound, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                        SoundMessageTimer.INSTANCE.setTimerAndUser(60L, event.getUser().getName());
                    }
                }
            }
        }

        final BetterTTV bttvAPI = BlazinBlazeTwitchMod.bAPI;
        final Collection<Emote> globalEmotes = BlazinBlazeTwitchMod.globalEmotes;
        final Map<User, List<Emote>> channelEmotes = BlazinBlazeTwitchMod.channelEmotes;
        final Map<User, List<Emote>> sharedEmotes = BlazinBlazeTwitchMod.sharedEmotes;

        if(globalEmotes != null) {
            for (Emote emote : globalEmotes) {
                String[] messageWords = message.split(" ");
                for(String word : messageWords) {
                    if(word.matches(emote.code())) {
                        MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                        if(server != null) {
                            for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                                EmoteScreenS2CPayload payload = new EmoteScreenS2CPayload(emote.id(), emote.imageType());
                                ServerPlayNetworking.send(serverPlayer, payload);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if(channelEmotes != null && !channelEmotes.isEmpty()) {
            List<Emote> emotes = channelEmotes.get(bttvAPI.getUser(Provider.TWITCH, event.getChannel().getId()));
            if(emotes != null) {
                for (Emote emote : emotes) {
                    String[] messageWords = message.split(" ");
                    for(String word : messageWords) {
                        if(word.matches(emote.code())) {
                            MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                            if(server != null) {
                                for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                                    EmoteScreenS2CPayload payload = new EmoteScreenS2CPayload(emote.id(), emote.imageType());
                                    ServerPlayNetworking.send(serverPlayer, payload);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if(sharedEmotes != null && !sharedEmotes.isEmpty()) {
            List<Emote> emotes = sharedEmotes.get(bttvAPI.getUser(Provider.TWITCH, event.getChannel().getId()));
            if(emotes != null) {
                for (Emote emote : emotes) {
                    String[] messageWords = message.split(" ");
                    for(String word : messageWords) {
                        if(word.matches(emote.code())) {
                            MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                            if(server != null) {
                                for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                                    EmoteScreenS2CPayload payload = new EmoteScreenS2CPayload(emote.id(), emote.imageType());
                                    ServerPlayNetworking.send(serverPlayer, payload);
                                    break;
                                }
                            }
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

    private void broadcast(String message, ServerPlayerEntity serverPlayer) {
        serverPlayer.sendMessage(Text.literal(message), false);
    }

    record SpawnableMob(EntityType type, double threshold) {}
}
