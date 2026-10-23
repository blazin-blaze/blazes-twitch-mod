package net.blazinblaze.blazetwitchmod.effect;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;

public class RandomTeleportEffect extends MobEffect {

    public RandomTeleportEffect() {
        super(MobEffectCategory.NEUTRAL, 0);
    }

    // Called every tick to check if the effect can be applied or not
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the effect every tick
        return true;
    }

    // Called when the effect is applied.
    @Override
    public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            double randomNumberY = world.getHeight(Heightmap.Types.WORLD_SURFACE, (int) entity.getX(), (int) entity.getZ());
            player.teleportTo(entity.getX(), randomNumberY + 30, entity.getZ());
            player.removeEffect(BlazinBlazeTwitchMod.RANDOM_TELEPORT);
            player.removeEffect(MobEffects.SLOW_FALLING);
            player.teleportTo(entity.getX(), randomNumberY, entity.getZ());
        }

        return super.applyEffectTick(world, entity, amplifier);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }
}
