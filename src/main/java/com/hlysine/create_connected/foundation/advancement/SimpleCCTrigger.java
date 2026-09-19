package com.hlysine.create_connected.foundation.advancement;

import com.hlysine.create_connected.CreateConnected;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.advancements.CriterionTrigger.Listener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleCCTrigger implements CriterionTrigger<SimpleCCTrigger.Instance> {

    private final Identifier id;

    private final Map<PlayerAdvancements, Set<Listener<Instance>>> players = new IdentityHashMap<>();

    public SimpleCCTrigger(String id) {
        this.id = CreateConnected.asResource(id);
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    @Override
    public void addPlayerListener(PlayerAdvancements advancements, Listener<Instance> listener) {
        players.computeIfAbsent(advancements, key -> new HashSet<>()).add(listener);
    }

    @Override
    public void removePlayerListener(PlayerAdvancements advancements, Listener<Instance> listener) {
        Set<Listener<Instance>> listeners = players.get(advancements);
        if (listeners == null)
            return;
        listeners.remove(listener);
        if (listeners.isEmpty())
            players.remove(advancements);
    }

    @Override
    public void removePlayerListeners(PlayerAdvancements advancements) {
        players.remove(advancements);
    }

    public void trigger(ServerPlayer player) {
        PlayerAdvancements advancements = player.getAdvancements();
        Set<Listener<Instance>> listeners = players.get(advancements);
        if (listeners == null || listeners.isEmpty())
            return;
        for (Listener<Instance> listener : List.copyOf(listeners))
            listener.run(advancements);
    }

    public static class Instance implements CriterionTriggerInstance {
        public static final Codec<Instance> CODEC = MapCodec.unitCodec(new Instance());

        @Override
        public void validate(ValidationContextSource validator) {
        }
    }
}
