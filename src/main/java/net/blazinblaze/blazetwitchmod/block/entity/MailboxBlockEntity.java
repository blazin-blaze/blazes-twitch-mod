package net.blazinblaze.blazetwitchmod.block.entity;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.block.custom.MailboxBlock;
import net.blazinblaze.blazetwitchmod.screen.MailboxScreenHandler;
import net.blazinblaze.blazetwitchmod.util.ImplementedInventory;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MailboxBlockEntity extends LootableContainerBlockEntity {

    private DefaultedList<ItemStack> inventory;
    private final ViewerCountManager stateManager;
    private String ownerName = "test";

    public MailboxBlockEntity(BlockPos pos, BlockState state) {
        super(BlazinBlazeTwitchModBlockEntities.MAILBOX_BLOCK_ENTITY, pos, state);

        this.inventory = DefaultedList.ofSize(15, ItemStack.EMPTY);

        this.stateManager = new ViewerCountManager() {
            protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
                MailboxBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_OPEN);
                MailboxBlockEntity.this.setOpen(state, true);
            }

            protected void onContainerClose(World world, BlockPos pos, BlockState state) {
                MailboxBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_CLOSE);
                MailboxBlockEntity.this.setOpen(state, false);
            }

            protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            }

            protected boolean isPlayerViewing(PlayerEntity player) {
                if (player.currentScreenHandler instanceof MailboxScreenHandler) {
                    Inventory inventory = ((MailboxScreenHandler)player.currentScreenHandler).getInventory();
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
    protected void readData(ReadView view) {
        super.readData(view);
        ownerName = view.getString("owner", "test");
        Inventories.readData(view, this.inventory);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putString("owner", ownerName);
        Inventories.writeData(view, this.inventory);
    }

    public int size() {return 15;}

    @Override
    public DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    public void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected Text getContainerName() {
        if(ownerName != null && !ownerName.matches("test")) {
            return Text.literal(String.format("%s's Mailbox", ownerName));
        }
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new MailboxScreenHandler(syncId, playerInventory, this);
    }

    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    public void tick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    void setOpen(BlockState state, boolean open) {
        this.world.setBlockState(this.getPos(), (BlockState)state.with(MailboxBlock.OPEN, open), 3);
    }

    public void setOwnerName(String name) {
        this.ownerName = name;
    }

    void playSound(BlockState state, SoundEvent soundEvent) {
        Vec3i vec3i = ((Direction)state.get(MailboxBlock.FACING)).getVector();
        double d = (double)this.pos.getX() + (double)0.5F + (double)vec3i.getX() / (double)2.0F;
        double e = (double)this.pos.getY() + (double)0.5F + (double)vec3i.getY() / (double)2.0F;
        double f = (double)this.pos.getZ() + (double)0.5F + (double)vec3i.getZ() / (double)2.0F;
        this.world.playSound((Entity)null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
    }
}
