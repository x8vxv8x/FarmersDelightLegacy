package com.wdcftgg.farmersdelightlegacy.client.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

final class JeiTooltipUtil {

    private JeiTooltipUtil() {
    }

    static void addRecipeIdTooltip(List<String> tooltip, String recipeId) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.gameSettings == null || !minecraft.gameSettings.advancedItemTooltips) {
            return;
        }
        if (recipeId == null || recipeId.isEmpty()) {
            return;
        }
        tooltip.add(TextFormatting.DARK_GRAY + I18n.format("farmersdelight.jei.recipe_id", recipeId));
    }
}
