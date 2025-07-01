package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.custom.LootCrateEntity;
import net.blazinblaze.blazetwitchmod.entity.model.BlazinBlazeModelLayers;
import net.blazinblaze.blazetwitchmod.entity.model.LootCrateModel;
import net.blazinblaze.blazetwitchmod.entity.state.LootCrateRenderState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class LootCrateRenderer extends EntityRenderer<LootCrateEntity, LootCrateRenderState> {

    private static final Identifier LOOT_CRATE_TEXTURE = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/loot_crate_entity/loot_crate_entity.png");
    private static LootCrateModel LOOT_CRATE_MODEL;

    public LootCrateRenderer(EntityRendererFactory.Context context) {
        super(context);
        LOOT_CRATE_MODEL = new LootCrateModel(context.getPart(BlazinBlazeModelLayers.LOOT_CRATE));
    }

    @Override
    public LootCrateRenderState createRenderState() {
        return new LootCrateRenderState();
    }

    @Override
    public void updateRenderState(LootCrateEntity entity, LootCrateRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
    }

    @Override
    public void render(LootCrateRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        LOOT_CRATE_MODEL.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(LOOT_CRATE_TEXTURE)), light, OverlayTexture.DEFAULT_UV);
        LOOT_CRATE_MODEL.setAngles(state);

        if (state.displayName != null) {
            this.renderLabelIfPresent(state, state.displayName, matrices, vertexConsumers, light);
        }
    }
}
