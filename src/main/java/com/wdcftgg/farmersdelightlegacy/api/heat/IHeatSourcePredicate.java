package com.wdcftgg.farmersdelightlegacy.api.heat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 直接热源判定回调接口。
 * <p>
 * 通过 {@link HeatSourceApi#registerDirectHeatSourcePredicate(IHeatSourcePredicate)} 注册后，
 * 可让外部模组将自定义方块纳入烹饪加热判定。
 */
@FunctionalInterface
public interface IHeatSourcePredicate {
    /**
     * 判断目标方块是否应被视为“直接热源”。
     *
     * @param world 当前世界
     * @param pos   当前被检测的热源方块坐标
     * @param state 当前坐标的方块状态
     * @return 返回 {@code true} 时，表示该方块可作为烹饪加热的直接热源
     */
    boolean isHeatSource(World world, BlockPos pos, IBlockState state);
}

