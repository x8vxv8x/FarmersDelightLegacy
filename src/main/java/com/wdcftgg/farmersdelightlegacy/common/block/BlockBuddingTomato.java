package com.wdcftgg.farmersdelightlegacy.common.block;

import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import java.util.Random;

public class BlockBuddingTomato extends BlockCrops {

    public BlockBuddingTomato() {
        this.setTickRandomly(true);
        this.setHardness(0.0F);
        this.setSoundType(net.minecraft.block.SoundType.PLANT);
    }

    @Override
    public int getMaxAge() {
        return 4;
    }

    @Override
    protected Item getSeed() {
        return ModItems.ITEMS.get("tomato_seeds");
    }

    @Override
    protected Item getCrop() {
        return ModItems.ITEMS.get("tomato");
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            worldIn.destroyBlock(pos, true);
            return;
        }
        super.updateTick(worldIn, pos, state, rand);
        if (!worldIn.isRemote && this.getAge(state) >= this.getMaxAge()) {
            worldIn.setBlockState(pos, ModBlocks.TOMATOES.getDefaultState(), 3);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            worldIn.destroyBlock(pos, true);
            return;
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        int maxAge = this.getMaxAge();
        int growth = Math.min(this.getAge(state) + this.getBonemealAgeIncrease(worldIn), 7);
        if (growth <= maxAge) {
            worldIn.setBlockState(pos, this.withAge(growth), 2);
        } else {
            int remainingGrowth = Math.max(0, growth - maxAge - 1);
            IBlockState vineState = ModBlocks.TOMATOES.getDefaultState()
                    .withProperty(BlockTomatoVine.ROPELOGGED, false)
                    .withProperty(this.getAgeProperty(), Math.min(remainingGrowth, 3));
            worldIn.setBlockState(pos, vineState, 2);
        }
    }

    @Override
    public EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos) {
        return EnumPlantType.Crop;
    }
}
