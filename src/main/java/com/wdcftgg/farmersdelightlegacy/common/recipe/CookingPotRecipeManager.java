package com.wdcftgg.farmersdelightlegacy.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class CookingPotRecipeManager {

    private static final float DEFAULT_RECIPE_EXPERIENCE = 0.35F;

    private static final List<CookingPotRecipe> RECIPES = new ArrayList<>();
    private static boolean loaded;

    private CookingPotRecipeManager() {
    }

    public static CookingPotRecipe findRecipe(List<ItemStack> inputStacks) {
        ensureLoaded();
        for (CookingPotRecipe recipe : RECIPES) {
            if (matches(recipe, inputStacks)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<CookingPotRecipe> getRecipes() {
        ensureLoaded();
        return new ArrayList<>(RECIPES);
    }

    private static boolean matches(CookingPotRecipe recipe, List<ItemStack> inputStacks) {
        List<CookingPotRecipe.IngredientEntry> expected = new ArrayList<>(recipe.getIngredients());

        for (ItemStack inputStack : inputStacks) {
            if (inputStack.isEmpty()) {
                continue;
            }

            int matchedIndex = -1;
            for (int index = 0; index < expected.size(); index++) {
                if (expected.get(index).matches(inputStack)) {
                    matchedIndex = index;
                    break;
                }
            }
            if (matchedIndex < 0) {
                return false;
            }
            expected.remove(matchedIndex);
        }

        return expected.isEmpty();
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }

        RECIPES.clear();
        loadJsonRecipes();
        loaded = true;
    }

    private static void loadJsonRecipes() {
        for (RecipeResourceScanner.RecipeJsonResource recipeResource : RecipeResourceScanner.scan("cooking_pot")) {
            JsonObject recipeJson = recipeResource.getJsonObject();
            String sourceModId = recipeResource.getModId();
            JsonArray ingredientsJson = recipeJson.getAsJsonArray("ingredients");
            JsonObject resultJson = recipeJson.getAsJsonObject("result");
            if (ingredientsJson == null || resultJson == null) {
                continue;
            }

            List<String> ingredients = new ArrayList<>();
            for (JsonElement ingredientElement : ingredientsJson) {
                if (ingredientElement.isJsonPrimitive()) {
                    ingredients.add(ingredientElement.getAsString());
                    continue;
                }
                if (!ingredientElement.isJsonObject()) {
                    continue;
                }

                JsonObject ingredientObject = ingredientElement.getAsJsonObject();
                if (ingredientObject.has("item")) {
                    ingredients.add(ingredientObject.get("item").getAsString());
                }
            }

            String resultItem = resultJson.has("item") ? resultJson.get("item").getAsString() : "";
            int resultCount = resultJson.has("count") ? resultJson.get("count").getAsInt() : 1;
            int cookTime = recipeJson.has("cookingtime") ? Math.max(1, recipeJson.get("cookingtime").getAsInt()) : 200;
            float experience = recipeJson.has("experience") ? Math.max(0.0F, recipeJson.get("experience").getAsFloat()) : DEFAULT_RECIPE_EXPERIENCE;
            ItemStack outputContainer = ItemStack.EMPTY;
            boolean hasContainerDefinition = recipeJson.has("container");
            if (recipeJson.has("container") && recipeJson.get("container").isJsonObject()) {
                JsonObject containerJson = recipeJson.getAsJsonObject("container");
                String containerItemPath = containerJson.has("item") ? containerJson.get("item").getAsString() : "";
                int containerCount = containerJson.has("count") ? Math.max(1, containerJson.get("count").getAsInt()) : 1;
                if (!containerItemPath.isEmpty()) {
                    outputContainer = stackOf(containerItemPath, containerCount, sourceModId);
                }
            }
            if (!resultItem.isEmpty()) {
                addRecipe(ingredients.toArray(new String[0]), stackOf(resultItem, Math.max(1, resultCount), sourceModId), outputContainer,
                        cookTime, experience, hasContainerDefinition, sourceModId);
            }
        }
    }

    private static void addRecipe(String[] ingredientPaths, ItemStack resultStack, ItemStack outputContainer,
                                  int cookTime, float experience, boolean hasContainerDefinition, String defaultNamespace) {
        if (resultStack.isEmpty()) {
            return;
        }

        List<CookingPotRecipe.IngredientEntry> ingredients = new ArrayList<>();
        for (String ingredientPath : ingredientPaths) {
            if (ingredientPath == null || ingredientPath.isEmpty()) {
                return;
            }

            if (ingredientPath.startsWith("ore:")) {
                String oreDictName = ingredientPath.substring(4);
                if (oreDictName.isEmpty()) {
                    return;
                }
                ingredients.add(CookingPotRecipe.IngredientEntry.forOreDict(oreDictName));
                continue;
            }

            Item ingredientItem = itemOf(ingredientPath, defaultNamespace);
            if (ingredientItem == null) {
                return;
            }
            ingredients.add(CookingPotRecipe.IngredientEntry.forItem(ingredientItem));
        }
        RECIPES.add(new CookingPotRecipe(ingredients, resultStack, outputContainer, cookTime, experience, hasContainerDefinition));
    }

    private static Item itemOf(String path, String defaultNamespace) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        ResourceLocation itemId;
        if (path.contains(":")) {
            itemId = new ResourceLocation(path);
        } else {
            itemId = new ResourceLocation(defaultNamespace, path);
        }
        return ForgeRegistries.ITEMS.getValue(itemId);
    }

    private static ItemStack stackOf(String path, int count, String defaultNamespace) {
        Item item = itemOf(path, defaultNamespace);
        if (item == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, count);
    }
}

