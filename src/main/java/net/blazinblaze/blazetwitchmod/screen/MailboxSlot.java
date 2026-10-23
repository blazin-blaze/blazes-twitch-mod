package net.blazinblaze.blazetwitchmod.screen;

import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.blazinblaze.blazetwitchmod.item.custom.MailItem;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;

public class MailboxSlot extends Slot {
    public MailboxSlot(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return (stack.is(BlazinBlazeTwitchModItems.MAIL_ITEM) ||
                stack.is(BlazinBlazeTwitchModItems.MAIL_ITEM_BLAZIN) ||
                stack.is(BlazinBlazeTwitchModItems.MAIL_ITEM_EPIC) ||
                stack.is(BlazinBlazeTwitchModItems.MAIL_ITEM_ERROR) ||
                stack.is(BlazinBlazeTwitchModItems.MAIL_ITEM_MINECRAFT) ||
                stack.is(BlazinBlazeTwitchModItems.MAIL_ITEM_STARRY) ||
                stack.is(BlazinBlazeTwitchModItems.MAIL_ITEM_SINISTER));
    }
}
