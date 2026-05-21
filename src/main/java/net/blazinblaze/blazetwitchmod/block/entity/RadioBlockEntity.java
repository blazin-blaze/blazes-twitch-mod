package net.blazinblaze.blazetwitchmod.block.entity;

import net.blazinblaze.blazetwitchmod.block.custom.MailboxBlock;
import net.blazinblaze.blazetwitchmod.screen.MailboxScreenHandler;
import net.blazinblaze.blazetwitchmod.screen.RadioScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class RadioBlockEntity extends LootableContainerBlockEntity {
    private DefaultedList<ItemStack> inventory;
    private String ownerName = "test";

    public RadioBlockEntity(BlockPos pos, BlockState state) {
        super(BlazinBlazeTwitchModBlockEntities.RADIO_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(15, ItemStack.EMPTY);
    }

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
            return Text.literal(String.format("%s's Radio", ownerName));
        }
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    public void setOwnerName(String name) {
        this.ownerName = name;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new RadioScreenHandler(syncId, playerInventory, this);
    }
}
