package net.blazinblaze.blazetwitchmod.entity.custom;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.TargetUtil;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

public class ChatterEntity extends TameableEntity {
    private static final Vec3i ITEM_PICKUP_RANGE_EXPANDER = new Vec3i(1, 1, 1);
    private final SimpleInventory inventory = new SimpleInventory(1);
    private float itemHoldAnimationTicks;
    private float lastItemHoldAnimationTicks;
    private float spinningAnimationTicks;
    private float lastSpinningAnimationTicks;
    private static final TrackedData<String> USERID = DataTracker.registerData(ChatterEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> USERNAME = DataTracker.registerData(ChatterEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> CHAT_COUNT = DataTracker.registerData(ChatterEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public ChatterEntity(EntityType<? extends ChatterEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.setCanPickUpLoot(this.canPickUpLoot());
    }

    protected void initGoals() {
        this.goalSelector.add(0, new FollowOwnerGoal(this, 1.0F, 5.0F, 1.0F));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, (double)1.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAllayAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, (double)20.0F).add(EntityAttributes.FLYING_SPEED, (double)0.1F).add(EntityAttributes.MOVEMENT_SPEED, (double)0.1F).add(EntityAttributes.ATTACK_DAMAGE, (double)2.0F);
    }

    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanOpenDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setMaxFollowRange(48.0F);
        return birdNavigation;
    }

    public void travel(Vec3d movementInput) {
        this.travelFlying(movementInput, this.getMovementSpeed());
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    protected SoundEvent getAmbientSound() {
        return this.hasStackEquipped(EquipmentSlot.MAINHAND) ? SoundEvents.ENTITY_ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ALLAY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ALLAY_DEATH;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    public void tickMovement() {
        super.tickMovement();
        if (!this.getWorld().isClient && this.isAlive() && this.age % 10 == 0) {
            this.heal(1.0F);
        }
    }

    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            this.lastItemHoldAnimationTicks = this.itemHoldAnimationTicks;
            if (this.isHoldingItem()) {
                this.itemHoldAnimationTicks = MathHelper.clamp(this.itemHoldAnimationTicks + 1.0F, 0.0F, 5.0F);
            } else {
                this.itemHoldAnimationTicks = MathHelper.clamp(this.itemHoldAnimationTicks - 1.0F, 0.0F, 5.0F);
            }
        }
        if(getChatCount() > 0) {
            setChatCount(getChatCount() - 1);
        }else {
            this.setCustomName(Text.literal(String.format("§l%s", getUsername())));
        }
    }

    public boolean isHoldingItem() {
        return !this.getStackInHand(Hand.MAIN_HAND).isEmpty();
    }

    protected boolean canDispenserEquipSlot(EquipmentSlot slot) {
        return false;
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        ItemStack itemStack2 = this.getStackInHand(Hand.MAIN_HAND);
        if (itemStack2.isEmpty() && !itemStack.isEmpty()) {
            ItemStack itemStack3 = itemStack.copyWithCount(1);
            this.setStackInHand(Hand.MAIN_HAND, itemStack3);
            this.decrementStackUnlessInCreative(player, itemStack);
            this.getWorld().playSoundFromEntity(player, this, SoundEvents.ENTITY_ALLAY_ITEM_GIVEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
            return ActionResult.SUCCESS;
        } else if (!itemStack2.isEmpty() && hand == Hand.MAIN_HAND && itemStack.isEmpty()) {
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.getWorld().playSoundFromEntity(player, this, SoundEvents.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
            this.swingHand(Hand.MAIN_HAND);

            player.giveItemStack(itemStack2);
            return ActionResult.SUCCESS;
        } else {
            return super.interactMob(player, hand);
        }
    }

    protected Vec3i getItemPickUpRangeExpander() {
        return ITEM_PICKUP_RANGE_EXPANDER;
    }

    public boolean canGather(ServerWorld world, ItemStack stack) {
        ItemStack itemStack = this.getStackInHand(Hand.MAIN_HAND);
        return !itemStack.isEmpty() && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && this.inventory.canInsert(stack) && this.areItemsEqual(itemStack, stack);
    }

    private boolean areItemsEqual(ItemStack stack, ItemStack stack2) {
        return ItemStack.areItemsEqual(stack, stack2) && !this.areDifferentPotions(stack, stack2);
    }

    private boolean areDifferentPotions(ItemStack stack, ItemStack stack2) {
        PotionContentsComponent potionContentsComponent = (PotionContentsComponent)stack.get(DataComponentTypes.POTION_CONTENTS);
        PotionContentsComponent potionContentsComponent2 = (PotionContentsComponent)stack2.get(DataComponentTypes.POTION_CONTENTS);
        return !Objects.equals(potionContentsComponent, potionContentsComponent2);
    }

    public boolean isFlappingWings() {
        return !this.isOnGround();
    }

    public float getItemHoldAnimationTicks(float tickProgress) {
        return MathHelper.lerp(tickProgress, this.lastItemHoldAnimationTicks, this.itemHoldAnimationTicks) / 5.0F;
    }

    public float getSpinningAnimationTicks(float tickProgress) {
        return MathHelper.lerp(tickProgress, this.lastSpinningAnimationTicks, this.spinningAnimationTicks) / 15.0F;
    }

    public boolean areItemsDifferent(ItemStack stack, ItemStack stack2) {
        return !this.areItemsEqual(stack, stack2);
    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    public String getUserId() {
        return this.dataTracker.get(USERID);
    }

    public void setUserId(String user) {
        this.dataTracker.set(USERID, user);
    }

    public String getUsername() {
        return this.dataTracker.get(USERNAME);
    }

    public void setUsername(String user) {
        this.dataTracker.set(USERNAME, user);
    }

    private int getChatCount() {
        return this.dataTracker.get(CHAT_COUNT);
    }

    private void setChatCount(int count) {
        this.dataTracker.set(CHAT_COUNT, count);
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(USERID, "test");
        builder.add(USERNAME, "test");
        builder.add(CHAT_COUNT, 0);
    }

    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putString("userId", getUserId());
        view.putString("username", getUsername());
        view.putInt("chat_count", getChatCount());
    }

    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        setUserId(view.getString("userId", "test"));
        setUsername(view.getString("username", "test"));
        setChatCount(view.getInt("chat_count", 0));
    }

    protected boolean shouldFollowLeash() {
        return false;
    }

    private void decrementStackUnlessInCreative(PlayerEntity player, ItemStack stack) {
        stack.decrementUnlessCreative(1, player);
    }

    public Vec3d getLeashOffset() {
        return new Vec3d((double)0.0F, (double)this.getStandingEyeHeight() * 0.6, (double)this.getWidth() * 0.1);
    }

    public boolean isDancing() {
        return false;
    }

    public boolean isSpinning() {
        return false;
    }

    public void handleStatus(byte status) {
        if (status == 18) {
            for(int i = 0; i < 3; ++i) {
                this.addHeartParticle();
            }
        } else {
            super.handleStatus(status);
        }

    }

    private void addHeartParticle() {
        double d = this.random.nextGaussian() * 0.02;
        double e = this.random.nextGaussian() * 0.02;
        double f = this.random.nextGaussian() * 0.02;
        this.getWorld().addParticleClient(ParticleTypes.HEART, this.getParticleX((double)1.0F), this.getRandomBodyY() + (double)0.5F, this.getParticleZ((double)1.0F), d, e, f);
    }

    public void setHeldItem(ItemStack stack) {
        ItemStack itemStack2 = this.getStackInHand(Hand.MAIN_HAND);
        if(!itemStack2.isEmpty()) {
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            if(this.getOwner() != null) {
                this.getWorld().playSoundFromEntity(this.getOwner(), this, SoundEvents.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
            }
            this.swingHand(Hand.MAIN_HAND);

            if(this.getWorld() instanceof ServerWorld serverWorld) {
                this.dropStack(serverWorld, itemStack2);
            }
        }
        this.setStackInHand(Hand.MAIN_HAND, stack.copyWithCount(1));
        this.playSound(SoundEvents.ITEM_BOOK_PUT);
    }

    public void setChat(String msg) {
        this.setCustomName(Text.literal(String.format("§l%s§r - %s", getUsername(), msg)));
        if(this.getOwner() != null) {
            this.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BIT.value());
        }
        this.setChatCount(10*20);
    }
}
