package com.wdcftgg.farmersdelightlegacy.api.heat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface IHeatSourceOffsetPredicate {

    boolean shouldOffsetDown(World world, BlockPos pos, IBlockState state);
}
