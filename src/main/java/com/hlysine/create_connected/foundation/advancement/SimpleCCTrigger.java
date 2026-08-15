package com.hlysine.create_connected.foundation.advancement;

import com.hlysine.create_connected.CreateConnected;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.PlayerAdvancements.TriggerInstanceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

import java.util.List;
import java.util.Map;

public class SimpleCCTrigger implements CriterionTrigger<SimpleCCTrigger.Instance> {

    private final Identifier id;

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

    public void trigger(ServerPlayer player) {
        PlayerAdvancements advancements = player.getAdvancements();
        Map<TriggerInstanceKey, Instance> listeners = advancements.getTriggerMapForType(this);
        if (listeners == null)
            return;
        for (TriggerInstanceKey criterion : List.copyOf(listeners.keySet()))
            advancements.award(criterion.advancement(), criterion.criterion());
    }

    public static class Instance implements CriterionTriggerInstance {
        public static final Codec<Instance> CODEC = MapCodec.unitCodec(new Instance());

        @Override
        public void validate(ValidationContextSource validator) {
        }
    }
}
