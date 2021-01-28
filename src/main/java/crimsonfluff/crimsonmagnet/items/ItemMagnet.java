package crimsonfluff.crimsonmagnet.items;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMagnet extends Item {
    public ItemMagnet() { super(new Properties()
            .group(ItemGroup.MISC)
            .maxStackSize(1)
            .maxDamage(CrimsonMagnet.CONFIGURATION.magnetDurability.get())); }

    private int tick=0;

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tip." + CrimsonMagnet.MOD_ID + ".item").mergeStyle(TextFormatting.GREEN));

        tooltip.add(new StringTextComponent("Range is " + CrimsonMagnet.CONFIGURATION.magnetRange.get() + " blocks").mergeStyle(TextFormatting.AQUA));
        tooltip.add(new StringTextComponent("XP Collection is " + (CrimsonMagnet.CONFIGURATION.magnetCollectXP.get() ? "Enabled" : "Disabled")).mergeStyle(TextFormatting.AQUA));

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.isItemEqual(new ItemStack(Items.IRON_INGOT)) ? true : super.getIsRepairable(toRepair, repair);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        boolean active = !stack.getOrCreateTag().getBoolean("active");

        stack.getOrCreateTag().putBoolean("active", active);
        playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1f, (active) ? 0.9f : 0.1f);

        //return new ActionResult<>(ActionResultType.SUCCESS, stack);
        return ActionResult.resultPass(stack);
    }

    // item enchant glint
    @Override
    public boolean hasEffect(ItemStack stack) { return (stack.getOrCreateTag().getBoolean("active")); }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote) {
            tick++;

            if (tick == 20) {
                tick = 0;
                //CrimsonMagnet.LOGGER.info("Tick World Server");

                if (stack.getOrCreateTag().getBoolean("active")) {
                    double x = entityIn.getPosX();
                    double y = entityIn.getPosY();
                    double z = entityIn.getPosZ();

                    PlayerEntity playerIn = (PlayerEntity) entityIn;
                    PlayerInventory inv = playerIn.inventory;

                    int r = (CrimsonMagnet.CONFIGURATION.magnetRange.get());
                    AxisAlignedBB area = new AxisAlignedBB(x - r, y - r, z - r, x + r, y + r, z + r);
                    List<ItemEntity> items = worldIn.getEntitiesWithinAABB(EntityType.ITEM, area, item -> !item.getPersistentData().contains("PreventRemoteMovement"));

                    boolean isSound = false;
                    boolean shouldBreak = false;

                    if (items.size() != 0) {
                        shouldBreak=true;

                        for (ItemEntity itemIE : items) {
                            if (CrimsonMagnet.CONFIGURATION.magnetShowParticles.get())
                                ((ServerWorld) worldIn).spawnParticle(ParticleTypes.POOF, itemIE.getPosX(), itemIE.getPosY(), itemIE.getPosZ(), 8, 0D, 0D, 0D, 0D);

                            // TODO: NOTE: Magnet does not have void option - should it?
                            // If Return StackSize is different then must have been picked up, so isSound=True
                            if (!inv.addItemStackToInventory(itemIE.getItem())) itemIE.setPosition(x, y, z);
                            else isSound = true;
                        }

                        if (isSound) worldIn.playSound(null, x, y, z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1f, 1.6f);
                    }

                    // Handle the XP
                    List<ExperienceOrbEntity> orbs = worldIn.getEntitiesWithinAABB(ExperienceOrbEntity.class, area);

                    if (orbs.size() != 0) {
                        shouldBreak=false;
                        stack.damageItem(1, playerIn, (player) -> { player.sendBreakAnimation(playerIn.inventory.player.getActiveHand()); });
                        worldIn.playSound(null, x, y, z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1f, 1f);

                        playerIn.xpCooldown=0;

                        ArrayList<ItemStack> MendingItems = new ArrayList<>();
                        ItemStack stacks;

                    // getRandomEquippedWithEnchantment only works with offhand, main hand, armor slots
                    // so make a list of valid items and add magnet to it
                    // then randomly choose an item to repair
                        for (int a=36; a<41; a++) {
                            stacks = playerIn.inventory.getStackInSlot(a);
                            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stacks) > 0)
                                if (stacks.isDamaged()) MendingItems.add(stacks);
                        }

                        // Check if Magnet is MENDING then add to list
                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0)
                            if (stack.isDamaged()) MendingItems.add(stack);

                        for (ExperienceOrbEntity orb : orbs) {
                            if (CrimsonMagnet.CONFIGURATION.magnetShowParticles.get())
                                ((ServerWorld) worldIn).spawnParticle(ParticleTypes.POOF, orb.getPosX(), orb.getPosY(), orb.getPosZ(), 8, 0D, 0D, 0D, 0D);

                        // what ?
                            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp(playerIn, orb))) return;

                        // Do I need to call this, as Im processing the Orb ?
                            playerIn.onItemPickup(orb, 1);

                        // Choose random item from MendingItems list
                            if (MendingItems.size() > 0) {
                                r = worldIn.rand.nextInt(MendingItems.size());
                                stacks = MendingItems.get(r);

                                int i = Math.min((int)(orb.xpValue * stacks.getXpRepairRatio()), stacks.getDamage());
                                orb.xpValue -= i/2;     //orb.durabilityToXp(i);
                                stacks.setDamage(stacks.getDamage() - i);

                                if (stacks.getDamage()==0) MendingItems.remove(r);
                            }

                            if (orb.xpValue > 0) playerIn.giveExperiencePoints(orb.xpValue);
                            orb.remove();
                        }
                    }

                    if (shouldBreak) stack.damageItem(1, playerIn, (player) -> { player.sendBreakAnimation(playerIn.inventory.player.getActiveHand()); });
                }
            }
        }
    }
}