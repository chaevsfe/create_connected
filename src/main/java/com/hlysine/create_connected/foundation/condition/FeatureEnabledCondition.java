package com.hlysine.create_connected.foundation.condition;

import com.hlysine.create_connected.config.FeatureToggle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;

public record FeatureEnabledCondition(Identifier feature) implements ResourceCondition {
    public static final MapCodec<FeatureEnabledCondition> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder
            .group(Identifier.CODEC.fieldOf("tag").forGetter(FeatureEnabledCondition::feature))
            .apply(builder, FeatureEnabledCondition::new)
    );

    @Override
    public ResourceConditionType<?> getType() {
        return CCCraftingConditions.FEATURE_ENABLED;
    }

    @Override
    public boolean test(RegistryOps.RegistryInfoLookup registryLookup) {
        return FeatureToggle.isEnabled(feature);
    }

    @Override
    public String toString() {
        return "feature_enabled(\"" + feature + "\")";
    }
}
