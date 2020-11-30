package crimsonfluff.crimsonmagnet;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Locale;

public enum GenericChestTypes implements IStringSerializable {
    MAGNET("Magnet", 1, 9, 176, 130, 8, 18, 8, 48, new ResourceLocation(CrimsonMagnet.MOD_ID, "textures/gui/crimson_chest.png"), 256, 256);

    public final String title;                      // title of gui
    public final int size;                          // number of inventory slots (rows * columns)
    public final int rows;                          // number of rows
    public final int cols;                          // number of columns
    public final int xSize;                         // width of GUI cutout
    public final int ySize;                         // height of GUI cutout
    public final int xSlot;                         // x position of first slot
    public final int ySlot;                         // y position of first slot
    public final int xSlotInv;                      // x position of Player Inventory first slot
    public final int ySlotInv;                      // y position of Player Inventory first slot
    public final ResourceLocation guiTexture;       // path to GUI texture to use
    public final int textureXSize;                  // width of GUI texture
    public final int textureYSize;                  // height of GUI texture

    GenericChestTypes(int rows, int cols, int xSize, int ySize, int xSlot, int ySlot, int xSlotInv, int ySlotInv, ResourceLocation guiTexture, int textureXSize, int textureYSize) {
        this(null, rows, cols, xSize, ySize, xSlot, ySlot, xSlotInv, ySlotInv, guiTexture, textureXSize, textureYSize);
    }

    GenericChestTypes(@Nullable String title, int rows, int cols, int xSize, int ySize, int xSlot, int ySlot, int xSlotInv, int ySlotInv, ResourceLocation guiTexture, int textureXSize, int textureYSize) {
        this.title = title == null ? this.name() : title;
        this.size = rows * cols;
        this.rows = rows;
        this.cols = cols;
        this.xSize = xSize;
        this.ySize = ySize;
        this.xSlot = xSlot;
        this.ySlot = ySlot;
        this.ySlotInv = ySlotInv;
        this.xSlotInv = xSlotInv;
        this.guiTexture = guiTexture;
        this.textureXSize = textureXSize;
        this.textureYSize = textureYSize;
    }

    @Override
    public String getString() { return this.name(); }
}