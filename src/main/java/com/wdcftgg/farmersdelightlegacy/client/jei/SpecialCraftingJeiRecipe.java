package com.wdcftgg.farmersdelightlegacy.client.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class SpecialCraftingJeiRecipe implements ICraftingRecipeWrapper {

    private final List<List<ItemStack>> inputOptions;
    private final ItemStack output;

    public SpecialCraftingJeiRecipe(List<List<ItemStack>> inputOptions, ItemStack output) {
        this.inputOptions = inputOptions;
        this.output = output;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, this.inputOptions);
        ingredients.setOutput(VanillaTypes.ITEM, this.output);
    }
}

