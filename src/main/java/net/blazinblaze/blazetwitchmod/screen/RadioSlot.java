package net.blazinblaze.blazetwitchmod.screen;

import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class RadioSlot extends Slot {
    public RadioSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return (stack.isOf(BlazinBlazeTwitchModItems.VOICE_MESSAGE_ITEM));
    }
}
