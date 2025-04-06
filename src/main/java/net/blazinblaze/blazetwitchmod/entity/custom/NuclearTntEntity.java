package net.blazinblaze.blazetwitchmod.entity.custom;

import net.blazinblaze.blazetwitchmod.block.BlazinBlazeTwitchModBlocks;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.util.NuclearExplosionBehavior;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NuclearTntEntity extends TntEntity {
    private static final TrackedData<Integer> FUSE;
    private static final TrackedData<BlockState> BLOCK_STATE;
    private static final short DEFAULT_FUSE = 160;
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

    public NuclearTntEntity(EntityType<? extends TntEntity> entityType, World world) {
        super(entityType, world);
        this.explosionPower = 200.0F;
        this.intersectionChecked = true;
    }

    public NuclearTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        this(BlazinBlazeTwitchModEntities.NUCLEAR_TNT_ENTITY, world);
        this.setPosition(x, y, z);
        double d = world.random.nextDouble() * (double)((float)Math.PI * 2F);
        this.setVelocity(-Math.sin(d) * 0.02, (double)0.2F, -Math.cos(d) * 0.02);
        this.setFuse(160);
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
        this.causingEntity = igniter;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FUSE, 160);
        builder.add(BLOCK_STATE, DEFAULT_BLOCK_STATE);
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    protected double getGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        this.tickPortalTeleportation();
        this.applyGravity();
        this.move(MovementType.SELF, this.getVelocity());
        this.tickBlockCollision();
        this.setVelocity(this.getVelocity().multiply(0.98));
        if (this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(0.7, (double)-0.5F, 0.7));
        }

        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0) {
            this.discard();
            if (!this.getWorld().isClient) {
                this.explode();
            }
        } else {
            this.updateWaterState();
            if (this.getWorld().isClient) {
                this.getWorld().addParticleClient(ParticleTypes.SMOKE, this.getX(), this.getY() + (double)0.5F, this.getZ(), (double)0.0F, (double)0.0F, (double)0.0F);
            }
        }

    }

    private void explode() {
        World var2 = this.getWorld();
        if (var2 instanceof ServerWorld serverWorld) {
            if (serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
                this.getWorld().createExplosion(this, Explosion.createDamageSource(this.getWorld(), this), this.teleported ? TELEPORTED_EXPLOSION_BEHAVIOR : null, this.getX(), this.getBodyY((double)0.0625F), this.getZ(), this.explosionPower, true, World.ExplosionSourceType.TNT);
            }
        }

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        RegistryOps<NbtElement> registryOps = this.getRegistryManager().getOps(NbtOps.INSTANCE);
        nbt.putShort("fuse", (short)this.getFuse());
        nbt.put("block_state", BlockState.CODEC, registryOps, this.getBlockState());
        if (this.explosionPower != 200.0F) {
            nbt.putFloat("explosion_power", this.explosionPower);
        }

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        RegistryOps<NbtElement> registryOps = this.getRegistryManager().getOps(NbtOps.INSTANCE);
        this.setFuse(nbt.getShort("fuse", (short)80));
        this.setBlockState((BlockState)nbt.get("block_state", BlockState.CODEC, registryOps).orElse(DEFAULT_BLOCK_STATE));
        this.explosionPower = MathHelper.clamp(nbt.getFloat("explosion_power", 200.0F), 0.0F, 128.0F);
    }

    @Nullable
    public LivingEntity getOwner() {
        return this.causingEntity;
    }

    @Override
    public void copyFrom(Entity original) {
        super.copyFrom(original);
        if (original instanceof NuclearTntEntity tntEntity) {
            this.causingEntity = tntEntity.causingEntity;
        }

    }

    @Override
    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    @Override
    public int getFuse() {
        return (Integer)this.dataTracker.get(FUSE);
    }

    @Override
    public void setBlockState(BlockState state) {
        this.dataTracker.set(BLOCK_STATE, state);
    }

    @Override
    public BlockState getBlockState() {
        return (BlockState)this.dataTracker.get(BLOCK_STATE);
    }

    private void setTeleported(boolean teleported) {
        this.teleported = teleported;
    }

    @Nullable
    public Entity teleportTo(TeleportTarget teleportTarget) {
        Entity entity = super.teleportTo(teleportTarget);
        if (entity instanceof NuclearTntEntity tntEntity) {
            tntEntity.setTeleported(true);
        }

        return entity;
    }

    static {
        FUSE = DataTracker.registerData(NuclearTntEntity.class, TrackedDataHandlerRegistry.INTEGER);
        BLOCK_STATE = DataTracker.registerData(NuclearTntEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
        DEFAULT_BLOCK_STATE = BlazinBlazeTwitchModBlocks.NUCLEAR_TNT.getDefaultState();
        TELEPORTED_EXPLOSION_BEHAVIOR = new NuclearExplosionBehavior() {
            public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
                return state.isOf(Blocks.NETHER_PORTAL) ? false : super.canDestroyBlock(explosion, world, pos, state, power);
            }

            public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                return blockState.isOf(Blocks.NETHER_PORTAL) ? Optional.empty() : super.getBlastResistance(explosion, world, pos, blockState, fluidState);
            }
        };
    }
}
