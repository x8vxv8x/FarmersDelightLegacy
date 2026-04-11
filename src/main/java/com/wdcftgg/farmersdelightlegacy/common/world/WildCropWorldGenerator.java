package com.wdcftgg.farmersdelightlegacy.common.world;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockMushroomColony;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockWildRice;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WildCropWorldGenerator implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != 0) {
            return;
        }

        BlockPos biomePos = new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8);
        Biome biome = world.getBiome(biomePos);

        generateWildCabbages(world, random, chunkX, chunkZ, biome);
        generateWildOnions(world, random, chunkX, chunkZ, biome);
        generateWildTomatoes(world, random, chunkX, chunkZ, biome);
        generateWildCarrots(world, random, chunkX, chunkZ, biome);
        generateWildPotatoes(world, random, chunkX, chunkZ, biome);
        generateWildBeetroots(world, random, chunkX, chunkZ, biome);
        generateWildRice(world, random, chunkX, chunkZ, biome);
        generateMushroomColonies(world, random, chunkX, chunkZ, biome, true);
        generateMushroomColonies(world, random, chunkX, chunkZ, biome, false);
    }

    private void generateWildCabbages(World world, Random random, int chunkX, int chunkZ, Biome biome) {
        if (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)) {
            return;
        }
        generatePatch(world, random, chunkX, chunkZ, 30, 64, 6, 3,
                this::placeSandyShrubFloor,
                this::placeWildCabbages,
                this::placeSandyShrub);
    }

    private void generateWildOnions(World world, Random random, int chunkX, int chunkZ, Biome biome) {
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.MUSHROOM)) {
            return;
        }
        float temperature = biome.getDefaultTemperature();
        if (temperature < 0.4F || temperature > 0.9F) {
            return;
        }
        generatePatch(world, random, chunkX, chunkZ, 120, 64, 6, 3,
                null,
                this::placeWildOnions,
                this::placeAllium);
    }

    private void generateWildTomatoes(World world, Random random, int chunkX, int chunkZ, Biome biome) {
        if (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.HOT) || BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) {
            return;
        }
        generatePatch(world, random, chunkX, chunkZ, 100, 64, 6, 3,
                null,
                this::placeWildTomatoes,
                this::placeDeadBush);
    }

    private void generateWildCarrots(World world, Random random, int chunkX, int chunkZ, Biome biome) {
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.MUSHROOM)) {
            return;
        }
        float temperature = biome.getDefaultTemperature();
        if (temperature < 0.4F || temperature > 0.9F) {
            return;
        }
        generatePatch(world, random, chunkX, chunkZ, 120, 64, 6, 3,
                this::placeCoarseDirtFloor,
                this::placeWildCarrots,
                this::placeGrass);
    }

    private void generateWildPotatoes(World world, Random random, int chunkX, int chunkZ, Biome biome) {
        float temperature = biome.getDefaultTemperature();
        if (temperature < 0.1F || temperature > 0.3F) {
            return;
        }
        generatePatch(world, random, chunkX, chunkZ, 100, 64, 6, 3,
                null,
                this::placeWildPotatoes,
                this::placeFern);
    }

    private void generateWildBeetroots(World world, Random random, int chunkX, int chunkZ, Biome biome) {
        if (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)) {
            return;
        }
        generatePatch(world, random, chunkX, chunkZ, 30, 64, 6, 3,
                this::placeSandyShrubFloor,
                this::placeWildBeetroots,
                this::placeSandyShrub);
    }

    private void generateWildRice(World world, Random random, int chunkX, int chunkZ, Biome biome) {
        if (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) {
            return;
        }
        if (random.nextInt(20) != 0) {
            return;
        }

        BlockPos origin = randomSurfaceOrigin(world, random, chunkX, chunkZ);
        for (int i = 0; i < 96; i++) {
            BlockPos pos = origin.add(randomOffset(random, 8), randomOffset(random, 4), randomOffset(random, 8));
            IBlockState state = world.getBlockState(pos);
            if (!state.getMaterial().isReplaceable() || !world.isAirBlock(pos.up())) {
                continue;
            }
            if (!ModBlocks.WILD_RICE.canPlaceBlockAt(world, pos)) {
                continue;
            }

            world.setBlockState(pos, ModBlocks.WILD_RICE.getDefaultState().withProperty(BlockWildRice.HALF, BlockDoublePlant.EnumBlockHalf.LOWER), 2);
            world.setBlockState(pos.up(), ModBlocks.WILD_RICE.getDefaultState().withProperty(BlockWildRice.HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
        }
    }

    private void generateMushroomColonies(World world, Random random, int chunkX, int chunkZ, Biome biome, boolean brown) {
        if (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.MUSHROOM)) {
            return;
        }

        int rarity = 15;
        if (random.nextInt(rarity) != 0) {
            return;
        }

        Block primaryBlock = brown ? ModBlocks.BROWN_MUSHROOM_COLONY : ModBlocks.RED_MUSHROOM_COLONY;
        Block secondaryBlock = brown ? Blocks.BROWN_MUSHROOM : Blocks.RED_MUSHROOM;
        BlockPos origin = randomSurfaceOrigin(world, random, chunkX, chunkZ);

        int spread = 7;
        for (int i = 0; i < 64; i++) {
            BlockPos pos = origin.add(randomOffset(random, spread - 2), randomOffset(random, 4), randomOffset(random, spread - 2));
            if (!world.isAirBlock(pos) || world.getBlockState(pos.down()).getBlock() != Blocks.MYCELIUM) {
                continue;
            }
            IBlockState state = primaryBlock.getDefaultState().withProperty(BlockMushroomColony.AGE, random.nextInt(4));
            if (primaryBlock.canPlaceBlockAt(world, pos)) {
                world.setBlockState(pos, state, 2);
            }
        }

        for (int i = 0; i < 64; i++) {
            BlockPos pos = origin.add(randomOffset(random, spread), randomOffset(random, 4), randomOffset(random, spread));
            if (!world.isAirBlock(pos) || world.getBlockState(pos.down()).getBlock() != Blocks.MYCELIUM) {
                continue;
            }
            if (secondaryBlock.canPlaceBlockAt(world, pos)) {
                world.setBlockState(pos, secondaryBlock.getDefaultState(), 2);
            }
        }
    }

    private void generatePatch(World world, Random random, int chunkX, int chunkZ,
                               int rarity, int tries, int xzSpread, int ySpread,
                               PatchPlacer floorPlacer,
                               PatchPlacer primaryPlacer,
                               PatchPlacer secondaryPlacer) {
        if (random.nextInt(rarity) != 0) {
            return;
        }

        BlockPos origin = randomSurfaceOrigin(world, random, chunkX, chunkZ);
        int spread = xzSpread + 1;

        if (floorPlacer != null) {
            for (int i = 0; i < tries; i++) {
                BlockPos pos = origin.add(randomOffset(random, spread), randomOffset(random, ySpread + 1), randomOffset(random, spread));
                floorPlacer.place(world, random, pos);
            }
        }

        int primarySpread = Math.max(1, spread - 2);
        for (int i = 0; i < tries; i++) {
            BlockPos pos = origin.add(randomOffset(random, primarySpread), randomOffset(random, ySpread + 1), randomOffset(random, primarySpread));
            primaryPlacer.place(world, random, pos);
        }

        for (int i = 0; i < tries; i++) {
            BlockPos pos = origin.add(randomOffset(random, spread), randomOffset(random, ySpread + 1), randomOffset(random, spread));
            secondaryPlacer.place(world, random, pos);
        }
    }

    private BlockPos randomSurfaceOrigin(World world, Random random, int chunkX, int chunkZ) {
        int x = (chunkX << 4) + random.nextInt(16) + 8;
        int z = (chunkZ << 4) + random.nextInt(16) + 8;
        return world.getHeight(new BlockPos(x, 0, z));
    }

    private int randomOffset(Random random, int spread) {
        return random.nextInt(spread) - random.nextInt(spread);
    }

    private boolean placeWildCabbages(World world, Random random, BlockPos pos) {
        return placeWildCrop(world, pos, ModBlocks.WILD_CABBAGES, this::isSandLike);
    }

    private boolean placeWildOnions(World world, Random random, BlockPos pos) {
        return placeWildCrop(world, pos, ModBlocks.WILD_ONIONS, this::isDirtLike);
    }

    private boolean placeWildTomatoes(World world, Random random, BlockPos pos) {
        return placeWildCrop(world, pos, ModBlocks.WILD_TOMATOES, this::isTomatoSoil);
    }

    private boolean placeWildCarrots(World world, Random random, BlockPos pos) {
        return placeWildCrop(world, pos, ModBlocks.WILD_CARROTS, this::isDirtLike);
    }

    private boolean placeWildPotatoes(World world, Random random, BlockPos pos) {
        return placeWildCrop(world, pos, ModBlocks.WILD_POTATOES, this::isDirtLike);
    }

    private boolean placeWildBeetroots(World world, Random random, BlockPos pos) {
        return placeWildCrop(world, pos, ModBlocks.WILD_BEETROOTS, this::isSandLike);
    }

    private boolean placeSandyShrub(World world, Random random, BlockPos pos) {
        return placeBlock(world, pos, ModBlocks.SANDY_SHRUB.getDefaultState(), this::isSandLike);
    }

    private boolean placeAllium(World world, Random random, BlockPos pos) {
        IBlockState allium = Blocks.RED_FLOWER.getStateFromMeta(BlockFlower.EnumFlowerType.ALLIUM.getMeta());
        return placeBlock(world, pos, allium, this::isDirtLike);
    }

    private boolean placeDeadBush(World world, Random random, BlockPos pos) {
        return placeBlock(world, pos, Blocks.DEADBUSH.getDefaultState(), this::isTomatoSoil);
    }

    private boolean placeGrass(World world, Random random, BlockPos pos) {
        IBlockState grass = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);
        return placeBlock(world, pos, grass, this::isDirtLike);
    }

    private boolean placeFern(World world, Random random, BlockPos pos) {
        IBlockState fern = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.FERN);
        return placeBlock(world, pos, fern, this::isDirtLike);
    }

    private boolean placeSandyShrubFloor(World world, Random random, BlockPos pos) {
        return false;
    }

    private boolean placeCoarseDirtFloor(World world, Random random, BlockPos pos) {
        if (!world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        }
        BlockPos floorPos = pos.down();
        IBlockState floorState = world.getBlockState(floorPos);
        if (!isDirtLike(floorState)) {
            return false;
        }

        IBlockState coarseDirt = Blocks.DIRT.getDefaultState().withProperty(net.minecraft.block.BlockDirt.VARIANT, net.minecraft.block.BlockDirt.DirtType.COARSE_DIRT);
        world.setBlockState(floorPos, coarseDirt, 2);
        return true;
    }

    private boolean placeWildCrop(World world, BlockPos pos, Block block, SoilPredicate soilPredicate) {
        return placeBlock(world, pos, block.getDefaultState(), soilPredicate);
    }

    private boolean placeBlock(World world, BlockPos pos, IBlockState state, SoilPredicate soilPredicate) {
        if (!world.isAirBlock(pos)) {
            return false;
        }
        IBlockState soil = world.getBlockState(pos.down());
        if (!soilPredicate.matches(soil)) {
            return false;
        }
        Block block = state.getBlock();
        if (!block.canPlaceBlockAt(world, pos)) {
            return false;
        }
        world.setBlockState(pos, state, 2);
        return true;
    }

    private boolean isDirtLike(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.DIRT || block == Blocks.GRASS;
    }

    private boolean isSandLike(IBlockState state) {
        return state.getBlock() == Blocks.SAND;
    }

    private boolean isTomatoSoil(IBlockState state) {
        if (isDirtLike(state)) {
            return true;
        }
        if (state.getBlock() == Blocks.SAND) {
            BlockSand.EnumType type = state.getValue(BlockSand.VARIANT);
            return type == BlockSand.EnumType.SAND || type == BlockSand.EnumType.RED_SAND;
        }
        return false;
    }

    private interface PatchPlacer {
        boolean place(World world, Random random, BlockPos pos);
    }

    private interface SoilPredicate {
        boolean matches(IBlockState state);
    }

}

