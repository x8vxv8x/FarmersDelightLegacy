package com.wdcftgg.farmersdelightlegacy.api.heat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 热源判定扩展 API。
 * <p>
 * 其他模组可在初始化阶段注册自定义 {@link IHeatSourcePredicate}，
 * 让自身方块参与 Farmers Delight Legacy 的“直接热源”判定流程。
 */
public final class HeatSourceApi {
    private static final List<IHeatSourcePredicate> DIRECT_HEAT_SOURCE_PREDICATES = new CopyOnWriteArrayList<>();

    private HeatSourceApi() {
    }

    /**
     * 注册一个“直接热源”判定回调。
     * <p>
     * 判定时按注册顺序遍历，任一回调返回 {@code true} 即视为命中并短路返回。
     *
     * @param predicate 自定义热源判定器，传入 {@code null} 会被忽略
     */
    public static void registerDirectHeatSourcePredicate(IHeatSourcePredicate predicate) {
        if (predicate != null) {
            DIRECT_HEAT_SOURCE_PREDICATES.add(predicate);
        }
    }

    /**
     * 注销一个已注册的“直接热源”判定回调。
     *
     * @param predicate 需要移除的判定器，若不存在则不做任何处理
     */
    public static void unregisterDirectHeatSourcePredicate(IHeatSourcePredicate predicate) {
        DIRECT_HEAT_SOURCE_PREDICATES.remove(predicate);
    }

    /**
     * 查询当前位置是否被任一外部注册回调判定为“直接热源”。
     *
     * @param world 当前世界
     * @param pos   当前被检测的热源方块坐标
     * @param state 当前坐标的方块状态
     * @return 只要任一已注册回调返回 {@code true}，就返回 {@code true}；否则返回 {@code false}
     */
    public static boolean isRegisteredAsDirectHeatSource(World world, BlockPos pos, IBlockState state) {
        for (IHeatSourcePredicate predicate : DIRECT_HEAT_SOURCE_PREDICATES) {
            if (predicate.isHeatSource(world, pos, state)) {
                return true;
            }
        }
        return false;
    }
}

