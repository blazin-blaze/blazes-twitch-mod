package net.blazinblaze.blazetwitchmod.entity.model;

import net.blazinblaze.blazetwitchmod.entity.animation.LootCrateAnimations;
import net.blazinblaze.blazetwitchmod.entity.state.LootCrateRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.CamelAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class LootCrateModel extends EntityModel<LootCrateRenderState> {
    private final ModelPart bone2;
    private final ModelPart bone;
    private final ModelPart bb_main;

    private final KeyframeAnimation idlingAnimation;

    public LootCrateModel(ModelPart root) {
        super(root);
        this.bone2 = root.getChild("bone2");
        this.bone = root.getChild("bone");
        this.bb_main = root.getChild("bb_main");
        this.idlingAnimation = LootCrateAnimations.LOOT_CRATE_IDLE_ANIM.bake(root);
    }
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition bone2 = modelPartData.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(12, 58).addBox(-1.0F, -4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 54).addBox(-6.0F, 8.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 54).addBox(-7.0F, 6.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(18, 58).addBox(-6.0F, 6.0F, 1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 58).addBox(-6.0F, 6.0F, -2.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 54).addBox(-4.0F, 6.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 58).addBox(-6.0F, 4.0F, -3.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(48, 60).addBox(-6.0F, 2.0F, -4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 48).addBox(-6.0F, 0.0F, -4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 51).addBox(-6.0F, -2.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 60).addBox(-6.0F, -4.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 54).addBox(-8.0F, 4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(32, 54).addBox(-9.0F, 2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(38, 56).addBox(-9.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(44, 56).addBox(-10.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(6, 58).addBox(-10.0F, -4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(36, 60).addBox(-6.0F, 4.0F, 2.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(42, 60).addBox(-6.0F, 2.0F, 3.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 42).addBox(-6.0F, 0.0F, 3.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 45).addBox(-6.0F, -2.0F, 4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 60).addBox(-6.0F, -4.0F, 4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 54).addBox(-3.0F, 4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(50, 56).addBox(-2.0F, 2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(56, 56).addBox(-2.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 58).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 26.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition bone = modelPartData.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 32).addBox(-11.0F, -2.0F, -5.0F, 10.0F, 12.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(41, 43).addBox(-10.0F, -2.0F, -6.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(41, 43).addBox(-10.0F, -2.0F, 5.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 32).addBox(-10.0F, -3.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(40, 32).addBox(-10.0F, 10.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 40.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(41, 43).addBox(-8.0F, -12.0F, -7.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-18.0F, 10.0F, 4.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r2 = bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(41, 43).addBox(-8.0F, -12.0F, -7.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 10.0F, 4.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition bb_main = modelPartData.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -24.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(modelData, 128, 128);
    }

    @Override
    public void setupAnim(LootCrateRenderState state) {
        super.setupAnim(state);
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.idlingAnimation.apply(state.idleAnimationState, state.ageInTicks, 1.0F);
    }

}
