package com.wdcftgg.farmersdelightlegacy.common.event;

import com.wdcftgg.farmersdelightlegacy.api.heat.HeatSourceApi;
import com.wdcftgg.farmersdelightlegacy.api.heat.HeatSourceOffsetApi;
import com.wdcftgg.farmersdelightlegacy.api.heat.IHeatSourceOffsetPredicate;
import com.wdcftgg.farmersdelightlegacy.api.heat.IHeatSourcePredicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HeatSourceExample {

    public static void registerHeatSourceExample() {

//        HeatSourceApi.registerDirectHeatSourcePredicate("examplemod:example1", (world, pos, state) ->
//                state.getBlock() != Blocks.ICE
//        );
//
//        HeatSourceApi.registerDirectHeatSourcePredicate(
//                "examplemod:example2",
//                new IHeatSourcePredicate() {
//                    @Override
//                    public boolean isHeatSource(World world, BlockPos pos, IBlockState state) {
//                        return state.getBlock() != Blocks.ICE;
//                    }
//                }
//        );


//        HeatSourceOffsetApi.registerOffsetPredicate("examplemod:example3", (world, pos, state) ->
//                state.getBlock() == Blocks.ICE
//        );
    }

}
