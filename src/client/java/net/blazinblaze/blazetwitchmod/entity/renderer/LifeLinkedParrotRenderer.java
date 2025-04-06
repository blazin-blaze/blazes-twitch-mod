package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class LifeLinkedParrotRenderer extends MobEntityRenderer<ParrotEntity, ParrotEntityRenderState, ParrotEntityModel>  {
    private static final Identifier RED_BLUE_TEXTURE = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_parrot/life_linked_parrot.png");
    private static final Identifier BLUE_TEXTURE = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_parrot/life_linked_parrot.png");
    private static final Identifier GREEN_TEXTURE = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_parrot/life_linked_parrot.png");
    private static final Identifier YELLOW_TEXTURE = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_parrot/life_linked_parrot.png");
    private static final Identifier GREY_TEXTURE = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_parrot/life_linked_parrot.png");

    public LifeLinkedParrotRenderer(EntityRendererFactory.Context context) {
        super(context, new ParrotEntityModel(context.getPart(EntityModelLayers.PARROT)), 0.3F);
    }

    public Identifier getTexture(ParrotEntityRenderState parrotEntityRenderState) {
        return getTexture(parrotEntityRenderState.variant);
    }

    public ParrotEntityRenderState createRenderState() {
        return new ParrotEntityRenderState();
    }

    public void updateRenderState(ParrotEntity parrotEntity, ParrotEntityRenderState parrotEntityRenderState, float f) {
        super.updateRenderState(parrotEntity, parrotEntityRenderState, f);
        parrotEntityRenderState.variant = parrotEntity.getVariant();
        float g = MathHelper.lerp(f, parrotEntity.lastFlapProgress, parrotEntity.flapProgress);
        float h = MathHelper.lerp(f, parrotEntity.lastMaxWingDeviation, parrotEntity.maxWingDeviation);
        parrotEntityRenderState.flapAngle = (MathHelper.sin(g) + 1.0F) * h;
        parrotEntityRenderState.parrotPose = ParrotEntityModel.getPose(parrotEntity);
    }

    public static Identifier getTexture(ParrotEntity.Variant variant) {
        Identifier var10000;
        switch (variant) {
            case RED_BLUE -> var10000 = RED_BLUE_TEXTURE;
            case BLUE -> var10000 = BLUE_TEXTURE;
            case GREEN -> var10000 = GREEN_TEXTURE;
            case YELLOW_BLUE -> var10000 = YELLOW_TEXTURE;
            case GRAY -> var10000 = GREY_TEXTURE;
            default -> throw new MatchException((String)null, (Throwable)null);
        }

        return var10000;
    }
}
