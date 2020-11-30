package crimsonfluff.crimsonmagnet.init;

import com.google.common.collect.Sets;
import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import crimsonfluff.crimsonmagnet.tiles.MagnetBlockTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class tilesInit {
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CrimsonMagnet.MOD_ID);

    public static final RegistryObject<TileEntityType<MagnetBlockTileEntity>> MAGNET_BLOCK_TILE = TILES.register(
            "magnet_block", () -> new TileEntityType<>(MagnetBlockTileEntity::new, Sets.newHashSet(blocksInit.MAGNET_BLOCK.get()), null));

}
