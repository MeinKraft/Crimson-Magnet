package crimsonfluff.crimsonmagnet.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class MagnetOutputSlot extends Slot {
    public MagnetOutputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    public boolean isItemValid(ItemStack stack) { return false; }
}
