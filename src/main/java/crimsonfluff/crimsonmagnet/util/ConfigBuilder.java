package crimsonfluff.crimsonmagnet.util;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBuilder {
    public final ForgeConfigSpec COMMON;

    public ForgeConfigSpec.IntValue magnetRange;
    public ForgeConfigSpec.IntValue magnetDurability;
    public ForgeConfigSpec.BooleanValue magnetShowParticles;

    public ForgeConfigSpec.IntValue magnetBlockRange;
    public ForgeConfigSpec.BooleanValue magnetBlockPlaySound;
    public ForgeConfigSpec.BooleanValue magnetBlockShowParticles;

    public ConfigBuilder() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Magnet Settings");

        magnetRange = builder
            .comment("What should the range of the Magnet be ?  Default: 16")
            .defineInRange("Range", 16, 5, 48);

        magnetShowParticles = builder
                .comment("Show particle effects ?  Default: true")
                .define("MagnetShowParticles", true);

        magnetDurability = builder
                .comment("Durability of the Magnet ?  Default: 250")
                .defineInRange("MagnetDurability", 250, 1, 9000);

        builder.pop();


        builder.push("Magnet Block Settings");
        magnetBlockRange = builder
                .comment("What should the range be ?  Default: 16")
                .defineInRange("BlockRange", 16, 16, 48);

        magnetBlockPlaySound = builder
                .comment("Play sound when collecting Items/XP ?  Default: true")
                .define("BlockPlaySound", true);

        magnetBlockShowParticles = builder
                .comment("Show particle effects ?  Default: true")
                .define("BlockShowParticles", true);

        builder.pop();

        COMMON = builder.build();
    }
}
