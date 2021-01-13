package crimsonfluff.crimsonmagnet.init;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import crimsonfluff.crimsonmagnet.items.ItemMagnet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class itemsInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrimsonMagnet.MOD_ID);

    public static final RegistryObject<Item> MAGNET_ITEM = ITEMS.register("magnet", ItemMagnet::new);

    public static final RegistryObject<Item> MAGNET_BLOCK = ITEMS.register("magnet_block",
            () -> new BlockItem(blocksInit.MAGNET_BLOCK.get(), new Item.Properties().group(ItemGroup.MISC)) {
                @OnlyIn(Dist.CLIENT)
                @Override
                public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                    tooltip.add(new TranslationTextComponent("tip." + CrimsonMagnet.MOD_ID + ".block").mergeStyle(TextFormatting.GREEN));

                    String isXPCS;
                    if (CrimsonMagnet.CONFIGURATION.magnetBlockCollectXP.get())
                        isXPCS = "Enabled";
                    else
                        isXPCS = "Disabled";

                    tooltip.add(new StringTextComponent("Range is " + CrimsonMagnet.CONFIGURATION.magnetBlockRange.get() + " blocks").mergeStyle(TextFormatting.AQUA));
                    tooltip.add(new StringTextComponent("XP Collection is " + isXPCS).mergeStyle(TextFormatting.AQUA));

                    super.addInformation(stack, worldIn, tooltip, flagIn);
                }
            });

    public static final RegistryObject<BucketItem> XP_BUCKET = ITEMS.register("xp_bucket",
            () -> new BucketItem(() -> fluidsInit.XP_FLUID.get(),
                    new Item.Properties().group(ItemGroup.MISC).maxStackSize(1)));
}
