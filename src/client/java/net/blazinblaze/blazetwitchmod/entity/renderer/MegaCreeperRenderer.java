package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.custom.MegaCreeperEntity;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class MegaCreeperRenderer extends CreeperRenderer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/entity/mega_creeper/mega_creeper.png");

    public MegaCreeperRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(CreeperRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void scale(CreeperRenderState creeperEntityRenderState, PoseStack matrixStack) {
        float f = creeperEntityRenderState.swelling;
        float g = 1.0F + Mth.sin(f * 200.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 2.0F);
        f *= f;
        f *= f;
        float h = (1.0F + f * 0.8F) * g;
        float i = (1.0F + f * 0.2F) / g;
        matrixStack.scale(h, i, h);
    }

    public void updateRenderState(MegaCreeperEntity creeperEntity, CreeperRenderState creeperEntityRenderState, float f) {
        super.extractRenderState(creeperEntity, creeperEntityRenderState, f);
        creeperEntityRenderState.swelling = creeperEntity.getSwelling(f);
        creeperEntityRenderState.isPowered = creeperEntity.isPowered();
    }
}
