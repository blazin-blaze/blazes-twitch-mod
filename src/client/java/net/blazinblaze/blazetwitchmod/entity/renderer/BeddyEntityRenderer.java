package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.custom.BeddyEntity;
import net.blazinblaze.blazetwitchmod.entity.model.BeddyEntityModel;
import net.blazinblaze.blazetwitchmod.entity.model.BlazinBlazeModelLayers;
import net.blazinblaze.blazetwitchmod.entity.state.BeddyRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class BeddyEntityRenderer extends MobRenderer<BeddyEntity, BeddyRenderState, BeddyEntityModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/beddy_entity/beddy_entity.png");

    public BeddyEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new BeddyEntityModel(context.bakeLayer(BlazinBlazeModelLayers.BEDDY)), 0.375f); // 0.375 shaowd radius
    }

    @Override
    public BeddyRenderState createRenderState() {
        return new BeddyRenderState();
    }

    @Override
    public void extractRenderState(BeddyEntity entity, BeddyRenderState state, float tickProgress) {
        super.extractRenderState(entity, state, tickProgress);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
    }

    @Override
    public Identifier getTextureLocation(BeddyRenderState state) {
        return TEXTURE;
    }
}
