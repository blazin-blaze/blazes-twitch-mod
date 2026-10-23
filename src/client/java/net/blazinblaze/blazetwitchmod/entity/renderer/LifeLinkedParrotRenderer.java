package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class LifeLinkedParrotRenderer extends MobRenderer<Parrot, ParrotRenderState, ParrotModel>  {
    private static final Identifier RED_BLUE_TEXTURE = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_parrot/life_linked_parrot.png");
    private static final Identifier BLUE_TEXTURE = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_parrot/life_linked_parrot.png");
    private static final Identifier GREEN_TEXTURE = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_parrot/life_linked_parrot.png");
    private static final Identifier YELLOW_TEXTURE = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_parrot/life_linked_parrot.png");
    private static final Identifier GREY_TEXTURE = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_parrot/life_linked_parrot.png");

    public LifeLinkedParrotRenderer(EntityRendererProvider.Context context) {
        super(context, new ParrotModel(context.bakeLayer(ModelLayers.PARROT)), 0.3F);
    }

    public Identifier getTextureLocation(ParrotRenderState parrotEntityRenderState) {
        return getTexture(parrotEntityRenderState.variant);
    }

    public ParrotRenderState createRenderState() {
        return new ParrotRenderState();
    }

    public void extractRenderState(Parrot parrotEntity, ParrotRenderState parrotEntityRenderState, float f) {
        super.extractRenderState(parrotEntity, parrotEntityRenderState, f);
        parrotEntityRenderState.variant = parrotEntity.getVariant();
        float g = Mth.lerp(f, parrotEntity.oFlap, parrotEntity.flap);
        float h = Mth.lerp(f, parrotEntity.oFlapSpeed, parrotEntity.flapSpeed);
        parrotEntityRenderState.flapAngle = (Mth.sin(g) + 1.0F) * h;
        parrotEntityRenderState.pose = ParrotModel.getPose(parrotEntity);
    }

    public static Identifier getTexture(Parrot.Variant variant) {
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
