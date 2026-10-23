package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.custom.LootCrateEntity;
import net.blazinblaze.blazetwitchmod.entity.model.BlazinBlazeModelLayers;
import net.blazinblaze.blazetwitchmod.entity.model.LootCrateModel;
import net.blazinblaze.blazetwitchmod.entity.state.LootCrateRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

public class LootCrateRenderer extends EntityRenderer<LootCrateEntity, LootCrateRenderState> {

    private static final Identifier LOOT_CRATE_TEXTURE = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/loot_crate_entity/loot_crate_entity.png");
    private static LootCrateModel LOOT_CRATE_MODEL;

    public LootCrateRenderer(EntityRendererProvider.Context context) {
        super(context);
        LOOT_CRATE_MODEL = new LootCrateModel(context.bakeLayer(BlazinBlazeModelLayers.LOOT_CRATE));
    }

    @Override
    public LootCrateRenderState createRenderState() {
        return new LootCrateRenderState();
    }

    @Override
    public void extractRenderState(LootCrateEntity entity, LootCrateRenderState state, float tickProgress) {
        super.extractRenderState(entity, state, tickProgress);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
    }

    @Override
    public void submit(LootCrateRenderState state, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        orderedRenderCommandQueue.submitModel(LOOT_CRATE_MODEL, state, matrices, LOOT_CRATE_MODEL.renderType(LOOT_CRATE_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        //OLD: LOOT_CRATE_MODEL.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(LOOT_CRATE_TEXTURE)), light, OverlayTexture.DEFAULT_UV);
        LOOT_CRATE_MODEL.setupAnim(state);

        if (state.nameTag != null) {
            this.submitNameDisplay(state, matrices, orderedRenderCommandQueue, cameraRenderState);
            //OLD: this.renderLabelIfPresent(state, state.displayName, matrices, vertexConsumers, light);
        }
    }
}
