package com.wdcftgg.farmersdelightlegacy.common.recipe;

import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class WaterDoughRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int wheatCount = 0;
        int waterBucketCount = 0;

        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            Item item = stack.getItem();
            if (item == Items.WHEAT) {
                wheatCount++;
            } else if (item == Items.WATER_BUCKET) {
                waterBucketCount++;
            } else {
                return false;
            }
        }

        return wheatCount == 1 && waterBucketCount == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        Item wheatDough = ModItems.get("wheat_dough");
        return wheatDough != null ? new ItemStack(wheatDough) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack getRecipeOutput() {
        Item wheatDough = ModItems.get("wheat_dough");
        return wheatDough != null ? new ItemStack(wheatDough) : ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> remainders = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.getItem() == Items.WATER_BUCKET) {
                remainders.set(slot, new ItemStack(Items.WATER_BUCKET));
            } else {
                remainders.set(slot, ForgeHooks.getContainerItem(stack));
            }
        }

        return remainders;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public String getGroup() {
        return "fd_dough";
    }
}
