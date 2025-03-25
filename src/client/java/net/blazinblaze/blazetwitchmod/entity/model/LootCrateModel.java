package net.blazinblaze.blazetwitchmod.entity.model;

import net.blazinblaze.blazetwitchmod.entity.animation.LootCrateAnimations;
import net.blazinblaze.blazetwitchmod.entity.state.LootCrateRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

public class LootCrateModel extends EntityModel<LootCrateRenderState> {
    private final ModelPart bone3;
    private final ModelPart bone;
    private final ModelPart bone2;
    public LootCrateModel(ModelPart root) {
        super(root);
        this.bone3 = root.getChild("bone3");
        this.bone = root.getChild("bone");
        this.bone2 = root.getChild("bone2");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone3 = modelPartData.addChild("bone3", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 16.0F, 0.0F));

        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(50, 36).cuboid(-7.0F, -4.0F, -5.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(34, 32).cuboid(-11.0F, -4.0F, -1.0F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(26, 40).cuboid(-9.0F, -4.0F, -3.0F, 6.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 32).cuboid(-13.0F, 8.0F, -1.0F, 14.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(40, 48).cuboid(-14.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                .uv(48, 48).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 36).cuboid(-11.0F, 8.0F, -3.0F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(48, 60).cuboid(-8.0F, 8.0F, -7.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(24, 44).cuboid(-9.0F, 8.0F, -5.0F, 6.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 64).cuboid(-8.0F, -2.0F, -7.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(48, 36).cuboid(-9.0F, -2.0F, -5.0F, 6.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(24, 40).cuboid(-11.0F, -2.0F, -3.0F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(32, 32).cuboid(-13.0F, -2.0F, -1.0F, 14.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, 40.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r1 = bone.addChild("cube_r1", ModelPartBuilder.create().uv(60, 60).cuboid(-8.0F, -2.0F, -3.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, 10.0F, 4.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r2 = bone.addChild("cube_r2", ModelPartBuilder.create().uv(40, 44).cuboid(-9.0F, -2.0F, -3.0F, 6.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(24, 36).cuboid(-11.0F, -2.0F, -1.0F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, 10.0F, 2.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r3 = bone.addChild("cube_r3", ModelPartBuilder.create().uv(24, 62).cuboid(-8.0F, -2.0F, -3.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, 0.0F, 4.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r4 = bone.addChild("cube_r4", ModelPartBuilder.create().uv(24, 48).cuboid(-9.0F, -2.0F, -3.0F, 6.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 40).cuboid(-11.0F, -2.0F, -1.0F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, 0.0F, 2.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r5 = bone.addChild("cube_r5", ModelPartBuilder.create().uv(2, 40).cuboid(-9.0F, -2.0F, -1.0F, 6.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(26, 48).cuboid(-7.0F, -2.0F, -3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, -2.0F, 2.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r6 = bone.addChild("cube_r6", ModelPartBuilder.create().uv(24, 52).cuboid(-8.0F, -8.0F, -3.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-10.0F, 8.0F, 3.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r7 = bone.addChild("cube_r7", ModelPartBuilder.create().uv(0, 44).cuboid(-8.0F, -8.0F, -3.0F, 4.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, 8.0F, 5.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r8 = bone.addChild("cube_r8", ModelPartBuilder.create().uv(18, 64).cuboid(-8.0F, -8.0F, -2.0F, 2.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-18.0F, 8.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r9 = bone.addChild("cube_r9", ModelPartBuilder.create().uv(12, 64).cuboid(-8.0F, -8.0F, -2.0F, 2.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-18.0F, 8.0F, -3.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r10 = bone.addChild("cube_r10", ModelPartBuilder.create().uv(40, 58).cuboid(-8.0F, -8.0F, -3.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-16.0F, 8.0F, 3.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r11 = bone.addChild("cube_r11", ModelPartBuilder.create().uv(16, 54).cuboid(-8.0F, -8.0F, -3.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-16.0F, 8.0F, -7.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r12 = bone.addChild("cube_r12", ModelPartBuilder.create().uv(56, 50).cuboid(-8.0F, -8.0F, -3.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-17.0F, 8.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r13 = bone.addChild("cube_r13", ModelPartBuilder.create().uv(56, 40).cuboid(-8.0F, -8.0F, -3.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-17.0F, 8.0F, -5.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r14 = bone.addChild("cube_r14", ModelPartBuilder.create().uv(0, 54).cuboid(-8.0F, -8.0F, -3.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-10.0F, 8.0F, -7.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r15 = bone.addChild("cube_r15", ModelPartBuilder.create().uv(8, 54).cuboid(-8.0F, -8.0F, -3.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, 8.0F, -5.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r16 = bone.addChild("cube_r16", ModelPartBuilder.create().uv(64, 9).cuboid(-8.0F, -8.0F, -2.0F, 2.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, 8.0F, -3.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r17 = bone.addChild("cube_r17", ModelPartBuilder.create().uv(64, 0).cuboid(-8.0F, -8.0F, -2.0F, 2.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, 8.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r18 = bone.addChild("cube_r18", ModelPartBuilder.create().uv(32, 52).cuboid(-8.0F, -8.0F, -3.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, 8.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r19 = bone.addChild("cube_r19", ModelPartBuilder.create().uv(12, 44).cuboid(-8.0F, -8.0F, -3.0F, 4.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, 8.0F, -9.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData bone2 = modelPartData.addChild("bone2", ModelPartBuilder.create().uv(70, 12).cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(48, 40).cuboid(-6.0F, 8.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 18).cuboid(-8.0F, 6.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 22).cuboid(-6.0F, 6.0F, 1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 26).cuboid(-6.0F, 6.0F, -3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 30).cuboid(-4.0F, 6.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 34).cuboid(-6.0F, 4.0F, -4.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(32, 66).cuboid(-6.0F, 2.0F, -5.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 68).cuboid(-6.0F, 0.0F, -5.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(40, 68).cuboid(-6.0F, -2.0F, -6.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(70, 8).cuboid(-6.0F, -4.0F, -6.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 42).cuboid(-9.0F, 4.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(56, 64).cuboid(-10.0F, 2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 64).cuboid(-10.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(24, 66).cuboid(-11.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(70, 4).cuboid(-11.0F, -4.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 46).cuboid(-6.0F, 4.0F, 2.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(48, 64).cuboid(-6.0F, 2.0F, 3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 50).cuboid(-6.0F, 0.0F, 3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 54).cuboid(-6.0F, -2.0F, 4.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(70, 0).cuboid(-6.0F, -4.0F, 4.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 38).cuboid(-3.0F, 4.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(48, 68).cuboid(-2.0F, 2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(56, 68).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 68).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, 26.0F, 0.0F, 3.1416F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(LootCrateRenderState state) {
        this.getRootPart().traverse().forEach(ModelPart::resetTransform);

        this.animate(state.idleAnimationState, LootCrateAnimations.LOOT_CRATE_IDLE_ANIM, state.age, 1.0F);
    }

}
