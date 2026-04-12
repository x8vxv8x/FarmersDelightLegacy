package com.wdcftgg.farmersdelightlegacy.common.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

public class PotionComfort extends Potion {

    public PotionComfort() {
        super(false, 0xF2E8C9);
        this.setPotionName("effect.farmersdelight.comfort");
    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn.isPotionActive(MobEffects.REGENERATION)) {
            return;
        }

        if (entityLivingBaseIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLivingBaseIn;
            if (player.getFoodStats().getSaturationLevel() > 0.0F) {
                return;
            }
        }

        if (entityLivingBaseIn.getHealth() < entityLivingBaseIn.getMaxHealth()) {
            entityLivingBaseIn.heal(1.0F);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 80 == 0;
    }

    @Override
    public boolean hasStatusIcon() {
        return false;
    }
}
