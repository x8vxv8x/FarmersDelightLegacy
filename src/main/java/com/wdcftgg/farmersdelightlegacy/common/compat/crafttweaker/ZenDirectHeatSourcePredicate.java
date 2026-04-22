package com.wdcftgg.farmersdelightlegacy.common.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.farmersdelight.function.DirectHeatSourcePredicate")
@FunctionalInterface
public interface ZenDirectHeatSourcePredicate extends ZenHeatSourcePredicateCallback {

    @Override
    @ZenMethod
    boolean test(IWorld world, IBlockPos pos, IBlockState iBlockState);
}

