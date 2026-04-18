package com.wdcftgg.farmersdelightlegacy.common.entity;

import com.wdcftgg.farmersdelightlegacy.common.advancement.ModAdvancements;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class EntityRottenTomato extends EntityThrowable {
    private static final ResourceLocation RAIDS_BACKPORT_PILLAGER_ID = new ResourceLocation("raids", "pillager");

    public EntityRottenTomato(World worldIn) {
        super(worldIn);
    }

    public EntityRottenTomato(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityRottenTomato(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected float getGravityVelocity() {
        return 0.03F;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            Item item = ModItems.ITEMS.get("rotten_tomato");
            int itemId = Item.getIdFromItem(item);
            for (int i = 0; i < 12; ++i) {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK,
                        this.posX,
                        this.posY,
                        this.posZ,
                        (this.rand.nextDouble() - 0.5D) * 0.2D,
                        this.rand.nextDouble() * 0.2D,
                        (this.rand.nextDouble() - 0.5D) * 0.2D,
                        itemId);
            }
            return;
        }
        super.handleStatusUpdate(id);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
            Entity target = result.entityHit;
            Entity thrower = this.getThrower();
            if (target == thrower) {
                return;
            }
            target.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), 0.0F);
            if (!this.world.isRemote && thrower instanceof EntityPlayerMP && shouldAwardAdvancement(target)) {
                ModAdvancements.HIT_RAIDER_WITH_ROTTEN_TOMATO.trigger((EntityPlayerMP) thrower);
            }
        }

        if (!this.world.isRemote) {
            this.world.playSound(null, this.posX, this.posY, this.posZ, ModSounds.ROTTEN_TOMATO_HIT,
                    SoundCategory.NEUTRAL, 1.0F,
                    0.9F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.world.setEntityState(this, (byte) 3);
            this.setDead();
        }
    }

    private boolean shouldAwardAdvancement(Entity target) {
        if (Loader.isModLoaded("raids")) {
            return RAIDS_BACKPORT_PILLAGER_ID.equals(EntityList.getKey(target));
        }

        return target instanceof IMob;
    }
}

