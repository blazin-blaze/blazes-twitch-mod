package net.blazinblaze.blazetwitchmod.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;

import java.util.Optional;

public class NuclearExplosionBehavior extends ExplosionDamageCalculator {

    @Override
    public float getKnockbackMultiplier(Entity entity) {
        return 5.0F;
    }

    @Override
    public float getEntityDamageAmount(Explosion explosion, Entity entity, float amount) {
        float f = explosion.radius() * 3.0F;
        Vec3 vec3d = explosion.center();
        double d = Math.sqrt(entity.distanceToSqr(vec3d)) / f;
        double e = (3.5 - d) * amount;
        return (float)((e * e + e) / 2.0 * 7.0 * f + 1.0);
    }
}
