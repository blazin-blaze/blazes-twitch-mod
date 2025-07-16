package net.blazinblaze.blazetwitchmod.entity.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.UUID;

public class BeddyEntity extends PathAwareEntity implements Leashable {
    private static final TrackedData<Integer> DESTRUCTION_TIMER = DataTracker.registerData(BeddyEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<String> USER = DataTracker.registerData(BeddyEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> UUID = DataTracker.registerData(BeddyEntity.class, TrackedDataHandlerRegistry.STRING);
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private Text label = Text.literal("§8§lBeddy");

    public BeddyEntity(World world) {
        this(BlazinBlazeTwitchModEntities.BEDDY_ENTITY, world);
        this.setCustomNameVisible(true);
    }

    public BeddyEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setCustomNameVisible(true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 2.0f));
    }

    public static DefaultAttributeContainer.Builder createCubeAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1000D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25F)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 2F);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(this.getWorld().isClient()) {
            setupAnimationStates();
        }

        int destructionTimer = getDestructionTimer();

        if(destructionTimer >= 1200) {
            Entity leashedEntity = this.getLeashHolder();
            if(leashedEntity != null) {
                if(leashedEntity instanceof PlayerEntity plr) {
                    plr.removeStatusEffect(StatusEffects.SLOWNESS);
                    plr.removeStatusEffect(StatusEffects.BLINDNESS);
                }
            }
            this.remove(RemovalReason.DISCARDED);
        } else {
            setDestructionTimer(destructionTimer + 1);
            destructionTimer = getDestructionTimer();
            double timeToSubstract = Math.floor((double) destructionTimer / 20);
            double time = (60 - timeToSubstract);
            label = Text.literal(String.format("§8§l%s's Beddy - §r", getUser()) + String.format(String.valueOf(time)));
            Entity leashedEntity = this.getLeashHolder();
            if(leashedEntity != null) {
                if(leashedEntity instanceof PlayerEntity plr) {
                    plr.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20*getDestructionTimer(), 255));
                    plr.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20*getDestructionTimer()));
                    this.attachLeash(plr, true);
                    if(this.distanceTo(leashedEntity) >= 12) {
                        this.teleport(leashedEntity.getX(), leashedEntity.getY() + 10, leashedEntity.getZ(), false);
                    }
                }
            }else {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;
                if(server != null) {
                    PlayerManager manager = server.getPlayerManager();
                    ServerPlayerEntity player = manager.getPlayer(java.util.UUID.fromString(getUUID()));
                    if(player != null && !player.isDisconnected()) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20*getDestructionTimer(), 255));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20*getDestructionTimer()));
                        this.attachLeash(player, true);
                        if(this.distanceTo(player) >= 12) {
                            this.teleport(player.getX(), player.getY() + 10, player.getZ(), false);
                        }
                    }
                }
            }
        }
    }

    public int getDestructionTimer() {
        return this.dataTracker.get(DESTRUCTION_TIMER);
    }

    public void setDestructionTimer(int destructionProgress) {
        this.dataTracker.set(DESTRUCTION_TIMER, destructionProgress);
    }

    public String getUser() {
        return this.dataTracker.get(USER);
    }

    public void setUser(String user) {
        this.dataTracker.set(USER, user);
    }

    public String getUUID() {
        return this.dataTracker.get(UUID);
    }

    public void setUUID(String user) {
        this.dataTracker.set(UUID, user);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DESTRUCTION_TIMER, 0);
        builder.add(USER, "test");
        builder.add(UUID, "test");
    }

    @Override
    protected void writeCustomData(WriteView nbt) {
        nbt.putInt("destruction_timer", getDestructionTimer());
        nbt.putString("user", getUser());
        nbt.putString("uuid", getUUID());
    }

    @Override
    public Text getDisplayName() {
        return label;
    }

    @Override
    public boolean shouldRenderName() {
        return true;
    }

    @Override
    protected void readCustomData(ReadView nbt) {
        setDestructionTimer(nbt.getInt("destruction_timer", 0));
        setUser(nbt.getString("user", "test"));
        setUUID(nbt.getString("uuid", "test"));
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }
}
