package crimsonfluff.crimsonmagnet.tiles;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import crimsonfluff.crimsonmagnet.init.itemsInit;
import crimsonfluff.crimsonmagnet.init.tilesInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class MagnetBlockTileEntity extends ChestTileEntity {
    public MagnetBlockTileEntity() { super(tilesInit.MAGNET_BLOCK_TILE.get()); }
    private int ticks=0;

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
                boolean isSound = false;
                int itemCount = 0;

                AxisAlignedBB area = new AxisAlignedBB(x - r, y - 2, z - r, x + r, y + 2, z + r);
                List<ItemEntity> items = world.getEntitiesWithinAABB(EntityType.ITEM, area, item -> !item.getPersistentData().contains("PreventRemoteMovement"));

                if (items.size() > 0) {
                    for (ItemEntity itemIE : items) {
                        itemCount = itemIE.getItem().getCount();

                        if (addItemToMagnetInventory(itemIE.getItem())) {
                            itemIE.remove();
                            isSound = true;

                            if (CrimsonMagnet.CONFIGURATION.magnetBlockShowParticles.get())
                                ((ServerWorld) world).spawnParticle(ParticleTypes.POOF, itemIE.getPosX(), itemIE.getPosY(), itemIE.getPosZ(), 2, 0D, 0D, 0D, 0D);

                        } else {
                            // if item count is different then some items MUST have been picked up
                            // therefore play sound
                            isSound = (itemIE.getItem().getCount() != itemCount);

                            if (!this.getStackInSlot(1).isEmpty()) {
                                if (CrimsonMagnet.CONFIGURATION.magnetBlockPlaySound.get())
                                    world.playSound(null, itemIE.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 2f);

                                if (CrimsonMagnet.CONFIGURATION.magnetBlockShowParticles.get())
                                    ((ServerWorld) world).spawnParticle(world.getBlockState(itemIE.getPosition().offset(Direction.DOWN)).getBlock() == Blocks.SOUL_SOIL ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, itemIE.getPosX(), itemIE.getPosY()+0.3, itemIE.getPosZ(), 5, 0D, 0D, 0D, 0D);

                                itemIE.remove();
                            } else
                                itemIE.setPosition(x + 0.5, y + 1, z + 0.5);
                        }
                    }

                    if (CrimsonMagnet.CONFIGURATION.magnetBlockPlaySound.get())
                        if (isSound) world.playSound(null, x, y, z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1.6f);
                }


                if (!this.getStackInSlot(0).isEmpty()) {
                    List<ExperienceOrbEntity> orbs = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, area);

                    if (orbs.size() > 0) {
                        isSound = false;

                        for (ExperienceOrbEntity ORB : orbs) {
                            ItemStack newOrb = new ItemStack(itemsInit.XP_ITEM.get());
                            newOrb.setCount(Integer.min(64, ORB.getXpValue()));        // should never really get 1x Orb of large size, but just in case
                            itemCount = newOrb.getCount();
                            
                            if (addItemToMagnetInventory(newOrb)) {
                                ORB.remove();
                                isSound = true;

                                if (CrimsonMagnet.CONFIGURATION.magnetBlockShowParticles.get())
                                    ((ServerWorld) world).spawnParticle(ParticleTypes.POOF, ORB.getPosX(), ORB.getPosY(), ORB.getPosZ(), 2, 0D, 0D, 0D, 0D);

                            } else {
                                isSound = (newOrb.getCount() != itemCount);

                                if (!this.getStackInSlot(1).isEmpty()) {
                                    if (CrimsonMagnet.CONFIGURATION.magnetBlockPlaySound.get())
                                        world.playSound(null, ORB.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 2f);

                                    if (CrimsonMagnet.CONFIGURATION.magnetBlockShowParticles.get())
                                        ((ServerWorld) world).spawnParticle(world.getBlockState(ORB.getPosition().offset(Direction.DOWN)).getBlock() == Blocks.SOUL_SOIL ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, ORB.getPosX(), ORB.getPosY() + 0.3, ORB.getPosZ(), 5, 0D, 0D, 0D, 0D);

                                    ORB.remove();
                                } else
                                    ORB.setPosition(x + 0.5, y + 1, z + 0.5);
                            }
                        }

                        if (CrimsonMagnet.CONFIGURATION.magnetBlockPlaySound.get())
                            if (isSound) world.playSound(null, x, y, z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                    }
                }
            }
        }
    }

    private boolean addItemToMagnetInventory(ItemStack inputStack) {
    int isSpace = 0;
    int itemsToCopy = 0;

        do {
            int slot = getSimilarSlot(inputStack);
            if (slot == -1) {
                slot = getEmptySlot();
                if (slot == -1) return false;

                // TODO: Using actual ItemStack may cause a problem - because of the ItemIE.remove() ?!?!
                this.setInventorySlotContents(slot, inputStack);    //  use ACTUAL ItemStack  ?!?!
                return true;

            } else {
                isSpace = this.getStackInSlot(slot).getMaxStackSize() - this.getStackInSlot(slot).getCount();
                itemsToCopy = Integer.min(isSpace, inputStack.getCount());

                inputStack.shrink(itemsToCopy);
                this.getStackInSlot(slot).grow(itemsToCopy);
            }

        } while (inputStack.getCount()>0);

        return true;
    }

    private int getSimilarSlot(ItemStack inputStack) {
        // Find similar stack in slot, else return -1
        int isSpace;

        for (int a = 2; a < this.getSizeInventory(); a++) {
            if (this.getStackInSlot(a).getItem() == inputStack.getItem()) {
                isSpace = this.getStackInSlot(a).getMaxStackSize() - this.getStackInSlot(a).getCount();

                if (isSpace > 0) return a;
            }
        }

        return -1;
    }

    private int getEmptySlot() {
        // Find EMPTY stack slot, else return -1
        for (int a = 2; a < this.getSizeInventory(); a++) {
            if (this.getStackInSlot(a).isEmpty()) return a;
        }

        return -1;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        // Super stores the Inventory and CustomName
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        // Super reads the Inventory and CustomName
        super.read(state, compound);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container." + CrimsonMagnet.MOD_ID + ".magnet");
    }
}
