package com.wdcftgg.farmersdelightlegacy.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCanvasHangingSign extends ModelBase {

    private static final float CHAIN_ANGLE = 0.7853982F;
    private static final float BACKFACE_ROTATION = (float) Math.PI;

    private final ModelRenderer board;
    private final ModelRenderer plank;
    private final ModelRenderer normalChains;
    private final ModelRenderer verticalChains;

    public ModelCanvasHangingSign() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.board = new ModelRenderer(this, 0, 12);
        this.board.addBox(-7.0F, 0.0F, -1.0F, 14, 10, 2);

        this.plank = new ModelRenderer(this, 0, 0);
        this.plank.addBox(-8.0F, -6.0F, -2.0F, 16, 2, 4);

        this.normalChains = new ModelRenderer(this);
        this.normalChains.addChild(createChainSegment(0, 6, -5.0F, -6.0F, -CHAIN_ANGLE));
        this.normalChains.addChild(createChainSegment(6, 6, -5.0F, -6.0F, CHAIN_ANGLE));
        this.normalChains.addChild(createChainSegment(0, 6, 5.0F, -6.0F, -CHAIN_ANGLE));
        this.normalChains.addChild(createChainSegment(6, 6, 5.0F, -6.0F, CHAIN_ANGLE));

        this.verticalChains = createDoubleSidedPlane(14, 6, -6.0F, -6.0F, 0.0F, 12, 6);
    }

    private ModelRenderer createChainSegment(int textureOffsetX, int textureOffsetY, float rotationPointX, float rotationPointY, float rotationAngleY) {
        ModelRenderer segment = createDoubleSidedPlane(textureOffsetX, textureOffsetY, -1.5F, 0.0F, 0.0F, 3, 6);
        segment.setRotationPoint(rotationPointX, rotationPointY, 0.0F);
        segment.rotateAngleY = rotationAngleY;
        return segment;
    }

    private ModelRenderer createDoubleSidedPlane(int textureOffsetX, int textureOffsetY, float boxX, float boxY, float boxZ, int width, int height) {
        ModelRenderer root = new ModelRenderer(this);
        ModelRenderer frontFace = new ModelRenderer(this, textureOffsetX, textureOffsetY);
        frontFace.addBox(boxX, boxY, boxZ, width, height, 0);
        root.addChild(frontFace);

        ModelRenderer backFace = new ModelRenderer(this, textureOffsetX, textureOffsetY);
        backFace.addBox(boxX, boxY, boxZ, width, height, 0);
        backFace.rotateAngleY = BACKFACE_ROTATION;
        root.addChild(backFace);

        return root;
    }

    public void renderCeiling(boolean attached, float scale) {
        this.plank.showModel = false;
        this.normalChains.showModel = !attached;
        this.verticalChains.showModel = attached;
        render(scale);
    }

    public void renderWall(float scale) {
        this.plank.showModel = true;
        this.normalChains.showModel = true;
        this.verticalChains.showModel = false;
        render(scale);
    }

    private void render(float scale) {
        this.board.render(scale);
        this.plank.render(scale);
        this.normalChains.render(scale);
        this.verticalChains.render(scale);
    }
}
