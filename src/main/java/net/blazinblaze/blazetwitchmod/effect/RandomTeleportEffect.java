package net.blazinblaze.blazetwitchmod.effect;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

public class RandomTeleportEffect extends StatusEffect {

    public RandomTeleportEffect() {
        super(StatusEffectCategory.NEUTRAL, 0);
    }

    // Called every tick to check if the effect can be applied or not
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the effect every tick
        return true;
    }

    // Called when the effect is applied.
    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            double randomNumberY = world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) entity.getX(), (int) entity.getZ());
            player.requestTeleport(entity.getX(), randomNumberY + 30, entity.getZ());
            player.removeStatusEffect(BlazinBlazeTwitchMod.RANDOM_TELEPORT);
            player.removeStatusEffect(StatusEffects.SLOW_FALLING);
            player.requestTeleport(entity.getX(), randomNumberY, entity.getZ());
        }

        return super.applyUpdateEffect(world, entity, amplifier);
    }

    @Override
    public boolean isInstant() {
        return true;
    }
}
