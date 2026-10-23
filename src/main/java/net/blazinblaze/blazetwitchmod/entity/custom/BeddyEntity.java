package net.blazinblaze.blazetwitchmod.entity.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class BeddyEntity extends PathfinderMob implements Leashable {
    private static final EntityDataAccessor<Integer> DESTRUCTION_TIMER = SynchedEntityData.defineId(BeddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> USER = SynchedEntityData.defineId(BeddyEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> UUID = SynchedEntityData.defineId(BeddyEntity.class, EntityDataSerializers.STRING);
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private Component label = Component.literal("§8§lBeddy");

    public BeddyEntity(Level world) {
        this(BlazinBlazeTwitchModEntities.BEDDY_ENTITY, world);
        this.setCustomNameVisible(true);
    }

    public BeddyEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.setCustomNameVisible(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 2.0f));
    }

    public static AttributeSupplier.Builder createCubeAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 2F);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(this.level().isClientSide()) {
            setupAnimationStates();
        }

        int destructionTimer = getDestructionTimer();

        if(destructionTimer >= 1200) {
            Entity leashedEntity = this.getLeashHolder();
            if(leashedEntity != null) {
                if(leashedEntity instanceof Player plr) {
                    plr.removeEffect(MobEffects.SLOWNESS);
                    plr.removeEffect(MobEffects.BLINDNESS);
                }
            }
            this.remove(RemovalReason.DISCARDED);
        } else {
            setDestructionTimer(destructionTimer + 1);
            destructionTimer = getDestructionTimer();
            double timeToSubstract = Math.floor((double) destructionTimer / 20);
            double time = (60 - timeToSubstract);
            label = Component.literal(String.format("§8§l%s's Beddy - §r", getUser()) + String.format(String.valueOf(time)));
            Entity leashedEntity = this.getLeashHolder();
            if(leashedEntity != null) {
                if(leashedEntity instanceof Player plr) {
                    plr.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, -1, 255));
                    plr.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, -1));
                    this.setLeashedTo(plr, true);
                    //if(this.distanceTo(leashedEntity) >= 12.0F) {
                        //this.teleport(leashedEntity.getX(), leashedEntity.getY() + 10, leashedEntity.getZ(), false);
                    //}
                }
            }else {
                MinecraftServer server = BlazinBlazeTwitchMod.serverRef;
                if(server != null) {
                    PlayerList manager = server.getPlayerList();
                    ServerPlayer player = manager.getPlayer(java.util.UUID.fromString(getPlayerUUID()));
                    if(player != null && !player.hasDisconnected()) {
                        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, -1, 255));
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, -1));
                        this.setLeashedTo(player, true);
                        //if(this.distanceTo(player) >= 12.0F) {
                            //this.teleport(player.getX(), player.getY() + 10, player.getZ(), false);
                        //}
                    }
                }
            }
        }
    }

    @Override
    public double leashSnapDistance() {
        return (double)100000000000.0F;
    }

    public int getDestructionTimer() {
        return this.entityData.get(DESTRUCTION_TIMER);
    }

    public void setDestructionTimer(int destructionProgress) {
        this.entityData.set(DESTRUCTION_TIMER, destructionProgress);
    }

    public String getUser() {
        return this.entityData.get(USER);
    }

    public void setUser(String user) {
        this.entityData.set(USER, user);
    }

    public String getPlayerUUID() {
        return this.entityData.get(UUID);
    }

    public void setUUID(String user) {
        this.entityData.set(UUID, user);
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DESTRUCTION_TIMER, 0);
        builder.define(USER, "test");
        builder.define(UUID, "test");
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput nbt) {
        nbt.putInt("destruction_timer", getDestructionTimer());
        nbt.putString("user", getUser());
        nbt.putString("uuid", getPlayerUUID());
    }

    @Override
    public Component getDisplayName() {
        return label;
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput nbt) {
        setDestructionTimer(nbt.getIntOr("destruction_timer", 0));
        setUser(nbt.getStringOr("user", "test"));
        setUUID(nbt.getStringOr("uuid", "test"));
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        return false;
    }
}
