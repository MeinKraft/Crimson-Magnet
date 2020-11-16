package crimsonfluff.crimsonmagnet.items;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ItemMagnet extends Item {
    public ItemMagnet() { super(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1)); }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add((new TranslationTextComponent("tip." + CrimsonMagnet.MOD_ID + ".item").mergeStyle(TextFormatting.GRAY)));

        String isXPCS;
        if (CrimsonMagnet.CONFIGURATION.magnetCollectXP.get())
            isXPCS = "Enabled";
        else
            isXPCS = "Disabled";

        tooltip.add(new StringTextComponent("Range is " + CrimsonMagnet.CONFIGURATION.magnetRange.get() + " blocks").mergeStyle(TextFormatting.YELLOW));
        tooltip.add(new StringTextComponent("XP Collection is " + isXPCS).mergeStyle(TextFormatting.YELLOW));

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

        if (active) fPitch = 0.9f;
        else fPitch = 0.01f;
        playerIn.world.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1f, fPitch);

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    // item enchant glint
    @Override
    public boolean hasEffect(ItemStack stack) { return (stack.getOrCreateTag().getBoolean("active")); }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn.ticksExisted%20 == 0) {
            //CrimsonMagnet.LOGGER.info("Magnet Ticking");

            if (stack.getOrCreateTag().getBoolean("active")) {
                int r = (CrimsonMagnet.CONFIGURATION.magnetRange.get());
                AxisAlignedBB area = new AxisAlignedBB(entityIn.getPositionVec().add(-r, -r, -r), entityIn.getPositionVec().add(r, r, r));

                List<ItemEntity> items = worldIn.getEntitiesWithinAABB(EntityType.ITEM, area, item -> !item.getPersistentData().contains("PreventRemoteMovement"));
                items.forEach(item -> item.setPosition(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ()));

                if (!worldIn.isRemote && entityIn instanceof PlayerEntity) {
                    if (CrimsonMagnet.CONFIGURATION.magnetCollectXP.get()) {
                        PlayerEntity player = (PlayerEntity) entityIn;
                        List<ExperienceOrbEntity> orbs = worldIn.getEntitiesWithinAABB(ExperienceOrbEntity.class, area);

                        orbs.forEach(orb -> {
                            orb.delayBeforeCanPickup = 0;
                            player.xpCooldown = 0;
                            orb.onCollideWithPlayer(player);
                        });
                    }
                }
            }
        }
    }
}