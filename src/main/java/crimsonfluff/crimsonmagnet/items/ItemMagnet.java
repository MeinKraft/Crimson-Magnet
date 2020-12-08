package crimsonfluff.crimsonmagnet.items;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemMagnet extends Item {
    public ItemMagnet() { super(new Properties().group(ItemGroup.MISC).maxStackSize(1)); }

    private int tick=0;

    //BlockPos converts to integer, so either center on the block its found on or store x/y/z independently
    //private ArrayList<BlockPos> particlePos = new ArrayList<BlockPos>();
    private ArrayList<Double> particlePosX = new ArrayList<Double>();
    private ArrayList<Double> particlePosY = new ArrayList<Double>();
    private ArrayList<Double> particlePosZ = new ArrayList<Double>();

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add((new TranslationTextComponent("tip." + CrimsonMagnet.MOD_ID + ".item").mergeStyle(TextFormatting.GREEN)));

        String isXPCS;
        if (CrimsonMagnet.CONFIGURATION.magnetCollectXP.get())
            isXPCS = "Enabled";
        else
            isXPCS = "Disabled";

        tooltip.add(new StringTextComponent("Range is " + CrimsonMagnet.CONFIGURATION.magnetRange.get() + " blocks").mergeStyle(TextFormatting.AQUA));
        tooltip.add(new StringTextComponent("XP Collection is " + isXPCS).mergeStyle(TextFormatting.AQUA));

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        // no need to check instance because were inside the item we need's class
        //if(!playerIn.world.isRemote && stack.getItem() instanceof ItemRuby){

        //boolean active = stack.getOrCreateTag().contains("active") && stack.getOrCreateTag().getBoolean("active");
        boolean active = !stack.getOrCreateTag().getBoolean("active");
        float fPitch;

        stack.getOrCreateTag().putBoolean("active", active);

        fPitch = (active) ? 0.9f : 0.01f;
        playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1f, fPitch);

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    // item enchant glint
    @Override
    public boolean hasEffect(ItemStack stack) { return (stack.getOrCreateTag().getBoolean("active")); }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        tick++;

        if (worldIn.isRemote) {
            //CrimsonMagnet.LOGGER.info("STAGE ONE");
            if (particlePosX.size() != 0) {
//                CrimsonMagnet.LOGGER.info("STAGE TWO");

                for (int a=0; a<particlePosX.size(); a++) {
                    worldIn.addParticle(ParticleTypes.CLOUD, particlePosX.get(a), particlePosY.get(a), particlePosZ.get(a), 0.0D, 0.0D, 0.0D);
                    //worldIn.addParticle(ParticleTypes.CLOUD, particlePosX.get(a), particlePosY.get(a), particlePosZ.get(a), 0.0D, 0.0D, 0.0D);
                }

                particlePosX.clear();
                particlePosY.clear();
                particlePosZ.clear();
                return;
            }
        }

        if (tick == 20) {
            tick = 0;

            if (!worldIn.isRemote) {
                if (stack.getOrCreateTag().getBoolean("active")) {
                    double x = entityIn.getPosX();
                    double y = entityIn.getPosY();
                    double z = entityIn.getPosZ();

                    PlayerEntity playerIn = (PlayerEntity) entityIn;
                    PlayerInventory inv = playerIn.inventory;

                    int r = (CrimsonMagnet.CONFIGURATION.magnetRange.get());
                    AxisAlignedBB area = new AxisAlignedBB(x - r, y - r, z - r, x + r, y + r, z + r);
                    List<ItemEntity> items = worldIn.getEntitiesWithinAABB(EntityType.ITEM, area, item -> !item.getPersistentData().contains("PreventRemoteMovement"));

                    int isSpace = 0;
                    boolean isSound = false;

                    //CrimsonMagnet.LOGGER.info("MAGNET: Ticking");

                    if (items.size() != 0) {
// try to merge items found with existing items already in Player inventory
                        for (ItemEntity itemIE : items) {
                            if (CrimsonMagnet.CONFIGURATION.magnetShowParticles.get()) {
                                particlePosX.add(itemIE.getPosX());
                                particlePosY.add(itemIE.getPosY());
                                particlePosZ.add(itemIE.getPosZ());
                            }

                            for (int a = 0; a < inv.getSizeInventory(); a++) {
                                // dont include slots 36,37,38,39 which are boots, leggings, chestplate, helmet
                                if ((a <= 35) || (a >= 40)) {
                                    if (inv.getStackInSlot(a).getItem() == itemIE.getItem().getItem()) {
                                        isSpace = inv.getStackInSlot(a).getMaxStackSize() - inv.getStackInSlot(a).getCount();

                                        if (isSpace != 0) {
                                            isSpace = Math.min(isSpace, itemIE.getItem().getCount());
                                            inv.getStackInSlot(a).grow(isSpace);
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
                                for (int a = 0; a < inv.getSizeInventory(); a++) {
                                    // dont include slots 36,37,38,39 which are boots, leggings, chestplate, helmet
                                    if ((a <= 35) || (a >= 40)) {
                                        if (inv.getStackInSlot(a).isEmpty()) {
                                            inv.setInventorySlotContents(a, itemIE.getItem());

                                            itemIE.remove();
                                            isSound = true;

                                            break;
                                        } else {
                                            if (inv.getStackInSlot(a).getItem() == itemIE.getItem().getItem()) {
                                                isSpace = inv.getStackInSlot(a).getMaxStackSize() - inv.getStackInSlot(a).getCount();

                                                if (isSpace != 0) {
                                                    isSpace = Math.min(isSpace, itemIE.getItem().getCount());
                                                    inv.getStackInSlot(a).grow(isSpace);
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
                            worldIn.playSound(null, x, y, z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1f, 1.6f);

// if we still have items in the list then move them to player feet position
// or void them ?
                        for (ItemEntity itemIE : items) {
                            if (!itemIE.getItem().isEmpty()) {
                                itemIE.setPosition(x + 0.5, y, z + 0.5);
                            }
                        }
                    }

// Handle the XP
                    List<ExperienceOrbEntity> orbs = worldIn.getEntitiesWithinAABB(ExperienceOrbEntity.class, area);

                    if (orbs.size() != 0) {
                        worldIn.playSound(null, x, y, z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1f, 1f);

                        for (ExperienceOrbEntity orb : orbs) {
                            if (CrimsonMagnet.CONFIGURATION.magnetShowParticles.get()) {
                                particlePosX.add(orb.getPosX());
                                particlePosY.add(orb.getPosY());
                                particlePosZ.add(orb.getPosZ());
                            }

                            playerIn.giveExperiencePoints(orb.getXpValue());
                            orb.remove();
                        }
                    }
                }
            }
        }
    }
}