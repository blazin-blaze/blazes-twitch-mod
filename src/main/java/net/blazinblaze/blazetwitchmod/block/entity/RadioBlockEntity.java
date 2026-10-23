package net.blazinblaze.blazetwitchmod.block.entity;

import net.blazinblaze.blazetwitchmod.block.custom.MailboxBlock;
import net.blazinblaze.blazetwitchmod.screen.MailboxScreenHandler;
import net.blazinblaze.blazetwitchmod.screen.RadioScreenHandler;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class RadioBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> inventory;
    private String ownerName = "test";

    public RadioBlockEntity(BlockPos pos, BlockState state) {
        super(BlazinBlazeTwitchModBlockEntities.RADIO_BLOCK_ENTITY, pos, state);
        this.inventory = NonNullList.withSize(15, ItemStack.EMPTY);
    }

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
            return Component.literal(String.format("%s's Radio", ownerName));
        }
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    public void setOwnerName(String name) {
        this.ownerName = name;
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new RadioScreenHandler(syncId, playerInventory, this);
    }
}
