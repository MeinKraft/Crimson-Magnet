package crimsonfluff.crimsonmagnet;

import crimsonfluff.crimsonmagnet.init.itemsInit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("crimsonmagnet")
public class CrimsonMagnet {
    public static final String MOD_ID = "crimsonmagnet";
    public static final Logger LOGGER = LogManager.getLogger("CrimsonMagnet");
    final IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();

    public static final ConfigBuilder CONFIGURATION = new ConfigBuilder();

    public CrimsonMagnet() {
        //CONFIGURATION = new ConfigBuilder();
        MOD_EVENTBUS.addListener(this::setup);
        MOD_EVENTBUS.addListener(this::doClientStuff);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CONFIGURATION.CLIENT);

        itemsInit.ITEMS.register(MOD_EVENTBUS);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLClientSetupEvent event) { }
    private void doClientStuff(final FMLClientSetupEvent event) { }

    //public ConfigBuilder getConfig() { return CONFIGURATION; }
}