package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.custom.BeddyEntity;
import net.blazinblaze.blazetwitchmod.entity.model.BeddyEntityModel;
import net.blazinblaze.blazetwitchmod.entity.model.BlazinBlazeModelLayers;
import net.blazinblaze.blazetwitchmod.entity.state.BeddyRenderState;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class BeddyEntityRenderer extends MobEntityRenderer<BeddyEntity, BeddyRenderState, BeddyEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/beddy_entity/beddy_entity.png");

    public BeddyEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BeddyEntityModel(context.getPart(BlazinBlazeModelLayers.BEDDY)), 0.375f); // 0.375 shaowd radius
    }

    @Override
    public BeddyRenderState createRenderState() {
        return new BeddyRenderState();
    }

    @Override
    public void updateRenderState(BeddyEntity entity, BeddyRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
    }

    @Override
    public Identifier getTexture(BeddyRenderState state) {
        return TEXTURE;
    }
}
