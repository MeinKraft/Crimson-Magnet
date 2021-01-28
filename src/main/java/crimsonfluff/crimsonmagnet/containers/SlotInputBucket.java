package crimsonfluff.crimsonmagnet.containers;

import crimsonfluff.crimsonmagnet.init.itemsInit;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class SlotInputBucket extends Slot {
    public SlotInputBucket(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() instanceof BucketItem) return (((BucketItem) stack.getItem()).getFluid() == Fluids.EMPTY);

        if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) return true;

        return false;
    }

    public void onSlotChanged() {
        if (this.inventory.getStackInSlot(0).getItem() instanceof BucketItem) {
            if (this.inventory.getStackInSlot(1).isEmpty()) {
                this.inventory.decrStackSize(0, 1);
                this.inventory.setInventorySlotContents(1, new ItemStack(itemsInit.XP_BUCKET.get(), 1));
            }
        }
        //CrimsonMagnet.LOGGER.info("Crafting: ");
    }
}
