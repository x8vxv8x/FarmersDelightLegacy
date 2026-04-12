package com.wdcftgg.farmersdelightlegacy.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRope extends BlockPane {

    public static final PropertyBool TIED_TO_BELL = PropertyBool.create("tied_to_bell");

    public BlockRope() {
        super(Material.CLOTH, true);
        this.setHardness(0.2F);
        this.setResistance(0.2F);
        this.setSoundType(SoundType.CLOTH);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(NORTH, false)
                .withProperty(EAST, false)
                .withProperty(SOUTH, false)
                .withProperty(WEST, false)
                .withProperty(TIED_TO_BELL, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{NORTH, EAST, WEST, SOUTH, TIED_TO_BELL});
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState actual = super.getActualState(state, worldIn, pos);
        return actual.withProperty(TIED_TO_BELL, isBellBlock(worldIn.getBlockState(pos.up())));
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldStack = playerIn.getHeldItem(hand);
        if (!heldStack.isEmpty()) {
            return false;
        }

        if (playerIn.isSneaking()) {
            return reelRope(worldIn, pos, playerIn);
        }

        return ringBellAbove(worldIn, pos);
    }

    private boolean reelRope(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
        int minY = 0;
        while (cursor.getY() >= minY) {
            if (worldIn.getBlockState(cursor).getBlock() == this) {
                cursor.move(EnumFacing.DOWN);
                continue;
            }
            cursor.move(EnumFacing.UP);
            break;
        }

        if (worldIn.getBlockState(cursor).getBlock() != this) {
            return false;
        }

        if (!worldIn.isRemote) {
            if (!playerIn.capabilities.isCreativeMode) {
                ItemStack ropeStack = new ItemStack(Item.getItemFromBlock(this));
                if (!playerIn.inventory.addItemStackToInventory(ropeStack)) {
                    playerIn.dropItem(ropeStack, false);
                }
            }
            worldIn.destroyBlock(cursor, false);
        }
        return true;
    }

    private boolean ringBellAbove(World worldIn, BlockPos pos) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
        for (int i = 0; i < 24; i++) {
            cursor.move(EnumFacing.UP);
            IBlockState stateAbove = worldIn.getBlockState(cursor);
            Block blockAbove = stateAbove.getBlock();
            if (blockAbove == this) {
                continue;
            }
            if (isBellBlock(stateAbove)) {
                worldIn.playSound(null, cursor, SoundEvents.BLOCK_NOTE_BELL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean isBellBlock(IBlockState state) {
        if (state == null || state.getBlock() == null || state.getBlock().getRegistryName() == null) {
            return false;
        }
        String path = state.getBlock().getRegistryName().getPath();
        return path != null && path.contains("bell");
    }
}
