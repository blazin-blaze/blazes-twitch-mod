package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.custom.ChatterEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.AllayEntityRenderState;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ChatterEntityRenderer extends MobEntityRenderer<ChatterEntity, AllayEntityRenderState, AllayEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/chatter_entity/chatter_entity.png");

    public ChatterEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AllayEntityModel(context.getPart(EntityModelLayers.ALLAY)), 0.4F);
        this.addFeature(new HeldItemFeatureRenderer(this));
    }

    public Identifier getTexture(AllayEntityRenderState allayEntityRenderState) {
        return TEXTURE;
    }

    public AllayEntityRenderState createRenderState() {
        return new AllayEntityRenderState();
    }

    public void updateRenderState(ChatterEntity allayEntity, AllayEntityRenderState allayEntityRenderState, float f) {
        super.updateRenderState(allayEntity, allayEntityRenderState, f);
        ArmedEntityRenderState.updateRenderState(allayEntity, allayEntityRenderState, this.itemModelResolver);
        allayEntityRenderState.dancing = allayEntity.isDancing();
        allayEntityRenderState.spinning = allayEntity.isSpinning();
        allayEntityRenderState.spinningAnimationTicks = allayEntity.getSpinningAnimationTicks(f);
        allayEntityRenderState.itemHoldAnimationTicks = allayEntity.getItemHoldAnimationTicks(f);
    }

    protected int getBlockLight(ChatterEntity allayEntity, BlockPos blockPos) {
        return 15;
    }
}
