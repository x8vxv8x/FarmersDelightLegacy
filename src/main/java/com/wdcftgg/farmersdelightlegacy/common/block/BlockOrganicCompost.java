package com.wdcftgg.farmersdelightlegacy.common.block;

import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockOrganicCompost extends Block {
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 7);

    public BlockOrganicCompost() {
        super(Material.GROUND);
        this.setHardness(0.7F);
        this.setResistance(2.7F);
        this.setSoundType(SoundType.GROUND);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (heldItem.isEmpty() || Item.getItemFromBlock(this) != heldItem.getItem()) {
            return false;
        }

        int level = state.getValue(LEVEL);
        if (level >= 7) {
            return false;
        }

        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, state.withProperty(LEVEL, level + 1), 2);
            SoundType soundType = this.getSoundType(state, worldIn, pos, playerIn);
            worldIn.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS,
                    (soundType.getVolume() + 1.0F) * 0.5F, soundType.getPitch() * 0.8F);
            if (!playerIn.capabilities.isCreativeMode) {
                heldItem.shrink(1);
            }
        }
        return true;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }

        int level = state.getValue(LEVEL);
        if (level < 7) {
            if (rand.nextInt(5) == 0) {
                worldIn.setBlockState(pos, state.withProperty(LEVEL, level + 1), 2);
            }
            return;
        }

        worldIn.setBlockState(pos, ModBlocks.RICH_SOIL.getDefaultState(), 3);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEVEL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, Math.max(0, Math.min(7, meta)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LEVEL);
    }
}
