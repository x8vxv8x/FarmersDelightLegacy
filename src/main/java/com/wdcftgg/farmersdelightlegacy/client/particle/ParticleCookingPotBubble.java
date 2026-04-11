package com.wdcftgg.farmersdelightlegacy.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ParticleCookingPotBubble extends Particle {

    private final TextureAtlasSprite[] bubbleFrames;

    protected ParticleCookingPotBubble(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                                       double xSpeedIn, double ySpeedIn, double zSpeedIn, TextureAtlasSprite[] bubbleFrames) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.bubbleFrames = bubbleFrames;
        this.setParticleTexture(this.bubbleFrames[0]);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        this.particleScale = 1F + this.rand.nextFloat() * 0.12F;
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.particleAlpha = 1.0F;
        this.particleMaxAge = 4;
        this.canCollide = false;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        int frameIndex = (int) ((this.particleAge / (float) this.particleMaxAge) * this.bubbleFrames.length);
        this.setParticleTexture(this.bubbleFrames[Math.min(frameIndex, this.bubbleFrames.length - 1)]);

        this.motionY -= 0.008D;
        this.move(this.motionX, this.motionY, this.motionZ);
    }

    public static class Factory implements IParticleFactory {

        private final TextureAtlasSprite[] bubbleFrames;

        public Factory(TextureAtlasSprite[] bubbleFrames) {
            this.bubbleFrames = bubbleFrames;
        }

        @Nullable
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                                       double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new ParticleCookingPotBubble(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, this.bubbleFrames);
        }
    }
}

