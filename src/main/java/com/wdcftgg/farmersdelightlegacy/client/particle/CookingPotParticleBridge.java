package com.wdcftgg.farmersdelightlegacy.client.particle;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public final class CookingPotParticleBridge {

    private static final ResourceLocation[] STEAM_TEXTURES = new ResourceLocation[12];
    private static final ResourceLocation[] BUBBLE_TEXTURES = new ResourceLocation[4];
    private static boolean warnedEarlyFactoryRegistration;

    static {
        for (int i = 0; i < STEAM_TEXTURES.length; i++) {
            STEAM_TEXTURES[i] = new ResourceLocation(FarmersDelightLegacy.MOD_ID, "particle/steam_" + i);
        }
        for (int i = 0; i < BUBBLE_TEXTURES.length; i++) {
            BUBBLE_TEXTURES[i] = new ResourceLocation(FarmersDelightLegacy.MOD_ID, "particle/bubble_pop_" + i);
        }
    }

    private CookingPotParticleBridge() {
    }

    public static void registerTextures(TextureMap textureMap) {
        for (ResourceLocation texture : STEAM_TEXTURES) {
            textureMap.registerSprite(texture);
        }
        for (ResourceLocation texture : BUBBLE_TEXTURES) {
            textureMap.registerSprite(texture);
        }
    }

    public static boolean registerFactories() {
        Minecraft minecraft = Minecraft.getMinecraft();
        TextureMap textureMap = minecraft.getTextureMapBlocks();
        if (minecraft.effectRenderer == null) {
            if (!warnedEarlyFactoryRegistration) {
                FarmersDelightLegacy.LOGGER.debug("粒子工厂延迟注册：effectRenderer 尚未初始化。");
                warnedEarlyFactoryRegistration = true;
            }
            return false;
        }

        TextureAtlasSprite[] steamFrames = new TextureAtlasSprite[STEAM_TEXTURES.length];
        for (int i = 0; i < STEAM_TEXTURES.length; i++) {
            steamFrames[i] = textureMap.getAtlasSprite(STEAM_TEXTURES[i].toString());
        }

        TextureAtlasSprite[] bubbleFrames = new TextureAtlasSprite[BUBBLE_TEXTURES.length];
        for (int i = 0; i < BUBBLE_TEXTURES.length; i++) {
            bubbleFrames[i] = textureMap.getAtlasSprite(BUBBLE_TEXTURES[i].toString());
        }
        ParticleManager particleManager = minecraft.effectRenderer;
        particleManager.registerParticle(ModParticles.STEAM_PARTICLE_ID, new ParticleSteam.Factory(steamFrames));
        particleManager.registerParticle(ModParticles.COOKING_POT_BUBBLE_PARTICLE_ID, new ParticleCookingPotBubble.Factory(bubbleFrames));
        return true;
    }

    public static void spawnSteam(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.effectRenderer != null) {
            minecraft.effectRenderer.spawnEffectParticle(ModParticles.STEAM_PARTICLE_ID, x, y, z, motionX, motionY, motionZ);
        }
    }

    public static void spawnCookingPotBubble(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.effectRenderer != null) {
            minecraft.effectRenderer.spawnEffectParticle(ModParticles.COOKING_POT_BUBBLE_PARTICLE_ID, x, y, z, motionX, motionY, motionZ);
        }
    }
}

