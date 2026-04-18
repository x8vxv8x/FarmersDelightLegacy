package com.wdcftgg.farmersdelightlegacy.common.compat;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public final class FutureMcSmithingCompat {

    private FutureMcSmithingCompat() {
    }

    public static void registerAll() {
        if (!Loader.isModLoaded("futuremc")) {
            return;
        }

        Item diamondKnife = ModItems.ITEMS.get("diamond_knife");
        Item netheriteKnife = ModItems.ITEMS.get("netherite_knife");
        Item netheriteIngot = Item.getByNameOrId("futuremc:netherite_ingot");
        if (diamondKnife == null || netheriteKnife == null || netheriteIngot == null) {
            FarmersDelightLegacy.LOGGER.warn("Future MC 锻造台刀升级配方注册失败：关键物品缺失。");
            return;
        }

        try {
            ItemStack baseProbe = new ItemStack(diamondKnife, 1, OreDictionary.WILDCARD_VALUE);
            ItemStack materialProbe = new ItemStack(netheriteIngot);
            Object smithingRecipes = getSmithingRecipesInstance();
            Object existingRecipe = findRecipe(smithingRecipes, baseProbe, materialProbe);
            if (existingRecipe != null) {
                if (getOutputItem(existingRecipe) == netheriteKnife) {
                    return;
                }
                FarmersDelightLegacy.LOGGER.warn("Future MC 锻造台已存在钻石刀升级配方，跳过当前注册。");
                return;
            }

            Ingredient baseIngredient = Ingredient.fromStacks(baseProbe);
            Ingredient materialIngredient = Ingredient.fromItem(netheriteIngot);
            getRecipeList(smithingRecipes).add(createSmithingRecipe(baseIngredient, materialIngredient, new ItemStack(netheriteKnife)));
            FarmersDelightLegacy.LOGGER.info("已为 Future MC 锻造台注册下界合金刀升级配方。");
        } catch (ReflectiveOperationException exception) {
            FarmersDelightLegacy.LOGGER.error("Future MC 锻造台刀升级配方注册失败。", exception);
        }
    }

    private static Object getSmithingRecipesInstance() throws ReflectiveOperationException {
        Class<?> smithingRecipesClass = Class.forName("thedarkcolour.futuremc.recipe.smithing.SmithingRecipes");
        return smithingRecipesClass.getField("INSTANCE").get(null);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> getRecipeList(Object smithingRecipes) throws ReflectiveOperationException {
        Method getRecipesMethod = smithingRecipes.getClass().getMethod("getRecipes");
        return (List<Object>) getRecipesMethod.invoke(smithingRecipes);
    }

    private static Object findRecipe(Object smithingRecipes, ItemStack baseProbe, ItemStack materialProbe) throws ReflectiveOperationException {
        Method getRecipeMethod = smithingRecipes.getClass().getMethod("getRecipe", ItemStack.class, ItemStack.class);
        return getRecipeMethod.invoke(smithingRecipes, baseProbe, materialProbe);
    }

    private static Object createSmithingRecipe(Ingredient baseIngredient, Ingredient materialIngredient, ItemStack resultStack) throws ReflectiveOperationException {
        Class<?> smithingRecipeClass = Class.forName("thedarkcolour.futuremc.recipe.smithing.SmithingRecipe");
        Constructor<?> constructor = smithingRecipeClass.getConstructor(Ingredient.class, Ingredient.class, ItemStack.class);
        return constructor.newInstance(baseIngredient, materialIngredient, resultStack);
    }

    private static Item getOutputItem(Object recipe) throws ReflectiveOperationException {
        Method getOutputMethod = recipe.getClass().getMethod("getOutput");
        ItemStack outputStack = (ItemStack) getOutputMethod.invoke(recipe);
        return outputStack.getItem();
    }
}
