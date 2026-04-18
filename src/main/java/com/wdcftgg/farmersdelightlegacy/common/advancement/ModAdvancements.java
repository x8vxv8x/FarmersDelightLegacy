package com.wdcftgg.farmersdelightlegacy.common.advancement;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.ResourceLocation;

public final class ModAdvancements {

    public static final SimplePlayerTrigger USE_CUTTING_BOARD =
            new SimplePlayerTrigger(new ResourceLocation(FarmersDelightLegacy.MOD_ID, "use_cutting_board"));
    public static final SimplePlayerTrigger HARVEST_ROPELOGGED_TOMATO =
            new SimplePlayerTrigger(new ResourceLocation(FarmersDelightLegacy.MOD_ID, "harvest_ropelogged_tomato"));
    public static final SimplePlayerTrigger HIT_RAIDER_WITH_ROTTEN_TOMATO =
            new SimplePlayerTrigger(new ResourceLocation(FarmersDelightLegacy.MOD_ID, "hit_raider_with_rotten_tomato"));

    private static boolean registered;

    private ModAdvancements() {
    }

    public static void registerAll() {
        if (registered) {
            return;
        }

        CriteriaTriggers.register(USE_CUTTING_BOARD);
        CriteriaTriggers.register(HARVEST_ROPELOGGED_TOMATO);
        CriteriaTriggers.register(HIT_RAIDER_WITH_ROTTEN_TOMATO);
        registered = true;
    }
}
