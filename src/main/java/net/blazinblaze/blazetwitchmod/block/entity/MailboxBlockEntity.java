package net.blazinblaze.blazetwitchmod.block.entity;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.block.custom.MailboxBlock;
import net.blazinblaze.blazetwitchmod.screen.MailboxScreenHandler;
import net.blazinblaze.blazetwitchmod.util.ImplementedInventory;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MailboxBlockEntity extends RandomizableContainerBlockEntity {

    private NonNullList<ItemStack> inventory;
    private final ContainerOpenersCounter stateManager;
    private String ownerName = "test";

    public MailboxBlockEntity(BlockPos pos, BlockState state) {
        super(BlazinBlazeTwitchModBlockEntities.MAILBOX_BLOCK_ENTITY, pos, state);

        this.inventory = NonNullList.withSize(15, ItemStack.EMPTY);

        this.stateManager = new ContainerOpenersCounter() {
            protected void onOpen(Level world, BlockPos pos, BlockState state) {
                MailboxBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
                MailboxBlockEntity.this.setOpen(state, true);
            }

            protected void onClose(Level world, BlockPos pos, BlockState state) {
                MailboxBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
                MailboxBlockEntity.this.setOpen(state, false);
            }

            protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            }

            public boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof MailboxScreenHandler) {
                    Container inventory = ((MailboxScreenHandler)player.containerMenu).getInventory();
                    return inventory == MailboxBlockEntity.this;
                } else {
                    return false;
                }
            }
        };
    }

    /*/@Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MailboxScreenHandler(syncId, playerInventory, this);
    }/*/

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        ownerName = view.getStringOr("owner", "test");
        ContainerHelper.loadAllItems(view, this.inventory);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        view.putString("owner", ownerName);
        ContainerHelper.saveAllItems(view, this.inventory);
    }

    public int getContainerSize() {return 15;}

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public void setItems(NonNullList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected Component getDefaultName() {
        if(ownerName != null && !ownerName.matches("test")) {
            return Component.literal(String.format("%s's Mailbox", ownerName));
        }
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new MailboxScreenHandler(syncId, playerInventory, this);
    }

    public void startOpen(ContainerUser player) {
        if (!this.remove && !player.getLivingEntity().isSpectator()) {
            this.stateManager.incrementOpeners(player.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState(), player.getContainerInteractionRange());
        }
    }

    public void stopOpen(ContainerUser player) {
        if (!this.remove && !player.getLivingEntity().isSpectator()) {
            this.stateManager.decrementOpeners(player.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void tick() {
        if (!this.remove) {
            this.stateManager.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
            boolean invEmpty = true;
            for(ItemStack i : inventory) {
                if(!i.isEmpty()) {
                    invEmpty = false;
                    break;
                }
            }
            if(invEmpty) {
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(MailboxBlock.ACTIVE, false), 3);
            }else {
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(MailboxBlock.ACTIVE, true), 3);
            }
        }
    }

    void setOpen(BlockState state, boolean open) {
        this.level.setBlock(this.getBlockPos(), (BlockState)state.setValue(MailboxBlock.OPEN, open), 3);
    }

    public void setOwnerName(String name) {
        this.ownerName = name;
    }

    void playSound(BlockState state, SoundEvent soundEvent) {
        Vec3i vec3i = ((Direction)state.getValue(MailboxBlock.FACING)).getUnitVec3i();
        double d = (double)this.worldPosition.getX() + (double)0.5F + (double)vec3i.getX() / (double)2.0F;
        double e = (double)this.worldPosition.getY() + (double)0.5F + (double)vec3i.getY() / (double)2.0F;
        double f = (double)this.worldPosition.getZ() + (double)0.5F + (double)vec3i.getZ() / (double)2.0F;
        this.level.playSound((Entity)null, d, e, f, soundEvent, SoundSource.BLOCKS, 0.5F, this.level.getRandom().nextFloat() * 0.1F + 0.9F);
    }
}
