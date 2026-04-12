package com.wdcftgg.farmersdelightlegacy.common.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PotionNourishment extends Potion {

    public PotionNourishment() {
        super(false, 0xF7D560);
        this.setPotionName("effect.farmersdelight.nourishment");
    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        if (!(entityLivingBaseIn instanceof EntityPlayer) || entityLivingBaseIn.world.isRemote) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entityLivingBaseIn;
        FoodStats foodStats = player.getFoodStats();
        boolean naturalRegen = player.world.getGameRules().getBoolean("naturalRegeneration");
        boolean playerHealingWithHunger = naturalRegen && player.shouldHeal() && foodStats.getFoodLevel() >= 18;
        if (!playerHealingWithHunger) {
            reduceExhaustion(foodStats);
        }
    }

    private static void reduceExhaustion(FoodStats foodStats) {
        try {
            float exhaustion = ObfuscationReflectionHelper.getPrivateValue(FoodStats.class, foodStats,
                    "foodExhaustionLevel", "field_75126_c");
            if (exhaustion > 0.0F) {
                // 与上游一致，只抵消现有消耗，不让 exhaustion 变成负值。
                ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, foodStats,
                        Math.max(0.0F, exhaustion - 4.0F), "foodExhaustionLevel", "field_75126_c");
            }
        } catch (RuntimeException ignored) {
            foodStats.addExhaustion(-4.0F);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean hasStatusIcon() {
        return false;
    }
}
