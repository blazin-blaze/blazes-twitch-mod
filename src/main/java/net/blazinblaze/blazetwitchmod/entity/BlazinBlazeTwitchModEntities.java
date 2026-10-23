package net.blazinblaze.blazetwitchmod.entity;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class BlazinBlazeTwitchModEntities {

    public static final EntityType<MegaCreeperEntity> MEGA_CREEPER = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "mega_creeper"),
            EntityType.Builder.of(MegaCreeperEntity::new, MobCategory.MONSTER)
                    .sized(1f, 2.5f).build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "mega_creeper"))));

    private static <T extends Entity> EntityType<T> register(ResourceKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type.build(key));
    }

    private static ResourceKey<EntityType<?>> keyOf(String id) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, id));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }

    public static final EntityType<NuclearTntEntity> NUCLEAR_TNT_ENTITY = register("nuclear_tnt_entity", EntityType.Builder.<NuclearTntEntity>of(NuclearTntEntity::new, MobCategory.MISC).noLootTable().fireImmune().sized(.98F, .98F).eyeHeight(0.15F).clientTrackingRange(10).updateInterval(10));
    public static final EntityType<LootCrateEntity> LOOT_CRATE_ENTITY = register("loot_crate_entity", EntityType.Builder.<LootCrateEntity>of(LootCrateEntity::new, MobCategory.MISC).noLootTable().fireImmune().sized(1F, 2.75F));
    public static final EntityType<BeddyEntity> BEDDY_ENTITY = register("beddy_entity", EntityType.Builder.<BeddyEntity>of(BeddyEntity::new, MobCategory.CREATURE).noLootTable().fireImmune().sized(1F, 1F));

    public static final EntityType<LifeLinkedCatEntity> LIFE_LINKED_CAT_ENTITY = register("life_linked_cat", EntityType.Builder.<LifeLinkedCatEntity>of(LifeLinkedCatEntity::new, MobCategory.CREATURE).sized(0.6F, 0.7F).eyeHeight(0.35F).passengerAttachments(0.5125F).clientTrackingRange(8));
    public static final EntityType<LifeLinkedParrotEntity> LIFE_LINKED_PARROT_ENTITY = register("life_linked_parrot", EntityType.Builder.<LifeLinkedParrotEntity>of(LifeLinkedParrotEntity::new, MobCategory.CREATURE).sized(0.5F, 0.9F).eyeHeight(0.54F).passengerAttachments(0.4625F).clientTrackingRange(8));
    public static final EntityType<LifeLinkedWolfEntity> LIFE_LINKED_WOLF_ENTITY = register("life_linked_wolf", EntityType.Builder.<LifeLinkedWolfEntity>of(LifeLinkedWolfEntity::new, MobCategory.CREATURE).sized(0.6F, 0.85F).eyeHeight(0.68F).passengerAttachments(new Vec3((double)0.0F, 0.81875, (double)-0.0625F)).clientTrackingRange(10));
    public static final EntityType<LifeLinkedFishEntity> LIFE_LINKED_FISH_ENTITY = register("life_linked_fish", EntityType.Builder.<LifeLinkedFishEntity>of(LifeLinkedFishEntity::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).eyeHeight(0.26F).clientTrackingRange(4));

    public static final EntityType<ChatterEntity> CHATTER_ENTITY = register("chatter_entity", EntityType.Builder.<ChatterEntity>of(ChatterEntity::new, MobCategory.CREATURE).sized(0.35F, 0.6F).eyeHeight(0.36F).ridingOffset(0.04F).clientTrackingRange(8).updateInterval(2));

    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(BEDDY_ENTITY, BeddyEntity.createCubeAttributes());
        FabricDefaultAttributeRegistry.register(LIFE_LINKED_CAT_ENTITY, Cat.createAttributes());
        FabricDefaultAttributeRegistry.register(LIFE_LINKED_WOLF_ENTITY, Wolf.createAttributes());
        FabricDefaultAttributeRegistry.register(LIFE_LINKED_PARROT_ENTITY, Parrot.createAttributes());
        FabricDefaultAttributeRegistry.register(LIFE_LINKED_FISH_ENTITY, Salmon.createAttributes());
        FabricDefaultAttributeRegistry.register(CHATTER_ENTITY, ChatterEntity.createAllayAttributes());
    }

    //.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "nuclear_tnt_entity")

    public static void registerModEntites() {
        BlazinBlazeTwitchMod.LOGGER.info("Registering mod entities for " + BlazinBlazeTwitchMod.MOD_ID);
    }

}
