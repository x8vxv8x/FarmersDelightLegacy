package com.wdcftgg.farmersdelightlegacy.common.recipe;

import com.wdcftgg.farmersdelightlegacy.common.item.ItemCookingPot;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCookingPot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class FoodServingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack cookingPotStack = ItemStack.EMPTY;
        ItemStack containerStack = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack selected = inv.getStackInSlot(i);
            if (selected.isEmpty()) {
                continue;
            }

            if (cookingPotStack.isEmpty() && selected.getItem() == Item.getItemFromBlock(ModBlocks.COOKING_POT)
                    && !TileEntityCookingPot.getMealFromItem(selected).isEmpty()) {
                cookingPotStack = selected;
                continue;
            }

            if (containerStack.isEmpty()) {
                containerStack = selected;
                continue;
            }
            return false;
        }

        if (cookingPotStack.isEmpty() || containerStack.isEmpty()) {
            return false;
        }

        ItemStack meal = TileEntityCookingPot.getMealFromItem(cookingPotStack);
        ItemStack requiredContainer = ItemCookingPot.inferContainer(cookingPotStack, meal);
        return !requiredContainer.isEmpty() && ItemStack.areItemsEqual(requiredContainer, containerStack);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack selected = inv.getStackInSlot(i);
            if (!selected.isEmpty() && selected.getItem() == Item.getItemFromBlock(ModBlocks.COOKING_POT)) {
                ItemStack meal = TileEntityCookingPot.getMealFromItem(selected).copy();
                if (!meal.isEmpty()) {
                    meal.setCount(1);
                    return meal;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> remainders = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack selected = inv.getStackInSlot(i);
            if (selected.isEmpty()) {
                continue;
            }

            if (selected.getItem().hasContainerItem(selected)) {
                remainders.set(i, selected.getItem().getContainerItem(selected));
                continue;
            }

            if (selected.getItem() == net.minecraft.item.Item.getItemFromBlock(ModBlocks.COOKING_POT)) {
                remainders.set(i, TileEntityCookingPot.consumeServingFromItem(selected));
            }
        }

        return remainders;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}

