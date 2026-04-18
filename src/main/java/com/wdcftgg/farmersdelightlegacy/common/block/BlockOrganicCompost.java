package com.wdcftgg.farmersdelightlegacy.common.block;

import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.EnumSkyBlock;
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

        float chance = 0.0F;
        boolean hasWater = false;
        int maxSkyLight = 0;

        for (BlockPos neighborPos : BlockPos.getAllInBoxMutable(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            IBlockState neighborState = worldIn.getBlockState(neighborPos);
            if (isCompostActivator(neighborState)) {
                chance += 0.02F;
            }
            if (neighborState.getMaterial() == Material.WATER) {
                hasWater = true;
            }
            int skyLight = worldIn.getLightFor(EnumSkyBlock.SKY, neighborPos.up());
            if (skyLight > maxSkyLight) {
                maxSkyLight = skyLight;
            }
        }

        chance += maxSkyLight > 12 ? 0.1F : 0.05F;
        if (hasWater) {
            chance += 0.1F;
        }

        if (rand.nextFloat() > chance) {
            return;
        }

        int level = state.getValue(LEVEL);
        if (level >= getMaxCompostingStage()) {
            worldIn.setBlockState(pos, ModBlocks.RICH_SOIL.getDefaultState(), 3);
        } else {
            worldIn.setBlockState(pos, state.withProperty(LEVEL, level + 1), 3);
        }
    }

    public int getMaxCompostingStage() {
        return 7;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return getMaxCompostingStage() + 1 - blockState.getValue(LEVEL);
    }

    private boolean isCompostActivator(IBlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.BROWN_MUSHROOM || block == Blocks.RED_MUSHROOM || block == Blocks.MYCELIUM) {
            return true;
        }
        if (block == Blocks.DIRT && state.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL) {
            return true;
        }
        return block == ModBlocks.ORGANIC_COMPOST
                || block == ModBlocks.RICH_SOIL
                || block == ModBlocks.RICH_SOIL_FARMLAND
                || block == ModBlocks.BROWN_MUSHROOM_COLONY
                || block == ModBlocks.RED_MUSHROOM_COLONY;
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
