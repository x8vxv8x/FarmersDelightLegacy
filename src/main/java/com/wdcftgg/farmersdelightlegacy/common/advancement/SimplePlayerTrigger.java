package com.wdcftgg.farmersdelightlegacy.common.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimplePlayerTrigger implements ICriterionTrigger<SimplePlayerTrigger.Instance> {

    private final ResourceLocation id;
    private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

    public SimplePlayerTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
        Listeners playerListeners = listeners.get(playerAdvancementsIn);
        if (playerListeners == null) {
            playerListeners = new Listeners(playerAdvancementsIn);
            listeners.put(playerAdvancementsIn, playerListeners);
        }
        playerListeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
        Listeners playerListeners = listeners.get(playerAdvancementsIn);
        if (playerListeners != null) {
            playerListeners.remove(listener);
            if (playerListeners.isEmpty()) {
                listeners.remove(playerAdvancementsIn);
            }
        }
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        listeners.remove(playerAdvancementsIn);
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new Instance(id);
    }

    public void trigger(EntityPlayerMP player) {
        Listeners playerListeners = listeners.get(player.getAdvancements());
        if (playerListeners != null) {
            playerListeners.trigger();
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        public Instance(ResourceLocation criterionIn) {
            super(criterionIn);
        }

        public boolean test() {
            return true;
        }
    }

    private static class Listeners {

        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<Instance>> listeners = Sets.newHashSet();

        private Listeners(PlayerAdvancements playerAdvancements) {
            this.playerAdvancements = playerAdvancements;
        }

        private boolean isEmpty() {
            return listeners.isEmpty();
        }

        private void add(Listener<Instance> listener) {
            listeners.add(listener);
        }

        private void remove(Listener<Instance> listener) {
            listeners.remove(listener);
        }

        private void trigger() {
            List<Listener<Instance>> matchedListeners = null;

            for (Listener<Instance> listener : listeners) {
                if (listener.getCriterionInstance().test()) {
                    if (matchedListeners == null) {
                        matchedListeners = Lists.newArrayList();
                    }
                    matchedListeners.add(listener);
                }
            }

            if (matchedListeners != null) {
                for (Listener<Instance> listener : matchedListeners) {
                    listener.grantCriterion(playerAdvancements);
                }
            }
        }
    }
}
