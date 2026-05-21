package net.blazinblaze.blazetwitchmod.screen;

import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.blazinblaze.blazetwitchmod.item.custom.MailItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class MailboxSlot extends Slot {
    public MailboxSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return (stack.isOf(BlazinBlazeTwitchModItems.MAIL_ITEM) ||
                stack.isOf(BlazinBlazeTwitchModItems.MAIL_ITEM_BLAZIN) ||
                stack.isOf(BlazinBlazeTwitchModItems.MAIL_ITEM_EPIC) ||
                stack.isOf(BlazinBlazeTwitchModItems.MAIL_ITEM_ERROR) ||
                stack.isOf(BlazinBlazeTwitchModItems.MAIL_ITEM_MINECRAFT) ||
                stack.isOf(BlazinBlazeTwitchModItems.MAIL_ITEM_STARRY) ||
                stack.isOf(BlazinBlazeTwitchModItems.MAIL_ITEM_SINISTER));
    }
}
