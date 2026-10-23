package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.animal.fish.SalmonModel;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.entity.state.SalmonRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;

public class LifeLinkedFishRenderer extends MobRenderer<Salmon, SalmonRenderState, SalmonModel>  {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/life_linked_fish/life_linked_fish.png");
    private final SalmonModel smallModel;
    private final SalmonModel mediumModel;
    private final SalmonModel largeModel;

    public LifeLinkedFishRenderer(EntityRendererProvider.Context context) {
        super(context, new SalmonModel(context.bakeLayer(ModelLayers.SALMON)), 0.4F);
        this.smallModel = new SalmonModel(context.bakeLayer(ModelLayers.SALMON_SMALL));
        this.mediumModel = new SalmonModel(context.bakeLayer(ModelLayers.SALMON));
        this.largeModel = new SalmonModel(context.bakeLayer(ModelLayers.SALMON_LARGE));
    }

    public void extractRenderState(Salmon salmonEntity, SalmonRenderState salmonEntityRenderState, float f) {
        super.extractRenderState(salmonEntity, salmonEntityRenderState, f);
        salmonEntityRenderState.variant = salmonEntity.getVariant();
    }

    public Identifier getTextureLocation(SalmonRenderState salmonEntityRenderState) {
        return TEXTURE;
    }

    public SalmonRenderState createRenderState() {
        return new SalmonRenderState();
    }

    protected void setupRotations(SalmonRenderState salmonEntityRenderState, PoseStack matrixStack, float f, float g) {
        super.setupRotations(salmonEntityRenderState, matrixStack, f, g);
        float h = 1.0F;
        float i = 1.0F;
        if (!salmonEntityRenderState.isInWater) {
            h = 1.3F;
            i = 1.7F;
        }

        float j = h * 4.3F * Mth.sin(i * 0.6F * salmonEntityRenderState.ageInTicks);
        matrixStack.mulPose(Axis.YP.rotationDegrees(j));
        if (!salmonEntityRenderState.isInWater) {
            matrixStack.translate(0.2F, 0.1F, 0.0F);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }

    }

    public void submit(SalmonRenderState salmonEntityRenderState, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (salmonEntityRenderState.variant == Salmon.Variant.SMALL) {
            this.model = this.smallModel;
        } else if (salmonEntityRenderState.variant == Salmon.Variant.LARGE) {
            this.model = this.largeModel;
        } else {
            this.model = this.mediumModel;
        }

        super.submit(salmonEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }
}
