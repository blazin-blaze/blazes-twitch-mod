package net.blazinblaze.blazetwitchmod.entity.custom;

import net.blazinblaze.blazetwitchmod.block.BlazinBlazeTwitchModBlocks;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.util.NuclearExplosionBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.NbtOps;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NuclearTntEntity extends PrimedTnt {
    private static final EntityDataAccessor<Integer> FUSE;
    private static final EntityDataAccessor<BlockState> BLOCK_STATE;
    private static final short DEFAULT_FUSE = 80;
    private static final float DEFAULT_EXPLOSION_POWER = 200.0F;
    private static final BlockState DEFAULT_BLOCK_STATE;
    private static final String BLOCK_STATE_NBT_KEY = "block_state";
    public static final String FUSE_NBT_KEY = "fuse";
    private static final String EXPLOSION_POWER_NBT_KEY = "explosion_power";
    private static final NuclearExplosionBehavior TELEPORTED_EXPLOSION_BEHAVIOR;
    @Nullable
    private LivingEntity causingEntity;
    private boolean teleported;
    private float explosionPower;

    public NuclearTntEntity(EntityType<? extends PrimedTnt> entityType, Level world) {
        super(entityType, world);
        this.explosionPower = 200.0F;
        this.blocksBuilding = true;
    }

    public NuclearTntEntity(Level world, double x, double y, double z, @Nullable LivingEntity igniter) {
        this(BlazinBlazeTwitchModEntities.NUCLEAR_TNT_ENTITY, world);
        this.setPos(x, y, z);
        double d = world.getRandom().nextDouble() * (double)((float)Math.PI * 2F);
        this.setDeltaMovement(-Math.sin(d) * 0.02, (double)0.2F, -Math.cos(d) * 0.02);
        this.setFuse(80);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.causingEntity = igniter;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FUSE, 80);
        builder.define(BLOCK_STATE, DEFAULT_BLOCK_STATE);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        this.handlePortal();
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.applyEffectsFromBlocks();
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, (double)-0.5F, 0.7));
        }

        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0) {
            this.discard();
            if (!this.level().isClientSide()) {
                this.explode();
            }
        } else {
            this.updateFluidInteraction();
            if (this.level().isClientSide()) {
                this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + (double)0.5F, this.getZ(), (double)0.0F, (double)0.0F, (double)0.0F);
            }
        }

    }

    private void explode() {
        Level var2 = this.level();
        if (var2 instanceof ServerLevel serverWorld) {
            if (serverWorld.getGameRules().get(GameRules.TNT_EXPLODES)) {
                this.level().explode(this, Explosion.getDefaultDamageSource(this.level(), this), this.teleported ? TELEPORTED_EXPLOSION_BEHAVIOR : null, this.getX(), this.getY((double)0.0625F), this.getZ(), this.explosionPower, true, Level.ExplosionInteraction.TNT);
            }
        }

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putShort("fuse", (short)this.getFuse());
        nbt.store("block_state", BlockState.CODEC, this.getBlockState());
        if (this.explosionPower != 200.0F) {
            nbt.putFloat("explosion_power", this.explosionPower);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        RegistryOps<Tag> registryOps = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        this.setFuse(nbt.getShortOr("fuse", (short)80));
        this.setBlockState((BlockState)nbt.read("block_state", BlockState.CODEC).orElse(DEFAULT_BLOCK_STATE));
        this.explosionPower = Mth.clamp(nbt.getFloatOr("explosion_power", 200.0F), 0.0F, 128.0F);
    }

    @Nullable
    public LivingEntity getOwner() {
        return this.causingEntity;
    }

    @Override
    public void restoreFrom(Entity original) {
        super.restoreFrom(original);
        if (original instanceof NuclearTntEntity tntEntity) {
            this.causingEntity = tntEntity.causingEntity;
        }

    }

    @Override
    public void setFuse(int fuse) {
        this.entityData.set(FUSE, fuse);
    }

    @Override
    public int getFuse() {
        return (Integer)this.entityData.get(FUSE);
    }

    @Override
    public void setBlockState(BlockState state) {
        this.entityData.set(BLOCK_STATE, state);
    }

    @Override
    public BlockState getBlockState() {
        return (BlockState)this.entityData.get(BLOCK_STATE);
    }

    private void setTeleported(boolean teleported) {
        this.teleported = teleported;
    }

    @Nullable
    public Entity teleport(TeleportTransition teleportTarget) {
        Entity entity = super.teleport(teleportTarget);
        if (entity instanceof NuclearTntEntity tntEntity) {
            tntEntity.setTeleported(true);
        }

        return entity;
    }

    static {
        FUSE = SynchedEntityData.defineId(NuclearTntEntity.class, EntityDataSerializers.INT);
        BLOCK_STATE = SynchedEntityData.defineId(NuclearTntEntity.class, EntityDataSerializers.BLOCK_STATE);
        DEFAULT_BLOCK_STATE = BlazinBlazeTwitchModBlocks.NUCLEAR_TNT.defaultBlockState();
        TELEPORTED_EXPLOSION_BEHAVIOR = new NuclearExplosionBehavior() {
            public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float power) {
                return state.is(Blocks.NETHER_PORTAL) ? false : super.shouldBlockExplode(explosion, world, pos, state, power);
            }

            public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                return blockState.is(Blocks.NETHER_PORTAL) ? Optional.empty() : super.getBlockExplosionResistance(explosion, world, pos, blockState, fluidState);
            }
        };
    }
}
