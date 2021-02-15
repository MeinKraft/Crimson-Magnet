package crimsonfluff.crimsonmagnet.containers;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SlotFilter extends Slot {
    public SlotFilter(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    public boolean isItemValid(ItemStack stack) { return stack.getItem() == Items.PAPER; }

    public int getSlotStackLimit() { return 1; }

    @Override
    public void putStack(ItemStack stack) {
        super.putStack(stack);

        if (!stack.isEmpty()) {
            ListNBT listnbt = new ListNBT();

            // TODO: Make a list of NON-DUPLICATES to keep NBT low

            for(int i = 3; i < this.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.inventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    CompoundNBT compoundNBT = new CompoundNBT();
                    write(compoundNBT, itemstack);
                    listnbt.add(compoundNBT);
                }
            }

            if (!listnbt.isEmpty()) stack.getOrCreateTag().put("Items", listnbt);
        }
    }

    // from ItemStack Write
    // removed the number of items because not needed, Keep NBT short as possible
    private CompoundNBT write(CompoundNBT nbt, ItemStack item) {
        ResourceLocation resourcelocation = Registry.ITEM.getKey(item.getItem());
        nbt.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());

        //nbt.putByte("Count", (byte)this.count);
        //if (item.getTag() != null) nbt.put("tag", item.getTag().copy());

//        CompoundNBT cnbt = item.serializeCaps();
//        if (cnbt != null && !cnbt.isEmpty()) {
//            nbt.put("ForgeCaps", cnbt);
//        }
        return nbt;
    }
}
