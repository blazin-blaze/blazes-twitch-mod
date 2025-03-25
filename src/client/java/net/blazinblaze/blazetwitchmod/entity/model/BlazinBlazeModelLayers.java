package net.blazinblaze.blazetwitchmod.entity.model;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class BlazinBlazeModelLayers {
    public static final EntityModelLayer LOOT_CRATE = createMain("loot_crate");
    public static final EntityModelLayer BEDDY = createMain("beddy");

    private static EntityModelLayer createMain(String name) {
        return new EntityModelLayer(Identifier.of(BlazinBlazeTwitchMod.MOD_ID, name), "main");
    }

    public static void registerModelLayers() {
        EntityModelLayerRegistry.registerModelLayer(BlazinBlazeModelLayers.LOOT_CRATE, LootCrateModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(BlazinBlazeModelLayers.BEDDY, BeddyEntityModel::getTexturedModelData);
    }
}
