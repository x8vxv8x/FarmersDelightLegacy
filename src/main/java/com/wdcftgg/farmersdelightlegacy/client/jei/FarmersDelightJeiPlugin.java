package com.wdcftgg.farmersdelightlegacy.client.jei;

import com.wdcftgg.farmersdelightlegacy.client.gui.GuiCookingPot;
import com.wdcftgg.farmersdelightlegacy.common.item.ItemCookingPot;
import com.wdcftgg.farmersdelightlegacy.common.recipe.*;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCookingPot;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JEIPlugin
public final class FarmersDelightJeiPlugin implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        IDrawable cookingPotIcon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.COOKING_POT));
        IDrawable cuttingBoardIcon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.CUTTING_BOARD));
        IDrawable campfireIcon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.STOVE));

        registry.addRecipeCategories(
                new CookingPotRecipeCategory(guiHelper, cookingPotIcon),
                new CuttingBoardRecipeCategory(guiHelper, cuttingBoardIcon),
                new CampfireRecipeCategory(guiHelper, campfireIcon)
        );
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(buildCookingPotRecipes(), JeiUids.COOKING_POT);
        registry.addRecipes(buildCuttingBoardRecipes(), JeiUids.CUTTING_BOARD);
        registry.addRecipes(buildCampfireRecipes(), JeiUids.CAMPFIRE);
        registry.handleRecipes(SpecialCraftingJeiRecipe.class, recipe -> recipe, VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipes(buildSpecialCraftingRecipes(), VanillaRecipeCategoryUid.CRAFTING);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.COOKING_POT), JeiUids.COOKING_POT);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.CUTTING_BOARD), JeiUids.CUTTING_BOARD);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.STOVE), JeiUids.CAMPFIRE);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.SKILLET), JeiUids.CAMPFIRE);

        registry.addRecipeClickArea(GuiCookingPot.class, 89, 25, 24, 17, JeiUids.COOKING_POT);

        addIngredientInfoItem(registry, "wheat_dough", "farmersdelight.jei.info.dough");
        addIngredientInfoItem(registry, "straw", "farmersdelight.jei.info.straw");
        addIngredientInfoItem(registry, "ham", "farmersdelight.jei.info.ham");
        addIngredientInfoItem(registry, "smoked_ham", "farmersdelight.jei.info.ham");
        addIngredientInfoItem(registry, "flint_knife", "farmersdelight.jei.info.knife");
        addIngredientInfoItem(registry, "iron_knife", "farmersdelight.jei.info.knife");
        addIngredientInfoItem(registry, "golden_knife", "farmersdelight.jei.info.knife");
        addIngredientInfoItem(registry, "diamond_knife", "farmersdelight.jei.info.knife");
        registerCropIngredientInfos(registry);
    }

    private static void addIngredientInfo(IModRegistry registry, ItemStack stack, String key) {
        if (!stack.isEmpty()) {
            registry.addIngredientInfo(stack, VanillaTypes.ITEM, key);
        }
    }

    private static void addIngredientInfoGroup(IModRegistry registry, String key, ItemStack... stacks) {
        List<ItemStack> validStacks = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                validStacks.add(stack);
            }
        }

        if (!validStacks.isEmpty()) {
            registry.addIngredientInfo(validStacks, VanillaTypes.ITEM, key);
        }
    }

    private static void registerCropIngredientInfos(IModRegistry registry) {
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_beetroots",
                new ItemStack(Items.BEETROOT),
                new ItemStack(ModBlocks.WILD_BEETROOTS));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_cabbages",
                stackFromItemName("cabbage"),
                stackFromItemName("cabbage_leaf"),
                new ItemStack(ModBlocks.WILD_CABBAGES));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_carrots",
                new ItemStack(Items.CARROT),
                new ItemStack(ModBlocks.WILD_CARROTS));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_onions",
                stackFromItemName("onion"),
                new ItemStack(ModBlocks.WILD_ONIONS));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_potatoes",
                new ItemStack(Items.POTATO),
                new ItemStack(ModBlocks.WILD_POTATOES));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_rice",
                stackFromItemName("rice"),
                stackFromItemName("rice_panicle"),
                new ItemStack(ModBlocks.WILD_RICE));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_tomatoes",
                stackFromItemName("tomato"),
                new ItemStack(ModBlocks.WILD_TOMATOES));
    }

    private static ItemStack stackFromItemName(String itemName) {
        Item item = ModItems.ITEMS.get(itemName);
        return item != null ? new ItemStack(item) : ItemStack.EMPTY;
    }

    private static void addIngredientInfoItem(IModRegistry registry, String itemName, String key) {
        addIngredientInfo(registry, stackFromItemName(itemName), key);
    }

    private static List<CookingPotJeiRecipe> buildCookingPotRecipes() {
        List<CookingPotJeiRecipe> result = new ArrayList<>();
        for (CookingPotRecipe recipe : CookingPotRecipeManager.getRecipes()) {
            result.add(CookingPotJeiRecipe.of(recipe));
        }
        return result;
    }

    private static List<CuttingBoardJeiRecipe> buildCuttingBoardRecipes() {
        List<CuttingBoardJeiRecipe> result = new ArrayList<>();
        for (CuttingBoardRecipeManager.CuttingBoardRecipeView recipe : CuttingBoardRecipeManager.getRecipes()) {
            result.add(CuttingBoardJeiRecipe.of(recipe));
        }
        return result;
    }

    private static List<CampfireJeiRecipe> buildCampfireRecipes() {
        List<CampfireJeiRecipe> result = new ArrayList<>();
        for (CampfireCookingRecipe recipe : CampfireCookingRecipeManager.getRecipes()) {
            result.add(CampfireJeiRecipe.of(recipe));
        }
        return result;
    }

    private static List<SpecialCraftingJeiRecipe> buildSpecialCraftingRecipes() {
        List<SpecialCraftingJeiRecipe> recipes = new ArrayList<>();
        addWaterDoughRecipe(recipes);
        addFoodServingRecipes(recipes);
        return recipes;
    }

    private static void addWaterDoughRecipe(List<SpecialCraftingJeiRecipe> recipes) {
        ItemStack wheatDough = stackFromItemName("wheat_dough");
        if (wheatDough.isEmpty()) {
            return;
        }

        List<List<ItemStack>> inputs = new ArrayList<>();
        inputs.add(singleStackList(new ItemStack(Items.WHEAT)));
        inputs.add(singleStackList(new ItemStack(Items.WATER_BUCKET)));
        recipes.add(new SpecialCraftingJeiRecipe(inputs, wheatDough));
    }

    private static void addFoodServingRecipes(List<SpecialCraftingJeiRecipe> recipes) {
        Map<String, SpecialCraftingJeiRecipe> uniqueRecipes = new LinkedHashMap<>();

        for (CookingPotRecipe cookingPotRecipe : CookingPotRecipeManager.getRecipes()) {
            ItemStack mealStack = cookingPotRecipe.getResultStack().copy();
            if (mealStack.isEmpty()) {
                continue;
            }

            ItemStack potStack = new ItemStack(ModBlocks.COOKING_POT);
            ItemStack configuredContainer = cookingPotRecipe.getOutputContainer();
            boolean useDefaultContainer = !cookingPotRecipe.hasContainerDefinition();
            TileEntityCookingPot.writeMealToItem(potStack, mealStack, configuredContainer, useDefaultContainer);

            ItemStack servingContainer = ItemCookingPot.inferContainer(potStack, mealStack);
            if (servingContainer.isEmpty()) {
                continue;
            }

            ItemStack output = mealStack.copy();
            output.setCount(1);

            List<List<ItemStack>> inputs = new ArrayList<>();
            inputs.add(singleStackList(potStack));
            inputs.add(singleStackList(servingContainer));

            String key = output.getItem().getRegistryName() + "|" + output.getMetadata() + "|"
                    + servingContainer.getItem().getRegistryName() + "|" + servingContainer.getMetadata();
            uniqueRecipes.putIfAbsent(key, new SpecialCraftingJeiRecipe(inputs, output));
        }

        recipes.addAll(uniqueRecipes.values());
    }

    private static List<ItemStack> singleStackList(ItemStack stack) {
        List<ItemStack> result = new ArrayList<>(1);
        result.add(stack);
        return result;
    }
}

