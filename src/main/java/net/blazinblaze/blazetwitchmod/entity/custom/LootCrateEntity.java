package net.blazinblaze.blazetwitchmod.entity.custom;

import ch.qos.logback.core.status.Status;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchModEvent;
import net.blazinblaze.blazetwitchmod.block.BlazinBlazeTwitchModBlocks;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.blazinblaze.blazetwitchmod.sound.BlazinBlazeTwitchModSounds;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.item.EnchantmentUtil;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Containers;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LootCrateEntity extends Entity { //implements InventoryOwner
    private static final EntityDataAccessor<Integer> DESTRUCTION_TIMER = SynchedEntityData.defineId(LootCrateEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_TOUCHED_GROUND = SynchedEntityData.defineId(LootCrateEntity.class, EntityDataSerializers.BOOLEAN);
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private static final Map<ItemStack, ItemRarity> LOOT_TABLE = Map.ofEntries(
            Map.entry(Items.COAL_BLOCK.getDefaultInstance(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.IRON_GOLEM_SPAWN_EGG.getDefaultInstance(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.DIAMOND_SWORD.getDefaultInstance(), new ItemRarity(0.1, false, true, "tool")),
            Map.entry(Items.ALLAY_SPAWN_EGG.getDefaultInstance(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.ANVIL.getDefaultInstance(), new ItemRarity(0.4, false, false, "none")),
            Map.entry(Items.BEACON.getDefaultInstance(), new ItemRarity(0.1, false, false, "none")),
            Map.entry(Items.DIAMOND_BLOCK.getDefaultInstance(), new ItemRarity(0.1, true, false, "none")),
            Map.entry(Items.NETHERITE_BLOCK.getDefaultInstance(), new ItemRarity(0.05, false, false, "none")),
            Map.entry(Items.GOLD_BLOCK.getDefaultInstance(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.IRON_BLOCK.getDefaultInstance(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.LAPIS_BLOCK.getDefaultInstance(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.COPPER_BLOCK.getDefaultInstance(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance(), new ItemRarity(0.1, true, false, "none")),
            Map.entry(Items.GOLDEN_APPLE.getDefaultInstance(), new ItemRarity(0.2, true, false, "none")),
            Map.entry(Items.SHULKER_BOX.getDefaultInstance(), new ItemRarity(0.1, false, false, "none")),
            Map.entry(Items.ELYTRA.getDefaultInstance(), new ItemRarity(0.01, false, true, "armor")),
            Map.entry(Items.BREAD.getDefaultInstance(), new ItemRarity(0.6, true, false, "none")),
            Map.entry(Items.COOKED_BEEF.getDefaultInstance(), new ItemRarity(0.6, true, false, "none")),
            Map.entry(Items.FIRE_CHARGE.getDefaultInstance(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.FLINT_AND_STEEL.getDefaultInstance(), new ItemRarity(0.5, false, false, "none")),
            Map.entry(Items.OBSIDIAN.getDefaultInstance(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.ENDER_CHEST.getDefaultInstance(), new ItemRarity(0.4, false, false, "none")),
            Map.entry(Items.ENDER_DRAGON_SPAWN_EGG.getDefaultInstance(), new ItemRarity(0.01, true, false, "none")),
            Map.entry(Items.WITHER_SPAWN_EGG.getDefaultInstance(), new ItemRarity(0.01, true, false, "none")),
            Map.entry(Items.ENDER_PEARL.getDefaultInstance(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.ENCHANTED_BOOK.getDefaultInstance(), new ItemRarity(0.3, false, false, "none")),
            Map.entry(Items.WOLF_SPAWN_EGG.getDefaultInstance(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.WOLF_ARMOR.getDefaultInstance(), new ItemRarity(0.4, false, true, "armor")),
            Map.entry(Items.CAT_SPAWN_EGG.getDefaultInstance(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.VILLAGER_SPAWN_EGG.getDefaultInstance(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.HORSE_SPAWN_EGG.getDefaultInstance(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.DIAMOND_HORSE_ARMOR.getDefaultInstance(), new ItemRarity(0.2, false, true, "armor")),
            Map.entry(Items.GOLDEN_HORSE_ARMOR.getDefaultInstance(), new ItemRarity(0.5, false, true, "armor")),
            Map.entry(Items.SKELETON_HORSE_SPAWN_EGG.getDefaultInstance(), new ItemRarity(0.3, true, false, "none")),
            Map.entry(Items.TOTEM_OF_UNDYING.getDefaultInstance(), new ItemRarity(0.4, false, false, "none")),
            Map.entry(Items.END_CRYSTAL.getDefaultInstance(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.EMERALD_BLOCK.getDefaultInstance(), new ItemRarity(0.3, true, false, "none")),
            Map.entry(Items.DIAMOND_PICKAXE.getDefaultInstance(), new ItemRarity(0.1, false, true, "tool")),
            Map.entry(Items.DIAMOND_AXE.getDefaultInstance(), new ItemRarity(0.1, false, true, "tool")),
            Map.entry(Items.DIAMOND_SHOVEL.getDefaultInstance(), new ItemRarity(0.1, false, true, "tool")),
            Map.entry(Items.DIAMOND_HOE.getDefaultInstance(), new ItemRarity(0.1, false, true, "tool")),
            Map.entry(Items.IRON_SWORD.getDefaultInstance(), new ItemRarity(0.3, false, true, "tool")),
            Map.entry(Items.IRON_PICKAXE.getDefaultInstance(), new ItemRarity(0.3, false, true, "tool")),
            Map.entry(Items.IRON_AXE.getDefaultInstance(), new ItemRarity(0.3, false, true, "tool")),
            Map.entry(Items.IRON_SHOVEL.getDefaultInstance(), new ItemRarity(0.3, false, true, "tool")),
            Map.entry(Items.IRON_HOE.getDefaultInstance(), new ItemRarity(0.3, false, true, "tool")),
            Map.entry(Items.NETHERITE_AXE.getDefaultInstance(), new ItemRarity(0.05, false, true, "tool")),
            Map.entry(Items.NETHERITE_HOE.getDefaultInstance(), new ItemRarity(0.05, false, true, "tool")),
            Map.entry(Items.NETHERITE_SWORD.getDefaultInstance(), new ItemRarity(0.05, false, true, "tool")),
            Map.entry(Items.NETHERITE_PICKAXE.getDefaultInstance(), new ItemRarity(0.05, false, true, "tool")),
            Map.entry(Items.NETHERITE_SHOVEL.getDefaultInstance(), new ItemRarity(0.05, false, true, "tool")),
            Map.entry(Items.IRON_HELMET.getDefaultInstance(), new ItemRarity(0.3, false, true, "armor")),
            Map.entry(Items.IRON_CHESTPLATE.getDefaultInstance(), new ItemRarity(0.3, false, true, "armor")),
            Map.entry(Items.IRON_LEGGINGS.getDefaultInstance(), new ItemRarity(0.3, false, true, "armor")),
            Map.entry(Items.IRON_BOOTS.getDefaultInstance(), new ItemRarity(0.3, false, true, "armor")),
            Map.entry(Items.DIAMOND_HELMET.getDefaultInstance(), new ItemRarity(0.1, false, true, "armor")),
            Map.entry(Items.DIAMOND_CHESTPLATE.getDefaultInstance(), new ItemRarity(0.1, false, true, "armor")),
            Map.entry(Items.DIAMOND_LEGGINGS.getDefaultInstance(), new ItemRarity(0.1, false, true, "armor")),
            Map.entry(Items.DIAMOND_BOOTS.getDefaultInstance(), new ItemRarity(0.1, false, true, "armor")),
            Map.entry(Items.NETHERITE_HELMET.getDefaultInstance(), new ItemRarity(0.05, false, true, "armor")),
            Map.entry(Items.NETHERITE_CHESTPLATE.getDefaultInstance(), new ItemRarity(0.05, false, true, "armor")),
            Map.entry(Items.NETHERITE_LEGGINGS.getDefaultInstance(), new ItemRarity(0.05, false, true, "armor")),
            Map.entry(Items.NETHERITE_BOOTS.getDefaultInstance(), new ItemRarity(0.05, false, true, "armor")),
            Map.entry(Items.POTION.getDefaultInstance(), new ItemRarity(0.5, false, false, "none")),
            Map.entry(Items.TNT.getDefaultInstance(), new ItemRarity(0.5, true, false, "none")),
            //Map.entry(BlazinBlazeTwitchModItems.BLAZE_SWORD.getDefaultStack(), new ItemRarity(0.001, false, true, "tool")),
            Map.entry(BlazinBlazeTwitchModBlocks.NUCLEAR_TNT.asItem().getDefaultInstance(), new ItemRarity(0.1, true, false, "none")),
            Map.entry(Items.BOW.getDefaultInstance(), new ItemRarity(0.5, false, true, "bow")),
            Map.entry(Items.CROSSBOW.getDefaultInstance(), new ItemRarity(0.5, false, true, "crossbow")),
            Map.entry(BlazinBlazeTwitchModItems.IMMUNITY_SHIELD_ITEM.getDefaultInstance(), new ItemRarity(0.001, false, false, "none")),
            Map.entry(BlazinBlazeTwitchModItems.FAKE_EYE_OF_ENDER.getDefaultInstance(), new ItemRarity(0.1, true, false, "none")),
            Map.entry(Items.ENDER_EYE.getDefaultInstance(), new ItemRarity(0.1, true, false, "none")));

    private static final ArrayList<ResourceKey<Enchantment>> toolEnchantments = new ArrayList<ResourceKey<Enchantment>>();
    private static final ArrayList<ResourceKey<Enchantment>> bowEnchantments = new ArrayList<ResourceKey<Enchantment>>();
    private static final ArrayList<ResourceKey<Enchantment>> crossbowEnchantments = new ArrayList<ResourceKey<Enchantment>>();
    private static final ArrayList<ResourceKey<Enchantment>> armorEnchantments = new ArrayList<ResourceKey<Enchantment>>();
    private static final ArrayList<Holder<Potion>> possiblePotions = new ArrayList<Holder<Potion>>();

    private Component label = Component.literal("§lLoot Crate");

    public LootCrateEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.setCustomNameVisible(true);

        bowEnchantments.add(Enchantments.FLAME);
        toolEnchantments.add(Enchantments.LOOTING);
        bowEnchantments.add(Enchantments.LOOTING);
        toolEnchantments.add(Enchantments.FORTUNE);
        toolEnchantments.add(Enchantments.SWEEPING_EDGE);
        toolEnchantments.add(Enchantments.BANE_OF_ARTHROPODS);
        toolEnchantments.add(Enchantments.SMITE);
        toolEnchantments.add(Enchantments.BINDING_CURSE);
        toolEnchantments.add(Enchantments.VANISHING_CURSE);
        armorEnchantments.add(Enchantments.BINDING_CURSE);
        armorEnchantments.add(Enchantments.VANISHING_CURSE);
        bowEnchantments.add(Enchantments.VANISHING_CURSE);
        toolEnchantments.add(Enchantments.VANISHING_CURSE);
        bowEnchantments.add(Enchantments.UNBREAKING);
        toolEnchantments.add(Enchantments.UNBREAKING);
        armorEnchantments.add(Enchantments.UNBREAKING);
        armorEnchantments.add(Enchantments.PROJECTILE_PROTECTION);
        bowEnchantments.add(Enchantments.INFINITY);
        toolEnchantments.add(Enchantments.KNOCKBACK);
        armorEnchantments.add(Enchantments.THORNS);
        toolEnchantments.add(Enchantments.SILK_TOUCH);
        toolEnchantments.add(Enchantments.SHARPNESS);
        bowEnchantments.add(Enchantments.PUNCH);
        toolEnchantments.add(Enchantments.EFFICIENCY);
        toolEnchantments.add(Enchantments.FIRE_ASPECT);
        armorEnchantments.add(Enchantments.PROTECTION);
        armorEnchantments.add(Enchantments.FIRE_PROTECTION);
        armorEnchantments.add(Enchantments.BLAST_PROTECTION);
        armorEnchantments.add(Enchantments.FEATHER_FALLING);
        armorEnchantments.add(Enchantments.FROST_WALKER);
        armorEnchantments.add(Enchantments.SOUL_SPEED);
        armorEnchantments.add(Enchantments.PROJECTILE_PROTECTION);
        armorEnchantments.add(Enchantments.MENDING);
        bowEnchantments.add(Enchantments.MENDING);
        toolEnchantments.add(Enchantments.MENDING);
        crossbowEnchantments.add(Enchantments.MENDING);
        crossbowEnchantments.add(Enchantments.UNBREAKING);
        crossbowEnchantments.add(Enchantments.LOOTING);
        crossbowEnchantments.add(Enchantments.MULTISHOT);
        crossbowEnchantments.add(Enchantments.QUICK_CHARGE);
        crossbowEnchantments.add(Enchantments.PIERCING);

        possiblePotions.add(Potions.FIRE_RESISTANCE);
        possiblePotions.add(Potions.INFESTED);
        possiblePotions.add(Potions.INVISIBILITY);
        possiblePotions.add(Potions.OOZING);
        possiblePotions.add(Potions.STRONG_HEALING);
        possiblePotions.add(Potions.STRONG_LEAPING);
        possiblePotions.add(Potions.STRONG_REGENERATION);
        possiblePotions.add(Potions.STRONG_SWIFTNESS);
        possiblePotions.add(Potions.STRONG_STRENGTH);
        possiblePotions.add(Potions.STRONG_TURTLE_MASTER);
        possiblePotions.add(Potions.LUCK);
        possiblePotions.add(Potions.NIGHT_VISION);
        possiblePotions.add(Potions.WATER_BREATHING);
        possiblePotions.add(Potions.WEAVING);
        possiblePotions.add(Potions.WIND_CHARGED);
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
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
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
    public boolean canCollideWith(Entity other) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.handlePortal();
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        this.isAffectedByBlocks();
        if (this.onGround()) {
            if(!getHasTouchedGround()) {
                this.playSound(BlazinBlazeTwitchModSounds.THUD_SOUND_EFFECT, 1.0F, 1.0F);
                this.level().addParticle(ParticleTypes.EXPLOSION, this.getX(), (this.getY() + .3), this.getZ(), 1.0, 1.0, 1.0);
                setHasTouchedGround(true);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
            }else {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
            }
        }

        if(this.level().isClientSide()) {
            setupAnimationStates();
        }

        int destructionTimer = getDestructionTimer();

        if(destructionTimer >= 1200) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            setDestructionTimer(destructionTimer + 1);
            destructionTimer = getDestructionTimer();
            double timeToSubstract = Math.floor((double) destructionTimer / 20);
            double time = (60 - timeToSubstract);
            label = Component.literal(String.format(String.valueOf(time)));
            //BlazinBlazeTwitchMod.LOGGER.info("NUMBER OF SECONDS: " + String.valueOf(time));
        }
        //BlazinBlazeTwitchMod.LOGGER.info("TICK!");
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand, final Vec3 location) {
        if(player.level() instanceof ServerLevel serverWorld) {
            this.playSound(SoundEvents.CHEST_OPEN, 1.0F, 0.5F);
            //this.dropStack(serverWorld, BlazinBlazeTwitchModItems.MAIL_ITEM.getDefaultStack());
            //dropLoot(serverWorld, this.getDamageSources().cramming());

            int numberOfItems = 0;

            do {

                Random random = new Random();
                List<ItemStack> list = LOOT_TABLE.keySet().stream().toList();
                ItemStack itemReference = list.get(random.nextInt(list.size()));
                ItemStack item = itemReference.copy();
                ItemRarity rarity = LOOT_TABLE.get(itemReference);
                Double threshold = rarity.threshold();

                if (ThreadLocalRandom.current().nextDouble() < threshold) {
                    boolean multiple = rarity.multiple();
                    boolean isEnchanted = rarity.isEnchantable();
                    String enchantableType = rarity.enchantableType();

                    if (multiple) {
                        if (ThreadLocalRandom.current().nextDouble() < (threshold * 1.2)) {
                            item.setCount(random.nextInt(16));
                            this.spawnAtLocation(serverWorld, item);
                            numberOfItems++;
                        } else {
                            this.spawnAtLocation(serverWorld, item);
                            numberOfItems++;
                        }
                    } else if (isEnchanted) {
                        if (ThreadLocalRandom.current().nextDouble() < 1) {
                            if (!Objects.equals(enchantableType, "none") && Objects.equals(enchantableType, "armor")) {

                                RegistryAccess registryManager = serverWorld.registryAccess();
                                ResourceKey<Enchantment> rndmEnchantment = armorEnchantments.get(random.nextInt(armorEnchantments.size()));
                                Optional<Holder.Reference<Enchantment>> enchantmentEntryTool = registryManager.lookupOrThrow(Registries.ENCHANTMENT).get(rndmEnchantment.identifier());
                                int levelTool = random.nextInt(5);

                                enchantmentEntryTool.ifPresentOrElse(value -> item.enchant(enchantmentEntryTool.get(), levelTool),
                                        () -> BlazinBlazeTwitchMod.LOGGER.info("Armor Enchantment not found!"));

                                this.spawnAtLocation(serverWorld, item);
                                numberOfItems++;
                            } else if (!Objects.equals(enchantableType, "none") && Objects.equals(enchantableType, "tool")) {

                                RegistryAccess registryManager = serverWorld.registryAccess();
                                ResourceKey<Enchantment> rndmEnchantment = toolEnchantments.get(random.nextInt(toolEnchantments.size()));
                                Optional<Holder.Reference<Enchantment>> enchantmentEntryTool = registryManager.lookupOrThrow(Registries.ENCHANTMENT).get(rndmEnchantment.identifier());
                                int levelTool = random.nextInt(5);

                                enchantmentEntryTool.ifPresentOrElse(value -> item.enchant(enchantmentEntryTool.get(), levelTool),
                                        () -> BlazinBlazeTwitchMod.LOGGER.info("Tool Enchantment not found!"));

                                this.spawnAtLocation(serverWorld, item);
                                numberOfItems++;
                            } else if (!Objects.equals(enchantableType, "none") && Objects.equals(enchantableType, "bow")) {

                                RegistryAccess registryManager = serverWorld.registryAccess();
                                ResourceKey<Enchantment> rndmEnchantment = bowEnchantments.get(random.nextInt(bowEnchantments.size()));
                                Optional<Holder.Reference<Enchantment>> enchantmentEntryTool = registryManager.lookupOrThrow(Registries.ENCHANTMENT).get(rndmEnchantment.identifier());
                                int levelTool = random.nextInt(5);

                                enchantmentEntryTool.ifPresentOrElse(value -> item.enchant(enchantmentEntryTool.get(), levelTool),
                                        () -> BlazinBlazeTwitchMod.LOGGER.info("Bow Enchantment not found!"));

                                this.spawnAtLocation(serverWorld, item);
                                numberOfItems++;
                            } else if (!Objects.equals(enchantableType, "none") && Objects.equals(enchantableType, "crossbow")) {

                                RegistryAccess registryManager = serverWorld.registryAccess();
                                ResourceKey<Enchantment> rndmEnchantment = crossbowEnchantments.get(random.nextInt(crossbowEnchantments.size()));
                                Optional<Holder.Reference<Enchantment>> enchantmentEntryTool = registryManager.lookupOrThrow(Registries.ENCHANTMENT).get(rndmEnchantment.identifier());
                                int levelTool = random.nextInt(5);

                                enchantmentEntryTool.ifPresentOrElse(value -> item.enchant(enchantmentEntryTool.get(), levelTool),
                                        () -> BlazinBlazeTwitchMod.LOGGER.info("Crossbow Enchantment not found!"));

                                this.spawnAtLocation(serverWorld, item);
                                numberOfItems++;
                            }
                        } else {
                            this.spawnAtLocation(serverWorld, item);
                            numberOfItems++;
                        }
                    } else {
                        Item stackItem = item.getItem();
                        if (stackItem == Items.POTION) {
                            ItemStack potionItem = PotionContents.createItemStack(Items.POTION, possiblePotions.get(random.nextInt(possiblePotions.size())));

                            this.spawnAtLocation(serverWorld, potionItem);
                            numberOfItems++;
                        } else if (stackItem == Items.ENCHANTED_BOOK) {
                            this.spawnAtLocation(serverWorld, item);
                            numberOfItems++;
                        } else {
                            this.spawnAtLocation(serverWorld, item);
                            numberOfItems++;
                        }
                    }
                }

            } while (numberOfItems < 3);

            this.kill(serverWorld);
            return InteractionResult.SUCCESS;
        }else {
            return InteractionResult.FAIL;
        }
    }

    public int getDestructionTimer() {
        return this.entityData.get(DESTRUCTION_TIMER);
    }

    public void setDestructionTimer(int destructionProgress) {
        this.entityData.set(DESTRUCTION_TIMER, destructionProgress);
    }

    public boolean getHasTouchedGround() {
        return this.entityData.get(HAS_TOUCHED_GROUND);
    }

    public void setHasTouchedGround(boolean hasGround) {
        this.entityData.set(HAS_TOUCHED_GROUND, hasGround);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DESTRUCTION_TIMER, 0);
        builder.define(HAS_TOUCHED_GROUND, false);
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput nbt) {
        nbt.putInt("destruction_timer", getDestructionTimer());
        nbt.putBoolean("has_touched_ground", getHasTouchedGround());
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
        setHasTouchedGround(nbt.getBooleanOr("has_touched_ground", false));
    }

    //private long getLootTableSeed() {
        //return 0L;
    //}

    //protected void dropLoot(ServerWorld world, DamageSource damageSource) {
        //<RegistryKey<LootTable>> optional = this.getLootTableKey();
        //if (!optional.isEmpty()) {
            //LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable((RegistryKey<LootTable>)optional.get());
            //LootWorldContext.Builder builder = new LootWorldContext.Builder(world)
                    //.add(LootContextParameters.THIS_ENTITY, this)
                    //.add(LootContextParameters.ORIGIN, this.getPos())
                    //.add(LootContextParameters.DAMAGE_SOURCE, damageSource)
                    //.addOptional(LootContextParameters.ATTACKING_ENTITY, damageSource.getAttacker())
                    //.addOptional(LootContextParameters.DIRECT_ATTACKING_ENTITY, damageSource.getSource());
            //if (causedByPlayer && this.attackingPlayer != null) {
                //builder = builder.add(LootContextParameters.LAST_DAMAGE_PLAYER, this.attackingPlayer).luck(this.attackingPlayer.getLuck());
            //}

            //LootWorldContext lootWorldContext = builder.build(LootContextTypes.ENTITY);
            //lootTable.generateLoot(lootWorldContext, this.getLootTableSeed(), stack -> this.dropStack(world, stack));
        //}
    //}

    //@Override
    //public SimpleInventory getInventory() {
        //return null;
    //}

    record ItemRarity(double threshold, boolean multiple, boolean isEnchantable, String enchantableType) {}
}
