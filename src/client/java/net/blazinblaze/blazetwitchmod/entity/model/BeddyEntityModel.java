package net.blazinblaze.blazetwitchmod.entity.model;

import net.blazinblaze.blazetwitchmod.entity.animation.BeddyAnimations;
import net.blazinblaze.blazetwitchmod.entity.animation.LootCrateAnimations;
import net.blazinblaze.blazetwitchmod.entity.state.BeddyRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModel;

public class BeddyEntityModel extends EntityModel<BeddyRenderState> {
    private final ModelPart bone3;
    private final ModelPart bone;
    private final ModelPart bone2;
    private final ModelPart bone4;

    private final Animation idlingAnimation;
    private final Animation walkingAnimation;

    public BeddyEntityModel(ModelPart root) {
        super(root);
        this.bone3 = root.getChild("bone3");
        this.bone = root.getChild("bone");
        this.bone2 = root.getChild("bone2");
        this.bone4 = root.getChild("bone4");
        this.idlingAnimation = BeddyAnimations.BEDDY_IDLE.createAnimation(root);
        this.walkingAnimation = BeddyAnimations.BEDDY_WALKING.createAnimation(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone3 = modelPartData.addChild("bone3", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 20.0F, 0.0F));

        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(20, 37).cuboid(-5.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F))
                .uv(20, 32).cuboid(-5.0F, 2.0F, -4.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(1.0F, 20.0F, 0.0F));

        ModelPartData bone2 = modelPartData.addChild("bone2", ModelPartBuilder.create().uv(28, 37).cuboid(-5.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F))
                .uv(30, 32).cuboid(-5.0F, 2.0F, -4.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(8.0F, 20.0F, 0.0F));

        ModelPartData bone4 = modelPartData.addChild("bone4", ModelPartBuilder.create().uv(0, 32).cuboid(7.0F, -7.0F, -1.0F, 4.0F, 7.0F, 1.0F, new Dilation(0.0F))
                .uv(10, 32).cuboid(-1.0F, -7.0F, -1.0F, 4.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(-5.0F, 8.0F, -8.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(BeddyRenderState state) {
        super.setAngles(state);
        this.getRootPart().traverse().forEach(ModelPart::resetTransform);

        this.walkingAnimation.applyWalking(state.limbSwingAnimationProgress, state.limbSwingAmplitude, 2f, 2.5f);
        this.idlingAnimation.apply(state.idleAnimationState, state.age, 1f);
    }

}
