package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.custom.ChatterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.model.animal.allay.AllayModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.AllayRenderState;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;

public class ChatterEntityRenderer extends MobRenderer<ChatterEntity, AllayRenderState, AllayModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/chatter_entity/chatter_entity.png");

    public ChatterEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new AllayModel(context.bakeLayer(ModelLayers.ALLAY)), 0.4F);
        this.addLayer(new ItemInHandLayer(this));
    }

    public Identifier getTextureLocation(AllayRenderState allayEntityRenderState) {
        return TEXTURE;
    }

    public AllayRenderState createRenderState() {
        return new AllayRenderState();
    }

    public void extractRenderState(ChatterEntity allayEntity, AllayRenderState allayEntityRenderState, float f) {
        super.extractRenderState(allayEntity, allayEntityRenderState, f);
        ArmedEntityRenderState.extractArmedEntityRenderState(allayEntity, allayEntityRenderState, this.itemModelResolver, f);
        allayEntityRenderState.isDancing = allayEntity.isDancing();
        allayEntityRenderState.isSpinning = allayEntity.isSpinning();
        allayEntityRenderState.spinningProgress = allayEntity.getSpinningAnimationTicks(f);
        allayEntityRenderState.holdingAnimationProgress = allayEntity.getItemHoldAnimationTicks(f);
    }

    protected int getBlockLightLevel(ChatterEntity allayEntity, BlockPos blockPos) {
        return 15;
    }
}
