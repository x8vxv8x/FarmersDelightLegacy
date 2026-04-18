package com.wdcftgg.farmersdelightlegacy.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockRicePanicles;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Set;

public class ItemKnife extends ItemSword {

    private static final float WEB_SPEED = 15.0F;
    private static final float KNIFE_SPEED = 8.0F;
    private static final float KNOCKBACK_REDUCTION = 0.1F;
    private static final float HAM_DROP_CHANCE = 0.5F;
    private static final float LOOTING_BONUS = 0.1F;
    private static final float SHORT_GRASS_STRAW_CHANCE = 0.2F;
    private static final float TALL_GRASS_STRAW_CHANCE = 0.2F;
    private static final Set<Enchantment> ALLOWED_ENCHANTMENTS = Sets.newHashSet(
            Enchantments.SHARPNESS,
            Enchantments.SMITE,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.KNOCKBACK,
            Enchantments.FIRE_ASPECT,
            Enchantments.LOOTING
    );
    private static final Set<Enchantment> DENIED_ENCHANTMENTS = Sets.newHashSet(Enchantments.FORTUNE);
    private final double attackDamage;
    private final double attackSpeed;

    public ItemKnife(Item.ToolMaterial material, double attackDamage) {
        super(material);
        this.attackDamage = attackDamage;
        this.attackSpeed = -2.0D;
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn) {
        return blockIn.getBlock() == Blocks.WEB;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        if (block == Blocks.WEB) {
            return WEB_SPEED;
        }

        if (block instanceof BlockCrops
                || block instanceof BlockBush
                || block == Blocks.MELON_BLOCK
                || block == Blocks.PUMPKIN
                || block == Blocks.LIT_PUMPKIN
                || material == Material.PLANTS
                || material == Material.VINE
                || material == Material.LEAVES
                || material == Material.GOURD
                || material == Material.CACTUS
                || material == Material.CLOTH
                || material == Material.CARPET) {
            return KNIFE_SPEED;
        }

        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (state.getBlockHardness(worldIn, pos) != 0.0F) {
            stack.damageItem(1, entityLiving);
        }
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (ALLOWED_ENCHANTMENTS.contains(enchantment)) {
            return true;
        }
        if (DENIED_ENCHANTMENTS.contains(enchantment)) {
            return false;
        }
        return enchantment.type != null && enchantment.type.canEnchantItem(this);
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> attributes = HashMultimap.create(super.getItemAttributeModifiers(equipmentSlot));
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            attributes.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
            attributes.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
            attributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage, 0));
            attributes.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed, 0));
        }
        return attributes;
    }

    public static boolean isKnife(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.getItem() instanceof ItemKnife) {
            return true;
        }

        int knifeOreId = OreDictionary.getOreID("toolKnife");
        if (knifeOreId >= 0) {
            for (int oreId : OreDictionary.getOreIDs(stack)) {
                if (oreId == knifeOreId) {
                    return true;
                }
            }
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return itemId != null && itemId.getPath().endsWith("_knife");
    }

    @Mod.EventBusSubscriber(modid = FarmersDelightLegacy.MOD_ID)
    public static final class KnifeEvents {

        private KnifeEvents() {
        }

        @SubscribeEvent
        public static void onKnifeKnockback(LivingKnockBackEvent event) {
            Entity attacker = event.getEntityLiving().getAttackingEntity();
            if (!(attacker instanceof EntityLivingBase)) {
                attacker = event.getEntityLiving().getRevengeTarget();
            }
            if (!(attacker instanceof EntityLivingBase)) {
                return;
            }

            ItemStack toolStack = ((EntityLivingBase) attacker).getHeldItemMainhand();
            if (!ItemKnife.isKnife(toolStack)) {
                return;
            }

            event.setStrength(Math.max(0.0F, event.getOriginalStrength() - KNOCKBACK_REDUCTION));
        }

        @SubscribeEvent
        public static void onCakeInteraction(PlayerInteractEvent.RightClickBlock event) {
            EntityPlayer player = event.getEntityPlayer();
            ItemStack toolStack = player.getHeldItem(event.getHand());
            if (!ItemKnife.isKnife(toolStack)) {
                return;
            }

            World world = event.getWorld();
            BlockPos pos = event.getPos();
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() != Blocks.CAKE) {
                return;
            }

            if (!world.isRemote) {
                int bites = state.getValue(BlockCake.BITES);
                if (bites < 6) {
                    world.setBlockState(pos, state.withProperty(BlockCake.BITES, bites + 1), 3);
                } else {
                    world.setBlockToAir(pos);
                }

                Item sliceItem = ModItems.get("cake_slice");
                if (sliceItem != null) {
                    double offset = bites * 0.1D;
                    EntityItem drop = new EntityItem(world, pos.getX() + 0.5D + offset, pos.getY() + 0.2D, pos.getZ() + 0.5D,
                            new ItemStack(sliceItem));
                    drop.motionX = -0.05D;
                    drop.motionY = 0.0D;
                    drop.motionZ = 0.0D;
                    world.spawnEntity(drop);
                }

                SoundType soundType = Blocks.WOOL.getSoundType(state, world, pos, player);
                world.playSound(null, pos, soundType.getBreakSound(), SoundCategory.PLAYERS, 0.8F, 0.8F);
                player.getCooldownTracker().setCooldown(toolStack.getItem(), 4);
            }

            player.swingArm(event.getHand());
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
        }

        @SubscribeEvent
        public static void onLivingDrops(LivingDropsEvent event) {
            Entity source = event.getSource().getTrueSource();
            if (!(source instanceof EntityLivingBase)) {
                return;
            }

            EntityLivingBase attacker = (EntityLivingBase) source;
            ItemStack toolStack = attacker.getHeldItemMainhand();
            if (!ItemKnife.isKnife(toolStack)) {
                return;
            }

            EntityLivingBase target = event.getEntityLiving();
            if (isPigLike(target)) {
                int lootingLevel = EnchantmentHelper.getLootingModifier(attacker);
                float chance = HAM_DROP_CHANCE + (lootingLevel * LOOTING_BONUS);
                if (target.world.rand.nextFloat() < chance) {
                    Item hamItem = target.isBurning() ? ModItems.get("smoked_ham") : ModItems.get("ham");
                    addExtraDrop(event, hamItem);
                }
            }

            if (isHoglinLike(target) && target.isBurning()) {
                addExtraDrop(event, ModItems.get("smoked_ham"));
            }

            if (target instanceof EntityChicken) {
                addExtraDrop(event, Items.FEATHER);
            }

            if (isLeatherSource(target)) {
                addExtraDrop(event, Items.LEATHER);
            }

            if (target instanceof EntityRabbit) {
                addExtraDrop(event, Items.RABBIT_HIDE);
            }

            if (target instanceof EntityShulker) {
                addExtraDrop(event, Items.SHULKER_SHELL);
            }

            if (target instanceof EntitySpider || target instanceof EntityCaveSpider) {
                addExtraDrop(event, Items.STRING);
            }
        }

        @SubscribeEvent
        public static void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
            EntityPlayer player = event.getHarvester();
            if (player == null || event.isSilkTouching()) {
                return;
            }

            ItemStack toolStack = player.getHeldItemMainhand();
            if (!ItemKnife.isKnife(toolStack)) {
                return;
            }

            IBlockState state = event.getState();
            Block block = state.getBlock();
            World world = (World) event.getWorld();

            if (block == Blocks.TALLGRASS) {
                BlockTallGrass.EnumType type = state.getValue(BlockTallGrass.TYPE);
                if (type == BlockTallGrass.EnumType.GRASS && world.rand.nextFloat() < SHORT_GRASS_STRAW_CHANCE) {
                    addDrop(event, "straw");
                }
                return;
            }

            if (block == Blocks.DOUBLE_PLANT) {
                BlockDoublePlant.EnumPlantType variant = state.getValue(BlockDoublePlant.VARIANT);
                if (variant == BlockDoublePlant.EnumPlantType.GRASS && world.rand.nextFloat() < TALL_GRASS_STRAW_CHANCE) {
                    addDrop(event, "straw");
                }
                return;
            }

            if (block == Blocks.WHEAT && state.getValue(BlockCrops.AGE) >= 7) {
                addDrop(event, "straw");
                return;
            }

            if (block == ModBlocks.RICE_PANICLES && state.getValue(BlockRicePanicles.AGE) >= 3) {
                addDrop(event, "straw");
            }
        }

        private static void addDrop(BlockEvent.HarvestDropsEvent event, String itemPath) {
            Item dropItem = ModItems.get(itemPath);
            if (dropItem != null) {
                event.getDrops().add(new ItemStack(dropItem));
            }
        }

        private static void addExtraDrop(LivingDropsEvent event, Item item) {
            if (item == null) {
                return;
            }

            for (EntityItem entityItem : event.getDrops()) {
                ItemStack dropStack = entityItem.getItem();
                if (!dropStack.isEmpty() && dropStack.getItem() == item) {
                    dropStack.grow(1);
                    return;
                }
            }

            EntityLivingBase target = event.getEntityLiving();
            event.getDrops().add(new EntityItem(target.world, target.posX, target.posY, target.posZ, new ItemStack(item)));
        }

        private static boolean isPigLike(EntityLivingBase target) {
            return target instanceof EntityPig;
        }

        private static boolean isHoglinLike(EntityLivingBase target) {
            ResourceLocation entityId = EntityList.getKey(target);
            return entityId != null && "hoglin".equals(entityId.getPath());
        }

        private static boolean isLeatherSource(EntityLivingBase target) {
            if (target instanceof EntityCow || target instanceof EntityMooshroom) {
                return true;
            }

            if (target instanceof AbstractHorse) {
                return true;
            }

            ResourceLocation entityId = EntityList.getKey(target);
            return entityId != null && "trader_llama".equals(entityId.getPath());
        }
    }
}
