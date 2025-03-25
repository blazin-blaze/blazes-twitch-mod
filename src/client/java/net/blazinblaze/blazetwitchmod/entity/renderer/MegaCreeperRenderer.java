package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.custom.MegaCreeperEntity;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class MegaCreeperRenderer extends CreeperEntityRenderer {
    private static final Identifier TEXTURE = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/mega_creeper/mega_creeper.png");

    public MegaCreeperRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(CreeperEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void scale(CreeperEntityRenderState creeperEntityRenderState, MatrixStack matrixStack) {
        float f = creeperEntityRenderState.fuseTime;
        float g = 1.0F + MathHelper.sin(f * 200.0F) * f * 0.01F;
        f = MathHelper.clamp(f, 0.0F, 2.0F);
        f *= f;
        f *= f;
        float h = (1.0F + f * 0.8F) * g;
        float i = (1.0F + f * 0.2F) / g;
        matrixStack.scale(h, i, h);
    }

    public void updateRenderState(MegaCreeperEntity creeperEntity, CreeperEntityRenderState creeperEntityRenderState, float f) {
        super.updateRenderState(creeperEntity, creeperEntityRenderState, f);
        creeperEntityRenderState.fuseTime = creeperEntity.getClientFuseTime(f);
        creeperEntityRenderState.charged = creeperEntity.isCharged();
    }
}
