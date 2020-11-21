package crimsonfluff.crimsonmagnet.init;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import crimsonfluff.crimsonmagnet.tiles.TileMagnetBlock;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class tilesInit {
    public static final DeferredRegister<TileEntityType<?>> TILES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CrimsonMagnet.MOD_ID);

    public static final RegistryObject<TileEntityType<TileMagnetBlock>> MAGNET_BLOCK_TILE = TILES.register("magnet_block_tile",
            ()-> TileEntityType.Builder.create(TileMagnetBlock::new, blocksInit.MAGNET_BLOCK.get()).build(null));
}
