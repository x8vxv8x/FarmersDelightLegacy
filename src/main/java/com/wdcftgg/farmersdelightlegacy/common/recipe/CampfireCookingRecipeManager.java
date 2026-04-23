package com.wdcftgg.farmersdelightlegacy.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CampfireCookingRecipeManager {

    private static final List<CampfireCookingRecipe> RECIPES = new ArrayList<>();
    private static final Map<String, CampfireCookingRecipe> SCRIPT_RECIPES = new LinkedHashMap<>();
    private static boolean loaded;

    private CampfireCookingRecipeManager() {
    }

    public static CampfireCookingRecipe findRecipe(ItemStack input) {
        ensureLoaded();
        for (CampfireCookingRecipe recipe : getAllRecipes()) {
            if (recipe.matches(input)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<CampfireCookingRecipe> getRecipes() {
        ensureLoaded();
        return getAllRecipes();
    }

    public static boolean registerScriptRecipe(String key, String[] ingredientTokens, ItemStack resultStack, int cookingTime) {
        if (key == null || key.isEmpty() || ingredientTokens == null || resultStack.isEmpty()) {
            return false;
        }

        List<CampfireCookingRecipe.IngredientEntry> ingredients = new ArrayList<>();
        for (String token : ingredientTokens) {
            if (token == null || token.isEmpty()) {
                return false;
            }
            if (token.startsWith("ore:")) {
                String oreDictName = token.substring(4);
                if (oreDictName.isEmpty()) {
                    return false;
                }
                ingredients.add(CampfireCookingRecipe.IngredientEntry.forOreDict(oreDictName));
                continue;
            }

            Item ingredientItem = itemOf(token, FarmersDelightLegacy.MOD_ID);
            if (ingredientItem == null) {
                return false;
            }
            ingredients.add(CampfireCookingRecipe.IngredientEntry.forItem(ingredientItem));
        }

        if (ingredients.isEmpty()) {
            return false;
        }

        CampfireCookingRecipe recipe = new CampfireCookingRecipe(key, ingredients, resultStack.copy(), Math.max(1, cookingTime));
        synchronized (SCRIPT_RECIPES) {
            SCRIPT_RECIPES.put(key, recipe);
        }
        return true;
    }

    public static int removeRecipesByOutput(ItemStack outputStack) {
        ensureLoaded();
        if (outputStack.isEmpty()) {
            return 0;
        }

        int removedCount = 0;
        synchronized (SCRIPT_RECIPES) {
            java.util.Iterator<Map.Entry<String, CampfireCookingRecipe>> iterator = SCRIPT_RECIPES.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, CampfireCookingRecipe> entry = iterator.next();
                if (matchesOutput(entry.getValue(), outputStack)) {
                    iterator.remove();
                    removedCount++;
                }
            }
        }

        java.util.Iterator<CampfireCookingRecipe> iterator = RECIPES.iterator();
        while (iterator.hasNext()) {
            if (matchesOutput(iterator.next(), outputStack)) {
                iterator.remove();
                removedCount++;
            }
        }
        return removedCount;
    }

    public static boolean unregisterScriptRecipe(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        synchronized (SCRIPT_RECIPES) {
            return SCRIPT_RECIPES.remove(key) != null;
        }
    }

    private static List<CampfireCookingRecipe> getAllRecipes() {
        List<CampfireCookingRecipe> result = new ArrayList<>();
        synchronized (SCRIPT_RECIPES) {
            result.addAll(SCRIPT_RECIPES.values());
        }
        result.addAll(RECIPES);
        return result;
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
        for (RecipeResourceScanner.RecipeJsonResource recipeResource : RecipeResourceScanner.scan("campfire")) {
            JsonObject recipeJson = recipeResource.getJsonObject();
            String sourceModId = recipeResource.getModId();
            JsonElement ingredientElement = recipeJson.get("ingredient");
            int cookingTime = recipeJson.has("cookingtime") ? recipeJson.get("cookingtime").getAsInt() : 600;
            ItemStack result = parseResult(recipeJson.get("result"), sourceModId);
            if (ingredientElement == null || result.isEmpty()) {
                continue;
            }

            List<CampfireCookingRecipe.IngredientEntry> ingredients = parseIngredients(ingredientElement, sourceModId);
            if (ingredients.isEmpty() || result.isEmpty()) {
                continue;
            }
            RECIPES.add(new CampfireCookingRecipe(recipeResource.getRecipeId(), ingredients, result, cookingTime));
        }
    }

    private static boolean matchesOutput(CampfireCookingRecipe recipe, ItemStack outputStack) {
        ItemStack resultStack = recipe.getResultStack();
        return !resultStack.isEmpty() && ItemStack.areItemsEqual(resultStack, outputStack);
    }

    private static List<CampfireCookingRecipe.IngredientEntry> parseIngredients(JsonElement ingredientElement, String defaultNamespace) {
        List<CampfireCookingRecipe.IngredientEntry> result = new ArrayList<>();
        if (ingredientElement.isJsonObject()) {
            addIngredientEntry(result, ingredientElement.getAsJsonObject(), defaultNamespace);
            return result;
        }
        if (!ingredientElement.isJsonArray()) {
            return result;
        }
        for (JsonElement candidate : ingredientElement.getAsJsonArray()) {
            if (candidate.isJsonObject()) {
                addIngredientEntry(result, candidate.getAsJsonObject(), defaultNamespace);
            }
        }
        return result;
    }

    private static void addIngredientEntry(List<CampfireCookingRecipe.IngredientEntry> target, JsonObject ingredientObject, String defaultNamespace) {
        if (ingredientObject.has("item")) {
            addItemIngredient(target, ingredientObject.get("item"), defaultNamespace);
            return;
        }

        if (ingredientObject.has("tag")) {
            String oreDictName = convertTagToOreDict(ingredientObject.get("tag").getAsString());
            if (oreDictName != null && !oreDictName.isEmpty()) {
                target.add(CampfireCookingRecipe.IngredientEntry.forOreDict(oreDictName));
            }
        }
    }

    private static void addItemIngredient(List<CampfireCookingRecipe.IngredientEntry> target, JsonElement itemElement, String defaultNamespace) {
        if (itemElement == null || itemElement.isJsonNull()) {
            return;
        }
        if (itemElement.isJsonPrimitive()) {
            Item item = itemOf(itemElement.getAsString(), defaultNamespace);
            if (item != null) {
                target.add(CampfireCookingRecipe.IngredientEntry.forItem(item));
            }
            return;
        }
        if (itemElement.isJsonObject()) {
            JsonObject nestedItemObject = itemElement.getAsJsonObject();
            if (nestedItemObject.has("item")) {
                addItemIngredient(target, nestedItemObject.get("item"), defaultNamespace);
            }
        }
    }

    private static ItemStack parseResult(JsonElement resultElement, String defaultNamespace) {
        if (resultElement == null || resultElement.isJsonNull()) {
            return ItemStack.EMPTY;
        }
        if (resultElement.isJsonPrimitive()) {
            return stackOf(resultElement.getAsString(), 1, defaultNamespace);
        }
        if (!resultElement.isJsonObject()) {
            return ItemStack.EMPTY;
        }

        JsonObject resultObject = resultElement.getAsJsonObject();
        if (!resultObject.has("item")) {
            return ItemStack.EMPTY;
        }

        String resultId = resultObject.get("item").getAsString();
        int resultCount = resultObject.has("count") ? Math.max(1, resultObject.get("count").getAsInt()) : 1;
        return stackOf(resultId, resultCount, defaultNamespace);
    }

    private static String convertTagToOreDict(String tagPath) {
        if (tagPath == null) {
            return null;
        }
        switch (tagPath) {
            case "forge:eggs":
            case "forge:cooked_eggs":
                return "listAllEgg";
            case "forge:raw_beef":
                return "listAllbeefraw";
            case "forge:raw_chicken":
                return "listAllchickenraw";
            case "forge:raw_mutton":
                return "listAllmuttonraw";
            case "forge:raw_fishes":
            case "forge:raw_fishes/cod":
                return "listAllfishraw";
            default:
                return null;
        }
    }

    private static Item itemOf(String path, String defaultNamespace) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        ResourceLocation itemId = path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(defaultNamespace, path);
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
