package crimsonfluff.crimsonmagnet.containers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import crimsonfluff.crimsonmagnet.CrimsonMagnet;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SackScreen extends ContainerScreen<SackContainer> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(CrimsonMagnet.MOD_ID, "textures/gui/magnet_sack.png");

    public SackScreen(SackContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.ySize = 184;
        this.xSize = 202 - 26;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.func_243248_b(matrixStack, this.title, 8, 6.0F, 4210752);

        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), 8, (float) (this.ySize - 93), 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        blit(matrixStack, x - 26, y, 0, 0, this.xSize + 26, this.ySize, 256, 256);
    }
}
