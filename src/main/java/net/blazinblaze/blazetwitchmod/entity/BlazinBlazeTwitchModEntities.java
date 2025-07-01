package net.blazinblaze.blazetwitchmod.entity;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class BlazinBlazeTwitchModEntities {

    public static final EntityType<MegaCreeperEntity> MEGA_CREEPER = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "mega_creeper"),
            EntityType.Builder.create(MegaCreeperEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1f, 2.5f).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "mega_creeper"))));

    private static <T extends Entity> EntityType<T> register(RegistryKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }

    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(BlazinBlazeTwitchMod.MOD_ID, id));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }

    public static final EntityType<NuclearTntEntity> NUCLEAR_TNT_ENTITY = register("nuclear_tnt_entity", EntityType.Builder.<NuclearTntEntity>create(NuclearTntEntity::new, SpawnGroup.MISC).dropsNothing().makeFireImmune().dimensions(.98F, .98F).eyeHeight(0.15F).maxTrackingRange(10).trackingTickInterval(10));
    public static final EntityType<LootCrateEntity> LOOT_CRATE_ENTITY = register("loot_crate_entity", EntityType.Builder.<LootCrateEntity>create(LootCrateEntity::new, SpawnGroup.MISC).dropsNothing().makeFireImmune().dimensions(1F, 2.75F));
    public static final EntityType<BeddyEntity> BEDDY_ENTITY = register("beddy_entity", EntityType.Builder.<BeddyEntity>create(BeddyEntity::new, SpawnGroup.CREATURE).dropsNothing().makeFireImmune().dimensions(1F, 1F));

    public static final EntityType<LifeLinkedCatEntity> LIFE_LINKED_CAT_ENTITY = register("life_linked_cat", EntityType.Builder.<LifeLinkedCatEntity>create(LifeLinkedCatEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 0.7F).eyeHeight(0.35F).passengerAttachments(0.5125F).maxTrackingRange(8));
    public static final EntityType<LifeLinkedParrotEntity> LIFE_LINKED_PARROT_ENTITY = register("life_linked_parrot", EntityType.Builder.<LifeLinkedParrotEntity>create(LifeLinkedParrotEntity::new, SpawnGroup.CREATURE).dimensions(0.5F, 0.9F).eyeHeight(0.54F).passengerAttachments(0.4625F).maxTrackingRange(8));
    public static final EntityType<LifeLinkedWolfEntity> LIFE_LINKED_WOLF_ENTITY = register("life_linked_wolf", EntityType.Builder.<LifeLinkedWolfEntity>create(LifeLinkedWolfEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 0.85F).eyeHeight(0.68F).passengerAttachments(new Vec3d((double)0.0F, 0.81875, (double)-0.0625F)).maxTrackingRange(10));
    public static final EntityType<LifeLinkedFishEntity> LIFE_LINKED_FISH_ENTITY = register("life_linked_fish", EntityType.Builder.<LifeLinkedFishEntity>create(LifeLinkedFishEntity::new, SpawnGroup.WATER_AMBIENT).dimensions(0.7F, 0.4F).eyeHeight(0.26F).maxTrackingRange(4));

    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(BEDDY_ENTITY, BeddyEntity.createCubeAttributes());
        FabricDefaultAttributeRegistry.register(LIFE_LINKED_CAT_ENTITY, CatEntity.createCatAttributes());
        FabricDefaultAttributeRegistry.register(LIFE_LINKED_WOLF_ENTITY, WolfEntity.createWolfAttributes());
        FabricDefaultAttributeRegistry.register(LIFE_LINKED_PARROT_ENTITY, ParrotEntity.createParrotAttributes());
        FabricDefaultAttributeRegistry.register(LIFE_LINKED_FISH_ENTITY, SalmonEntity.createFishAttributes());
    }

    //.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "nuclear_tnt_entity")

    public static void registerModEntites() {
        BlazinBlazeTwitchMod.LOGGER.info("Registering mod entities for " + BlazinBlazeTwitchMod.MOD_ID);
    }

}
