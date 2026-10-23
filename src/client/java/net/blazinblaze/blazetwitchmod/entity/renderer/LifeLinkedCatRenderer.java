package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.client.model.animal.feline.AbstractFelineModel;
import net.minecraft.client.model.animal.feline.AdultCatModel;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;

public class LifeLinkedCatRenderer extends MobRenderer<Cat, CatRenderState, AbstractFelineModel<CatRenderState>> {
    public LifeLinkedCatRenderer(EntityRendererProvider.Context context) {
        super(context, new AdultCatModel(context.bakeLayer(ModelLayers.CAT)), 0.4F);
        this.addLayer(new CatCollarLayer(this, context.getModelSet()));
    }

    public Identifier getTextureLocation(CatRenderState catEntityRenderState) {
        return Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_cat/life_linked_cat.png");
    }

    public CatRenderState createRenderState() {
        return new CatRenderState();
    }

    public void extractRenderState(Cat catEntity, CatRenderState catEntityRenderState, float f) {
        super.extractRenderState(catEntity, catEntityRenderState, f);
        catEntityRenderState.texture = ((CatVariant)catEntity.getVariant().value()).assetInfo(catEntityRenderState.isBaby).texturePath();
        catEntityRenderState.isCrouching = catEntity.isCrouching();
        catEntityRenderState.isSprinting = catEntity.isSprinting();
        catEntityRenderState.isSitting = catEntity.isInSittingPose();
        catEntityRenderState.lieDownAmount = catEntity.getLieDownAmount(f);
        catEntityRenderState.lieDownAmountTail = catEntity.getLieDownAmountTail(f);
        catEntityRenderState.relaxStateOneAmount = catEntity.getRelaxStateOneAmount(f);
        catEntityRenderState.isLyingOnTopOfSleepingPlayer = catEntity.isLyingOnTopOfSleepingPlayer();
        catEntityRenderState.collarColor = catEntity.isTame() ? catEntity.getCollarColor() : null;
    }

    protected void setupRotations(CatRenderState catEntityRenderState, PoseStack matrixStack, float f, float g) {
        super.setupRotations(catEntityRenderState, matrixStack, f, g);
        float h = catEntityRenderState.lieDownAmount;
        if (h > 0.0F) {
            matrixStack.translate(0.4F * h, 0.15F * h, 0.1F * h);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(h, 0.0F, 90.0F)));
            if (catEntityRenderState.isLyingOnTopOfSleepingPlayer) {
                matrixStack.translate(0.15F * h, 0.0F, 0.0F);
            }
        }

    }
}
