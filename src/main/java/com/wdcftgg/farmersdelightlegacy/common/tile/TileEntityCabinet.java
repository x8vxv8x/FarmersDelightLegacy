package com.wdcftgg.farmersdelightlegacy.common.tile;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockCabinet;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class TileEntityCabinet extends TileEntity implements IInventory {

    private static final int SLOT_COUNT = 27;
    private static final int RECHECK_DELAY_TICKS = 5;

    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private String customName;
    private int openerCount;

    @Override
    public int getSizeInventory() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.itemStacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.itemStacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack current = this.itemStacks.get(index);
        if (current.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (current.getCount() <= count) {
            this.itemStacks.set(index, ItemStack.EMPTY);
            this.markDirty();
            return current;
        }

        ItemStack split = current.splitStack(count);
        if (current.getCount() <= 0) {
            this.itemStacks.set(index, ItemStack.EMPTY);
        }
        this.markDirty();
        return split;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack current = this.itemStacks.get(index);
        this.itemStacks.set(index, ItemStack.EMPTY);
        this.markDirty();
        return current;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.itemStacks.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world == null || !(this.world.getTileEntity(this.pos) instanceof TileEntityCabinet)) {
            return false;
        }
        return player.getDistanceSq((double) this.pos.getX() + 0.5D,
                (double) this.pos.getY() + 0.5D,
                (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (player.isSpectator() || this.world == null || this.world.isRemote) {
            return;
        }

        if (this.openerCount < 0) {
            this.openerCount = 0;
        }

        this.openerCount++;
        if (this.openerCount == 1) {
            updateOpenState(true);
            playCabinetSound(world.rand.nextBoolean() ? ModSounds.CABINET_OPEN1 : ModSounds.CABINET_OPEN2);
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (player.isSpectator() || this.world == null || this.world.isRemote) {
            return;
        }

        if (this.openerCount > 0) {
            this.openerCount--;
        }

        if (this.openerCount <= 0) {
            this.openerCount = 0;
            updateOpenState(false);
            playCabinetSound(ModSounds.CABINET_CLOSE);
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.itemStacks = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : FarmersDelightLegacy.MOD_ID + ".container.cabinet";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.customName) : new TextComponentTranslation(this.getName());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = this.itemStacks.get(i);
            if (!stack.isEmpty()) {
                NBTTagCompound stackTag = new NBTTagCompound();
                stackTag.setByte("Slot", (byte) i);
                stack.writeToNBT(stackTag);
                itemList.appendTag(stackTag);
            }
        }
        compound.setTag("Items", itemList);

        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.itemStacks = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        NBTTagList itemList = compound.getTagList("Items", 10);
        for (int i = 0; i < itemList.tagCount(); i++) {
            NBTTagCompound stackTag = itemList.getCompoundTagAt(i);
            int slot = stackTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < SLOT_COUNT) {
                this.itemStacks.set(slot, new ItemStack(stackTag));
            }
        }

        this.customName = compound.hasKey("CustomName", 8) ? compound.getString("CustomName") : null;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world != null && this.pos != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.updateComparatorOutputLevel(this.pos, this.getBlockType());
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
        }
    }

    private void updateOpenState(boolean open) {
        if (this.world == null) {
            return;
        }

        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() instanceof BlockCabinet && state.getValue(BlockCabinet.OPEN) != open) {
            this.world.setBlockState(this.pos, state.withProperty(BlockCabinet.OPEN, open), 3);
        }
    }

    private void playCabinetSound(net.minecraft.util.SoundEvent soundEvent) {
        if (this.world == null || soundEvent == null) {
            return;
        }

        IBlockState state = this.world.getBlockState(this.pos);
        EnumFacing facing = state.getBlock() instanceof BlockCabinet ? state.getValue(BlockCabinet.FACING) : EnumFacing.NORTH;
        Vec3i facingVector = facing.getDirectionVec();
        double x = this.pos.getX() + 0.5D + facingVector.getX() / 2.0D;
        double y = this.pos.getY() + 0.5D + facingVector.getY() / 2.0D;
        double z = this.pos.getZ() + 0.5D + facingVector.getZ() / 2.0D;
        this.world.playSound(null, x, y, z, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
    }
}

