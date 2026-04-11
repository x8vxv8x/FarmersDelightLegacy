package com.wdcftgg.farmersdelightlegacy.common.item;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockCanvasWallHangingSign;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCanvasSign;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCanvasHangingSign extends ItemBlock {

    private final Block wallBlock;

    public ItemCanvasHangingSign(Block hangingBlock, Block wallBlock) {
        super(hangingBlock);
        this.wallBlock = wallBlock;
        this.maxStackSize = 16;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!facing.getAxis().isHorizontal()) {
            return EnumActionResult.FAIL;
        }

        IBlockState targetState = worldIn.getBlockState(pos);
        boolean canReplace = targetState.getBlock().isReplaceable(worldIn, pos);
        BlockPos placePos = canReplace ? pos : pos.offset(facing);

        if (!player.canPlayerEdit(placePos, facing, stack)) {
            return EnumActionResult.FAIL;
        }

        PlacementSelection placementSelection = selectPlacement(facing);
        if (placementSelection == null) {
            return EnumActionResult.FAIL;
        }

        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        }

        if (!worldIn.mayPlace(placementSelection.block, placePos, false, facing, null)) {
            return EnumActionResult.FAIL;
        }

        worldIn.setBlockState(placePos, placementSelection.state, 11);
        TileEntity tileEntity = worldIn.getTileEntity(placePos);
        if (tileEntity instanceof TileEntityCanvasSign) {
            TileEntityCanvasSign canvasSign = (TileEntityCanvasSign) tileEntity;
            configureHangingTextFace(player, placePos, placementSelection.state, canvasSign);
            if (!setTileEntityNBT(worldIn, player, placePos, stack)) {
                player.openEditSign((TileEntitySign) tileEntity);
            }
        }

        SoundType soundType = placementSelection.state.getBlock().getSoundType(placementSelection.state, worldIn, placePos, player);
        worldIn.playSound(null, placePos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }

    private PlacementSelection selectPlacement(EnumFacing clickedFace) {
        return createWallPlacement(clickedFace);
    }

    private PlacementSelection createWallPlacement(EnumFacing clickedFace) {
        if (!(wallBlock instanceof BlockCanvasWallHangingSign)) {
            return null;
        }

        if (!clickedFace.getAxis().isHorizontal()) {
            return null;
        }

        BlockCanvasWallHangingSign hangingWallBlock = (BlockCanvasWallHangingSign) wallBlock;
        IBlockState placedState = hangingWallBlock.getDefaultState().withProperty(BlockCanvasWallHangingSign.FACING, clickedFace);
        return new PlacementSelection(hangingWallBlock, placedState);
    }

    private void configureHangingTextFace(EntityPlayer player, BlockPos placePos, IBlockState placedState, TileEntityCanvasSign canvasSign) {
        if (!(placedState.getBlock() instanceof BlockCanvasWallHangingSign)) {
            canvasSign.setHangingTextOnBack(false);
            return;
        }

        EnumFacing defaultTextFacing = getWallHangingTextFacing(placedState, false);
        EnumFacing flippedTextFacing = defaultTextFacing.getOpposite();
        EnumFacing playerSide = getNearestPlayerSide(player, placePos);

        int defaultScore = directionSimilarity(defaultTextFacing, playerSide);
        int flippedScore = directionSimilarity(flippedTextFacing, playerSide);
        boolean textOnBack = flippedScore > defaultScore;
        EnumFacing blockFacing = placedState.getValue(BlockCanvasWallHangingSign.FACING);
        // 仅 N/S 方向反转正反面判定，符合当前悬挂告示牌旋转链路。
        if (blockFacing == EnumFacing.NORTH || blockFacing == EnumFacing.SOUTH) {
            textOnBack = !textOnBack;
        }
        canvasSign.setHangingTextOnBack(textOnBack);
    }

    private EnumFacing getWallHangingTextFacing(IBlockState placedState, boolean flipped) {
        EnumFacing facing = placedState.getValue(BlockCanvasWallHangingSign.FACING);
        EnumFacing attachedFace = facing.getOpposite();
        float rotation = -attachedFace.getHorizontalAngle() - 90.0F;
        if (flipped) {
            rotation += 180.0F;
        }
        return EnumFacing.fromAngle(rotation);
    }

    private EnumFacing getNearestPlayerSide(EntityPlayer player, BlockPos signPos) {
        double centerX = signPos.getX() + 0.5D;
        double centerZ = signPos.getZ() + 0.5D;
        double diffX = player.posX - centerX;
        double diffZ = player.posZ - centerZ;

        if (Math.abs(diffX) > Math.abs(diffZ)) {
            return diffX >= 0.0D ? EnumFacing.EAST : EnumFacing.WEST;
        }
        return diffZ >= 0.0D ? EnumFacing.SOUTH : EnumFacing.NORTH;
    }

    private int directionSimilarity(EnumFacing first, EnumFacing second) {
        return first.getXOffset() * second.getXOffset() + first.getZOffset() * second.getZOffset();
    }


    private static class PlacementSelection {
        private final Block block;
        private final IBlockState state;

        private PlacementSelection(Block block, IBlockState state) {
            this.block = block;
            this.state = state;
        }
    }
}
