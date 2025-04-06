package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.WolfArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class LifeLinkedWolfRenderer extends MobEntityRenderer<WolfEntity, WolfEntityRenderState, WolfEntityModel> {
    public LifeLinkedWolfRenderer(EntityRendererFactory.Context context) {
        super(context, new WolfEntityModel(context.getPart(EntityModelLayers.WOLF)), 0.5F);
        this.addFeature(new WolfArmorFeatureRenderer(this, context.getEntityModels(), context.getEquipmentRenderer()));
        this.addFeature(new WolfCollarFeatureRenderer(this));
    }

    protected int getMixColor(WolfEntityRenderState wolfEntityRenderState) {
        float f = wolfEntityRenderState.furWetBrightnessMultiplier;
        return f == 1.0F ? -1 : ColorHelper.fromFloats(1.0F, f, f, f);
    }

    public Identifier getTexture(WolfEntityRenderState wolfEntityRenderState) {
        return Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_wolf/life_linked_wolf.png");
    }

    public WolfEntityRenderState createRenderState() {
        return new WolfEntityRenderState();
    }

    public void updateRenderState(WolfEntity wolfEntity, WolfEntityRenderState wolfEntityRenderState, float f) {
        super.updateRenderState(wolfEntity, wolfEntityRenderState, f);
        wolfEntityRenderState.angerTime = wolfEntity.hasAngerTime();
        wolfEntityRenderState.inSittingPose = wolfEntity.isInSittingPose();
        wolfEntityRenderState.tailAngle = wolfEntity.getTailAngle();
        wolfEntityRenderState.begAnimationProgress = wolfEntity.getBegAnimationProgress(f);
        wolfEntityRenderState.shakeProgress = wolfEntity.getShakeProgress(f);
        wolfEntityRenderState.texture = wolfEntity.getTextureId();
        wolfEntityRenderState.furWetBrightnessMultiplier = wolfEntity.getFurWetBrightnessMultiplier(f);
        wolfEntityRenderState.collarColor = wolfEntity.isTamed() ? wolfEntity.getCollarColor() : null;
        wolfEntityRenderState.bodyArmor = wolfEntity.getBodyArmor().copy();
    }
}
