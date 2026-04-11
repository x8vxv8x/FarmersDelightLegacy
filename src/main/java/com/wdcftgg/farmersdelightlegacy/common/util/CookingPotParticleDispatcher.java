package com.wdcftgg.farmersdelightlegacy.common.util;

import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class CookingPotParticleDispatcher {

    private static final String BRIDGE_CLASS_NAME = "com.wdcftgg.farmersdelightlegacy.client.particle.CookingPotParticleBridge";

    private static boolean initialized;
    private static Method spawnSteamMethod;
    private static Method spawnBubbleMethod;

    private CookingPotParticleDispatcher() {
    }

    public static void spawnSteam(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        if (world == null || !world.isRemote) {
            return;
        }
        ensureInitialized();
        invoke(spawnSteamMethod, world, x, y, z, motionX, motionY, motionZ);
    }

    public static void spawnCookingPotBubble(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        if (world == null || !world.isRemote) {
            return;
        }
        ensureInitialized();
        invoke(spawnBubbleMethod, world, x, y, z, motionX, motionY, motionZ);
    }

    private static void ensureInitialized() {
        if (initialized) {
            return;
        }
        initialized = true;
        try {
            Class<?> bridgeClass = Class.forName(BRIDGE_CLASS_NAME);
            Class<?>[] signature = new Class[]{World.class, double.class, double.class, double.class, double.class, double.class, double.class};
            spawnSteamMethod = bridgeClass.getMethod("spawnSteam", signature);
            spawnBubbleMethod = bridgeClass.getMethod("spawnCookingPotBubble", signature);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            spawnSteamMethod = null;
            spawnBubbleMethod = null;
        }
    }

    private static void invoke(Method method, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        if (method == null) {
            return;
        }
        try {
            method.invoke(null, world, x, y, z, motionX, motionY, motionZ);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
    }
}

