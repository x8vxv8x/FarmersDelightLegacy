package com.wdcftgg.farmersdelightlegacy.common.item;

import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemRice extends Item {

    private final Block cropBlock;

    public ItemRice(Block cropBlock) {
        this.cropBlock = cropBlock;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos placePos = pos.up();
        if (facing != EnumFacing.UP || !player.canPlayerEdit(placePos, facing, stack)) {
            return EnumActionResult.FAIL;
        }

        IBlockState targetState = worldIn.getBlockState(pos);
        boolean canPlace = this.isValidRiceSoil(targetState)
                && this.hasNearbyWater(worldIn, pos)
                && worldIn.isAirBlock(placePos)
                && worldIn.mayPlace(this.cropBlock, placePos, false, facing, player)
                && this.cropBlock.canPlaceBlockAt(worldIn, placePos);
        if (canPlace) {
            worldIn.setBlockState(placePos, this.cropBlock.getDefaultState(), 11);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            return EnumActionResult.SUCCESS;
        }

        if (!worldIn.isRemote && this.isValidRiceSoil(targetState)) {
            player.sendStatusMessage(new TextComponentTranslation("farmersdelight.block.rice.invalid_placement"), true);
        }
        return EnumActionResult.FAIL;
    }

    private boolean isValidRiceSoil(IBlockState state) {
        Block soil = state.getBlock();
        return soil instanceof BlockFarmland || soil instanceof BlockDirt || soil == ModBlocks.RICH_SOIL_FARMLAND;
    }

    private boolean hasNearbyWater(World worldIn, BlockPos soilPos) {
        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
            if (worldIn.getBlockState(soilPos.offset(facing)).getMaterial() == net.minecraft.block.material.Material.WATER) {
                return true;
            }
        }
        return false;
    }
}
