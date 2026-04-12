package com.wdcftgg.farmersdelightlegacy.common.event;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.item.enchantment.EnchantmentBackstabbing;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = FarmersDelightLegacy.MOD_ID)
public final class BackstabbingEventHandler {

    private BackstabbingEventHandler() {
    }

    @SubscribeEvent
    public static void onKnifeBackstab(LivingHurtEvent event) {
        if (ModEnchantments.BACKSTABBING == null) {
            return;
        }

        Entity attacker = event.getSource().getTrueSource();
        if (!(attacker instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) attacker;
        ItemStack weapon = player.getHeldItemMainhand();
        int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.BACKSTABBING, weapon);
        if (level <= 0) {
            return;
        }

        if (!EnchantmentBackstabbing.isLookingBehindTarget(event.getEntityLiving(), event.getSource().getDamageLocation())) {
            return;
        }

        if (!event.getEntityLiving().world.isRemote) {
            event.setAmount(EnchantmentBackstabbing.getBackstabbingDamagePerLevel(event.getAmount(), level));
            event.getEntityLiving().world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }
}
