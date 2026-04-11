package com.wdcftgg.farmersdelightlegacy.common.registry;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.tile.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModTileEntities {

    private ModTileEntities() {
    }

    public static void registerAll() {
        GameRegistry.registerTileEntity(TileEntityCookingPot.class, new ResourceLocation(FarmersDelightLegacy.MOD_ID, "cooking_pot"));
        GameRegistry.registerTileEntity(TileEntityCuttingBoard.class, new ResourceLocation(FarmersDelightLegacy.MOD_ID, "cutting_board"));
        GameRegistry.registerTileEntity(TileEntityCabinet.class, new ResourceLocation(FarmersDelightLegacy.MOD_ID, "cabinet"));
        GameRegistry.registerTileEntity(TileEntityBasket.class, new ResourceLocation(FarmersDelightLegacy.MOD_ID, "basket"));
        GameRegistry.registerTileEntity(TileEntityFeast.class, new ResourceLocation(FarmersDelightLegacy.MOD_ID, "feast"));
        GameRegistry.registerTileEntity(TileEntityStove.class, new ResourceLocation(FarmersDelightLegacy.MOD_ID, "stove"));
        GameRegistry.registerTileEntity(TileEntitySkillet.class, new ResourceLocation(FarmersDelightLegacy.MOD_ID, "skillet"));
        GameRegistry.registerTileEntity(TileEntityCanvasSign.class, new ResourceLocation(FarmersDelightLegacy.MOD_ID, "canvas_sign"));
    }
}

