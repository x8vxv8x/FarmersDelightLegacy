package com.wdcftgg.farmersdelightlegacy.api.heat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 热源检测偏移扩展 API。
 * <p>
 * 用于声明“锅具下方这一格不是热源本体，而是需要继续向下透传检测”的支撑方块。
 */
public final class HeatSourceOffsetApi {
    private static final Map<String, IHeatSourceOffsetPredicate> OFFSET_PREDICATES = new LinkedHashMap<>();

    private HeatSourceOffsetApi() {
    }

    public static void registerOffsetPredicate(String key, IHeatSourceOffsetPredicate predicate) {
        if (key != null && predicate != null) {
            synchronized (OFFSET_PREDICATES) {
                OFFSET_PREDICATES.put(key, predicate);
            }
        }
    }

    public static void unregisterOffsetPredicate(String key) {
        if (key != null) {
            synchronized (OFFSET_PREDICATES) {
                OFFSET_PREDICATES.remove(key);
            }
        }
    }

    public static boolean shouldOffsetDown(World world, BlockPos pos, IBlockState state) {
        IHeatSourceOffsetPredicate[] predicates;
        synchronized (OFFSET_PREDICATES) {
            predicates = OFFSET_PREDICATES.values().toArray(new IHeatSourceOffsetPredicate[0]);
        }
        for (IHeatSourceOffsetPredicate predicate : predicates) {
            if (predicate.shouldOffsetDown(world, pos, state)) {
                return true;
            }
        }
        return false;
    }
}
