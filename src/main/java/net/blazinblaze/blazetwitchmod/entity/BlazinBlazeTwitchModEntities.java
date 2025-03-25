package net.blazinblaze.blazetwitchmod.entity;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.custom.BeddyEntity;
import net.blazinblaze.blazetwitchmod.entity.custom.LootCrateEntity;
import net.blazinblaze.blazetwitchmod.entity.custom.MegaCreeperEntity;
import net.blazinblaze.blazetwitchmod.entity.custom.NuclearTntEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.TntEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

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
    public static final EntityType<BeddyEntity> BEDDY_ENTITY = register("beddy_entity", EntityType.Builder.<BeddyEntity>create(BeddyEntity::new, SpawnGroup.MISC).dropsNothing().makeFireImmune().dimensions(1F, 1F));

    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(BEDDY_ENTITY, BeddyEntity.createCubeAttributes());
    }

    //.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "nuclear_tnt_entity")

    public static void registerModEntites() {
        BlazinBlazeTwitchMod.LOGGER.info("Registering mod entities for " + BlazinBlazeTwitchMod.MOD_ID);
    }

}
