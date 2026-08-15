package com.hlysine.create_connected.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class FeatureToggle {
    public static final Set<Identifier> TOGGLEABLE_FEATURES = new HashSet<>();
    public static final Map<Identifier, Identifier> DEPENDENT_FEATURES = new HashMap<>();
    public static final Map<Identifier, Set<FeatureCategory>> FEATURE_CATEGORIES = new HashMap<>();
    public static final Map<Identifier, Supplier<Boolean>> FEATURE_CONDITIONS = new HashMap<>();

    private static final List<Runnable> VISIBILITY_LISTENERS = new ArrayList<>();

    private static boolean sealed;

    public static void register(Identifier key) {
        assertOpen(key);
        TOGGLEABLE_FEATURES.add(key);
    }

    public static void register(Identifier key, FeatureCategory... categories) {
        register(key);
        FEATURE_CATEGORIES.put(key, Set.of(categories));
    }

    public static void registerDependent(Identifier key, Identifier dependency) {
        assertOpen(key);
        DEPENDENT_FEATURES.put(key, dependency);
    }

    public static void registerDependent(Identifier key, Identifier dependency, FeatureCategory... categories) {
        registerDependent(key, dependency);
        FEATURE_CATEGORIES.put(key, Set.of(categories));
    }

    public static void registerDependent(Identifier key, Block dependency) {
        registerDependent(key, BuiltInRegistries.BLOCK.getKey(dependency));
    }

    public static void registerDependent(Identifier key, Block dependency, FeatureCategory... categories) {
        registerDependent(key, BuiltInRegistries.BLOCK.getKey(dependency), categories);
    }

    public static void addCondition(Identifier key, Supplier<Boolean> condition) {
        assertOpen(key);
        FEATURE_CONDITIONS.put(key, condition);
    }

    /**
     * Check whether a feature is enabled.
     * If the provided {@link Identifier} is not registered with this feature toggle, it is assumed to be enabled.
     *
     * @param key The {@link Identifier} of the feature.
     * @return Whether the feature is enabled.
     */
    public static boolean isEnabled(Identifier key) {
        CCommon common = CCConfigs.common();
        if (common != null && FEATURE_CATEGORIES.containsKey(key)) {
            Set<FeatureCategory> categories = FEATURE_CATEGORIES.get(key);
            for (FeatureCategory category : categories) {
                if (!common.categories.isEnabled(category)) return false;
            }
        }
        if (FEATURE_CONDITIONS.containsKey(key)) {
            if (!FEATURE_CONDITIONS.get(key).get()) return false;
        }
        if (common != null && common.toggle.hasToggle(key)) {
            return common.toggle.isEnabled(key);
        } else {
            Identifier dependency = DEPENDENT_FEATURES.get(key);
            if (dependency != null) return isEnabled(dependency);
        }
        return true;
    }

    public static void assertAllRegistered() {
        if (TOGGLEABLE_FEATURES.isEmpty())
            throw new IllegalStateException("Feature toggles must be registered before the config is built");

        for (Map.Entry<Identifier, Identifier> entry : DEPENDENT_FEATURES.entrySet())
            if (!isKnown(entry.getValue()))
                throw new IllegalStateException("Feature " + entry.getKey() + " depends on unregistered feature " + entry.getValue());

        for (Identifier key : FEATURE_CONDITIONS.keySet())
            if (!isKnown(key))
                throw new IllegalStateException("Condition registered for unregistered feature " + key);

        for (Identifier key : FEATURE_CATEGORIES.keySet())
            if (!isKnown(key))
                throw new IllegalStateException("Categories registered for unregistered feature " + key);

        sealed = true;
    }

    public static void addVisibilityListener(Runnable listener) {
        VISIBILITY_LISTENERS.add(listener);
    }

    public static void refreshItemVisibility() {
        for (Runnable listener : VISIBILITY_LISTENERS)
            listener.run();
    }

    private static boolean isKnown(Identifier key) {
        return TOGGLEABLE_FEATURES.contains(key) || DEPENDENT_FEATURES.containsKey(key);
    }

    private static void assertOpen(Identifier key) {
        if (sealed)
            throw new IllegalStateException("Feature " + key + " was registered after the config was built");
    }
}
