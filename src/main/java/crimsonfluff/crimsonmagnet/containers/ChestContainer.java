package crimsonfluff.crimsonmagnet.containers;

import crimsonfluff.crimsonmagnet.init.containersInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChestContainer extends Container {
    private final IInventory inventory;
    private final GenericChestTypes chestType;

    public static ChestContainer createMagnetContainer(int windowId, PlayerInventory playerInventory) {
        return new ChestContainer(containersInit.GENERIC_CHEST.get(), windowId, playerInventory, new Inventory(GenericChestTypes.MAGNET.size), GenericChestTypes.MAGNET);
    }

    public static ChestContainer createMagnetContainer(int windowId, PlayerInventory playerInventory, IInventory inventory) {
        return new ChestContainer(containersInit.GENERIC_CHEST.get(), windowId, playerInventory, inventory, GenericChestTypes.MAGNET);
    }

    public ChestContainer(ContainerType<?> containerType, int windowId, PlayerInventory playerInventory, IInventory inventory, GenericChestTypes chestType) {
        super(containerType, windowId);
        assertInventorySize(inventory, chestType.size);

        this.inventory = inventory;
        this.chestType = chestType;

        inventory.openInventory(playerInventory.player);

        // MagnetChest Inventory
        for (int chestRows = 0; chestRows < chestType.rows; chestRows++) {
            for (int chestCols = 0; chestCols < chestType.cols; chestCols++) {
                this.addSlot(new MagnetOutputSlot(inventory, (chestRows*9)+chestCols, chestType.xSlot + chestCols * 18, chestType.ySlot + chestRows * 18));
            }
        }

        // Player Inventory
        for (int chestRows = 0; chestRows < 3; chestRows++) {
            for (int chestCols = 0; chestCols < 9; chestCols++) {
                this.addSlot(new Slot(playerInventory, 9 + (chestRows * 9) + chestCols, chestType.xSlot + chestCols * 18, chestType.ySlotInv + chestRows * 18));
            }
        }

        // Hotbar
        for (int chestCols = 0; chestCols < 9; chestCols++) {
            this.addSlot(new Slot(playerInventory, chestCols, chestType.xSlot + chestCols * 18, chestType.ySize - 24));
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
            if (index < this.chestType.size) {
                if (!this.mergeItemStack(itemstack1, this.chestType.size, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // if FROM PlayerInventory try to merge with HOTBAR
                if (index >= 9 && index < 36) {
                    if (!this.mergeItemStack(itemstack1, 36, 45, false)) {
                        return ItemStack.EMPTY;
                    }

                    // if FROM HOTBAR then try to merge with PlayerInventory
                } else if (!this.mergeItemStack(itemstack1, 9, 36, false)) {
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

    @OnlyIn(Dist.CLIENT)
    public GenericChestTypes getChestType() {
        return this.chestType;
    }
}