package com.wdcftgg.farmersdelightlegacy;

import com.wdcftgg.farmersdelightlegacy.common.event.HeatSourceExample;
import com.wdcftgg.farmersdelightlegacy.common.gui.ModGuiHandler;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModTileEntities;
import com.wdcftgg.farmersdelightlegacy.common.world.WildCropWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = FarmersDelightLegacy.MOD_ID,
        name = FarmersDelightLegacy.MOD_NAME,
        version = FarmersDelightLegacy.VERSION
        , dependencies="required-after:fluidlogged_api")
public class FarmersDelightLegacy {

    public static final String MOD_ID = "farmersdelight";
    public static final String MOD_NAME = "Farmer's Delight Legacy";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(FarmersDelightLegacy.MOD_ID)
    public static FarmersDelightLegacy INSTANCE;

    public static FarmersDelightLegacy getInstance() {
        return INSTANCE;
    }

    public static final Logger LOGGER = LogManager.getLogger(FarmersDelightLegacy.MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModTileEntities.registerAll();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ModGuiHandler());
        GameRegistry.registerWorldGenerator(new WildCropWorldGenerator(), 0);
        HeatSourceExample.registerHeatSourceExample();
        LOGGER.info("{} preInit 完成，准备注册内容。", FarmersDelightLegacy.MOD_NAME);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("{} init 完成。", FarmersDelightLegacy.MOD_NAME);
    }

}
