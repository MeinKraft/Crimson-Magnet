package crimsonfluff.crimsonmagnet.init;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import crimsonfluff.crimsonmagnet.items.ItemMagnet;
import crimsonfluff.crimsonmagnet.items.ItemMagnetSack;
import crimsonfluff.crimsonmagnet.items.ItemXPItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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

    public static final RegistryObject<Item> XP_ITEM = ITEMS.register("xp_item", ItemXPItem::new);
    public static final RegistryObject<Item> MAGNET_ITEM = ITEMS.register("magnet", ItemMagnet::new);
    public static final RegistryObject<Item> MAGNET_SACK_ITEM = ITEMS.register("magnet_sack", ItemMagnetSack::new);

    public static final RegistryObject<Item> MAGNET_BLOCK = ITEMS.register("magnet_block",
        () -> new BlockItem(blocksInit.MAGNET_BLOCK.get(), new Item.Properties().group(ItemGroup.MISC)) {
            @OnlyIn(Dist.CLIENT)
            @Override
            public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                tooltip.add(new TranslationTextComponent("tip." + CrimsonMagnet.MOD_ID + ".block").mergeStyle(TextFormatting.GREEN));
                tooltip.add(new StringTextComponent("Range is " + CrimsonMagnet.CONFIGURATION.magnetBlockRange.get() + " blocks").mergeStyle(TextFormatting.AQUA));

                super.addInformation(stack, worldIn, tooltip, flagIn);
            }
        });
}
