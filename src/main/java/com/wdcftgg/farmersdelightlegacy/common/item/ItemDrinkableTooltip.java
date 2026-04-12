package com.wdcftgg.farmersdelightlegacy.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemDrinkableTooltip extends ItemFoodTooltip {

    private final DrinkEffect drinkEffect;

    public ItemDrinkableTooltip(int amount, float saturation, boolean alwaysEdible, @Nullable ResourceLocation effectId,
                                int effectDuration, int effectAmplifier, float effectChance, DrinkEffect drinkEffect,
                                String... extraTooltipKeys) {
        super(amount, saturation, false, effectId, effectDuration, effectAmplifier, effectChance, extraTooltipKeys);
        this.drinkEffect = drinkEffect;
        if (alwaysEdible) {
            this.setAlwaysEdible();
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
        if (!worldIn.isRemote) {
            this.drinkEffect.apply(worldIn, player);
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, net.minecraft.entity.EntityLivingBase entityLiving) {
        ItemStack result = super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!(entityLiving instanceof EntityPlayer)) {
            return result;
        }

        EntityPlayer player = (EntityPlayer) entityLiving;
        if (player.capabilities.isCreativeMode) {
            return result;
        }

        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
        if (result.isEmpty()) {
            return bottle;
        }

        if (!player.inventory.addItemStackToInventory(bottle)) {
            player.dropItem(bottle, false);
        }
        return result;
    }

    public enum DrinkEffect {
        NONE {
            @Override
            void apply(World worldIn, EntityPlayer player) {
            }
        },
        CLEAR_ONE {
            @Override
            void apply(World worldIn, EntityPlayer player) {
                clearRandomEffect(player, false, worldIn);
            }
        },
        CLEAR_ONE_HARMFUL {
            @Override
            void apply(World worldIn, EntityPlayer player) {
                clearRandomEffect(player, true, worldIn);
            }
        },
        HEAL_MINOR {
            @Override
            void apply(World worldIn, EntityPlayer player) {
                player.heal(2.0F);
            }
        };

        abstract void apply(World worldIn, EntityPlayer player);

        static void clearRandomEffect(EntityPlayer player, boolean harmfulOnly, World worldIn) {
            List<Potion> compatibleEffects = new ArrayList<>();
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (harmfulOnly && !effect.getPotion().isBadEffect()) {
                    continue;
                }
                boolean curableByMilk = effect.getCurativeItems().stream().anyMatch(curative -> curative.getItem() == Items.MILK_BUCKET);
                if (curableByMilk) {
                    compatibleEffects.add(effect.getPotion());
                }
            }

            if (!compatibleEffects.isEmpty()) {
                Potion selectedPotion = compatibleEffects.get(worldIn.rand.nextInt(compatibleEffects.size()));
                player.removePotionEffect(selectedPotion);
            }
        }
    }
}
