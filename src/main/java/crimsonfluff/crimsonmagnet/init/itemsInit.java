package crimsonfluff.crimsonmagnet.init;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import crimsonfluff.crimsonmagnet.items.ItemMagnet;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class itemsInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrimsonMagnet.MOD_ID);

    public static final RegistryObject<Item> MAGNET = ITEMS.register("magnet", ItemMagnet::new);

    public static final RegistryObject<Item> MAGNET_BLOCK = ITEMS.register("magnet_block",
            () -> new BlockItem(blocksInit.MAGNET_BLOCK.get(), new Item.Properties().group(ItemGroup.MISC)));

    public static final RegistryObject<BucketItem> XP_BUCKET = ITEMS.register("xp_bucket",
            () -> new BucketItem(() -> fluidsInit.XP_FLUID.get(),
                    new Item.Properties().group(ItemGroup.MISC).maxStackSize(1)));
}
