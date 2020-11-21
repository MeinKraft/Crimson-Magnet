package crimsonfluff.crimsonmagnet.init;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import crimsonfluff.crimsonmagnet.blocks.BlockMagnet;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class blocksInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CrimsonMagnet.MOD_ID);

    public static final RegistryObject<Block> MAGNET_BLOCK = BLOCKS.register("magnet_block", BlockMagnet::new);
}
