package crimsonfluff.crimsonmagnet.containers;

import crimsonfluff.crimsonmagnet.init.containersInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class MagnetBlockContainer extends Container {
    private final IInventory inventory;

    public MagnetBlockContainer(int windowId, PlayerInventory playerInventory) {
        this(windowId, playerInventory, new Inventory(11));
    }

    public MagnetBlockContainer(int windowId, PlayerInventory playerInventory, IInventory inventory) {
        super(containersInit.MAGNET_CHEST.get(), windowId);
        this.inventory = inventory;
        inventory.openInventory(playerInventory.player);

        // MagnetChest Inventory
        this.addSlot(new SlotXPCollect(this.inventory, 0,8-26,18));
        this.addSlot(new SlotVoid(this.inventory, 1,8-26,36));

        for (int chestRows = 0; chestRows < 9; chestRows++) {
            this.addSlot(new SlotOutputOnly(inventory, 2 + chestRows, 8 + chestRows * 18, 18));
        }

        // Player Inventory
        for (int chestRows = 0; chestRows < 3; chestRows++) {
            for (int chestCols = 0; chestCols < 9; chestCols++) {
                this.addSlot(new Slot(playerInventory, 9 + (chestRows * 9) + chestCols, 8 + chestCols * 18, 48 + chestRows * 18));
            }
        }

        // Hotbar
        for (int chestCols = 0; chestCols < 9; chestCols++) {
            this.addSlot(new Slot(playerInventory, chestCols, 8 + chestCols * 18, 130 - 24));
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

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 11) {
                if (!this.mergeItemStack(itemstack1, 11, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }

            } else if (!this.mergeItemStack(itemstack1, 0, 2, false)) {
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
