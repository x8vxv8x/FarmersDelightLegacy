package com.wdcftgg.farmersdelightlegacy.common.block;

import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

public class BlockRicePanicles extends BlockCrops {

    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
    private static final AxisAlignedBB[] SHAPE_BY_AGE = new AxisAlignedBB[]{
            new AxisAlignedBB(3.0D / 16.0D, 0.0D, 3.0D / 16.0D, 13.0D / 16.0D, 8.0D / 16.0D, 13.0D / 16.0D),
            new AxisAlignedBB(3.0D / 16.0D, 0.0D, 3.0D / 16.0D, 13.0D / 16.0D, 10.0D / 16.0D, 13.0D / 16.0D),
            new AxisAlignedBB(2.0D / 16.0D, 0.0D, 2.0D / 16.0D, 14.0D / 16.0D, 12.0D / 16.0D, 14.0D / 16.0D),
            new AxisAlignedBB(1.0D / 16.0D, 0.0D, 1.0D / 16.0D, 15.0D / 16.0D, 1.0D, 15.0D / 16.0D)
    };

    public BlockRicePanicles() {
        this.setTickRandomly(true);
        this.setHardness(0.0F);
        this.setSoundType(net.minecraft.block.SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty(this.getAgeProperty(), 0));
    }

    @Override
    protected PropertyInteger getAgeProperty() {
        return AGE;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{AGE});
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return Math.max(0, Math.min(3, this.getAge(state)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.withAge(meta & 3);
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE_BY_AGE[Math.max(0, Math.min(this.getMaxAge(), this.getAge(state)))];
    }

    @Override
    protected Item getSeed() {
        return ModItems.ITEMS.get("rice");
    }

    @Override
    protected Item getCrop() {
        return ModItems.ITEMS.get("rice_panicle");
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state,
                             @Nullable TileEntity te, ItemStack stack) {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (worldIn.isRemote || player.capabilities.isCreativeMode || !this.isMaxAge(state)) {
            return;
        }

        Item droppedItem = this.getMatureDropItem(stack);
        if (droppedItem != null) {
            spawnAsEntity(worldIn, pos, new ItemStack(droppedItem));
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (!this.isMaxAge(state)) {
            return;
        }

        Item panicleItem = ModItems.ITEMS.get("rice_panicle");
        if (panicleItem != null) {
            drops.add(new ItemStack(panicleItem));
        }
    }

    @Override
    protected int getBonemealAgeIncrease(World worldIn) {
        return super.getBonemealAgeIncrease(worldIn) / 3;
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() == ModBlocks.RICE;
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        boolean goodLight = worldIn.getLightFromNeighbors(pos) >= 8 || worldIn.canSeeSky(pos);
        return goodLight && this.canSustainBush(worldIn.getBlockState(pos.down()));
    }

    private Item getMatureDropItem(ItemStack toolStack) {
        if (this.isKnifeTool(toolStack)) {
            return ModItems.ITEMS.get("rice");
        }
        return ModItems.ITEMS.get("rice_panicle");
    }

    private boolean isKnifeTool(ItemStack toolStack) {
        if (toolStack.isEmpty()) {
            return false;
        }

        if (toolStack.getItem() == Items.SHEARS) {
            return false;
        }

        int knifeOreId = OreDictionary.getOreID("toolKnife");
        if (knifeOreId >= 0) {
            for (int oreId : OreDictionary.getOreIDs(toolStack)) {
                if (oreId == knifeOreId) {
                    return true;
                }
            }
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(toolStack.getItem());
        return itemId != null && itemId.getPath().endsWith("_knife");
    }
}
