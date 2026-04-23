package com.wdcftgg.farmersdelightlegacy.common.compat;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.api.heat.HeatSourceApi;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Map;

public final class CampfireHeatSourceCompat {

    private static final ResourceLocation CAMPFIRE_BLOCK_ID = new ResourceLocation("campfire", "campfire");
    private static final ResourceLocation BRAZIER_BLOCK_ID = new ResourceLocation("campfire", "brazier");

    private CampfireHeatSourceCompat() {
    }

    public static void registerAll() {
        if (!Loader.isModLoaded("campfire")) {
            return;
        }

        Block campfireBlock = ForgeRegistries.BLOCKS.getValue(CAMPFIRE_BLOCK_ID);
        Block brazierBlock = ForgeRegistries.BLOCKS.getValue(BRAZIER_BLOCK_ID);
        if (campfireBlock == null && brazierBlock == null) {
            FarmersDelightLegacy.LOGGER.warn("Campfire 模组热源兼容注册失败：未找到 campfire 或 brazier 方块。");
            return;
        }

        if (campfireBlock != null) {
            HeatSourceApi.registerDirectHeatSourcePredicate("campfire:compat_campfire",
                    (world, pos, state) -> matchesLitBlock(state, campfireBlock));
        }
        if (brazierBlock != null) {
            HeatSourceApi.registerDirectHeatSourcePredicate("campfire:compat_brazier",
                    (world, pos, state) -> matchesLitBlock(state, brazierBlock));
        }

        FarmersDelightLegacy.LOGGER.info("已注册 Campfire 模组的营火与火盆热源兼容。");
    }

    private static boolean matchesLitBlock(IBlockState state, Block expectedBlock) {
        if (state == null || expectedBlock == null || state.getBlock() != expectedBlock) {
            return false;
        }

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet()) {
            IProperty<?> property = entry.getKey();
            if (!"lit".equals(property.getName())) {
                continue;
            }

            Comparable<?> value = entry.getValue();
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            break;
        }

        return true;
    }
}
