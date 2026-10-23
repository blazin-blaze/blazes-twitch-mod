package net.blazinblaze.blazetwitchmod.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class LootCrateRenderState extends EntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();

    public AnimationState getIdleAnimationState() {
        return idleAnimationState;
    }
}
