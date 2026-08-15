package com.hlysine.create_connected.foundation.condition;

import com.hlysine.create_connected.CreateConnected;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.Identifier;

public class CCCraftingConditions {

    public static final ResourceConditionType<FeatureEnabledCondition> FEATURE_ENABLED =
            ResourceConditionType.create(Identifier.fromNamespaceAndPath(CreateConnected.MODID, "feature_enabled"), FeatureEnabledCondition.CODEC);

    public static final ResourceConditionType<FeatureEnabledInCopycatsCondition> FEATURE_ENABLED_IN_COPYCATS =
            ResourceConditionType.create(Identifier.fromNamespaceAndPath(CreateConnected.MODID, "feature_enabled_in_copycats"), FeatureEnabledInCopycatsCondition.CODEC);

    public static void register() {
        ResourceConditions.register(FEATURE_ENABLED);
        ResourceConditions.register(FEATURE_ENABLED_IN_COPYCATS);
    }
}
