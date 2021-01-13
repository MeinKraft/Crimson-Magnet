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

import java.util.List;

public class ItemMagnet extends Item {
    public ItemMagnet() { super(new Properties().group(ItemGroup.MISC).maxStackSize(1)); }

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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        boolean active = !stack.getOrCreateTag().getBoolean("active");

        stack.getOrCreateTag().putBoolean("active", active);
        playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1f, (active) ? 0.9f : 0.01f);

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

                    if (items.size() != 0) {
                        for (ItemEntity itemIE : items) {
                            if (CrimsonMagnet.CONFIGURATION.magnetShowParticles.get())
                                ((ServerWorld) worldIn).spawnParticle(ParticleTypes.CLOUD, itemIE.getPosX(), itemIE.getPosY(), itemIE.getPosZ(), 2, 0D, 0D, 0D, 0D);

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
                        // giveExperiencePoints is supposed to play sound, but does not always happen - maybe drowned out by magnet pickup sound?
                        worldIn.playSound(null, x, y, z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1f, 1f);

                        for (ExperienceOrbEntity orb : orbs) {
                            if (CrimsonMagnet.CONFIGURATION.magnetShowParticles.get())
                                ((ServerWorld) worldIn).spawnParticle(ParticleTypes.CLOUD, orb.getPosX(), orb.getPosY(), orb.getPosZ(), 2, 0D, 0D, 0D, 0D);

                            playerIn.giveExperiencePoints(orb.getXpValue());
                            orb.remove();
                        }
                    }
                }
            }
        }
    }
}