package crimsonfluff.crimsonmagnet;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBuilder {
    //public final ForgeConfigSpec SERVER;
    public final ForgeConfigSpec CLIENT;

    /*
    public ForgeConfigSpec.IntValue maxMillisecondsUploadingPerFrame;
    public ForgeConfigSpec.DoubleValue radialMenuVolume;
    public ForgeConfigSpec.BooleanValue enableToolbarIcons;
    public ForgeConfigSpec.LongValue bitsPerTypeSlot;
    */

    public ForgeConfigSpec.IntValue magnetRange;
    public ForgeConfigSpec.BooleanValue magnetCollectXP;

    public ConfigBuilder() {
        //CLIENT
        {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            builder.comment("General Settings");
            builder.push("general");

            magnetRange = builder
                    .comment("What should the range of the Magnet be ?")
                    .defineInRange("Range", 16, 5, 48);

            magnetCollectXP = builder
                    .comment("Should the Magnet collect XP Orbs ?")
                    .define("CollectXP", true);

            builder.pop();

            CLIENT = builder.build();
        }
    }
}
