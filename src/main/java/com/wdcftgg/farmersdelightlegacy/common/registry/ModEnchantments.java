package com.wdcftgg.farmersdelightlegacy.common.registry;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.item.enchantment.EnchantmentBackstabbing;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;

public final class ModEnchantments {

    public static Enchantment BACKSTABBING;

    private ModEnchantments() {
    }

    public static void registerAll(RegistryEvent.Register<Enchantment> event) {
        BACKSTABBING = new EnchantmentBackstabbing();
        BACKSTABBING.setRegistryName(new ResourceLocation(FarmersDelightLegacy.MOD_ID, "backstabbing"));
        event.getRegistry().register(BACKSTABBING);
    }
}
