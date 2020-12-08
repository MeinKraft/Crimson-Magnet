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
import net.minecraft.particles.ParticleTypes;
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
import java.util.ArrayList;
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

    //BlockPos converts to integer, so either center on the block its found on or store x/y/z independently
    //private ArrayList<BlockPos> particlePos = new ArrayList<BlockPos>();
    // MUST be static or wont work, tried this.particlePosX.size()
    private static ArrayList<Double> particlePosX = new ArrayList<Double>();
    private static ArrayList<Double> particlePosY = new ArrayList<Double>();
    private static ArrayList<Double> particlePosZ = new ArrayList<Double>();

    @Override
    public void tick() {
        if (world.isRemote) {
            //CrimsonMagnet.LOGGER.info("STAGE ONE");
            if (particlePosX.size() != 0) {
                //CrimsonMagnet.LOGGER.info("STAGE TWO");

                for (int a=0; a<particlePosX.size(); a++) {
                    world.addParticle(ParticleTypes.CLOUD, particlePosX.get(a), particlePosY.get(a), particlePosZ.get(a), 0.0D, 0.0D, 0.0D);
                    //worldIn.addParticle(ParticleTypes.CLOUD, particlePosX.get(a), particlePosY.get(a), particlePosZ.get(a), 0.0D, 0.0D, 0.0D);
                }

                particlePosX.clear();
                particlePosY.clear();
                particlePosZ.clear();
                return;
            }
        }

        ticks++;
        if (ticks==20) {
            ticks=0;

            if (!world.isRemote()) {
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
                            if (CrimsonMagnet.CONFIGURATION.magnetBlockShowParticles.get()) {
                                particlePosX.add(orb.getPosX());
                                particlePosY.add(orb.getPosY());
                                particlePosZ.add(orb.getPosZ());
                            }

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

                //CrimsonMagnet.LOGGER.info("MAGNET: Ticking");

                if (items.size() != 0) {
// try to merge items found with existing items already in Player inventory
                    for (ItemEntity itemIE : items) {
                        if (CrimsonMagnet.CONFIGURATION.magnetBlockShowParticles.get()) {
                            particlePosX.add(itemIE.getPosX());
                            particlePosY.add(itemIE.getPosY());
                            particlePosZ.add(itemIE.getPosZ());
                        }

                        for (int a = 0; a < this.getSizeInventory(); a++) {
                            // dont include slots 36,37,38,39 which are boots, leggings, chestplate, helmet
                            if ((a <= 35) || (a >= 40)) {
                                if (this.getStackInSlot(a).getItem() == itemIE.getItem().getItem()) {
                                    isSpace = this.getStackInSlot(a).getMaxStackSize() - this.getStackInSlot(a).getCount();

                                    if (isSpace != 0) {
                                        isSpace = Math.min(isSpace, itemIE.getItem().getCount());
                                        this.getStackInSlot(a).grow(isSpace);
                                        itemIE.getItem().shrink(isSpace);
                                        isSound = true;

                                        if (itemIE.getItem().getCount() == 0) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

// if we still have items in the list then add to any empty slots available
                    for (ItemEntity itemIE : items) {
                        if (itemIE.getItem().getCount() != 0) {
                            for (int a = 0; a < this.getSizeInventory(); a++) {
                                // dont include slots 36,37,38,39 which are boots, leggings, chestplate, helmet
                                if ((a <= 35) || (a >= 40)) {
                                    if (this.getStackInSlot(a).isEmpty()) {
                                        this.setInventorySlotContents(a, itemIE.getItem());

                                        itemIE.remove();
                                        isSound = true;

                                        break;
                                    } else {
                                        if (this.getStackInSlot(a).getItem() == itemIE.getItem().getItem()) {
                                            isSpace = this.getStackInSlot(a).getMaxStackSize() - this.getStackInSlot(a).getCount();

                                            if (isSpace != 0) {
                                                isSpace = Math.min(isSpace, itemIE.getItem().getCount());
                                                this.getStackInSlot(a).grow(isSpace);
                                                itemIE.getItem().shrink(isSpace);
                                                isSound = true;

                                                if (itemIE.getItem().getCount() == 0) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (isSound)
                        world.playSound(null, x, y, z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1f, 1.6f);

// if we still have items in the list then move them to player feet position
// or void them ?
                    for (ItemEntity itemIE : items) {
                        if (!itemIE.getItem().isEmpty()) {
                            itemIE.setPosition(x + 0.5, y, z + 0.5);
                        }
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