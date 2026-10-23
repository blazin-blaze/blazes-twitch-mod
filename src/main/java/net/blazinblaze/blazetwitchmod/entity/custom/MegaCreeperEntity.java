package net.blazinblaze.blazetwitchmod.entity.custom;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MegaCreeperEntity extends Creeper {
    private static final EntityDataAccessor<Integer> FUSE_SPEED;
    private static final EntityDataAccessor<Boolean> CHARGED;
    private static final EntityDataAccessor<Boolean> IGNITED;
    private static final boolean DEFAULT_CHARGED = false;
    private static final boolean DEFAULT_IGNITED = false;
    private static final short DEFAULT_FUSE = 20;
    private static final int DEFAULT_EXPLOSION_RADIUS = 200;
    private int lastFuseTime;
    private int currentFuseTime;
    private int fuseTime = 20;
    private int explosionRadius = 200;
    private int headsDropped;

    public MegaCreeperEntity(EntityType<? extends Creeper> entityType, Level world) {
        super(entityType, world);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Ocelot.class, 6.0F, (double)1.0F, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Cat.class, 6.0F, (double)1.0F, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, (double)1.0F, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
    }

    public static AttributeSupplier.Builder createCreeperAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double)1F);
    }

    public int getMaxFallDistance() {
        return this.getTarget() == null ? this.getComfortableFallDistance(0.0F) : this.getComfortableFallDistance(this.getHealth() - 1.0F);
    }

    public boolean causeFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource) {
        boolean bl = super.causeFallDamage(fallDistance, damagePerDistance, damageSource);
        this.currentFuseTime += (int)(fallDistance * (double)1.5F);
        if (this.currentFuseTime > this.fuseTime - 5) {
            this.currentFuseTime = this.fuseTime - 5;
        }

        return bl;
    }

    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FUSE_SPEED, -1);
        builder.define(CHARGED, false);
        builder.define(IGNITED, false);
    }

    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("powered", this.isPowered());
        nbt.putShort("Fuse", (short)this.fuseTime);
        nbt.putInt("ExplosionRadius", this.explosionRadius);
        nbt.putBoolean("ignited", this.isIgnited());
    }

    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        this.entityData.set(CHARGED, nbt.getBooleanOr("powered", false));
        this.fuseTime = nbt.getShortOr("Fuse", (short)20);
        this.explosionRadius = nbt.getIntOr("ExplosionRadius", 200);
        if (nbt.getBooleanOr("ignited", false)) {
            this.ignite();
        }

    }

    public void tick() {
        if (this.isAlive()) {
            this.lastFuseTime = this.currentFuseTime;
            if (this.isIgnited()) {
                this.setSwellDir(1);
            }

            int i = this.getSwellDir();
            if (i > 0 && this.currentFuseTime == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
                this.gameEvent(GameEvent.PRIME_FUSE);
            }

            this.currentFuseTime += i;
            if (this.currentFuseTime < 0) {
                this.currentFuseTime = 0;
            }

            if (this.currentFuseTime >= this.fuseTime) {
                this.currentFuseTime = this.fuseTime;
                this.explode();
            }
        }

        super.tick();
    }

    public void setTarget(@Nullable LivingEntity target) {
        if (!(target instanceof Goat)) {
            super.setTarget(target);
        }
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.CREEPER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }

    protected void dropCustomDeathLoot(ServerLevel world, DamageSource source, boolean causedByPlayer) {
        super.dropCustomDeathLoot(world, source, causedByPlayer);
        Entity entity = source.getEntity();
        if (entity != this && entity instanceof MegaCreeperEntity creeperEntity) {
            if (creeperEntity.shouldDropHead()) {
                creeperEntity.onHeadDropped();
                this.spawnAtLocation(world, Items.CREEPER_HEAD);
            }
        }

    }

    public boolean doHurtTarget(ServerLevel world, Entity target) {
        return true;
    }

    public boolean isPowered() {
        return (Boolean)this.entityData.get(CHARGED);
    }

    public float getSwelling(float tickProgress) {
        return Mth.lerp(tickProgress, (float)this.lastFuseTime, (float)this.currentFuseTime) / (float)(this.fuseTime - 2);
    }

    public int getSwellDir() {
        return (Integer)this.entityData.get(FUSE_SPEED);
    }

    public void setSwellDir(int fuseSpeed) {
        this.entityData.set(FUSE_SPEED, fuseSpeed);
    }

    public void thunderHit(ServerLevel world, LightningBolt lightning) {
        super.thunderHit(world, lightning);
        this.entityData.set(CHARGED, true);
    }

    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(ItemTags.CREEPER_IGNITERS)) {
            SoundEvent soundEvent = itemStack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level().isClientSide()) {
                this.ignite();
                if (!itemStack.isDamageableItem()) {
                    itemStack.shrink(1);
                } else {
                    itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                }
            }

            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(player, hand);
        }
    }

    private void explode() {
        Level f = this.level();
        if (f instanceof ServerLevel serverWorld) {
            float f0 = this.isPowered() ? 2.0F : 1.0F;
            this.dead = true;
            serverWorld.explode(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * f0, Level.ExplosionInteraction.MOB);
            this.spawnEffectsCloud();
            this.triggerOnDeathMobEffects(serverWorld, RemovalReason.KILLED);
            this.discard();
        }

    }

    private void spawnEffectsCloud() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            areaEffectCloudEntity.setRadius(2.5F);
            areaEffectCloudEntity.setRadiusOnUse(-0.5F);
            areaEffectCloudEntity.setWaitTime(10);
            areaEffectCloudEntity.setDuration(300);
            areaEffectCloudEntity.setPotionDurationScale(0.25F);
            areaEffectCloudEntity.setRadiusPerTick(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());

            for(MobEffectInstance statusEffectInstance : collection) {
                areaEffectCloudEntity.addEffect(new MobEffectInstance(statusEffectInstance));
            }

            this.level().addFreshEntity(areaEffectCloudEntity);
        }

    }

    public boolean isIgnited() {
        return (Boolean)this.entityData.get(IGNITED);
    }

    public void ignite() {
        this.entityData.set(IGNITED, true);
    }

    public boolean shouldDropHead() {
        return this.isPowered() && this.headsDropped < 1;
    }

    public void onHeadDropped() {
        ++this.headsDropped;
    }

    static {
        FUSE_SPEED = SynchedEntityData.defineId(MegaCreeperEntity.class, EntityDataSerializers.INT);
        CHARGED = SynchedEntityData.defineId(MegaCreeperEntity.class, EntityDataSerializers.BOOLEAN);
        IGNITED = SynchedEntityData.defineId(MegaCreeperEntity.class, EntityDataSerializers.BOOLEAN);
    }
}
