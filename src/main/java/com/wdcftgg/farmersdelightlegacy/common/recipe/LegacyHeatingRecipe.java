package com.wdcftgg.farmersdelightlegacy.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class LegacyHeatingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    private static boolean smeltingRecipesRegistered;

    private final String group;
    private final Ingredient ingredient;
    private final ItemStack recipeOutput;
    private final float experience;
    private final int cookingTime;

    public LegacyHeatingRecipe(String group, Ingredient ingredient, ItemStack recipeOutput, float experience, int cookingTime) {
        this.group = group == null ? "" : group;
        this.ingredient = ingredient == null ? Ingredient.EMPTY : ingredient;
        this.recipeOutput = recipeOutput.copy();
        this.experience = Math.max(0.0F, experience);
        this.cookingTime = Math.max(1, cookingTime);
    }

    public static void registerSmeltingRecipes() {
        if (smeltingRecipesRegistered) {
            return;
        }

        Set<String> registeredInputs = new LinkedHashSet<>();
        Collection<IRecipe> recipes = ForgeRegistries.RECIPES.getValuesCollection();
        for (IRecipe recipe : recipes) {
            if (!(recipe instanceof LegacyHeatingRecipe)) {
                continue;
            }

            LegacyHeatingRecipe legacyRecipe = (LegacyHeatingRecipe) recipe;
            for (ItemStack inputStack : legacyRecipe.ingredient.getMatchingStacks()) {
                if (inputStack.isEmpty()) {
                    continue;
                }

                String inputKey = buildStackKey(inputStack);
                if (!registeredInputs.add(inputKey)) {
                    continue;
                }

                FurnaceRecipes.instance().addSmeltingRecipe(inputStack.copy(), legacyRecipe.recipeOutput.copy(), legacyRecipe.experience);
            }
        }

        smeltingRecipesRegistered = true;
    }

    private static String buildStackKey(ItemStack stack) {
        ResourceLocation itemId = stack.getItem().getRegistryName();
        return String.valueOf(itemId) + "#" + stack.getMetadata();
    }

    private static void unlockMatchingRecipes(EntityPlayerMP player, ItemStack stack) {
        if (player == null || stack.isEmpty()) {
            return;
        }

        Set<IRecipe> matchingRecipes = new LinkedHashSet<>();
        for (IRecipe recipe : ForgeRegistries.RECIPES.getValuesCollection()) {
            if (recipe instanceof LegacyHeatingRecipe) {
                LegacyHeatingRecipe legacyRecipe = (LegacyHeatingRecipe) recipe;
                if (legacyRecipe.ingredient.apply(stack)) {
                    matchingRecipes.add(legacyRecipe);
                }
            }
        }

        if (!matchingRecipes.isEmpty()) {
            player.unlockRecipes(NonNullList.from(null, matchingRecipes.toArray(new IRecipe[0])));
        }
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return recipeOutput.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return recipeOutput.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(ingredient);
        return ingredients;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public float getExperience() {
        return experience;
    }

    public static final class Factory implements IRecipeFactory {

        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            Ingredient ingredient = CraftingHelper.getIngredient(json.get("ingredient"), context);
            ItemStack result = parseResult(context, json.get("result"));
            if (ingredient == Ingredient.EMPTY || result.isEmpty()) {
                throw new JsonSyntaxException("无效的加热配方定义");
            }

            String group = JsonUtils.getString(json, "group", "");
            float experience = JsonUtils.getFloat(json, "experience", 0.0F);
            int cookingTime = JsonUtils.getInt(json, "cookingtime", 200);
            return new LegacyHeatingRecipe(group, ingredient, result, experience, cookingTime);
        }

        private ItemStack parseResult(JsonContext context, JsonElement resultElement) {
            if (resultElement == null || resultElement.isJsonNull()) {
                return ItemStack.EMPTY;
            }

            if (resultElement.isJsonObject()) {
                return CraftingHelper.getItemStack(resultElement.getAsJsonObject(), context);
            }

            if (resultElement.isJsonPrimitive()) {
                JsonObject resultObject = new JsonObject();
                resultObject.addProperty("item", resultElement.getAsString());
                return CraftingHelper.getItemStack(resultObject, context);
            }

            throw new JsonSyntaxException("加热配方结果必须是物品对象或物品 ID");
        }
    }

    @Mod.EventBusSubscriber(modid = FarmersDelightLegacy.MOD_ID)
    public static final class UnlockEvents {

        private UnlockEvents() {
        }

        @SubscribeEvent
        public static void onItemPickup(EntityItemPickupEvent event) {
            if (event.getEntityPlayer() instanceof EntityPlayerMP) {
                unlockMatchingRecipes((EntityPlayerMP) event.getEntityPlayer(), event.getItem().getItem());
            }
        }

        @SubscribeEvent
        public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
            if (event.player instanceof EntityPlayerMP) {
                unlockMatchingRecipes((EntityPlayerMP) event.player, event.crafting);
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (!(event.player instanceof EntityPlayerMP)) {
                return;
            }

            EntityPlayerMP player = (EntityPlayerMP) event.player;
            for (ItemStack stack : player.inventory.mainInventory) {
                unlockMatchingRecipes(player, stack);
            }
            for (ItemStack stack : player.inventory.offHandInventory) {
                unlockMatchingRecipes(player, stack);
            }
        }
    }
}
