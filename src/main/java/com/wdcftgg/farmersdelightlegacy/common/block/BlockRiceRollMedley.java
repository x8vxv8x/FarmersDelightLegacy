package com.wdcftgg.farmersdelightlegacy.common.block;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockRiceRollMedley extends BlockFeast {

    public static final PropertyInteger ROLL_SERVINGS = PropertyInteger.create("servings", 0, 8);

    private static final AxisAlignedBB PLATE_SHAPE = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.125D, 0.9375D);
    private static final AxisAlignedBB FOOD_SHAPE = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.25D, 0.9375D);

    private static final ResourceLocation[] SERVING_ITEMS = new ResourceLocation[]{
            new ResourceLocation("farmersdelight", "cod_roll"),
            new ResourceLocation("farmersdelight", "cod_roll"),
            new ResourceLocation("farmersdelight", "salmon_roll"),
            new ResourceLocation("farmersdelight", "salmon_roll"),
            new ResourceLocation("farmersdelight", "salmon_roll"),
            new ResourceLocation("farmersdelight", "kelp_roll_slice"),
            new ResourceLocation("farmersdelight", "kelp_roll_slice"),
            new ResourceLocation("farmersdelight", "kelp_roll_slice")
    };

    public BlockRiceRollMedley() {
        super(8, "salmon_roll", null, true);
    }

    @Override
    public PropertyInteger getServingsProperty() {
        return ROLL_SERVINGS;
    }

    @Override
    protected ItemStack getServingStackForServings(int servings) {
        int servingIndex = servings - 1;
        if (servingIndex < 0 || servingIndex >= SERVING_ITEMS.length) {
            return ItemStack.EMPTY;
        }
        Item item = ForgeRegistries.ITEMS.getValue(SERVING_ITEMS[servingIndex]);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override
    protected ItemStack getRequiredServingItem(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                               EnumHand hand, ItemStack servingResult, int servings) {
        return null;
    }

    @Override
    public AxisAlignedBB getBoundingBox(net.minecraft.block.state.IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(getServingsProperty()) == 0 ? PLATE_SHAPE : FOOD_SHAPE;
    }
}

