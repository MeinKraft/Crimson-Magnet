package crimsonfluff.crimsonmagnet;

import crimsonfluff.crimsonmagnet.init.itemsInit;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.SlotTypeMessage;


@Mod("crimsonmagnet")
public class CrimsonMagnet {
    public static final String MOD_ID = "crimsonmagnet";
    public static final Logger LOGGER = LogManager.getLogger("CrimsonMagnet");
    final IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();

    public static final ConfigBuilder CONFIGURATION = new ConfigBuilder();

    public CrimsonMagnet() {
        MOD_EVENTBUS.addListener(this::setup);
        MOD_EVENTBUS.addListener(this::doClientStuff);
        MOD_EVENTBUS.addListener(this::enqueueIMC);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CONFIGURATION.CLIENT);

        itemsInit.ITEMS.register(MOD_EVENTBUS);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLClientSetupEvent event) { }

    private void doClientStuff(final FMLClientSetupEvent event) { }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        if (Curios.isModLoaded()) {
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                    () -> new SlotTypeMessage
                            .Builder("magnet")
                            .size(1)
                            .icon(new ResourceLocation(CrimsonMagnet.MOD_ID, "item/empty_magnet_slot"))
                            .build());
        }
    }

    @Mod.EventBusSubscriber(modid = CrimsonMagnet.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientProxy {
        @SubscribeEvent
        public static void stitchTextures(TextureStitchEvent.Pre event) {
            if (event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE)) {
                event.addSprite(new ResourceLocation(CrimsonMagnet.MOD_ID, "item/empty_magnet_slot"));
            }
        }
    }
}
