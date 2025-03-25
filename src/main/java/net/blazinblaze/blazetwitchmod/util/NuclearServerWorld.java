package net.blazinblaze.blazetwitchmod.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.SpecialSpawner;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

public class NuclearServerWorld extends ServerWorld {
    public NuclearServerWorld(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<SpecialSpawner> spawners, boolean shouldTickTime, @Nullable RandomSequencesState randomSequencesState) {
        super(server, workerExecutor, session, properties, worldKey, dimensionOptions, worldGenerationProgressListener, debugWorld, seed, spawners, shouldTickTime, randomSequencesState);
    }

    @Override
    public void createExplosion(
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            World.ExplosionSourceType explosionSourceType,
            ParticleEffect smallParticle,
            ParticleEffect largeParticle,
            RegistryEntry<SoundEvent> soundEvent
    ) {
        Explosion.DestructionType destructionType = switch (explosionSourceType) {
            case NONE -> Explosion.DestructionType.KEEP;
            case BLOCK -> this.getDestructionType(GameRules.BLOCK_EXPLOSION_DROP_DECAY);
            case MOB -> this.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
                    ? this.getDestructionType(GameRules.MOB_EXPLOSION_DROP_DECAY)
                    : Explosion.DestructionType.KEEP;
            case TNT -> this.getDestructionType(GameRules.TNT_EXPLOSION_DROP_DECAY);
            case TRIGGER -> Explosion.DestructionType.TRIGGER_BLOCK;
        };
        Vec3d vec3d = new Vec3d(x, y, z);
        NuclearExplosionImpl explosionImpl = new NuclearExplosionImpl(this, entity, damageSource, behavior, vec3d, power, createFire, destructionType);
        explosionImpl.explode();
        ParticleEffect particleEffect = explosionImpl.isSmall() ? smallParticle : largeParticle;

        for (ServerPlayerEntity serverPlayerEntity : this.getPlayers()) {
            if (serverPlayerEntity.squaredDistanceTo(vec3d) < 4096.0) {
                Optional<Vec3d> optional = Optional.ofNullable((Vec3d)explosionImpl.getKnockbackByPlayer().get(serverPlayerEntity));
                serverPlayerEntity.networkHandler.sendPacket(new ExplosionS2CPacket(vec3d, optional, particleEffect, soundEvent));
            }
        }
    }

    private Explosion.DestructionType getDestructionType(GameRules.Key<GameRules.BooleanRule> decayRule) {
        return this.getGameRules().getBoolean(decayRule) ? Explosion.DestructionType.DESTROY_WITH_DECAY : Explosion.DestructionType.DESTROY;
    }
}
