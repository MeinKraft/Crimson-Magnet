package crimsonfluff.crimsonmagnet.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SlotXPCollect extends Slot {
    public SlotXPCollect(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    public boolean isItemValid(ItemStack stack) { return stack.getItem() == Items.EMERALD; }

    public int getSlotStackLimit() { return 1; }
}
