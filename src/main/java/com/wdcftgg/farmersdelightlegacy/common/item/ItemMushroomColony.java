package com.wdcftgg.farmersdelightlegacy.common.item;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockMushroomColony;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMushroomColony extends ItemBlock {
    public ItemMushroomColony(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, EnumFacing side,
                                float hitX, float hitY, float hitZ, IBlockState newState) {
        IBlockState matureState = newState;
        if (newState.getBlock() instanceof BlockMushroomColony) {
            matureState = newState.withProperty(BlockMushroomColony.AGE, ((BlockMushroomColony) newState.getBlock()).getMaxAge());
        }
        return super.placeBlockAt(stack, player, worldIn, pos, side, hitX, hitY, hitZ, matureState);
    }
}
