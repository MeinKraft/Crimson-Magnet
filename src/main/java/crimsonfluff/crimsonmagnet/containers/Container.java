package crimsonfluff.crimsonmagnet.containers;

import crimsonfluff.crimsonmagnet.init.containersInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class Container extends net.minecraft.inventory.container.Container {
    private final IInventory inventory;

    public static Container createMagnetContainer(int windowId, PlayerInventory playerInventory) {
        return new Container(containersInit.GENERIC_CHEST.get(), windowId, playerInventory, new Inventory(11));
    }

    public static Container createMagnetContainer(int windowId, PlayerInventory playerInventory, IInventory inventory) {
        return new Container(containersInit.GENERIC_CHEST.get(), windowId, playerInventory, inventory);
    }

    public Container(ContainerType<?> containerType, int windowId, PlayerInventory playerInventory, IInventory inventory) {
        super(containerType, windowId);
        assertInventorySize(inventory, 11);
        this.inventory = inventory;

        inventory.openInventory(playerInventory.player);


        // MagnetChest Inventory
        this.addSlot(new SlotInputBucket(inventory, 0, 26 , 17));
        this.addSlot(new SlotOutputBucketOnly(inventory, 1, 134, 17));

        for (int chestRows = 0; chestRows < 9; chestRows++) {
            this.addSlot(new SlotOutputOnly(inventory, 2+chestRows, 8 + chestRows * 18, 39));
        }

        // Player Inventory
        for (int chestRows = 0; chestRows < 3; chestRows++) {
            for (int chestCols = 0; chestCols < 9; chestCols++) {
                this.addSlot(new Slot(playerInventory, 9 + (chestRows * 9) + chestCols, 8 + chestCols * 18, 70 + chestRows * 18));
            }
        }

        // Hotbar
        for (int chestCols = 0; chestCols < 9; chestCols++) {
            this.addSlot(new Slot(playerInventory, chestCols, 8 + chestCols * 18, 152 - 24));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.inventory.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        //CrimsonMagnet.LOGGER.info("SLOT: " + slot.slotNumber + " : " + this.chestType.size + " : " + this.inventorySlots.size());

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            // if Shift Clicked FROM MagnetBlock try to merge with HOTBAR and/or PlayerInventory
            if (index < 11) {
                if (!this.mergeItemStack(itemstack1, 11, this.inventorySlots.size(), true))
                    return ItemStack.EMPTY;

            } else {

                // Try to insert Shift Clicked item into slot(0) (BucketIn)
                // NOTE: The slot *should* validate Buckets/FluidContainers
                if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    // if FROM PlayerInventory try to merge with HOTBAR
                    if (index < 38) {
                        if (!this.mergeItemStack(itemstack1, 38, 47, false))
                            return ItemStack.EMPTY;

                        // if FROM HOTBAR then try to merge with PlayerInventory
                    } else if (!this.mergeItemStack(itemstack1, 11, 38, false))
                        return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.inventory.closeInventory(playerIn);
    }
}