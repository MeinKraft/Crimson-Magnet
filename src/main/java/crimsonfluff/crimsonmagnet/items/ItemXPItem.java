package crimsonfluff.crimsonmagnet.items;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ItemXPItem extends Item {
    public ItemXPItem() { super(new Properties().group(ItemGroup.MISC)); }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tip."+ CrimsonMagnet.MOD_ID+".xp_item").mergeStyle(TextFormatting.YELLOW));

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1f, 1f);

        int isAmount = playerIn.isCrouching() ? stack.getCount() : 1;

        if (!playerIn.isCreative()) stack.shrink(isAmount);

        playerIn.giveExperiencePoints(isAmount);

        return ActionResult.resultSuccess(stack);
    }
}
