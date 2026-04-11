package com.wdcftgg.farmersdelightlegacy.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntitySign;

public class TileEntityCanvasSign extends TileEntitySign {

	private static final String HANGING_TEXT_BACK_TAG = "HangingTextOnBack";
	private boolean hangingTextOnBack;

	public boolean isHangingTextOnBack() {
		return this.hangingTextOnBack;
	}

	public void setHangingTextOnBack(boolean hangingTextOnBack) {
		if (this.hangingTextOnBack == hangingTextOnBack) {
			return;
		}
		this.hangingTextOnBack = hangingTextOnBack;
		this.markDirty();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean(HANGING_TEXT_BACK_TAG, this.hangingTextOnBack);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.hangingTextOnBack = compound.getBoolean(HANGING_TEXT_BACK_TAG);
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
		if (this.world != null) {
			IBlockState state = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, state, state, 3);
		}
	}
}

