package net.blazinblaze.blazetwitchmod.entity.custom;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

public class ChatterEntity extends TamableAnimal {
    private static final Vec3i ITEM_PICKUP_RANGE_EXPANDER = new Vec3i(1, 1, 1);
    private final SimpleContainer inventory = new SimpleContainer(1);
    private float itemHoldAnimationTicks;
    private float lastItemHoldAnimationTicks;
    private float spinningAnimationTicks;
    private float lastSpinningAnimationTicks;
    private static final EntityDataAccessor<String> USERID = SynchedEntityData.defineId(ChatterEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> USERNAME = SynchedEntityData.defineId(ChatterEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> CHAT_COUNT = SynchedEntityData.defineId(ChatterEntity.class, EntityDataSerializers.INT);

    public ChatterEntity(EntityType<? extends ChatterEntity> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setCanPickUpLoot(this.canPickUpLoot());
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FollowOwnerGoal(this, 1.0F, 5.0F, 1.0F));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, (double)1.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAllayAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, (double)20.0F).add(Attributes.FLYING_SPEED, (double)0.1F).add(Attributes.MOVEMENT_SPEED, (double)0.1F).add(Attributes.ATTACK_DAMAGE, (double)2.0F);
    }

    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation birdNavigation = new FlyingPathNavigation(this, world);
        birdNavigation.setCanOpenDoors(false);
        birdNavigation.setCanFloat(true);
        birdNavigation.setRequiredPathLength(48.0F);
        return birdNavigation;
    }

    public void travel(Vec3 movementInput) {
        this.travelFlying(movementInput, this.getSpeed());
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    protected SoundEvent getAmbientSound() {
        return this.hasItemInSlot(EquipmentSlot.MAINHAND) ? SoundEvents.ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.isAlive() && this.tickCount % 10 == 0) {
            this.heal(1.0F);
        }
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.lastItemHoldAnimationTicks = this.itemHoldAnimationTicks;
            if (this.isHoldingItem()) {
                this.itemHoldAnimationTicks = Mth.clamp(this.itemHoldAnimationTicks + 1.0F, 0.0F, 5.0F);
            } else {
                this.itemHoldAnimationTicks = Mth.clamp(this.itemHoldAnimationTicks - 1.0F, 0.0F, 5.0F);
            }
        }
        if(getChatCount() > 0) {
            setChatCount(getChatCount() - 1);
        }else {
            this.setCustomName(Component.literal(String.format("§l%s", getUsername())));
        }
    }

    public boolean isHoldingItem() {
        return !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

    protected boolean canDispenserEquipIntoSlot(EquipmentSlot slot) {
        return false;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack itemStack2 = this.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack2.isEmpty() && !itemStack.isEmpty()) {
            ItemStack itemStack3 = itemStack.copyWithCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, itemStack3);
            this.decrementStackUnlessInCreative(player, itemStack);
            this.level().playSound(player, this, SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
            return InteractionResult.SUCCESS;
        } else if (!itemStack2.isEmpty() && hand == InteractionHand.MAIN_HAND && itemStack.isEmpty()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.level().playSound(player, this, SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
            this.swing(InteractionHand.MAIN_HAND);

            player.addItem(itemStack2);
            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(player, hand);
        }
    }

    protected Vec3i getPickupReach() {
        return ITEM_PICKUP_RANGE_EXPANDER;
    }

    public boolean wantsToPickUp(ServerLevel world, ItemStack stack) {
        ItemStack itemStack = this.getItemInHand(InteractionHand.MAIN_HAND);
        return !itemStack.isEmpty() && world.getGameRules().get(GameRules.MOB_GRIEFING) && this.inventory.canAddItem(stack) && this.areItemsEqual(itemStack, stack);
    }

    private boolean areItemsEqual(ItemStack stack, ItemStack stack2) {
        return ItemStack.isSameItem(stack, stack2) && !this.areDifferentPotions(stack, stack2);
    }

    private boolean areDifferentPotions(ItemStack stack, ItemStack stack2) {
        PotionContents potionContentsComponent = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
        PotionContents potionContentsComponent2 = (PotionContents)stack2.get(DataComponents.POTION_CONTENTS);
        return !Objects.equals(potionContentsComponent, potionContentsComponent2);
    }

    public boolean isFlapping() {
        return !this.onGround();
    }

    public float getItemHoldAnimationTicks(float tickProgress) {
        return Mth.lerp(tickProgress, this.lastItemHoldAnimationTicks, this.itemHoldAnimationTicks) / 5.0F;
    }

    public float getSpinningAnimationTicks(float tickProgress) {
        return Mth.lerp(tickProgress, this.lastSpinningAnimationTicks, this.spinningAnimationTicks) / 15.0F;
    }

    public boolean equipmentHasChanged(ItemStack stack, ItemStack stack2) {
        return !this.areItemsEqual(stack, stack2);
    }

    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    public String getUserId() {
        return this.entityData.get(USERID);
    }

    public void setUserId(String user) {
        this.entityData.set(USERID, user);
    }

    public String getUsername() {
        return this.entityData.get(USERNAME);
    }

    public void setUsername(String user) {
        this.entityData.set(USERNAME, user);
    }

    private int getChatCount() {
        return this.entityData.get(CHAT_COUNT);
    }

    private void setChatCount(int count) {
        this.entityData.set(CHAT_COUNT, count);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
        return null;
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(USERID, "test");
        builder.define(USERNAME, "test");
        builder.define(CHAT_COUNT, 0);
    }

    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putString("userId", getUserId());
        view.putString("username", getUsername());
        view.putInt("chat_count", getChatCount());
    }

    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        setUserId(view.getStringOr("userId", "test"));
        setUsername(view.getStringOr("username", "test"));
        setChatCount(view.getIntOr("chat_count", 0));
    }

    protected boolean shouldStayCloseToLeashHolder() {
        return false;
    }

    private void decrementStackUnlessInCreative(Player player, ItemStack stack) {
        stack.consume(1, player);
    }

    public Vec3 getLeashOffset() {
        return new Vec3((double)0.0F, (double)this.getEyeHeight() * 0.6, (double)this.getBbWidth() * 0.1);
    }

    public boolean isDancing() {
        return false;
    }

    public boolean isSpinning() {
        return false;
    }

    public void handleEntityEvent(byte status) {
        if (status == 18) {
            for(int i = 0; i < 3; ++i) {
                this.addHeartParticle();
            }
        } else {
            super.handleEntityEvent(status);
        }

    }

    private void addHeartParticle() {
        double d = this.random.nextGaussian() * 0.02;
        double e = this.random.nextGaussian() * 0.02;
        double f = this.random.nextGaussian() * 0.02;
        this.level().addParticle(ParticleTypes.HEART, this.getRandomX((double)1.0F), this.getRandomY() + (double)0.5F, this.getRandomZ((double)1.0F), d, e, f);
    }

    public void setHeldItem(ItemStack stack) {
        ItemStack itemStack2 = this.getItemInHand(InteractionHand.MAIN_HAND);
        if(!itemStack2.isEmpty()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            if(this.getOwner() != null) {
                this.level().playSound(this.getOwner(), this, SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
            }
            this.swing(InteractionHand.MAIN_HAND);

            if(this.level() instanceof ServerLevel serverWorld) {
                this.spawnAtLocation(serverWorld, itemStack2);
            }
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, stack.copyWithCount(1));
        this.makeSound(SoundEvents.BOOK_PUT);
    }

    public void setChat(String msg) {
        this.setCustomName(Component.literal(String.format("§l%s§r - %s", getUsername(), msg)));
        if(this.getOwner() != null) {
            this.makeSound(SoundEvents.NOTE_BLOCK_BIT.value());
        }
        this.setChatCount(10*20);
    }
}
