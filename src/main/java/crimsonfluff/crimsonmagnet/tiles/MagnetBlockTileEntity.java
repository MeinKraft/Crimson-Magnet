package crimsonfluff.crimsonmagnet.tiles;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import crimsonfluff.crimsonmagnet.GenericChestTypes;
import crimsonfluff.crimsonmagnet.init.fluidsInit;
import crimsonfluff.crimsonmagnet.init.tilesInit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MagnetBlockTileEntity extends ChestTileEntity {
    public MagnetBlockTileEntity() {
        super(tilesInit.MAGNET_BLOCK_TILE.get(), GenericChestTypes.MAGNET);
    }

    protected FluidTank tank = new FluidTank(FluidAttributes.BUCKET_VOLUME * 64) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == fluidsInit.XP_FLUID.get();
        }
    };
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> tank);

    private int ticks=0;

    @Override
    public void tick() {
        ticks++;
        if (ticks==20) {
            ticks=0;

            int x = this.getPos().getX();
            int y = this.getPos().getY();
            int z = this.getPos().getZ();

            int r = (CrimsonMagnet.CONFIGURATION.magnetBlockRange.get());
            AxisAlignedBB area = new AxisAlignedBB(x - r, y - r, z - r, x + r, y + r, z + r);

            // Handle the XP
            if (CrimsonMagnet.CONFIGURATION.magnetBlockCollectXP.get()) {
                List<ExperienceOrbEntity> orbs = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, area);

                if (orbs.size() != 0) {
                    if (CrimsonMagnet.CONFIGURATION.magnetBlockCollectSound.get()) {
                        world.playSound(null, x, y, z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                    }

                    for (ExperienceOrbEntity orb : orbs) {
                        if (orb.getXpValue() <= (this.tank.getCapacity() - this.tank.getFluidAmount())) {
                            this.tank.fill(new FluidStack(fluidsInit.XP_FLUID.get(), orb.getXpValue()), IFluidHandler.FluidAction.EXECUTE);
                            orb.remove();

                        } else {
                            if (CrimsonMagnet.CONFIGURATION.magnetBlockVoid.get())
                                orb.remove();
                            else
                                orb.setPosition(x + 0.5, y + 1, z + 0.5);
                        }
                    }
                }
            }

            List<ItemEntity> items = world.getEntitiesWithinAABB(EntityType.ITEM, area, item -> !item.getPersistentData().contains("PreventRemoteMovement"));
            int isSpace = 0;
            boolean isSound = false;

            if (items.size() != 0) {
                // try to merge items found with existing items already in MagnetBlock inventory
                for (ItemEntity itemIE : items) {
                    for (int a = 0; a < this.getSizeInventory(); a++) {
                        if (this.getStackInSlot(a).isItemEqual(itemIE.getItem())) {
                            isSpace = this.getStackInSlot(a).getMaxStackSize() - this.getStackInSlot(a).getCount();

                            if (isSpace != 0) {
                                isSpace = Math.min(isSpace, itemIE.getItem().getCount());
                                this.getStackInSlot(a).grow(isSpace);
                                itemIE.getItem().shrink(isSpace);
                                isSound=true;
                            }
                        }
                    }
                }

// if we still have items in the list then add to any empty slots available
                for (ItemEntity itemIE : items) {
                    if (!itemIE.getItem().isEmpty()) {
                        for (int a = 0; a < this.getSizeInventory(); a++) {
                            //if (this.getStackInSlot(a).isEmpty()) {
                                this.setInventorySlotContents(a, itemIE.getItem());
                                itemIE.remove();
                                isSound=true;

                                break;
                            //}
                        }
                    }
                }

                if (isSound) {
                    if (CrimsonMagnet.CONFIGURATION.magnetBlockCollectSound.get()) {
                        world.playSound(null, x, y, z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1.6f);
                    }
                }

// if we still have items in the list then move them to top of MagnetBlock
// or void them
                for (ItemEntity itemIE : items) {
                    if (!itemIE.getItem().isEmpty()) {
                        if (CrimsonMagnet.CONFIGURATION.magnetBlockVoid.get()) {
                            itemIE.remove();
                        } else
                            itemIE.setPosition(x + 0.5, y + 1, z + 0.5);

                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return fluidHandler.cast();

        return super.getCapability(cap, side);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        //CompoundNBT compound = getItemHandler().serializeNBT();
        //tag.put("inv", compound);
        this.tank.writeToNBT(tag);
        return super.write(tag);
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        this.tank.readFromNBT(tag);

        //if (tag.contains("inv")) { getItemHandler().deserializeNBT((CompoundNBT) tag.get("inv")); }
    }
}