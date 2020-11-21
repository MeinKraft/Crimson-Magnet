package crimsonfluff.crimsonmagnet;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBuilder {
    //public final ForgeConfigSpec CLIENT;
    public final ForgeConfigSpec CLIENT;

    public ForgeConfigSpec.IntValue magnetRange;
    public ForgeConfigSpec.IntValue magnetBlockRange;
    public ForgeConfigSpec.BooleanValue magnetCollectXP;
    public ForgeConfigSpec.BooleanValue magnetBlockCollectXP;
    public ForgeConfigSpec.BooleanValue magnetBlockVoid;
    public ForgeConfigSpec.BooleanValue magnetBlockCollectXPSound;
    public ForgeConfigSpec.BooleanValue magnetShowParticles;
    public ForgeConfigSpec.BooleanValue magnetBlockShowParticles;

    public ConfigBuilder() {
        //CLIENT
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Magnet Settings");

        magnetRange = builder
            .comment("What should the range of the Magnet be ?")
            .defineInRange("Range", 16, 5, 48);

        magnetCollectXP = builder
                .comment("Should the Magnet collect XP Orbs ?")
                .define("CollectXP", true);

        magnetShowParticles = builder
                .comment("Should the Magnet show particle effects ?")
                .define("MagnetShowParticles", true);

        magnetBlockRange = builder
                .comment("What should the range of the Magnet Block be ?")
                .defineInRange("BlockRange", 16, 16, 48);

        magnetBlockCollectXP = builder
                .comment("Should the Magnet Block collect XP Orbs ?")
                .define("BlockCollectXP", true);

        magnetBlockVoid = builder
                .comment("Should the Magnet Block Void Items/XP when full ?")
                .define("BlockVoidCollect", true);

        magnetBlockCollectXPSound = builder
                .comment("Should the Magnet Block play sound when collecting XP ?")
                .define("BlockCollectXPSound", true);

        magnetBlockShowParticles = builder
                .comment("Should the Magnet Block show particle effects ?")
                .define("BlockShowParticles", true);

        builder.pop();

        CLIENT = builder.build();
    }
}
