package com.wdcftgg.farmersdelightlegacy.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SkilletCompositeBakedModel implements IBakedModel {

    private static final String NBT_COOKING = "Cooking";
    private static final TRSRTransformation INGREDIENT_TRANSFORM = createIngredientTransform();

    private final IBakedModel originalModel;
    private final IBakedModel cookingSkilletModel;
    private final ItemOverrideList overrideList;

    public SkilletCompositeBakedModel(IBakedModel originalModel, IBakedModel cookingSkilletModel) {
        this.originalModel = originalModel;
        this.cookingSkilletModel = cookingSkilletModel;
        this.overrideList = createOverrideList();
    }

    private static TRSRTransformation createIngredientTransform() {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
                new Vector3f(0.0F, -0.4F, 0.0F),
                TRSRTransformation.quatFromXYZDegrees(new Vector3f(270.0F, 0.0F, 0.0F)),
                new Vector3f(0.625F, 0.625F, 0.625F),
                null
        ));
    }

    private ItemOverrideList createOverrideList() {
        return new ItemOverrideList(Collections.emptyList()) {
            @Override
            public IBakedModel handleItemState(IBakedModel model, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                ItemStack cookingStack = getCookingStack(stack);
                if (cookingStack.isEmpty()) {
                    return SkilletCompositeBakedModel.this.originalModel;
                }

                return new CompositeBakedModel(
                        SkilletCompositeBakedModel.this.cookingSkilletModel,
                        bakeIngredientModel(cookingStack, world, entity)
                );
            }
        };
    }

    private static IBakedModel bakeIngredientModel(ItemStack cookingStack, @Nullable World world, @Nullable EntityLivingBase entity) {
        ResourceLocation itemId = cookingStack.getItem().getRegistryName();
        if (itemId == null) {
            return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(cookingStack, world, entity);
        }

        try {
            IModel unbakedModel = ModelLoaderRegistry.getModel(new ModelResourceLocation(itemId, "inventory"));
            return unbakedModel.bake(
                    INGREDIENT_TRANSFORM,
                    DefaultVertexFormats.ITEM,
                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString())
            );
        } catch (Exception ignored) {
            return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(cookingStack, world, entity);
        }
    }

    private static ItemStack getCookingStack(ItemStack skilletStack) {
        NBTTagCompound tag = skilletStack.getTagCompound();
        if (tag == null || !tag.hasKey(NBT_COOKING, 10)) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(tag.getCompoundTag(NBT_COOKING));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return this.originalModel.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.originalModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.originalModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.originalModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.originalModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.originalModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.overrideList;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return this.originalModel.handlePerspective(cameraTransformType);
    }

    private static final class CompositeBakedModel implements IBakedModel {

        private final IBakedModel skilletModel;
        private final List<BakedQuad> generalQuads;
        private final Map<EnumFacing, List<BakedQuad>> faceQuads;

        private CompositeBakedModel(IBakedModel skilletModel, IBakedModel ingredientModel) {
            this.skilletModel = skilletModel;
            this.generalQuads = Collections.unmodifiableList(buildMergedQuads(skilletModel, ingredientModel, null));
            this.faceQuads = new EnumMap<>(EnumFacing.class);

            for (EnumFacing side : EnumFacing.values()) {
                this.faceQuads.put(side, Collections.unmodifiableList(buildMergedQuads(skilletModel, ingredientModel, side)));
            }
        }

        private static List<BakedQuad> buildMergedQuads(IBakedModel skilletModel, IBakedModel ingredientModel, @Nullable EnumFacing side) {
            List<BakedQuad> mergedQuads = new ArrayList<>();
            mergedQuads.addAll(ingredientModel.getQuads(null, side, 0L));
            mergedQuads.addAll(skilletModel.getQuads(null, side, 0L));
            return mergedQuads;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            return side == null ? this.generalQuads : this.faceQuads.get(side);
        }

        @Override
        public boolean isAmbientOcclusion() {
            return this.skilletModel.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return this.skilletModel.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return this.skilletModel.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return this.skilletModel.getParticleTexture();
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return this.skilletModel.getItemCameraTransforms();
        }

        @Override
        public ItemOverrideList getOverrides() {
            return ItemOverrideList.NONE;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
            Pair<? extends IBakedModel, Matrix4f> basePerspective = this.skilletModel.handlePerspective(cameraTransformType);
            return Pair.of(this, basePerspective.getRight());
        }
    }
}
