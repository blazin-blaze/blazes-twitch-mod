package net.blazinblaze.blazetwitchmod.entity.model;

import net.blazinblaze.blazetwitchmod.entity.animation.BeddyAnimations;
import net.blazinblaze.blazetwitchmod.entity.animation.LootCrateAnimations;
import net.blazinblaze.blazetwitchmod.entity.state.BeddyRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class BeddyEntityModel extends EntityModel<BeddyRenderState> {
    private final ModelPart bone3;
    private final ModelPart bone;
    private final ModelPart bone2;
    private final ModelPart bone4;

    private final KeyframeAnimation idlingAnimation;
    private final KeyframeAnimation walkingAnimation;

    public BeddyEntityModel(ModelPart root) {
        super(root);
        this.bone3 = root.getChild("bone3");
        this.bone = root.getChild("bone");
        this.bone2 = root.getChild("bone2");
        this.bone4 = root.getChild("bone4");
        this.idlingAnimation = BeddyAnimations.BEDDY_IDLE.bake(root);
        this.walkingAnimation = BeddyAnimations.BEDDY_WALKING.bake(root);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition bone3 = modelPartData.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        PartDefinition bone = modelPartData.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(20, 37).addBox(-5.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(20, 32).addBox(-5.0F, 2.0F, -4.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 20.0F, 0.0F));

        PartDefinition bone2 = modelPartData.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(28, 37).addBox(-5.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 32).addBox(-5.0F, 2.0F, -4.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 20.0F, 0.0F));

        PartDefinition bone4 = modelPartData.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(0, 32).addBox(7.0F, -7.0F, -1.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 32).addBox(-1.0F, -7.0F, -1.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 8.0F, -8.0F));
        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void setupAnim(BeddyRenderState state) {
        super.setupAnim(state);
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.walkingAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2f, 2.5f);
        this.idlingAnimation.apply(state.idleAnimationState, state.ageInTicks, 1f);
    }

}
