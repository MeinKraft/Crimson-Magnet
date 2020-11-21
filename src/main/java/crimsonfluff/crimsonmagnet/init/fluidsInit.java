package crimsonfluff.crimsonmagnet.init;

import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class fluidsInit {
        public static final DeferredRegister<Fluid> FLUIDS =
                DeferredRegister.create(ForgeRegistries.FLUIDS, CrimsonMagnet.MOD_ID);

        public static final RegistryObject<FlowingFluid> XP_FLUID = FLUIDS.register("xp_fluid",
                () -> new ForgeFlowingFluid.Source(fluidsInit.XP_FLUID_PROPERTIES));

        public static final RegistryObject<FlowingFluid> XP_FLUID_FLOWING = FLUIDS.register("xp_flowing",
                () -> new ForgeFlowingFluid.Flowing(fluidsInit.XP_FLUID_PROPERTIES));

        public static final ForgeFlowingFluid.Properties XP_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
                () -> XP_FLUID.get(), () -> XP_FLUID_FLOWING.get(),
                FluidAttributes.builder(
                        new ResourceLocation(CrimsonMagnet.MOD_ID, "fluid/xp_still"),
                        new ResourceLocation(CrimsonMagnet.MOD_ID,"fluid/xp_flowing"))
                        .density(5)
                        .luminosity(13)
                        .rarity(Rarity.RARE)
                        .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
                        .overlay(new ResourceLocation(CrimsonMagnet.MOD_ID,"fluid/xp_overlay")))
                .block(() -> fluidsInit.XP_FLUID_BLOCK.get()).bucket(() -> itemsInit.XP_BUCKET.get());

        public static final RegistryObject<FlowingFluidBlock> XP_FLUID_BLOCK = blocksInit.BLOCKS.register("xp_fluid_block",
                () -> new FlowingFluidBlock(() -> fluidsInit.XP_FLUID.get(), Block.Properties.create(Material.WATER)
                        .setLightLevel(ToIntFunction-> 13)
                        .hardnessAndResistance(100.0f)
                        .noDrops()));
}
