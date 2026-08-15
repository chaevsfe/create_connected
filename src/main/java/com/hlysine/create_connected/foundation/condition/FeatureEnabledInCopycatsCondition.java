package com.hlysine.create_connected.foundation.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;

public record FeatureEnabledInCopycatsCondition(Identifier feature) implements ResourceCondition {
    public static final MapCodec<FeatureEnabledInCopycatsCondition> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder
            .group(Identifier.CODEC.fieldOf("tag").forGetter(FeatureEnabledInCopycatsCondition::feature))
            .apply(builder, FeatureEnabledInCopycatsCondition::new)
    );

    @Override
    public ResourceConditionType<?> getType() {
        return CCCraftingConditions.FEATURE_ENABLED_IN_COPYCATS;
    }

    @Override
    public boolean test(RegistryOps.RegistryInfoLookup registryLookup) {
        return false;
    }

    @Override
    public String toString() {
        return "feature_enabled_in_copycats(\"" + feature + "\")";
    }
}
