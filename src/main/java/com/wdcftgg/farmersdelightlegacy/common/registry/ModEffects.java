package com.wdcftgg.farmersdelightlegacy.common.registry;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.effect.PotionComfort;
import com.wdcftgg.farmersdelightlegacy.common.effect.PotionNourishment;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;

public final class ModEffects {

    public static Potion COMFORT;
    public static Potion NOURISHMENT;

    private ModEffects() {
    }

    public static void registerAll(RegistryEvent.Register<Potion> event) {
        COMFORT = new PotionComfort();
        COMFORT.setRegistryName(new ResourceLocation(FarmersDelightLegacy.MOD_ID, "comfort"));
        event.getRegistry().register(COMFORT);

        NOURISHMENT = new PotionNourishment();
        NOURISHMENT.setRegistryName(new ResourceLocation(FarmersDelightLegacy.MOD_ID, "nourishment"));
        event.getRegistry().register(NOURISHMENT);
    }
}
