package net.blazinblaze.blazetwitchmod.entity.model;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

public class BlazinBlazeModelLayers {
    public static final ModelLayerLocation LOOT_CRATE = createMain("loot_crate");
    public static final ModelLayerLocation BEDDY = createMain("beddy");

    private static ModelLayerLocation createMain(String name) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, name), "main");
    }

    public static void registerModelLayers() {
        ModelLayerRegistry.registerModelLayer(BlazinBlazeModelLayers.LOOT_CRATE, LootCrateModel::getTexturedModelData);
        ModelLayerRegistry.registerModelLayer(BlazinBlazeModelLayers.BEDDY, BeddyEntityModel::getTexturedModelData);
    }
}
