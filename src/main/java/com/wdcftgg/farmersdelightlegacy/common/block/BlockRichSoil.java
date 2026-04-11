package com.wdcftgg.farmersdelightlegacy.common.block;

import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRichSoil extends Block {
    public BlockRichSoil() {
        super(Material.GROUND);
        this.setHardness(0.7F);
        this.setResistance(2.7F);
        this.setSoundType(SoundType.GROUND);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof ItemHoe)) {
            return false;
        }
        if (facing == EnumFacing.DOWN || !worldIn.isAirBlock(pos.up())) {
            return false;
        }

        if (worldIn.isRemote) {
            return true;
        }

        IBlockState farmlandState = ModBlocks.RICH_SOIL_FARMLAND.getDefaultState()
                .withProperty(BlockFarmland.MOISTURE, 0);
        worldIn.setBlockState(pos, farmlandState, 11);
        SoundType soundType = farmlandState.getBlock().getSoundType(farmlandState, worldIn, pos, playerIn);
        worldIn.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS,
                (soundType.getVolume() + 1.0F) * 0.5F, soundType.getPitch() * 0.8F);
        heldItem.damageItem(1, playerIn);
        return true;
    }
}
