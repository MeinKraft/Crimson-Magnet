package crimsonfluff.crimsonmagnet.blocks;

import crimsonfluff.crimsonmagnet.tiles.MagnetBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockMagnet extends Block {
    public BlockMagnet() {
        super(Properties.create(Material.ROCK)
            .hardnessAndResistance(5.0f, 6.0f)
            .sound(SoundType.STONE)
            .harvestLevel(1)
            .harvestTool(ToolType.PICKAXE)
            .setRequiresTool());
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MagnetBlockTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            INamedContainerProvider inamedcontainerprovider = this.getContainer(state, worldIn, pos);
            if (inamedcontainerprovider != null) player.openContainer(inamedcontainerprovider);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            MagnetBlockTileEntity tile = (MagnetBlockTileEntity) worldIn.getTileEntity(pos);
            if (tile != null) tile.setCustomName(stack.getDisplayName());
        }
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World worldIn, BlockPos pos) {
        return Container.calcRedstoneFromInventory((IInventory) worldIn.getTileEntity(pos));
    }

    @Override
    @Nullable
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        return tile instanceof INamedContainerProvider ? (INamedContainerProvider) tile : null;
    }

    @Override
    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tile = worldIn.getTileEntity(pos);
        return tile != null && tile.receiveClientEvent(id, param);
//        return tile == null ? false : tile.receiveClientEvent(id, param);
    }

    // Called when block is destroyed
    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        MagnetBlockTileEntity tile = (MagnetBlockTileEntity) worldIn.getTileEntity(pos);
        if (tile != null && state.getBlock() != newState.getBlock()) {

            // if MagnetBlock is broken then explode Inventory/XP all over the place
            InventoryHelper.dropInventoryItems(worldIn, pos, tile);

            worldIn.updateComparatorOutputLevel(pos, this);
            worldIn.removeTileEntity(pos);
        }
    }
}
