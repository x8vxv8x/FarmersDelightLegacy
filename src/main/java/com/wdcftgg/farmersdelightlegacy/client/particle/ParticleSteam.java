package com.wdcftgg.farmersdelightlegacy.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ParticleSteam extends Particle {

    private final TextureAtlasSprite[] frames;

    protected ParticleSteam(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                            double xSpeedIn, double ySpeedIn, double zSpeedIn, TextureAtlasSprite[] frames) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.frames = frames;
        this.particleScale = 3.0F;
        this.particleMaxAge = 80 + this.rand.nextInt(50);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn + (double) (this.rand.nextFloat() / 500.0F);
        this.motionZ = zSpeedIn;
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.particleAlpha = 0.6F;
        this.canCollide = false;
        this.setParticleTexture(this.frames[this.rand.nextInt(this.frames.length)]);
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

        if (this.particleAge++ < this.particleMaxAge && this.particleAlpha > 0.0F) {
            this.motionX += this.rand.nextFloat() / 5000.0F * (this.rand.nextBoolean() ? 1.0F : -1.0F);
            this.motionZ += this.rand.nextFloat() / 5000.0F * (this.rand.nextBoolean() ? 1.0F : -1.0F);
            this.motionY -= 0.000003D;
            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.particleAge >= this.particleMaxAge - 60 && this.particleAlpha > 0.01F) {
                this.particleAlpha -= 0.02F;
            }
        } else {
            this.setExpired();
        }
    }

    public static class Factory implements IParticleFactory {

        private final TextureAtlasSprite[] frames;

        public Factory(TextureAtlasSprite[] frames) {
            this.frames = frames;
        }

        @Nullable
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                                       double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new ParticleSteam(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, this.frames);
        }
    }
}

