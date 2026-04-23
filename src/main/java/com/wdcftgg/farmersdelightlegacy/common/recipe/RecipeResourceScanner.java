package com.wdcftgg.farmersdelightlegacy.common.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RecipeResourceScanner {

    private static final Gson GSON = new Gson();

    private RecipeResourceScanner() {
    }

    public static List<RecipeJsonResource> scan(String recipeDirectory) {
        List<RecipeJsonResource> resources = new ArrayList<>();
        Map<String, Integer> modOrder = new HashMap<>();
        int order = 0;

        for (ModContainer modContainer : Loader.instance().getActiveModList()) {
            if (modContainer == null || modContainer.getModId() == null || modContainer.getModId().isEmpty()) {
                continue;
            }

            modOrder.put(modContainer.getModId(), order++);
            collectResources(modContainer, recipeDirectory, resources);
        }

        resources.sort(Comparator
                .comparingInt((RecipeJsonResource resource) -> modOrder.getOrDefault(resource.getModId(), Integer.MAX_VALUE))
                .thenComparing(RecipeJsonResource::getRelativePath));
        return resources;
    }

    private static void collectResources(ModContainer modContainer, String recipeDirectory, List<RecipeJsonResource> target) {
        String basePath = "assets/" + modContainer.getModId() + "/fd_recipes/" + recipeDirectory;

        CraftingHelper.findFiles(modContainer, basePath, null, (root, file) -> {
            if (!Files.isRegularFile(file)) {
                return true;
            }

            String relativePath = root.relativize(file).toString().replace('\\', '/');
            if (!relativePath.endsWith(".json") || relativePath.startsWith("_")) {
                return true;
            }

            JsonObject recipeJson = readJson(file);
            if (recipeJson != null) {
                target.add(new RecipeJsonResource(modContainer.getModId(), relativePath, recipeJson));
            }
            return true;
        }, true, true);
    }

    private static JsonObject readJson(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, JsonObject.class);
        } catch (IOException exception) {
            FarmersDelightLegacy.LOGGER.error("读取自定义配方失败：{}", file, exception);
            return null;
        }
    }

    public static final class RecipeJsonResource {
        private final String modId;
        private final String relativePath;
        private final JsonObject jsonObject;

        private RecipeJsonResource(String modId, String relativePath, JsonObject jsonObject) {
            this.modId = modId;
            this.relativePath = relativePath;
            this.jsonObject = jsonObject;
        }

        public String getModId() {
            return modId;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public String getRecipeId() {
            String recipePath = this.relativePath;
            if (recipePath.endsWith(".json")) {
                recipePath = recipePath.substring(0, recipePath.length() - 5);
            }
            return this.modId + ":" + recipePath;
        }

        public JsonObject getJsonObject() {
            return jsonObject;
        }
    }
}
