package crimsonfluff.crimsonmagnet.containers;

import crimsonfluff.crimsonmagnet.GenericChestTypes;
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

        // Chest Inventory
        for (int chestRows = 0; chestRows < chestType.rows; chestRows++) {
            for (int chestCols = 0; chestCols < chestType.cols; chestCols++) {
                this.addSlot(new Slot(inventory, (chestRows*9)+chestCols, chestType.xSlot + chestCols * 18, chestType.ySlot + chestRows * 18));
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

/*    @Override
    public void putStackInSlot(int slotID, ItemStack stack) {
        CrimsonChest.LOGGER.info("SLOT"+slotID);
        if (slotID>=9) this.getSlot(slotID).putStack(stack);
    }*/

    // dont allow dragging items into inventory
    @Override
    public boolean canDragIntoSlot(Slot slotIn) {
        return slotIn.getSlotIndex() >= this.getInventory().size();
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.chestType.size) {
                if (!this.mergeItemStack(itemstack1, this.chestType.size, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                //if (!this.mergeItemStack(itemstack1, 0, this.chestType.size, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
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