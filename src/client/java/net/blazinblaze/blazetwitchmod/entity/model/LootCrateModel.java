package net.blazinblaze.blazetwitchmod.entity.model;

import net.blazinblaze.blazetwitchmod.entity.animation.LootCrateAnimations;
import net.blazinblaze.blazetwitchmod.entity.state.LootCrateRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.CamelAnimations;
import net.minecraft.client.render.entity.model.EntityModel;

public class LootCrateModel extends EntityModel<LootCrateRenderState> {
    private final ModelPart bone2;
    private final ModelPart bone;
    private final ModelPart bb_main;

    private final Animation idlingAnimation;

    public LootCrateModel(ModelPart root) {
        super(root);
        this.bone2 = root.getChild("bone2");
        this.bone = root.getChild("bone");
        this.bb_main = root.getChild("bb_main");
        this.idlingAnimation = LootCrateAnimations.LOOT_CRATE_IDLE_ANIM.createAnimation(root);
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone2 = modelPartData.addChild("bone2", ModelPartBuilder.create().uv(12, 58).cuboid(-1.0F, -4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 54).cuboid(-6.0F, 8.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(8, 54).cuboid(-7.0F, 6.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(18, 58).cuboid(-6.0F, 6.0F, 1.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 58).cuboid(-6.0F, 6.0F, -2.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 54).cuboid(-4.0F, 6.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(30, 58).cuboid(-6.0F, 4.0F, -3.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(48, 60).cuboid(-6.0F, 2.0F, -4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(60, 48).cuboid(-6.0F, 0.0F, -4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(60, 51).cuboid(-6.0F, -2.0F, -5.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(60, 60).cuboid(-6.0F, -4.0F, -5.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 54).cuboid(-8.0F, 4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(32, 54).cuboid(-9.0F, 2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(38, 56).cuboid(-9.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(44, 56).cuboid(-10.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(6, 58).cuboid(-10.0F, -4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(36, 60).cuboid(-6.0F, 4.0F, 2.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(42, 60).cuboid(-6.0F, 2.0F, 3.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(60, 42).cuboid(-6.0F, 0.0F, 3.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(60, 45).cuboid(-6.0F, -2.0F, 4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(54, 60).cuboid(-6.0F, -4.0F, 4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(20, 54).cuboid(-3.0F, 4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(50, 56).cuboid(-2.0F, 2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(56, 56).cuboid(-2.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 58).cuboid(-1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, 26.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 32).cuboid(-11.0F, -2.0F, -5.0F, 10.0F, 12.0F, 10.0F, new Dilation(0.0F))
                .uv(41, 43).cuboid(-10.0F, -2.0F, -6.0F, 8.0F, 12.0F, 1.0F, new Dilation(0.0F))
                .uv(41, 43).cuboid(-10.0F, -2.0F, 5.0F, 8.0F, 12.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 32).cuboid(-10.0F, -3.0F, -4.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(40, 32).cuboid(-10.0F, 10.0F, -4.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, 40.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r1 = bone.addChild("cube_r1", ModelPartBuilder.create().uv(41, 43).cuboid(-8.0F, -12.0F, -7.0F, 8.0F, 12.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-18.0F, 10.0F, 4.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r2 = bone.addChild("cube_r2", ModelPartBuilder.create().uv(41, 43).cuboid(-8.0F, -12.0F, -7.0F, 8.0F, 12.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-7.0F, 10.0F, 4.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -24.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(LootCrateRenderState state) {
        super.setAngles(state);
        this.getRootPart().traverse().forEach(ModelPart::resetTransform);

        this.idlingAnimation.apply(state.idleAnimationState, state.age, 1.0F);
    }

}
