package com.wdcftgg.farmersdelightlegacy.api.heat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 热源判定扩展 API。
 * <p>
 * 其他模组可在初始化阶段注册自定义 {@link IHeatSourcePredicate}，
 * 让自身方块参与 Farmers Delight Legacy 的“直接热源”判定流程。
 */
public final class HeatSourceApi {
    private static final Map<String, IHeatSourcePredicate> DIRECT_HEAT_SOURCE_PREDICATES = new LinkedHashMap<>();

    private HeatSourceApi() {
    }

    /**
     * 注册一个“直接热源”判定回调。
     * <p>
     * 判定时按注册顺序遍历，任一回调返回 {@code true} 即视为命中并短路返回。
     *
     * @param key       判定器唯一标识，传入 {@code null} 会被忽略
     * @param predicate 自定义热源判定器，传入 {@code null} 会被忽略
     */
    public static void registerDirectHeatSourcePredicate(String key, IHeatSourcePredicate predicate) {
        if (key != null && predicate != null) {
            synchronized (DIRECT_HEAT_SOURCE_PREDICATES) {
                DIRECT_HEAT_SOURCE_PREDICATES.put(key, predicate);
            }
        }
    }

    /**
     * 注销一个已注册的“直接热源”判定回调。
     *
     * @param key 判定器唯一标识，若不存在则不做任何处理
     */
    public static void unregisterDirectHeatSourcePredicate(String key) {
        if (key != null) {
            synchronized (DIRECT_HEAT_SOURCE_PREDICATES) {
                DIRECT_HEAT_SOURCE_PREDICATES.remove(key);
            }
        }
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
        IHeatSourcePredicate[] predicates;
        synchronized (DIRECT_HEAT_SOURCE_PREDICATES) {
            predicates = DIRECT_HEAT_SOURCE_PREDICATES.values().toArray(new IHeatSourcePredicate[0]);
        }
        for (IHeatSourcePredicate predicate : predicates) {
            if (predicate.isHeatSource(world, pos, state)) {
                return true;
            }
        }
        return false;
    }
}

