package com.hlysine.create_connected.compat;

import com.hlysine.create_connected.CreateConnected;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Method;
import java.util.Set;

public class CopycatsManager {
    private static final String FEATURE_TOGGLE_CLASS = "com.copycatsplus.copycats.config.FeatureToggle";

    private static final Set<String> SHARED_FEATURES = Set.of(
            "copycat_block",
            "copycat_slab",
            "copycat_beam",
            "copycat_vertical_step",
            "copycat_stairs",
            "copycat_fence",
            "copycat_fence_gate",
            "copycat_wall",
            "copycat_board",
            "copycat_box",
            "copycat_catwalk");

    public static boolean existsInCopycats(Identifier key) {
        String namespace = key.getNamespace();
        if (!namespace.equals(CreateConnected.MODID) && !namespace.equals(Mods.COPYCATS.id()))
            return false;
        return SHARED_FEATURES.contains(key.getPath());
    }

    public static boolean isFeatureEnabled(Identifier key) {
        if (!Mods.COPYCATS.isLoaded() || !existsInCopycats(key))
            return false;
        Identifier copycatsKey = Mods.COPYCATS.rl(key.getPath());
        if (!BuiltInRegistries.ITEM.containsKey(copycatsKey))
            return false;
        Method isEnabled = FeatureToggle.IS_ENABLED;
        if (isEnabled == null)
            return true;
        try {
            return (boolean) isEnabled.invoke(null, copycatsKey);
        } catch (ReflectiveOperationException | RuntimeException e) {
            CreateConnected.LOGGER.warn("Could not query Copycats+ feature toggle for {}", copycatsKey, e);
            return true;
        }
    }

    private static final class FeatureToggle {
        private static final Method IS_ENABLED = find();

        private static Method find() {
            try {
                return Class.forName(FEATURE_TOGGLE_CLASS).getMethod("isEnabled", Identifier.class);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
                CreateConnected.LOGGER.warn("Copycats+ feature toggle not found, treating every shared copycat as enabled in Copycats+", e);
                return null;
            }
        }
    }
}
