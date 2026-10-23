package net.blazinblaze.blazetwitchmod;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.common.util.BucketUtils;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Stream;
import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.entity.custom.BeddyEntity;
import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.blazinblaze.blazetwitchmod.mixin.CatEntityAccess;
import net.blazinblaze.blazetwitchmod.mixin.WolfEntityAccess;
import net.blazinblaze.blazetwitchmod.networking.FollowNotifyS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.PlaySoundS2CPayload;
import net.blazinblaze.blazetwitchmod.sound.BlazinBlazeTwitchModSounds;
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
            Map.entry("lightning bolt", new SpawnableMob(EntityType.LIGHTNING_BOLT, 0.1)));
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

        catVariants.add(CatVariant.JELLIE);
        catVariants.add(CatVariant.RED);
        catVariants.add(CatVariant.WHITE);
        catVariants.add(CatVariant.BLACK);
        catVariants.add(CatVariant.SIAMESE);
        catVariants.add(CatVariant.ALL_BLACK);
        catVariants.add(CatVariant.BRITISH_SHORTHAIR);
        catVariants.add(CatVariant.CALICO);
        catVariants.add(CatVariant.PERSIAN);
        catVariants.add(CatVariant.RAGDOLL);
        catVariants.add(CatVariant.TABBY);

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

        if(BlazinBlazeTwitchMod.serverRef != null) {
            FollowNotifyS2CPayload payload = new FollowNotifyS2CPayload(event.getUser().getName());
            PlaySoundS2CPayload payload2 = new PlaySoundS2CPayload(BlazinBlazeTwitchModSounds.TADA_SOUND_EFFECT, 0.5F, 1.0F);

            Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

            worlds.forEach((world) -> {
                for (ServerPlayerEntity player : PlayerLookup.world((ServerWorld) world)) {
                    ServerPlayNetworking.send(player, payload);
                    ServerPlayNetworking.send(player, payload2);
                    broadcast(String.format("§5§l[BLAZIN] %s just followed %s!", event.getUser().getName(), event.getChannel().getName()), player);
                }
            });
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
        if(message.startsWith("!enemy")) {
            String lowerNpcName = message.substring("!enemy ".length()).toLowerCase();
            SpawnableMob mob = SPAWNABLE_MOBS.get(lowerNpcName);

            //Old Version
            //String[] fullMessage = message.split("\\s+");
            //String command = fullMessage[0];
            // lowerNpcName = fullMessage[1];

            if (mob != null && ThreadLocalRandom.current().nextDouble() < mob.threshold() && mobSpawnBucket.tryConsume(1L)) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    //ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(TARGET_PLAYER);

                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();

                            if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                Entity entity = mob.type().spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                if(ThreadLocalRandom.current().nextDouble() < .3) {
                                    if(!lowerNpcName.equals("lightning bolt") && !lowerNpcName.equals("tnt")) {
                                        Random random = new Random();
                                        RegistryEntry<StatusEffect> rndmPotion = statusEffects.get(random.nextInt(statusEffects.size()));
                                        String potionName = rndmPotion.value().getName().getString().toUpperCase();
                                        ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(rndmPotion, 50000, 2, true, true));
                                        String potionColor = POTION_COLORS.get(potionName.toLowerCase());
                                        entity.setCustomName(Text.literal(potionColor + "§o[" + potionName + "]§r " + username));
                                        OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload(potionColor + "ENTITY SPAWNED WITH " + potionName + "!");
                                        ServerPlayNetworking.send(serverPlayer, payload);
                                        serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, 0.5F);
                                        broadcast(String.format("%s%s just spawned in a %s!", potionColor, event.getUser().getName(), lowerNpcName), serverPlayer);
                                    }
                                }else {
                                    entity.setCustomName(Text.literal(username));
                                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§f§lENTITY SPAWNED!");
                                    ServerPlayNetworking.send(serverPlayer, payload);
                                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                                    broadcast(String.format("%s just spawned in a %s!", event.getUser().getName(), lowerNpcName), serverPlayer);
                                }
                            }
                        }
                    });
                }
            }
        }
        if(message.startsWith("!fling")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {

                            serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20*5, 155));
                            //((PlayerEntity) serverPlayer).jump();

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§b§lFLUNG!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(BlazinBlazeTwitchModSounds.WEE_SOUND_EFFECT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("%s just flung Blaze!", event.getUser().getName()), serverPlayer);
                        }
                    });
                }
            }
        }
        if(message.startsWith("!nuke")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lNUKE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§4§l%s just nuked Blaze!", event.getUser().getName()), serverPlayer);

                            for(int i = 0; i < 25; i++) {
                                if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                    EntityType.TNT.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED).setFuse(5);
                                }
                            }
                        }
                    });
                }
            }
        }
        if(message.startsWith("!megaNuke")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();

                            BlazinBlazeTwitchModEntities.NUCLEAR_TNT_ENTITY.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED).setFuse(160);

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lMEGA NUKE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.NEUTRAL, 1.0F, 0.5F);
                            broadcast(String.format("§4§l%s just MEGA nuked Blaze!", event.getUser().getName()), serverPlayer);
                        }
                    });
                }
            }
        }
        if(message.startsWith("!teleport")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;



                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            Random random = new Random();

                            double randomNumberX = random.nextDouble(10000000 - (-10000000) + 1) + (-10000000);
                            double randomNumberZ = random.nextDouble(10000000 - (-10000000) + 1) + (-10000000);
                            //ChunkLoader.create(world.getChunkManager().chunkLoadingManager, ChunkStatus.FULL, ChunkPos.fromRegion((int) randomNumberX, (int) randomNumberZ));
                            //BlazinBlazeTwitchMod.LOGGER.info(String.valueOf(randomNumberX));
                            //BlazinBlazeTwitchMod.LOGGER.info(String.valueOf(randomNumberZ));

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§d§lTELEPORTED!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                            broadcast(String.format("§d§l%s just teleported Blaze!", event.getUser().getName()), serverPlayer);
                            //BlazinBlazeEventHandler.addToList(player);
                            serverPlayer.requestTeleport(randomNumberX, 1000, randomNumberZ);
                            var instance = new StatusEffectInstance(BlazinBlazeTwitchMod.RANDOM_TELEPORT, 8*20, 0, false, true, false);
                            serverPlayer.addStatusEffect(instance);
                            //double randomNumberY = world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) randomNumberX, (int) randomNumberZ);
                            //ServerPlayer.requestTeleport(randomNumberX, randomNumberY, randomNumberZ);
                            //BlazinBlazeTwitchMod.LOGGER.info(String.valueOf(randomNumberY));
                        }
                    });
                }
            }
        }
        if(message.startsWith("!lootCrate")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§9§lLOOT CRATE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.NEUTRAL, 0.5F, 1.0F);

                            broadcast(String.format("§9§l%s just dropped Blaze a loot crate!", event.getUser().getName()), serverPlayer);

                            BlockPos topY = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, spawnLocation);

                            DynamicRegistryManager registryManager = world.getRegistryManager();

                            if(spawnLocation.getY() < topY.getY()) {
                                BlockPos newLocation = new BlockPos(spawnLocation.getX(), (spawnLocation.getY() + 5), spawnLocation.getZ());
                                if(world.isAir(newLocation)) {
                                    BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(world, newLocation, SpawnReason.MOB_SUMMONED);
                                }else{
                                    if(Objects.equals(world.getDimensionEntry(), registryManager.getOrThrow(RegistryKeys.DIMENSION_TYPE).getEntry(DimensionTypes.THE_NETHER.getValue()).get())) {
                                        BlazinBlazeLootPlayerData playerState = BlazinBlazeStateSaverAndLoader.getPlayerState(serverPlayer);
                                        playerState.netherLootCrates += 1;

                                        PacketByteBuf data = PacketByteBufs.create();
                                        data.writeInt(playerState.netherLootCrates);
                                        broadcast("§oYou will receive your loot crate when you leave the nether!", serverPlayer);
                                    }else {
                                        BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(world, new BlockPos((int) serverPlayer.getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) serverPlayer.getX(), (int) serverPlayer.getZ()), (int) serverPlayer.getZ()), SpawnReason.MOB_SUMMONED);
                                        broadcast("§oSpawned loot crate at the surface of current position!", serverPlayer);
                                    }
                                }
                            }else {
                                BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(world, new BlockPos(spawnLocation.getX(), (spawnLocation.getY() + 100), spawnLocation.getZ()), SpawnReason.MOB_SUMMONED);
                            }
                        }
                    });
                }

            }
        }
        if(message.startsWith("!wolf")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();

                            if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
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

                                    wolfVariant.ifPresentOrElse(value -> wolf.setVariant(wolfVariant.get()),
                                            () -> BlazinBlazeTwitchMod.LOGGER.info("WolfVariant not found!"));

                                    if(ThreadLocalRandom.current().nextDouble() < .3) {
                                        wolf.equipBodyArmor(Items.WOLF_ARMOR.getDefaultStack());
                                        wolf.setCustomName(Text.literal("§6§l§o[ARMORED]§r " + username));
                                    }else {
                                        wolf.setCustomName(Text.literal(username));
                                    }
                                }
                            }

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§7§lWOLF SPAWNED!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_WOLF_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                            broadcast(String.format("%s just gave a Wolf to Blaze!", event.getUser().getName()), serverPlayer);
                        }
                    });
                }
            }
        }
        if(message.startsWith("!cat")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();

                            if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
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

                                    catVariant.ifPresentOrElse(value -> cat.setVariant(catVariant.get()),
                                            () -> BlazinBlazeTwitchMod.LOGGER.info("Cat Variant not found!"));

                                    cat.setCustomName(Text.literal(username));
                                }
                            }

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§e§lCAT SPAWNED!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_CAT_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                            broadcast(String.format("%s just gave a Cat to Blaze!", event.getUser().getName()), serverPlayer);
                        }
                    });
                }
            }
        }
        if(message.startsWith("!parrot")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();

                            if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                ParrotEntity parrot = EntityType.PARROT.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);

                                if(parrot != null) {
                                    Random random = new Random();

                                    ParrotEntity.Variant rndmElem = parrotVariants.get(random.nextInt(parrotVariants.size()));

                                    parrot.setTamed(true, true);
                                    parrot.setOwner(serverPlayer);
                                    parrot.setVariant(rndmElem);

                                    parrot.setCustomName(Text.literal(username));
                                }
                            }

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§1§lPARROT SPAWNED!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_PARROT_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                            broadcast(String.format("%s just gave a Parrot to Blaze!", event.getUser().getName()), serverPlayer);
                        }
                    });
                }
            }
        }
        if(message.startsWith("!potion")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            Random random = new Random();
                            RegistryEntry<StatusEffect> rndmPotion = statusEffects2.get(random.nextInt(statusEffects2.size()));
                            String potionName = rndmPotion.value().getName().getString().toLowerCase();

                            serverPlayer.addStatusEffect(new StatusEffectInstance(rndmPotion, 1200, 2));

                            String potionColor2 = POTION_COLORS2.get(potionName.toLowerCase());

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload(potionColor2 + "POTION EFFECT!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("%s%s just gave the %s effect to Blaze!", potionColor2, event.getUser().getName(), potionName), serverPlayer);
                        }
                    });
                }
            }
        }
        if(message.startsWith("!cBomb")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            BlockPos spawnLocation = serverPlayer.getBlockPos();
                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§f§lCHICKEN BOMB!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§f§l%s just Chicken Bombed Blaze!", event.getUser().getName()), serverPlayer);

                            for(int i = 0; i < 1000; i++) {
                                if (world.isChunkLoaded(serverPlayer.getChunkPos().toLong())) {
                                    EntityType.CHICKEN.spawn(world, spawnLocation, SpawnReason.MOB_SUMMONED);
                                }
                            }
                        }
                    });
                }
            }
        }
        if(message.startsWith("!message")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
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
                            broadcast(String.format("§7§l%s just sent Blaze a message!", event.getUser().getName()), serverPlayer);
                        }
                    });
                }
            }
        }
        if(message.startsWith("!dItem")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            ItemStack activeItem = serverPlayer.getStackInHand(serverPlayer.getActiveHand());
                            if(activeItem != null) {
                                activeItem.decrement(activeItem.getCount());
                            }

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§c§lITEM DELETE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("%s just deleted Blaze's item!", event.getUser().getName()), serverPlayer);
                        }
                    });
                }
            }
        }
        if(message.startsWith("!dInventory")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            PlayerInventory inventory = serverPlayer.getInventory();
                            inventory.clear();

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§c§lINVENTORY DELETE!");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§4§l%s just deleted Blaze's ENTIRE INVENTORY!", event.getUser().getName()), serverPlayer);
                        }
                    });
                }
            }
        }
        if(message.startsWith("!void")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            BlockPos location = serverPlayer.getBlockPos();
                            serverPlayer.requestTeleport(location.getX(), -65, location.getZ());

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§8§lVOID...");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.AMBIENT_CAVE.value(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§8§l%s just threw Blaze into the VOID!", event.getUser().getName()), serverPlayer);
                        }
                    });
                }
            }
        }
        if(message.startsWith("!buddy")) {
            if(ThreadLocalRandom.current().nextDouble() < 1) {

                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

                if(server != null) {
                    Iterable<ServerWorld> worlds = BlazinBlazeTwitchMod.serverRef.getWorlds();

                    worlds.forEach((world) -> {
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            BlockPos location = serverPlayer.getBlockPos();
                            BeddyEntity beddy = BlazinBlazeTwitchModEntities.BEDDY_ENTITY.spawn(world, location, SpawnReason.MOB_SUMMONED);
                            PigEntity pig = EntityType.PIG.spawn(world, location, SpawnReason.MOB_SUMMONED);
                            pig.attachLeash(beddy, false);
                            serverPlayer.startRiding(pig, true);

                            OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§8§lBEDDY...");
                            ServerPlayNetworking.send(serverPlayer, payload);
                            serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_STONE_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            broadcast(String.format("§8§l%s just attached Beddy to Blaze!", event.getUser().getName()), serverPlayer);
                        }
                    });
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
