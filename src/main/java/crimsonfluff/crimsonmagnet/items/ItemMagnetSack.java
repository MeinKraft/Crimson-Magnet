package crimsonfluff.crimsonmagnet.items;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import crimsonfluff.crimsonmagnet.containers.SackContainer;
import crimsonfluff.crimsonmagnet.init.itemsInit;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemMagnetSack extends Item {
    public ItemMagnetSack() {
        super(new Properties().group(ItemGroup.MISC).maxStackSize(1));
    }

    public IInventory inventoryChest = null;//new Inventory(39);

    private int tick = 0;
    private boolean init = false;

    @Override
    public boolean updateItemStackNBT(CompoundNBT nbt) {
        CrimsonMagnet.LOGGER.info("EVERHERE");

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tip." + CrimsonMagnet.MOD_ID + ".magnet_sack").mergeStyle(TextFormatting.GREEN));

        tooltip.add(new StringTextComponent("Range is " + CrimsonMagnet.CONFIGURATION.magnetRange.get() + " blocks").mergeStyle(TextFormatting.AQUA));

        if (inventoryChest != null) {
            tooltip.add(new StringTextComponent("XP Collection is " + (this.inventoryChest.getStackInSlot(0).isEmpty() ? "\u00A74Inactive" : "\u00A72Active")).mergeStyle(TextFormatting.AQUA));
            tooltip.add(new StringTextComponent("Void is " + (this.inventoryChest.getStackInSlot(1).isEmpty() ? "\u00A74Inactive" : "\u00A72Active")).mergeStyle(TextFormatting.AQUA));
            tooltip.add(new StringTextComponent("Filter is " + (this.inventoryChest.getStackInSlot(2).isEmpty() ? "\u00A74Inactive" : "\u00A72Active")).mergeStyle(TextFormatting.AQUA));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) return ActionResult.resultSuccess(stack);

        boolean active;

        long WINDOW = Minecraft.getInstance().getMainWindow().getHandle();
        if (InputMappings.isKeyDown(WINDOW, GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(WINDOW, GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            NetworkHooks.openGui((ServerPlayerEntity) playerIn, new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return stack.hasDisplayName() ? stack.getDisplayName() : new TranslationTextComponent("container." + CrimsonMagnet.MOD_ID + ".sack");
                }

                @Nullable
                @Override
                public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
//                    CrimsonMagnet.LOGGER.info("ITEMS: " + inventoryChest.getStackInSlot(0).toString());
//                    CrimsonMagnet.LOGGER.info("OPEN: " + stack.getTag());

                    //loadAllItems(stack.getTag());
                    return new SackContainer(windowId, inventory, ((ItemMagnetSack)inventory.getCurrentItem().getItem()).inventoryChest);  // ((ItemMagnetSack) stack.getItem()).inventoryChest
                }
            }, data -> data.writeInt(playerIn.inventory.currentItem));

        } else {
            active = !stack.getOrCreateTag().getBoolean("active");
            stack.getOrCreateTag().putBoolean("active", active);

            playerIn.sendStatusMessage(new StringTextComponent("\u00A7b"+stack.getDisplayName().getString()+" is now " + (active ? "\u00A72Active" : "\u00A74Inactive")), true);
            worldIn.playSound(null, playerIn.getPosition(), SoundEvents.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1f, (active) ? 0.9f : 0.1f);
        }

        return ActionResult.resultSuccess(stack);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return (stack.getOrCreateTag().getBoolean("active"));
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {

//    if (inventoryChest==null) {
//        inventoryChest = new Inventory(39);
//        loadAllItems(stack.getTag());
//    }

        if (!worldIn.isRemote) {
            tick++;

            // find a better way to read the nbt items list on world load?
//            if (init==false) {
//                init = true;
//
//                if (stack.hasTag()) loadAllItems(stack.getTag());
//            }

            if (tick == 10) {
                tick = 0;
                CrimsonMagnet.LOGGER.info("Tick: " + itemSlot + " : " + stack.getDisplayName().getString());

                if (stack.getOrCreateTag().getBoolean("active")) {
                    double x = entityIn.getPosX();
                    double y = entityIn.getPosY();
                    double z = entityIn.getPosZ();

                    int r = (CrimsonMagnet.CONFIGURATION.magnetRange.get());
                    boolean isSound = false;
                    int itemCount;

                    AxisAlignedBB area = new AxisAlignedBB(x - r, y - r, z - r, x + r, y + r, z + r);
                    List<ItemEntity> items = worldIn.getEntitiesWithinAABB(EntityType.ITEM, area, item -> !item.getPersistentData().contains("PreventRemoteMovement"));

                    boolean isOK = true;
                    ItemStack filterItem;

                    if (items.size() > 0) {
                        for (ItemEntity itemIE : items) {
                            // check items against filter
                            if (!inventoryChest.getStackInSlot(2).isEmpty()) {
                                isOK = false;

                                for (int a = 3; a < inventoryChest.getSizeInventory(); a++) {
                                    filterItem = inventoryChest.getStackInSlot(a);

                                    if (itemIE.getItem().getItem() == filterItem.getItem()) {
                                        isOK = true;
                                        break;
                                    }
                                }
                            } //else isOK = true;

                            if (isOK) {
                                itemCount = itemIE.getItem().getCount();        // Leave here - need count before to see if any items actually moved
                                                                                // into magnet inventory from addItemToMagnetInventory

                                if (addItemToMagnetInventory(itemIE.getItem())) {
                                    if (CrimsonMagnet.CONFIGURATION.magnetShowParticles.get())
                                        ((ServerWorld) worldIn).spawnParticle(ParticleTypes.POOF, itemIE.getPosX(), itemIE.getPosY(), itemIE.getPosZ(), 2, 0D, 0D, 0D, 0D);

                                    isSound = true;
                                    itemIE.remove();

                                } else {
                                    // if item count is different then some items MUST have been picked up
                                    // therefore play sound
                                    isSound = (itemIE.getItem().getCount() != itemCount);

                                    if (!inventoryChest.getStackInSlot(1).isEmpty()) {
                                        if (CrimsonMagnet.CONFIGURATION.magnetBlockPlaySound.get())
                                            worldIn.playSound(null, itemIE.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 2f);

                                        if (CrimsonMagnet.CONFIGURATION.magnetShowParticles.get())
                                            ((ServerWorld) worldIn).spawnParticle(worldIn.getBlockState(itemIE.getPosition().offset(Direction.DOWN)).getBlock() == Blocks.SOUL_SOIL ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, itemIE.getPosX(), itemIE.getPosY() + 0.3, itemIE.getPosZ(), 5, 0D, 0D, 0D, 0D);

                                        itemIE.remove();

                                    } else
                                        itemIE.setPosition(x + 0.5, y + 1, z + 0.5);
                                }
                            }
                        }

                        if (isSound)
                            worldIn.playSound(null, x, y, z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1.6f);
                    }


                    if (!inventoryChest.getStackInSlot(0).isEmpty()) {
                        List<ExperienceOrbEntity> orbs = worldIn.getEntitiesWithinAABB(ExperienceOrbEntity.class, area);

                        if (orbs.size() > 0) {
                            isSound = false;

                            for (ExperienceOrbEntity ORB : orbs) {
                                ItemStack newOrb = new ItemStack(itemsInit.XP_ITEM.get());
                                newOrb.setCount(Integer.min(64, ORB.getXpValue()));        // should never really get 1x Orb of large size, but just in case
                                itemCount = newOrb.getCount();

                                if (addItemToMagnetInventory(newOrb)) {
                                    ORB.remove();
                                    isSound = true;

                                    if (CrimsonMagnet.CONFIGURATION.magnetShowParticles.get())
                                        ((ServerWorld) worldIn).spawnParticle(ParticleTypes.POOF, ORB.getPosX(), ORB.getPosY(), ORB.getPosZ(), 2, 0D, 0D, 0D, 0D);

                                } else {
                                    isSound = (newOrb.getCount() != itemCount);

                                    if (!inventoryChest.getStackInSlot(1).isEmpty()) {
                                        if (CrimsonMagnet.CONFIGURATION.magnetBlockPlaySound.get())
                                            worldIn.playSound(null, ORB.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 2f);

                                        if (CrimsonMagnet.CONFIGURATION.magnetShowParticles.get())
                                            ((ServerWorld) worldIn).spawnParticle(worldIn.getBlockState(ORB.getPosition().offset(Direction.DOWN)).getBlock() == Blocks.SOUL_SOIL ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, ORB.getPosX(), ORB.getPosY() + 0.3, ORB.getPosZ(), 5, 0D, 0D, 0D, 0D);

                                        ORB.remove();
                                    } else
                                        ORB.setPosition(x + 0.5, y + 1, z + 0.5);
                                }
                            }

                            if (CrimsonMagnet.CONFIGURATION.magnetBlockPlaySound.get())
                                if (isSound)
                                    worldIn.playSound(null, x, y, z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                        }
                    }

                    // if items changed then re-save Items as NBT
                    // TODO: Replace isSound (here) with hasChanged - if contents changed then re-save list
                    // if Container is open then reload list ?
                    // or just pause magnet while container is open ?
                    if (isSound) {
                        stack.setAnimationsToGo(5);

                        ListNBT listnbt = new ListNBT();

                        for (int i = 0; i < inventoryChest.getSizeInventory(); ++i) {
                            ItemStack itemstack = inventoryChest.getStackInSlot(i);

                            if (!itemstack.isEmpty()) {
                                CompoundNBT compoundnbt = new CompoundNBT();
                                compoundnbt.putByte("Slot", (byte) i);
                                itemstack.write(compoundnbt);
                                listnbt.add(compoundnbt);
                            }
                        }

                        if (!listnbt.isEmpty()) stack.getOrCreateTag().put("Items", listnbt);
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
                inventoryChest.setInventorySlotContents(slot, inputStack);    //  use ACTUAL ItemStack  ?!?!
                return true;

            } else {
                isSpace = inventoryChest.getStackInSlot(slot).getMaxStackSize() - inventoryChest.getStackInSlot(slot).getCount();
                itemsToCopy = Integer.min(isSpace, inputStack.getCount());

                inputStack.shrink(itemsToCopy);
                inventoryChest.getStackInSlot(slot).grow(itemsToCopy);
            }

        } while (inputStack.getCount()>0);

        return true;
    }

    private int getSimilarSlot(ItemStack inputStack) {
        // Find similar stack in slot, else return -1
        int isSpace;

        for (int a = 3; a < inventoryChest.getSizeInventory(); a++) {
            if (inventoryChest.getStackInSlot(a).getItem() == inputStack.getItem()) {
                isSpace = inventoryChest.getStackInSlot(a).getMaxStackSize() - inventoryChest.getStackInSlot(a).getCount();

                if (isSpace > 0) return a;
            }
        }

        return -1;
    }

    private int getEmptySlot() {
        // Find EMPTY stack slot, else return -1
        for (int a = 3; a < inventoryChest.getSizeInventory(); a++) {
            if (inventoryChest.getStackInSlot(a).isEmpty()) return a;
        }

        return -1;
    }

    public void loadAllItems(CompoundNBT tag) {
        ListNBT listnbt = tag.getList("Items", 10);
        //if (listnbt.size()==0) return;

        inventoryChest.clear();

        for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;

//            if (j >= 0 && j < inventoryChest.getSizeInventory()) {
                inventoryChest.setInventorySlotContents(j, ItemStack.read(compoundnbt));
//            }
        }
    }
}
