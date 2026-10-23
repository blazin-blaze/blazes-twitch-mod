package net.blazinblaze.blazetwitchmod.screen;

import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;

public class RadioSlot extends Slot {
    public RadioSlot(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return (stack.is(BlazinBlazeTwitchModItems.VOICE_MESSAGE_ITEM));
    }
}
