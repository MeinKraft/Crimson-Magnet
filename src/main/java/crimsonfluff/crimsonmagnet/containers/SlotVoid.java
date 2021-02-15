package crimsonfluff.crimsonmagnet.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SlotVoid extends Slot {
    public SlotVoid(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    public boolean isItemValid(ItemStack stack) { return stack.getItem() == Items.OBSIDIAN; }

    public int getSlotStackLimit() { return 1; }
}
