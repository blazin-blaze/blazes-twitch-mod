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
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.enchantment.provider.EnchantmentProviders;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LootCrateEntity extends Entity { //implements InventoryOwner
    private static final TrackedData<Integer> DESTRUCTION_TIMER = DataTracker.registerData(LootCrateEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> HAS_TOUCHED_GROUND = DataTracker.registerData(LootCrateEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private static final Map<ItemStack, ItemRarity> LOOT_TABLE = Map.ofEntries(
            Map.entry(Items.COAL_BLOCK.getDefaultStack(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.IRON_GOLEM_SPAWN_EGG.getDefaultStack(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.DIAMOND_SWORD.getDefaultStack(), new ItemRarity(0.1, false, true, "tool")),
            Map.entry(Items.ALLAY_SPAWN_EGG.getDefaultStack(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.ANVIL.getDefaultStack(), new ItemRarity(0.4, false, false, "none")),
            Map.entry(Items.BEACON.getDefaultStack(), new ItemRarity(0.1, false, false, "none")),
            Map.entry(Items.DIAMOND_BLOCK.getDefaultStack(), new ItemRarity(0.1, true, false, "none")),
            Map.entry(Items.NETHERITE_BLOCK.getDefaultStack(), new ItemRarity(0.05, false, false, "none")),
            Map.entry(Items.GOLD_BLOCK.getDefaultStack(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.IRON_BLOCK.getDefaultStack(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.LAPIS_BLOCK.getDefaultStack(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.COPPER_BLOCK.getDefaultStack(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.ENCHANTED_GOLDEN_APPLE.getDefaultStack(), new ItemRarity(0.1, true, false, "none")),
            Map.entry(Items.GOLDEN_APPLE.getDefaultStack(), new ItemRarity(0.2, true, false, "none")),
            Map.entry(Items.SHULKER_BOX.getDefaultStack(), new ItemRarity(0.1, false, false, "none")),
            Map.entry(Items.ELYTRA.getDefaultStack(), new ItemRarity(0.01, false, true, "armor")),
            Map.entry(Items.BREAD.getDefaultStack(), new ItemRarity(0.6, true, false, "none")),
            Map.entry(Items.COOKED_BEEF.getDefaultStack(), new ItemRarity(0.6, true, false, "none")),
            Map.entry(Items.FIRE_CHARGE.getDefaultStack(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.FLINT_AND_STEEL.getDefaultStack(), new ItemRarity(0.5, false, false, "none")),
            Map.entry(Items.OBSIDIAN.getDefaultStack(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.ENDER_CHEST.getDefaultStack(), new ItemRarity(0.4, false, false, "none")),
            Map.entry(Items.ENDER_DRAGON_SPAWN_EGG.getDefaultStack(), new ItemRarity(0.01, true, false, "none")),
            Map.entry(Items.WITHER_SPAWN_EGG.getDefaultStack(), new ItemRarity(0.01, true, false, "none")),
            Map.entry(Items.ENDER_PEARL.getDefaultStack(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.ENCHANTED_BOOK.getDefaultStack(), new ItemRarity(0.3, false, false, "none")),
            Map.entry(Items.WOLF_SPAWN_EGG.getDefaultStack(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.WOLF_ARMOR.getDefaultStack(), new ItemRarity(0.4, false, true, "armor")),
            Map.entry(Items.CAT_SPAWN_EGG.getDefaultStack(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.VILLAGER_SPAWN_EGG.getDefaultStack(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.HORSE_SPAWN_EGG.getDefaultStack(), new ItemRarity(0.4, true, false, "none")),
            Map.entry(Items.DIAMOND_HORSE_ARMOR.getDefaultStack(), new ItemRarity(0.2, false, true, "armor")),
            Map.entry(Items.GOLDEN_HORSE_ARMOR.getDefaultStack(), new ItemRarity(0.5, false, true, "armor")),
            Map.entry(Items.SKELETON_HORSE_SPAWN_EGG.getDefaultStack(), new ItemRarity(0.3, true, false, "none")),
            Map.entry(Items.TOTEM_OF_UNDYING.getDefaultStack(), new ItemRarity(0.4, false, false, "none")),
            Map.entry(Items.END_CRYSTAL.getDefaultStack(), new ItemRarity(0.5, true, false, "none")),
            Map.entry(Items.EMERALD_BLOCK.getDefaultStack(), new ItemRarity(0.3, true, false, "none")),
            Map.entry(Items.DIAMOND_PICKAXE.getDefaultStack(), new ItemRarity(0.1, false, true, "tool")),
            Map.entry(Items.DIAMOND_AXE.getDefaultStack(), new ItemRarity(0.1, false, true, "tool")),
            Map.entry(Items.DIAMOND_SHOVEL.getDefaultStack(), new ItemRarity(0.1, false, true, "tool")),
            Map.entry(Items.DIAMOND_HOE.getDefaultStack(), new ItemRarity(0.1, false, true, "tool")),
            Map.entry(Items.IRON_SWORD.getDefaultStack(), new ItemRarity(0.3, false, true, "tool")),
            Map.entry(Items.IRON_PICKAXE.getDefaultStack(), new ItemRarity(0.3, false, true, "tool")),
            Map.entry(Items.IRON_AXE.getDefaultStack(), new ItemRarity(0.3, false, true, "tool")),
            Map.entry(Items.IRON_SHOVEL.getDefaultStack(), new ItemRarity(0.3, false, true, "tool")),
            Map.entry(Items.IRON_HOE.getDefaultStack(), new ItemRarity(0.3, false, true, "tool")),
            Map.entry(Items.NETHERITE_AXE.getDefaultStack(), new ItemRarity(0.05, false, true, "tool")),
            Map.entry(Items.NETHERITE_HOE.getDefaultStack(), new ItemRarity(0.05, false, true, "tool")),
            Map.entry(Items.NETHERITE_SWORD.getDefaultStack(), new ItemRarity(0.05, false, true, "tool")),
            Map.entry(Items.NETHERITE_PICKAXE.getDefaultStack(), new ItemRarity(0.05, false, true, "tool")),
            Map.entry(Items.NETHERITE_SHOVEL.getDefaultStack(), new ItemRarity(0.05, false, true, "tool")),
            Map.entry(Items.IRON_HELMET.getDefaultStack(), new ItemRarity(0.3, false, true, "armor")),
            Map.entry(Items.IRON_CHESTPLATE.getDefaultStack(), new ItemRarity(0.3, false, true, "armor")),
            Map.entry(Items.IRON_LEGGINGS.getDefaultStack(), new ItemRarity(0.3, false, true, "armor")),
            Map.entry(Items.IRON_BOOTS.getDefaultStack(), new ItemRarity(0.3, false, true, "armor")),
            Map.entry(Items.DIAMOND_HELMET.getDefaultStack(), new ItemRarity(0.1, false, true, "armor")),
            Map.entry(Items.DIAMOND_CHESTPLATE.getDefaultStack(), new ItemRarity(0.1, false, true, "armor")),
            Map.entry(Items.DIAMOND_LEGGINGS.getDefaultStack(), new ItemRarity(0.1, false, true, "armor")),
            Map.entry(Items.DIAMOND_BOOTS.getDefaultStack(), new ItemRarity(0.1, false, true, "armor")),
            Map.entry(Items.NETHERITE_HELMET.getDefaultStack(), new ItemRarity(0.05, false, true, "armor")),
            Map.entry(Items.NETHERITE_CHESTPLATE.getDefaultStack(), new ItemRarity(0.05, false, true, "armor")),
            Map.entry(Items.NETHERITE_LEGGINGS.getDefaultStack(), new ItemRarity(0.05, false, true, "armor")),
            Map.entry(Items.NETHERITE_BOOTS.getDefaultStack(), new ItemRarity(0.05, false, true, "armor")),
            Map.entry(Items.POTION.getDefaultStack(), new ItemRarity(0.5, false, false, "none")),
            Map.entry(Items.TNT.getDefaultStack(), new ItemRarity(0.5, true, false, "none")),
            //Map.entry(BlazinBlazeTwitchModItems.BLAZE_SWORD.getDefaultStack(), new ItemRarity(0.001, false, true, "tool")),
            Map.entry(BlazinBlazeTwitchModBlocks.NUCLEAR_TNT.asItem().getDefaultStack(), new ItemRarity(0.1, true, false, "none")),
            Map.entry(Items.BOW.getDefaultStack(), new ItemRarity(0.5, false, true, "bow")),
            Map.entry(Items.CROSSBOW.getDefaultStack(), new ItemRarity(0.5, false, true, "crossbow")),
            Map.entry(BlazinBlazeTwitchModItems.IMMUNITY_SHIELD_ITEM.getDefaultStack(), new ItemRarity(0.001, false, false, "none")),
            Map.entry(BlazinBlazeTwitchModItems.FAKE_EYE_OF_ENDER.getDefaultStack(), new ItemRarity(0.1, true, false, "none")),
            Map.entry(Items.ENDER_EYE.getDefaultStack(), new ItemRarity(0.1, true, false, "none")));

    private static final ArrayList<RegistryKey<Enchantment>> toolEnchantments = new ArrayList<RegistryKey<Enchantment>>();
    private static final ArrayList<RegistryKey<Enchantment>> bowEnchantments = new ArrayList<RegistryKey<Enchantment>>();
    private static final ArrayList<RegistryKey<Enchantment>> crossbowEnchantments = new ArrayList<RegistryKey<Enchantment>>();
    private static final ArrayList<RegistryKey<Enchantment>> armorEnchantments = new ArrayList<RegistryKey<Enchantment>>();
    private static final ArrayList<RegistryEntry<Potion>> possiblePotions = new ArrayList<RegistryEntry<Potion>>();

    private Text label = Text.literal("§lLoot Crate");

    public LootCrateEntity(EntityType<?> type, World world) {
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
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
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
    public boolean collidesWith(Entity other) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickPortalTeleportation();
        this.applyGravity();
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));
        this.shouldTickBlockCollision();
        if (this.isOnGround()) {
            if(!getHasTouchedGround()) {
                this.playSound(BlazinBlazeTwitchModSounds.THUD_SOUND_EFFECT, 1.0F, 1.0F);
                this.getWorld().addParticleClient(ParticleTypes.EXPLOSION, this.getX(), (this.getY() + .3), this.getZ(), 1.0, 1.0, 1.0);
                setHasTouchedGround(true);
                this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
            }else {
                this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
            }
        }

        if(this.getWorld().isClient()) {
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
            label = Text.literal(String.format(String.valueOf(time)));
            //BlazinBlazeTwitchMod.LOGGER.info("NUMBER OF SECONDS: " + String.valueOf(time));
        }
        //BlazinBlazeTwitchMod.LOGGER.info("TICK!");
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if(player.getWorld() instanceof ServerWorld serverWorld) {
            this.playSound(SoundEvents.BLOCK_CHEST_OPEN, 1.0F, 0.5F);
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
                            this.dropStack(serverWorld, item);
                            numberOfItems++;
                        } else {
                            this.dropStack(serverWorld, item);
                            numberOfItems++;
                        }
                    } else if (isEnchanted) {
                        if (ThreadLocalRandom.current().nextDouble() < 1) {
                            if (!Objects.equals(enchantableType, "none") && Objects.equals(enchantableType, "armor")) {

                                DynamicRegistryManager registryManager = serverWorld.getRegistryManager();
                                RegistryKey<Enchantment> rndmEnchantment = armorEnchantments.get(random.nextInt(armorEnchantments.size()));
                                Optional<RegistryEntry.Reference<Enchantment>> enchantmentEntryTool = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(rndmEnchantment.getValue());
                                int levelTool = random.nextInt(5);

                                enchantmentEntryTool.ifPresentOrElse(value -> item.addEnchantment(enchantmentEntryTool.get(), levelTool),
                                        () -> BlazinBlazeTwitchMod.LOGGER.info("Armor Enchantment not found!"));

                                this.dropStack(serverWorld, item);
                                numberOfItems++;
                            } else if (!Objects.equals(enchantableType, "none") && Objects.equals(enchantableType, "tool")) {

                                DynamicRegistryManager registryManager = serverWorld.getRegistryManager();
                                RegistryKey<Enchantment> rndmEnchantment = toolEnchantments.get(random.nextInt(toolEnchantments.size()));
                                Optional<RegistryEntry.Reference<Enchantment>> enchantmentEntryTool = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(rndmEnchantment.getValue());
                                int levelTool = random.nextInt(5);

                                enchantmentEntryTool.ifPresentOrElse(value -> item.addEnchantment(enchantmentEntryTool.get(), levelTool),
                                        () -> BlazinBlazeTwitchMod.LOGGER.info("Tool Enchantment not found!"));

                                this.dropStack(serverWorld, item);
                                numberOfItems++;
                            } else if (!Objects.equals(enchantableType, "none") && Objects.equals(enchantableType, "bow")) {

                                DynamicRegistryManager registryManager = serverWorld.getRegistryManager();
                                RegistryKey<Enchantment> rndmEnchantment = bowEnchantments.get(random.nextInt(bowEnchantments.size()));
                                Optional<RegistryEntry.Reference<Enchantment>> enchantmentEntryTool = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(rndmEnchantment.getValue());
                                int levelTool = random.nextInt(5);

                                enchantmentEntryTool.ifPresentOrElse(value -> item.addEnchantment(enchantmentEntryTool.get(), levelTool),
                                        () -> BlazinBlazeTwitchMod.LOGGER.info("Bow Enchantment not found!"));

                                this.dropStack(serverWorld, item);
                                numberOfItems++;
                            } else if (!Objects.equals(enchantableType, "none") && Objects.equals(enchantableType, "crossbow")) {

                                DynamicRegistryManager registryManager = serverWorld.getRegistryManager();
                                RegistryKey<Enchantment> rndmEnchantment = crossbowEnchantments.get(random.nextInt(crossbowEnchantments.size()));
                                Optional<RegistryEntry.Reference<Enchantment>> enchantmentEntryTool = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(rndmEnchantment.getValue());
                                int levelTool = random.nextInt(5);

                                enchantmentEntryTool.ifPresentOrElse(value -> item.addEnchantment(enchantmentEntryTool.get(), levelTool),
                                        () -> BlazinBlazeTwitchMod.LOGGER.info("Crossbow Enchantment not found!"));

                                this.dropStack(serverWorld, item);
                                numberOfItems++;
                            }
                        } else {
                            this.dropStack(serverWorld, item);
                            numberOfItems++;
                        }
                    } else {
                        Item stackItem = item.getItem();
                        if (stackItem == Items.POTION) {
                            ItemStack potionItem = PotionContentsComponent.createStack(Items.POTION, possiblePotions.get(random.nextInt(possiblePotions.size())));

                            this.dropStack(serverWorld, potionItem);
                            numberOfItems++;
                        } else if (stackItem == Items.ENCHANTED_BOOK) {
                            this.dropStack(serverWorld, item);
                            numberOfItems++;
                        } else {
                            this.dropStack(serverWorld, item);
                            numberOfItems++;
                        }
                    }
                }

            } while (numberOfItems < 3);

            this.kill(serverWorld);
            return ActionResult.SUCCESS;
        }else {
            return ActionResult.FAIL;
        }
    }

    public int getDestructionTimer() {
        return this.dataTracker.get(DESTRUCTION_TIMER);
    }

    public void setDestructionTimer(int destructionProgress) {
        this.dataTracker.set(DESTRUCTION_TIMER, destructionProgress);
    }

    public boolean getHasTouchedGround() {
        return this.dataTracker.get(HAS_TOUCHED_GROUND);
    }

    public void setHasTouchedGround(boolean hasGround) {
        this.dataTracker.set(HAS_TOUCHED_GROUND, hasGround);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(DESTRUCTION_TIMER, 0);
        builder.add(HAS_TOUCHED_GROUND, false);
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
    }

    @Override
    protected void writeCustomData(WriteView nbt) {
        nbt.putInt("destruction_timer", getDestructionTimer());
        nbt.putBoolean("has_touched_ground", getHasTouchedGround());
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
        setHasTouchedGround(nbt.getBoolean("has_touched_ground", false));
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
