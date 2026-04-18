package com.wdcftgg.farmersdelightlegacy.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModOreDictionary;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

public final class CuttingBoardRecipeManager {

    private static final String DEFAULT_TOOL_TOKEN = "ore:toolKnife";
    private static final Map<String, String> TAG_TO_OREDICT = ModOreDictionary.getTagToOreDictMap();

    private static final List<CuttingRecipeEntry> RECIPES = new ArrayList<>();
    private static boolean loaded;

    private CuttingBoardRecipeManager() {
    }

    public static boolean hasRecipe(ItemStack inputStack, ItemStack toolStack) {
        ensureLoaded();
        if (inputStack.isEmpty() || toolStack.isEmpty()) {
            return false;
        }

        for (CuttingRecipeEntry recipeEntry : RECIPES) {
            if (recipeEntry.matches(inputStack, toolStack)) {
                return true;
            }
        }
        return false;
    }

    public static List<ItemStack> getProcessedResults(ItemStack inputStack, ItemStack toolStack, Random random) {
        ensureLoaded();
        if (inputStack.isEmpty() || toolStack.isEmpty()) {
            return Collections.emptyList();
        }

        for (CuttingRecipeEntry recipeEntry : RECIPES) {
            if (recipeEntry.matches(inputStack, toolStack)) {
                return recipeEntry.rollResults(random);
            }
        }
        return Collections.emptyList();
    }

    public static boolean isUsedAsRecipeTool(ItemStack toolStack) {
        ensureLoaded();
        if (toolStack.isEmpty()) {
            return false;
        }
        for (CuttingRecipeEntry recipeEntry : RECIPES) {
            if (recipeEntry.toolMatcher.matchesTool(toolStack)) {
                return true;
            }
        }
        return false;
    }

    public static List<CuttingBoardRecipeView> getRecipes() {
        ensureLoaded();
        List<CuttingBoardRecipeView> result = new ArrayList<>();
        for (CuttingRecipeEntry recipeEntry : RECIPES) {
            result.add(new CuttingBoardRecipeView(
                    recipeEntry.inputMatcher.asStacks(),
                    recipeEntry.toolMatcher.asStacks(),
                    recipeEntry.getDisplayResults(),
                    recipeEntry.getDisplayResultChances()));
        }
        return result;
    }

    public static final class CuttingBoardRecipeView {
        private final List<ItemStack> inputOptions;
        private final List<ItemStack> toolOptions;
        private final List<ItemStack> resultStacks;
        private final List<Float> resultChances;

        private CuttingBoardRecipeView(List<ItemStack> inputOptions, List<ItemStack> toolOptions, List<ItemStack> resultStacks, List<Float> resultChances) {
            this.inputOptions = copyStacks(inputOptions);
            this.toolOptions = copyStacks(toolOptions);
            this.resultStacks = copyStacks(resultStacks);
            this.resultChances = new ArrayList<>(resultChances);
        }

        public List<ItemStack> getInputOptions() {
            return copyStacks(inputOptions);
        }

        public List<ItemStack> getToolOptions() {
            return copyStacks(toolOptions);
        }

        public List<ItemStack> getResultStacks() {
            return copyStacks(resultStacks);
        }

        public List<Float> getResultChances() {
            return new ArrayList<>(resultChances);
        }
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
        for (RecipeResourceScanner.RecipeJsonResource recipeResource : RecipeResourceScanner.scan("cutting_board")) {
            parseAndRegister(recipeResource.getJsonObject(), recipeResource.getModId());
        }
    }

    private static void parseAndRegister(JsonObject recipeJson, String defaultNamespace) {
        if (!passesConditions(recipeJson)) {
            return;
        }
        IngredientMatcher inputMatcher = readInput(recipeJson, defaultNamespace);
        IngredientMatcher toolMatcher = readTool(recipeJson, defaultNamespace);
        List<ResultEntry> resultEntries = readResults(recipeJson, defaultNamespace);
        if (!inputMatcher.isValid() || !toolMatcher.isValid() || resultEntries.isEmpty()) {
            return;
        }
        RECIPES.add(new CuttingRecipeEntry(inputMatcher, toolMatcher, resultEntries));
    }

    private static boolean passesConditions(JsonObject recipeJson) {
        if (!recipeJson.has("conditions") || !recipeJson.get("conditions").isJsonObject()) {
            return true;
        }

        JsonObject conditionsObject = recipeJson.getAsJsonObject("conditions");
        return matchesModCondition(conditionsObject.get("mod_loaded"), true)
                && matchesModCondition(conditionsObject.get("mod_not_loaded"), false);
    }

    private static boolean matchesModCondition(JsonElement conditionElement, boolean expectedLoaded) {
        if (conditionElement == null || conditionElement.isJsonNull()) {
            return true;
        }

        List<String> modIds = new ArrayList<>();
        collectConditionStrings(conditionElement, modIds);
        for (String modId : modIds) {
            if (Loader.isModLoaded(modId) != expectedLoaded) {
                return false;
            }
        }
        return true;
    }

    private static void collectConditionStrings(JsonElement element, List<String> values) {
        if (element == null || element.isJsonNull()) {
            return;
        }

        if (element.isJsonPrimitive()) {
            String value = element.getAsString();
            if (!value.isEmpty()) {
                values.add(value);
            }
            return;
        }

        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                collectConditionStrings(child, values);
            }
        }
    }

    private static IngredientMatcher readInput(JsonObject recipeJson, String defaultNamespace) {
        if (recipeJson.has("ingredient")) {
            IngredientMatcher ingredientMatcher = IngredientMatcher.fromJson(recipeJson.get("ingredient"), defaultNamespace);
            if (ingredientMatcher.isValid()) {
                return ingredientMatcher;
            }
        }
        if (recipeJson.has("input") && recipeJson.get("input").isJsonPrimitive()) {
            return IngredientMatcher.fromToken(recipeJson.get("input").getAsString(), defaultNamespace);
        }
        if (recipeJson.has("ingredients") && recipeJson.get("ingredients").isJsonArray()) {
            return IngredientMatcher.fromJson(recipeJson.get("ingredients"), defaultNamespace);
        }
        return IngredientMatcher.invalid();
    }

    private static IngredientMatcher readTool(JsonObject recipeJson, String defaultNamespace) {
        if (recipeJson.has("tool")) {
            IngredientMatcher toolMatcher = IngredientMatcher.fromJson(recipeJson.get("tool"), defaultNamespace);
            if (toolMatcher.isValid()) {
                return toolMatcher;
            }
        }
        return IngredientMatcher.fromToken(DEFAULT_TOOL_TOKEN, defaultNamespace);
    }

    private static List<ResultEntry> readResults(JsonObject recipeJson, String defaultNamespace) {
        List<ResultEntry> resultEntries = new ArrayList<>();

        if (recipeJson.has("output") && recipeJson.get("output").isJsonPrimitive()) {
            String outputPath = recipeJson.get("output").getAsString();
            int outputCount = recipeJson.has("count") ? Math.max(1, recipeJson.get("count").getAsInt()) : 1;
            Item outputItem = itemOf(outputPath, defaultNamespace);
            if (outputItem != null) {
                resultEntries.add(new ResultEntry(new ItemStack(outputItem, outputCount), 1.0F));
            }
            return resultEntries;
        }

        if (recipeJson.has("result")) {
            JsonElement resultElement = recipeJson.get("result");
            if (resultElement.isJsonArray()) {
                JsonArray resultArray = resultElement.getAsJsonArray();
                for (JsonElement element : resultArray) {
                    ResultEntry entry = parseResultEntry(element, defaultNamespace);
                    if (entry != null) {
                        resultEntries.add(entry);
                    }
                }
            } else {
                ResultEntry single = parseResultEntry(resultElement, defaultNamespace);
                if (single != null) {
                    resultEntries.add(single);
                }
            }
        }

        return resultEntries;
    }

    private static ResultEntry parseResultEntry(JsonElement resultElement, String defaultNamespace) {
        if (resultElement == null || !resultElement.isJsonObject()) {
            return null;
        }

        JsonObject resultObject = resultElement.getAsJsonObject();
        if (!resultObject.has("item") || !resultObject.get("item").isJsonPrimitive()) {
            return null;
        }

        Item resultItem = itemOf(resultObject.get("item").getAsString(), defaultNamespace);
        if (resultItem == null) {
            return null;
        }

        int count = 1;
        if (resultObject.has("count")) {
            count = Math.max(1, resultObject.get("count").getAsInt());
        }

        int metadata = 0;
        if (resultObject.has("data")) {
            metadata = Math.max(0, resultObject.get("data").getAsInt());
        }

        float chance = 1.0F;
        if (resultObject.has("chance")) {
            chance = clampChance((float) resultObject.get("chance").getAsDouble());
        } else if (resultObject.has("probability")) {
            chance = clampChance((float) resultObject.get("probability").getAsDouble());
        }

        return new ResultEntry(new ItemStack(resultItem, count, metadata), chance);
    }

    private static float clampChance(float chance) {
        if (chance < 0.0F) {
            return 0.0F;
        }
        if (chance > 1.0F) {
            return 1.0F;
        }
        return chance;
    }

    private static List<ItemStack> copyStacks(List<ItemStack> source) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : source) {
            if (!stack.isEmpty()) {
                result.add(stack.copy());
            }
        }
        return result;
    }

    private static final class CuttingRecipeEntry {

        private final IngredientMatcher inputMatcher;
        private final IngredientMatcher toolMatcher;
        private final List<ResultEntry> resultEntries;

        private CuttingRecipeEntry(IngredientMatcher inputMatcher, IngredientMatcher toolMatcher, List<ResultEntry> resultEntries) {
            this.inputMatcher = inputMatcher;
            this.toolMatcher = toolMatcher;
            this.resultEntries = new ArrayList<>(resultEntries);
        }

        private boolean matches(ItemStack inputStack, ItemStack toolStack) {
            return inputMatcher.matches(inputStack) && toolMatcher.matchesTool(toolStack);
        }

        private List<ItemStack> rollResults(Random random) {
            Random actualRandom = random == null ? new Random() : random;
            List<ItemStack> rolledResults = new ArrayList<>();
            for (ResultEntry resultEntry : resultEntries) {
                if (resultEntry.chance >= 1.0F || actualRandom.nextFloat() <= resultEntry.chance) {
                    rolledResults.add(resultEntry.stack.copy());
                }
            }
            return rolledResults;
        }

        private List<ItemStack> getDisplayResults() {
            List<ItemStack> display = new ArrayList<>();
            for (ResultEntry entry : resultEntries) {
                display.add(entry.stack.copy());
            }
            return display;
        }

        private List<Float> getDisplayResultChances() {
            List<Float> display = new ArrayList<>();
            for (ResultEntry entry : resultEntries) {
                display.add(entry.chance);
            }
            return display;
        }
    }

    private static final class ResultEntry {

        private final ItemStack stack;
        private final float chance;

        private ResultEntry(ItemStack stack, float chance) {
            this.stack = stack.copy();
            this.chance = clampChance(chance);
        }
    }

    private static final class IngredientMatcher {

        private final List<ItemStack> itemStacks;
        private final List<String> oreDictNames;

        private IngredientMatcher(List<ItemStack> itemStacks, List<String> oreDictNames) {
            this.itemStacks = itemStacks;
            this.oreDictNames = oreDictNames;
        }

        private static IngredientMatcher invalid() {
            return new IngredientMatcher(Collections.<ItemStack>emptyList(), Collections.<String>emptyList());
        }

        private static IngredientMatcher fromToken(String token, String defaultNamespace) {
            if (token == null || token.isEmpty()) {
                return invalid();
            }

            List<ItemStack> itemStacks = new ArrayList<>();
            List<String> oreDictNames = new ArrayList<>();
            addToken(token, OreDictionary.WILDCARD_VALUE, itemStacks, oreDictNames, defaultNamespace);
            return new IngredientMatcher(itemStacks, oreDictNames);
        }

        private static IngredientMatcher fromJson(JsonElement element, String defaultNamespace) {
            if (element == null || element.isJsonNull()) {
                return invalid();
            }

            List<ItemStack> itemStacks = new ArrayList<>();
            List<String> oreDictNames = new ArrayList<>();
            collectTokens(element, itemStacks, oreDictNames, defaultNamespace);
            return new IngredientMatcher(itemStacks, oreDictNames);
        }

        private static void collectTokens(JsonElement element, List<ItemStack> itemStacks, List<String> oreDictNames, String defaultNamespace) {
            if (element == null || element.isJsonNull()) {
                return;
            }

            if (element.isJsonPrimitive()) {
                addToken(element.getAsString(), OreDictionary.WILDCARD_VALUE, itemStacks, oreDictNames, defaultNamespace);
                return;
            }

            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement child : array) {
                    collectTokens(child, itemStacks, oreDictNames, defaultNamespace);
                }
                return;
            }

            if (!element.isJsonObject()) {
                return;
            }

            JsonObject jsonObject = element.getAsJsonObject();
            if (jsonObject.has("item") && jsonObject.get("item").isJsonPrimitive()) {
                int metadata = OreDictionary.WILDCARD_VALUE;
                if (jsonObject.has("data") && jsonObject.get("data").isJsonPrimitive()) {
                    metadata = Math.max(0, jsonObject.get("data").getAsInt());
                }
                addToken(jsonObject.get("item").getAsString(), metadata, itemStacks, oreDictNames, defaultNamespace);
            }
            if (jsonObject.has("tag") && jsonObject.get("tag").isJsonPrimitive()) {
                String oreToken = tagToOreToken(jsonObject.get("tag").getAsString());
                if (oreToken != null) {
                    addToken(oreToken, OreDictionary.WILDCARD_VALUE, itemStacks, oreDictNames, defaultNamespace);
                }
            }
            if (jsonObject.has("action") && jsonObject.get("action").isJsonPrimitive()) {
                String actionToken = actionToOreToken(jsonObject.get("action").getAsString());
                if (actionToken != null) {
                    addToken(actionToken, OreDictionary.WILDCARD_VALUE, itemStacks, oreDictNames, defaultNamespace);
                }
            }
        }

        private static void addToken(String token, int metadata, List<ItemStack> itemStacks, List<String> oreDictNames, String defaultNamespace) {
            if (token == null || token.isEmpty()) {
                return;
            }

            if (token.startsWith("ore:")) {
                String oreName = token.substring(4);
                if (!oreName.isEmpty() && !oreDictNames.contains(oreName)) {
                    oreDictNames.add(oreName);
                }
                return;
            }

            Item item = itemOf(token, defaultNamespace);
            if (item != null) {
                for (ItemStack candidate : itemStacks) {
                    if (candidate.getItem() == item && candidate.getMetadata() == metadata) {
                        return;
                    }
                }
                itemStacks.add(new ItemStack(item, 1, metadata));
            }
        }

        private boolean isValid() {
            return !itemStacks.isEmpty() || !oreDictNames.isEmpty();
        }

        private boolean matches(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }
            for (ItemStack candidate : itemStacks) {
                if (candidate.getItem() == stack.getItem()) {
                    int candidateMeta = candidate.getMetadata();
                    if (candidateMeta == OreDictionary.WILDCARD_VALUE || candidateMeta == stack.getMetadata()) {
                        return true;
                    }
                }
            }
            if (oreDictNames.isEmpty()) {
                return false;
            }

            int[] stackOreIds = OreDictionary.getOreIDs(stack);
            for (String oreName : oreDictNames) {
                int expectedId = OreDictionary.getOreID(oreName);
                if (expectedId < 0) {
                    continue;
                }
                for (int oreId : stackOreIds) {
                    if (oreId == expectedId) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean matchesTool(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }

            if (matchesDirectItem(stack)) {
                return true;
            }

            for (String oreName : oreDictNames) {
                for (ItemStack oreStack : OreDictionary.getOres(oreName)) {
                    if (isItemAndMetaMatch(oreStack, stack)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean matchesDirectItem(ItemStack stack) {
            for (ItemStack candidate : itemStacks) {
                if (isItemAndMetaMatch(candidate, stack)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isItemAndMetaMatch(ItemStack candidate, ItemStack target) {
            if (candidate.isEmpty() || target.isEmpty() || candidate.getItem() != target.getItem()) {
                return false;
            }

            int candidateMeta = candidate.getMetadata();
            if (candidateMeta == OreDictionary.WILDCARD_VALUE) {
                return true;
            }
            if (target.isItemStackDamageable()) {
                return true;
            }
            return candidateMeta == target.getMetadata();
        }

        private List<ItemStack> asStacks() {
            List<ItemStack> result = new ArrayList<>();
            for (ItemStack itemStack : itemStacks) {
                int metadata = itemStack.getMetadata();
                if (metadata == OreDictionary.WILDCARD_VALUE) {
                    result.add(new ItemStack(itemStack.getItem()));
                } else {
                    result.add(itemStack.copy());
                }
            }
            for (String oreName : oreDictNames) {
                for (ItemStack oreStack : OreDictionary.getOres(oreName)) {
                    result.add(oreStack.copy());
                }
            }
            return result;
        }
    }

    private static String tagToOreToken(String tagPath) {
        if (tagPath == null || tagPath.isEmpty()) {
            return null;
        }
        String oreName = TAG_TO_OREDICT.get(tagPath);
        if (oreName == null || oreName.isEmpty()) {
            return null;
        }
        return "ore:" + oreName;
    }

    private static String actionToOreToken(String actionPath) {
        if (actionPath == null || actionPath.isEmpty()) {
            return null;
        }
        if ("axe_dig".equals(actionPath) || "axe_strip".equals(actionPath)) {
            return "ore:toolAxe";
        }
        if ("pickaxe_dig".equals(actionPath)) {
            return "ore:toolPickaxe";
        }
        if ("shovel_dig".equals(actionPath)) {
            return "ore:toolShovel";
        }
        return null;
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
}

