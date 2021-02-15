package crimsonfluff.crimsonmagnet.init;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import crimsonfluff.crimsonmagnet.containers.MagnetBlockContainer;
import crimsonfluff.crimsonmagnet.containers.SackContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class containersInit {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, CrimsonMagnet.MOD_ID);

    public static final RegistryObject<ContainerType<MagnetBlockContainer>> MAGNET_CHEST = CONTAINERS.register("generic_chest",
            () -> new ContainerType<>(MagnetBlockContainer::new));

    public static final RegistryObject<ContainerType<SackContainer>> SACK_CHEST = CONTAINERS.register("sack_chest",
            () -> new ContainerType<>(SackContainer::new));

}
