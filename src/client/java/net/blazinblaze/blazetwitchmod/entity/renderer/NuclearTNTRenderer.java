package net.blazinblaze.blazetwitchmod.entity.renderer;

import net.blazinblaze.blazetwitchmod.entity.custom.NuclearTntEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.state.TntRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;

@Environment(EnvType.CLIENT)
public class NuclearTNTRenderer extends EntityRenderer<NuclearTntEntity, TntRenderState> {

    public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private final BlockModelResolver blockModelResolver;

    public NuclearTNTRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockModelResolver = context.getBlockModelResolver();
    }

    public void submit(TntRenderState tntEntityRenderState, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.pushPose();
        matrixStack.translate(0.0F, 0.5F, 0.0F);
        float f = tntEntityRenderState.fuseRemainingInTicks;
        if (tntEntityRenderState.fuseRemainingInTicks < 10.0F) {
            float g = 1.0F - tntEntityRenderState.fuseRemainingInTicks / 10.0F;
            g = Mth.clamp(g, 0.0F, 1.0F);
            g *= g;
            g *= g;
            float h = 1.0F + g * 0.3F;
            matrixStack.scale(h, h, h);
        }

        matrixStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        matrixStack.translate(-0.5F, -0.5F, 0.5F);
        matrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        if (tntEntityRenderState.blockState.isEmpty()) {
            TntMinecartRenderer.submitWhiteSolidBlock(
                   tntEntityRenderState.blockState, matrixStack, orderedRenderCommandQueue, tntEntityRenderState.lightCoords, (int)f / 5 % 2 == 0, tntEntityRenderState.outlineColor
            );
        }

        matrixStack.popPose();
        super.submit(tntEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public TntRenderState createRenderState() {
        return new TntRenderState();
    }

    public void extractRenderState(NuclearTntEntity tntEntity, TntRenderState tntEntityRenderState, float f) {
        super.extractRenderState(tntEntity, tntEntityRenderState, f);
        tntEntityRenderState.fuseRemainingInTicks = tntEntity.getFuse() - f + 1.0F;
        this.blockModelResolver.update(tntEntityRenderState.blockState, tntEntity.getBlockState(), BLOCK_DISPLAY_CONTEXT);
    }

}
