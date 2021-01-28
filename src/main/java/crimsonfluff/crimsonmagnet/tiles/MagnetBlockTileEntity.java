package crimsonfluff.crimsonmagnet.tiles;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import crimsonfluff.crimsonmagnet.init.fluidsInit;
import crimsonfluff.crimsonmagnet.init.tilesInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
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
    protected FluidTank tank = new FluidTank(FluidAttributes.BUCKET_VOLUME * 1) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == fluidsInit.XP_FLUID.get();
        }
    };
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> tank);
    public MagnetBlockTileEntity() { super(tilesInit.MAGNET_BLOCK_TILE.get()); }

    private int ticks=0;
    public int tankFluidAmount() { return tank.getFluidAmount(); }

    @Override
    public void tick() {
        if (!world.isRemote) {
            ticks++;

            if (ticks == 20) {
                ticks = 0;

                int x = this.getPos().getX();
                int y = this.getPos().getY();
                int z = this.getPos().getZ();

                int r = (CrimsonMagnet.CONFIGURATION.magnetBlockRange.get());
                AxisAlignedBB area = new AxisAlignedBB(x - r, y - r, z - r, x + r, y + r, z + r);

                boolean isSound = false;

                // Handle the XP !
                if (CrimsonMagnet.CONFIGURATION.magnetBlockCollectXP.get()) {
                    List<ExperienceOrbEntity> orbs = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, area);

                    if (orbs.size() != 0) {
                        for (ExperienceOrbEntity orb : orbs) {
                            if (orb.getXpValue() <= (this.tank.getCapacity() - this.tank.getFluidAmount())) {
                                this.tank.fill(new FluidStack(fluidsInit.XP_FLUID.get(), orb.getXpValue()), IFluidHandler.FluidAction.EXECUTE);
                                orb.remove();

                                isSound = true;
                                if (CrimsonMagnet.CONFIGURATION.magnetBlockShowParticles.get())
                                    ((ServerWorld) world).spawnParticle(ParticleTypes.CLOUD, orb.getPosX(), orb.getPosY(), orb.getPosZ(), 2, 0D, 0D, 0D, 0D);

                            }
                            else {
                                if (CrimsonMagnet.CONFIGURATION.magnetBlockVoid.get()) {
                                    if (CrimsonMagnet.CONFIGURATION.magnetBlockShowParticles.get())
                                        ((ServerWorld) world).spawnParticle(world.getBlockState(orb.getPosition().offset(Direction.DOWN)).getBlock() == Blocks.SOUL_SOIL ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, orb.getPosX(), orb.getPosY(), orb.getPosZ(), 2, 0D, 0D, 0D, 0D);

                                    if (CrimsonMagnet.CONFIGURATION.magnetBlockCollectSound.get())
                                        world.playSound(null, orb.getPosition(), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1f, 2f);

                                    orb.remove();
                                }
                                else {
                                    orb.setPosition(x + 0.5, y + 1, z + 0.5);
                                    if (CrimsonMagnet.CONFIGURATION.magnetBlockShowParticles.get())
                                        ((ServerWorld) world).spawnParticle(ParticleTypes.CLOUD, orb.getPosX(), orb.getPosY(), orb.getPosZ(), 2, 0D, 0D, 0D, 0D);
                                }
                            }
                        }

                        if (isSound) {
                            if (CrimsonMagnet.CONFIGURATION.magnetBlockCollectSound.get())
                                world.playSound(null, x, y, z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                        }
                    }
                }


                // NOTE: Start from a=2 because first 2 slots are xpBucketIn and xpBucketOut
                // Handle the Items !
                List<ItemEntity> items = world.getEntitiesWithinAABB(EntityType.ITEM, area, item -> !item.getPersistentData().contains("PreventRemoteMovement"));

                int isSpace;    // TODO: Could re-use 'r'
                isSound = false;

                if (items.size() != 0) {
                    // try to merge items found with existing items already in MagnetBlock inventory
                    for (ItemEntity itemIE : items) {
                        for (int a = 2; a < this.getSizeInventory(); a++) {
                            if (this.getStackInSlot(a).getItem() == itemIE.getItem().getItem()) {
                                isSpace = this.getStackInSlot(a).getMaxStackSize() - this.getStackInSlot(a).getCount();

                                if (isSpace != 0) {
                                    isSpace = Math.min(isSpace, itemIE.getItem().getCount());
                                    this.getStackInSlot(a).grow(isSpace);
                                    itemIE.getItem().shrink(isSpace);

                                    isSound = true;
                                    if (itemIE.getItem().getCount() == 0) break;
                                }
                            }
                        }

                        if (CrimsonMagnet.CONFIGURATION.magnetBlockShowParticles.get())
                            ((ServerWorld) world).spawnParticle(ParticleTypes.CLOUD, itemIE.getPosX(), itemIE.getPosY(), itemIE.getPosZ(), 2, 0D, 0D, 0D, 0D);
                    }

                    // if items NOT isEmpty then add them into EMPTY slots
                    // TODO: Test .getCount instead of isEmpty <-cheaper
                    int a = 2;
                    for (ItemEntity itemIE : items) {
                        if (itemIE.getItem().getCount() != 0) {
                            if (a<this.getSizeInventory()) {
                                do {
                                    if (this.getStackInSlot(a).isEmpty()) break;

                                    a++;
                                } while (a < this.getSizeInventory());
                            }

                            if (a<this.getSizeInventory()) {
                                this.setInventorySlotContents(a, itemIE.getItem());

                                isSound = true;
                                itemIE.remove();
                            } else {
                                if (CrimsonMagnet.CONFIGURATION.magnetBlockVoid.get()) {
                                    if (CrimsonMagnet.CONFIGURATION.magnetBlockShowParticles.get())
                                        ((ServerWorld) world).spawnParticle(world.getBlockState(itemIE.getPosition().offset(Direction.DOWN)).getBlock() == Blocks.SOUL_SOIL ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, itemIE.getPosX(), itemIE.getPosY(), itemIE.getPosZ(), 2, 0D, 0D, 0D, 0D);

                                    itemIE.remove();
                                } else
                                    itemIE.setPosition(x + 0.5, y + 1, z + 0.5);
                            }
                        }
                    }

                    if (isSound) {
                        if (CrimsonMagnet.CONFIGURATION.magnetBlockCollectSound.get())
                            world.playSound(null, x, y, z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
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
    public CompoundNBT write(CompoundNBT compound) {
        // Super stores the Inventory and CustomName
        this.tank.writeToNBT(compound);
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        // Super reads the Inventory and CustomName
        super.read(state, compound);
        this.tank.readFromNBT(compound);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container." + CrimsonMagnet.MOD_ID + ".magnet");
    }
}