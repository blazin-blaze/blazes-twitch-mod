package net.blazinblaze.blazetwitchmod.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.Optional;

public class NuclearExplosionBehavior extends ExplosionBehavior {

    @Override
    public float getKnockbackModifier(Entity entity) {
        return 5.0F;
    }

    @Override
    public float calculateDamage(Explosion explosion, Entity entity, float amount) {
        float f = explosion.getPower() * 3.0F;
        Vec3d vec3d = explosion.getPosition();
        double d = Math.sqrt(entity.squaredDistanceTo(vec3d)) / f;
        double e = (3.5 - d) * amount;
        return (float)((e * e + e) / 2.0 * 7.0 * f + 1.0);
    }
}
