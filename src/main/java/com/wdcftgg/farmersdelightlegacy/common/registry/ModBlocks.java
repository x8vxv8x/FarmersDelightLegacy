package com.wdcftgg.farmersdelightlegacy.common.registry;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.ModCreativeTab;
import com.wdcftgg.farmersdelightlegacy.common.block.*;
import com.wdcftgg.farmersdelightlegacy.common.item.ItemCanvasHangingSign;
import com.wdcftgg.farmersdelightlegacy.common.item.ItemCanvasSign;
import com.wdcftgg.farmersdelightlegacy.common.item.ItemSkillet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class ModBlocks {

    public static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    public static final List<Item> BLOCK_ITEMS = new ArrayList<>();

    public static final Block TOMATOES = registerBlockOnly("tomatoes", new BlockTomatoVine());
    public static final Block RICE = registerBlockOnly("rice", new BlockRice());
    public static final Block CABBAGES = registerBlockOnly("cabbages", new BlockCabbage());
    public static final Block ONIONS = registerBlockOnly("onions", new BlockOnion());
    public static final Block BUDDING_TOMATOES = registerBlockOnly("budding_tomatoes", new BlockBuddingTomato());
    public static final Block RICE_PANICLES = registerBlockOnly("rice_panicles", new BlockRicePanicles());

    public static final Block COOKING_POT = register("cooking_pot", new BlockCookingPot());
    public static final Block CUTTING_BOARD = register("cutting_board", new BlockCuttingBoard());
    public static final Block BASKET = register("basket", new BlockBasket());
    public static final Block CARROT_CRATE = register("carrot_crate", basicBlock(Material.WOOD, SoundType.WOOD, 2.0F));
    public static final Block POTATO_CRATE = register("potato_crate", basicBlock(Material.WOOD, SoundType.WOOD, 2.0F));
    public static final Block BEETROOT_CRATE = register("beetroot_crate", basicBlock(Material.WOOD, SoundType.WOOD, 2.0F));
    public static final Block CABBAGE_CRATE = register("cabbage_crate", basicBlock(Material.WOOD, SoundType.WOOD, 2.0F));
    public static final Block TOMATO_CRATE = register("tomato_crate", basicBlock(Material.WOOD, SoundType.WOOD, 2.0F));
    public static final Block ONION_CRATE = register("onion_crate", basicBlock(Material.WOOD, SoundType.WOOD, 2.0F));
    public static final Block RICE_BALE = register("rice_bale", basicBlock(Material.CLOTH, SoundType.PLANT, 0.5F));
    public static final Block RICE_BAG = register("rice_bag", basicBlock(Material.CLOTH, SoundType.CLOTH, 0.8F));
    public static final Block STRAW_BALE = register("straw_bale", basicBlock(Material.CLOTH, SoundType.PLANT, 0.5F));
    public static final Block SAFETY_NET = register("safety_net", new BlockSafetyNet());
    public static final Block ACACIA_CABINET = register("acacia_cabinet", new BlockCabinet());
    public static final Block BAMBOO_CABINET = register("bamboo_cabinet", new BlockCabinet());
    public static final Block BIRCH_CABINET = register("birch_cabinet", new BlockCabinet());
    public static final Block CHERRY_CABINET = register("cherry_cabinet", new BlockCabinet());
    public static final Block CRIMSON_CABINET = register("crimson_cabinet", new BlockCabinet());
    public static final Block DARK_OAK_CABINET = register("dark_oak_cabinet", new BlockCabinet());
    public static final Block JUNGLE_CABINET = register("jungle_cabinet", new BlockCabinet());
    public static final Block MANGROVE_CABINET = register("mangrove_cabinet", new BlockCabinet());
    public static final Block OAK_CABINET = register("oak_cabinet", new BlockCabinet());
    public static final Block SPRUCE_CABINET = register("spruce_cabinet", new BlockCabinet());
    public static final Block WARPED_CABINET = register("warped_cabinet", new BlockCabinet());
    public static final Block STOVE = register("stove", new BlockStove());
    public static final Block SKILLET = register("skillet", new BlockSkillet(), ItemSkillet::new);
    public static final Block ROAST_CHICKEN_BLOCK = register("roast_chicken_block", new BlockRoastChickenFeast(4, "roast_chicken", "minecraft:bowl", true));
    public static final Block HONEY_GLAZED_HAM_BLOCK = register("honey_glazed_ham_block", new BlockHoneyGlazedHamFeast(4, "honey_glazed_ham", "minecraft:bowl", true));
    public static final Block SHEPHERDS_PIE_BLOCK = register("shepherds_pie_block", new BlockShepherdsPieFeast(4, "shepherds_pie", "minecraft:bowl", true));
    public static final Block STUFFED_PUMPKIN_BLOCK = register("stuffed_pumpkin_block", new BlockFeast(4, "stuffed_pumpkin", "minecraft:bowl", false));
    public static final Block RICE_ROLL_MEDLEY_BLOCK = register("rice_roll_medley_block", new BlockRiceRollMedley());
    public static final Block ORGANIC_COMPOST = register("organic_compost", new BlockOrganicCompost());
    public static final Block RICH_SOIL = register("rich_soil", new BlockRichSoil());
    public static final Block RICH_SOIL_FARMLAND = register("rich_soil_farmland", new BlockRichSoilFarmland());
    public static final Block APPLE_PIE = register("apple_pie", new BlockPie("apple_pie_slice"));
    public static final Block SWEET_BERRY_CHEESECAKE = register("sweet_berry_cheesecake", new BlockPie("sweet_berry_cheesecake_slice"));
    public static final Block CHOCOLATE_PIE = register("chocolate_pie", new BlockPie("chocolate_pie_slice"));
    public static final Block SANDY_SHRUB = register("sandy_shrub", new BlockSandyShrub());
    public static final Block WILD_CABBAGES = register("wild_cabbages",
            new BlockWildCrop("farmersdelight:cabbage_seeds", 2, "farmersdelight:cabbage", 0.2F, null, 0, 0));
    public static final Block WILD_ONIONS = register("wild_onions",
            new BlockWildCrop("farmersdelight:onion", 2, null, 0.0F, "minecraft:allium", 1, 3));
    public static final Block WILD_TOMATOES = register("wild_tomatoes",
            new BlockWildCrop("farmersdelight:tomato_seeds", 2, "farmersdelight:tomato", 0.2F, null, 0, 0));
    public static final Block WILD_CARROTS = register("wild_carrots",
            new BlockWildCrop("minecraft:carrot", 2, null, 0.0F, null, 0, 0));
    public static final Block WILD_POTATOES = register("wild_potatoes",
            new BlockWildCrop("minecraft:potato", 2, null, 0.0F, null, 0, 0));
    public static final Block WILD_BEETROOTS = register("wild_beetroots",
            new BlockWildCrop("minecraft:beetroot_seeds", 2, "minecraft:beetroot", 0.2F, null, 0, 0));
    public static final Block WILD_RICE = register("wild_rice", new BlockWildRice());
    public static final Block BROWN_MUSHROOM_COLONY = register("brown_mushroom_colony", new BlockMushroomColony("minecraft:brown_mushroom"));
    public static final Block RED_MUSHROOM_COLONY = register("red_mushroom_colony", new BlockMushroomColony("minecraft:red_mushroom"));
    public static final Block ROPE = register("rope", new BlockRope());
    public static final Block CANVAS_RUG = register("canvas_rug", new BlockCanvasRug());
    public static final Block TATAMI = register("tatami", new BlockTatami());
    public static final Block FULL_TATAMI_MAT = register("full_tatami_mat", new BlockTatamiMat());
    public static final Block HALF_TATAMI_MAT = register("half_tatami_mat", new BlockTatamiHalfMat());

    static {
        registerCanvasSignFamily();
    }

    private ModBlocks() {
    }

    public static void registerAll(RegistryEvent.Register<Block> event) {
        for (Block block : BLOCKS.values()) {
            event.getRegistry().register(block);
        }
    }

    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        for (Item itemBlock : BLOCK_ITEMS) {
            event.getRegistry().register(itemBlock);
        }
    }

    private static Block register(String path, Block block) {
        return register(path, block, ItemBlock::new);
    }

    private static Block register(String path, Block block, Function<Block, ItemBlock> itemFactory) {
        block.setRegistryName(new ResourceLocation(FarmersDelightLegacy.MOD_ID, path));
        block.setTranslationKey(FarmersDelightLegacy.MOD_ID + "." + path);
        block.setCreativeTab(ModCreativeTab.TAB);
        BLOCKS.put(path, block);

        ItemBlock itemBlock = itemFactory.apply(block);
        itemBlock.setRegistryName(block.getRegistryName());
        itemBlock.setTranslationKey(block.getTranslationKey());
        itemBlock.setCreativeTab(ModCreativeTab.TAB);
        BLOCK_ITEMS.add(itemBlock);
        return block;
    }

    private static Block registerBlockOnly(String path, Block block) {
        block.setRegistryName(new ResourceLocation(FarmersDelightLegacy.MOD_ID, path));
        block.setTranslationKey(FarmersDelightLegacy.MOD_ID + "." + path);
        BLOCKS.put(path, block);
        return block;
    }

    private static Block basicBlock(Material material, SoundType soundType, float hardness) {
        return new BasicBlock(material, soundType, hardness);
    }

    private static void registerCanvasSignFamily() {
        registerCanvasSignPair("", "canvas_sign", "canvas_wall_sign");
        registerHangingCanvasSignPair("", "hanging_canvas_sign", "wall_hanging_canvas_sign");

        registerCanvasSignPair("white", "white_canvas_sign", "white_canvas_wall_sign");
        registerHangingCanvasSignPair("white", "white_hanging_canvas_sign", "white_wall_hanging_canvas_sign");

        registerCanvasSignPair("orange", "orange_canvas_sign", "orange_canvas_wall_sign");
        registerHangingCanvasSignPair("orange", "orange_hanging_canvas_sign", "orange_wall_hanging_canvas_sign");

        registerCanvasSignPair("magenta", "magenta_canvas_sign", "magenta_canvas_wall_sign");
        registerHangingCanvasSignPair("magenta", "magenta_hanging_canvas_sign", "magenta_wall_hanging_canvas_sign");

        registerCanvasSignPair("light_blue", "light_blue_canvas_sign", "light_blue_canvas_wall_sign");
        registerHangingCanvasSignPair("light_blue", "light_blue_hanging_canvas_sign", "light_blue_wall_hanging_canvas_sign");

        registerCanvasSignPair("yellow", "yellow_canvas_sign", "yellow_canvas_wall_sign");
        registerHangingCanvasSignPair("yellow", "yellow_hanging_canvas_sign", "yellow_wall_hanging_canvas_sign");

        registerCanvasSignPair("lime", "lime_canvas_sign", "lime_canvas_wall_sign");
        registerHangingCanvasSignPair("lime", "lime_hanging_canvas_sign", "lime_wall_hanging_canvas_sign");

        registerCanvasSignPair("pink", "pink_canvas_sign", "pink_canvas_wall_sign");
        registerHangingCanvasSignPair("pink", "pink_hanging_canvas_sign", "pink_wall_hanging_canvas_sign");

        registerCanvasSignPair("gray", "gray_canvas_sign", "gray_canvas_wall_sign");
        registerHangingCanvasSignPair("gray", "gray_hanging_canvas_sign", "gray_wall_hanging_canvas_sign");

        registerCanvasSignPair("light_gray", "light_gray_canvas_sign", "light_gray_canvas_wall_sign");
        registerHangingCanvasSignPair("light_gray", "light_gray_hanging_canvas_sign", "light_gray_wall_hanging_canvas_sign");

        registerCanvasSignPair("cyan", "cyan_canvas_sign", "cyan_canvas_wall_sign");
        registerHangingCanvasSignPair("cyan", "cyan_hanging_canvas_sign", "cyan_wall_hanging_canvas_sign");

        registerCanvasSignPair("purple", "purple_canvas_sign", "purple_canvas_wall_sign");
        registerHangingCanvasSignPair("purple", "purple_hanging_canvas_sign", "purple_wall_hanging_canvas_sign");

        registerCanvasSignPair("blue", "blue_canvas_sign", "blue_canvas_wall_sign");
        registerHangingCanvasSignPair("blue", "blue_hanging_canvas_sign", "blue_wall_hanging_canvas_sign");

        registerCanvasSignPair("brown", "brown_canvas_sign", "brown_canvas_wall_sign");
        registerHangingCanvasSignPair("brown", "brown_hanging_canvas_sign", "brown_wall_hanging_canvas_sign");

        registerCanvasSignPair("green", "green_canvas_sign", "green_canvas_wall_sign");
        registerHangingCanvasSignPair("green", "green_hanging_canvas_sign", "green_wall_hanging_canvas_sign");

        registerCanvasSignPair("red", "red_canvas_sign", "red_canvas_wall_sign");
        registerHangingCanvasSignPair("red", "red_hanging_canvas_sign", "red_wall_hanging_canvas_sign");

        registerCanvasSignPair("black", "black_canvas_sign", "black_canvas_wall_sign");
        registerHangingCanvasSignPair("black", "black_hanging_canvas_sign", "black_wall_hanging_canvas_sign");
    }

    private static void registerCanvasSignPair(String colorName, String standingPath, String wallPath) {
        ResourceLocation texture = signTexture(colorName);
        Block wallBlock = registerBlockOnly(wallPath, new BlockCanvasWallSign(texture, standingPath));
        register(standingPath, new BlockCanvasStandingSign(texture), block -> new ItemCanvasSign(block, wallBlock));
    }

    private static void registerHangingCanvasSignPair(String colorName, String hangingPath, String wallPath) {
        ResourceLocation texture = hangingSignTexture(colorName);
        Block wallBlock = registerBlockOnly(wallPath, new BlockCanvasWallHangingSign(texture, hangingPath));
        register(hangingPath, new BlockCanvasHangingSign(texture, hangingPath), block -> new ItemCanvasHangingSign(block, wallBlock));
    }

    private static ResourceLocation signTexture(String colorName) {
        String path = colorName.isEmpty() ? "textures/entity/signs/canvas.png" : "textures/entity/signs/canvas_" + colorName + ".png";
        return new ResourceLocation(FarmersDelightLegacy.MOD_ID, path);
    }

    private static ResourceLocation hangingSignTexture(String colorName) {
        String path = colorName.isEmpty() ? "textures/entity/signs/hanging/canvas.png" : "textures/entity/signs/hanging/canvas_" + colorName + ".png";
        return new ResourceLocation(FarmersDelightLegacy.MOD_ID, path);
    }

    private static final class BasicBlock extends Block {
        private BasicBlock(Material material, SoundType soundType, float hardness) {
            super(material);
            this.setHardness(hardness);
            this.setResistance(hardness + 2.0F);
            this.setSoundType(soundType);
        }
    }

    private static final class ModCropBlock extends BlockCrops {
        private final ResourceLocation seedName;
        private final ResourceLocation cropName;
        private final int maxAge;

        private ModCropBlock(String seedPath, String cropPath) {
            this(seedPath, cropPath, 7);
        }

        private ModCropBlock(String seedPath, String cropPath, int maxAge) {
            this.seedName = new ResourceLocation(FarmersDelightLegacy.MOD_ID, seedPath);
            this.cropName = new ResourceLocation(FarmersDelightLegacy.MOD_ID, cropPath);
            this.maxAge = maxAge;
            this.setTickRandomly(true);
            this.setHardness(0.0F);
            this.setSoundType(SoundType.PLANT);
        }

        @Override
        public int getMaxAge() {
            return this.maxAge;
        }

        @Override
        protected Item getSeed() {
            return ForgeRegistries.ITEMS.getValue(seedName);
        }

        @Override
        protected Item getCrop() {
            return ForgeRegistries.ITEMS.getValue(cropName);
        }

        @Override
        protected boolean canSustainBush(net.minecraft.block.state.IBlockState state) {
            return state.getBlock() == Blocks.FARMLAND || state.getBlock() == ModBlocks.RICH_SOIL_FARMLAND || state.getBlock() == this;
        }
    }
}

