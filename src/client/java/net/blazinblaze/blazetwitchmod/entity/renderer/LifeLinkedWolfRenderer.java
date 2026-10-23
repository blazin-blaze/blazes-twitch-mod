package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.client.model.animal.wolf.AdultWolfModel;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WolfArmorLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class LifeLinkedWolfRenderer extends MobRenderer<Wolf, WolfRenderState, WolfModel> {
    public LifeLinkedWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new AdultWolfModel(context.bakeLayer(ModelLayers.WOLF)), 0.5F);
        this.addLayer(new WolfArmorLayer(this, context.getModelSet(), context.getEquipmentRenderer()));
        this.addLayer(new WolfCollarLayer(this));
    }

    protected int getModelTint(WolfRenderState wolfEntityRenderState) {
        float f = wolfEntityRenderState.wetShade;
        return f == 1.0F ? -1 : ARGB.colorFromFloat(1.0F, f, f, f);
    }

    public Identifier getTextureLocation(WolfRenderState wolfEntityRenderState) {
        return Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_wolf/life_linked_wolf.png");
    }

    public WolfRenderState createRenderState() {
        return new WolfRenderState();
    }

    public void extractRenderState(Wolf wolfEntity, WolfRenderState wolfEntityRenderState, float f) {
        super.extractRenderState(wolfEntity, wolfEntityRenderState, f);
        wolfEntityRenderState.isAngry = wolfEntity.isAngry();
        wolfEntityRenderState.isSitting = wolfEntity.isInSittingPose();
        wolfEntityRenderState.tailAngle = wolfEntity.getTailAngle();
        wolfEntityRenderState.headRollAngle = wolfEntity.getHeadRollAngle(f);
        wolfEntityRenderState.shakeAnim = wolfEntity.getShakeAnim(f);
        wolfEntityRenderState.texture = wolfEntity.getTexture();
        wolfEntityRenderState.wetShade = wolfEntity.getWetShade(f);
        wolfEntityRenderState.collarColor = wolfEntity.isTame() ? wolfEntity.getCollarColor() : null;
        wolfEntityRenderState.bodyArmorItem = wolfEntity.getBodyArmorItem().copy();
    }
}
