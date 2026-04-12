package com.wdcftgg.farmersdelightlegacy.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTatami extends BlockDirectional {

    public static final PropertyBool PAIRED = PropertyBool.create("paired");

    public BlockTatami() {
        super(Material.CLOTH);
        this.setHardness(0.8F);
        this.setResistance(0.8F);
        this.setSoundType(SoundType.CLOTH);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN).withProperty(PAIRED, false));
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        BlockPos targetPos = pos.offset(facing.getOpposite());
        IBlockState targetState = worldIn.getBlockState(targetPos);
        boolean canPair = !placer.isSneaking()
                && targetState.getBlock() == this
                && !targetState.getValue(PAIRED);
        return this.getDefaultState()
                .withProperty(FACING, facing.getOpposite())
                .withProperty(PAIRED, canPair);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, net.minecraft.item.ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isRemote || placer.isSneaking()) {
            return;
        }

        BlockPos facingPos = pos.offset(state.getValue(FACING));
        IBlockState facingState = worldIn.getBlockState(facingPos);
        if (facingState.getBlock() == this && !facingState.getValue(PAIRED)) {
            worldIn.setBlockState(facingPos, state.withProperty(FACING, state.getValue(FACING).getOpposite()).withProperty(PAIRED, true), 3);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (state.getValue(PAIRED) && fromPos.equals(pos.offset(state.getValue(FACING)))) {
            IBlockState facingState = worldIn.getBlockState(fromPos);
            if (facingState.getBlock() != this) {
                worldIn.setBlockState(pos, state.withProperty(PAIRED, false), 3);
                return;
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING, PAIRED});
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getIndex();
        if (state.getValue(PAIRED)) {
            meta |= 8;
        }
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta & 7);
        if (facing == null) {
            facing = EnumFacing.DOWN;
        }
        return this.getDefaultState()
                .withProperty(FACING, facing)
                .withProperty(PAIRED, (meta & 8) != 0);
    }
}
