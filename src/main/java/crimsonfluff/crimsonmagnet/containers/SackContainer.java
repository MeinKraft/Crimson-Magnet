package crimsonfluff.crimsonmagnet.containers;

import crimsonfluff.crimsonmagnet.init.containersInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SackContainer extends Container {
    public final IInventory inventory;

    public SackContainer(int windowId, PlayerInventory playerInventory) {
        this(windowId, playerInventory, new Inventory(39));
    }

    public SackContainer(int windowId, PlayerInventory playerInventory, IInventory inventory) {
        super(containersInit.SACK_CHEST.get(), windowId);
        this.inventory = inventory;

        // Magnet Sack Chest Inventory
        this.addSlot(new SlotXPCollect(inventory, 0,8-26,18));
        this.addSlot(new SlotVoid(inventory, 1,8-26,36));
        this.addSlot(new SlotFilter(inventory, 2,8-26,54));

        for (int chestRows = 0; chestRows < 4; chestRows++) {
            for (int chestCols = 0; chestCols < 9; chestCols++)
                this.addSlot(new Slot(inventory, 3 + (chestRows * 9) + chestCols, 8 + chestCols * 18, 18 + chestRows * 18));
        }

        // Player Inventory
        for (int chestRows = 0; chestRows < 3; chestRows++) {
            for (int chestCols = 0; chestCols < 9; chestCols++)
                this.addSlot(new Slot(playerInventory, 9 + (chestRows * 9) + chestCols, 8 + chestCols * 18, 102 + chestRows * 18));
        }

        // Hotbar
        for (int chestCols = 0; chestCols < 9; chestCols++)
            this.addSlot(new Slot(playerInventory, chestCols, 8 + chestCols * 18, 160));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) { return true; }

// Vanilla ChestContainer class
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 39) {
                if (!this.mergeItemStack(itemstack1, 39, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }

            } else if (!this.mergeItemStack(itemstack1, 0, 39, false)) {
                return ItemStack.EMPTY;
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
