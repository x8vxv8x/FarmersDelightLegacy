package com.wdcftgg.farmersdelightlegacy.common.item;

import com.wdcftgg.farmersdelightlegacy.common.entity.EntityRottenTomato;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModSounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemRottenTomato extends Item {

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack heldStack = playerIn.getHeldItem(handIn);
        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, ModSounds.ROTTEN_TOMATO_THROW,
                SoundCategory.NEUTRAL, 0.5F, 0.4F / (worldIn.rand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            EntityRottenTomato projectile = new EntityRottenTomato(worldIn, playerIn);
            projectile.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntity(projectile);
        }

        playerIn.addStat(StatList.getObjectUseStats(this));
        if (!playerIn.capabilities.isCreativeMode) {
            heldStack.shrink(1);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
    }
}

