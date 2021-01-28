package crimsonfluff.crimsonmagnet.containers;

import crimsonfluff.crimsonmagnet.init.itemsInit;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;

public class SlotOutputBucketOnly extends Slot {
    public SlotOutputBucketOnly(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    public boolean isItemValid(ItemStack stack) { return false; }

    public void onSlotChanged() {
        if (this.inventory.getStackInSlot(1).isEmpty()) {
            if (this.inventory.getStackInSlot(0).getItem() instanceof BucketItem) {
                this.inventory.decrStackSize(0, 1);
                this.inventory.setInventorySlotContents(1, new ItemStack(itemsInit.XP_BUCKET.get(), 1));
            }
        }
    }
}
