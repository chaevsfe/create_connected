package com.hlysine.create_connected;

import com.hlysine.create_connected.config.CCConfigs;
import com.hlysine.create_connected.config.FeatureToggle;
import com.hlysine.create_connected.foundation.advancement.CCAdvancements;
import com.hlysine.create_connected.foundation.advancement.CCTriggers;
import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.hlysine.create_connected.registries.CCCreativeTabs;
import com.hlysine.create_connected.registries.CCItems;
import com.hlysine.create_connected.registries.CCRegistration;
import com.hlysine.create_connected.registries.CCSoundEvents;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class CreateConnected implements ModInitializer {

    public static final String MODID = "create_connected";

    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        CreateConnectedPlugin.verifyEarlyRegistrationComplete();
        CCItems.register();
        CCBlockEntityTypes.register();
        CCCreativeTabs.register();
        CCSoundEvents.register();
        CCAdvancements.register();
        CCTriggers.register();
        CCRegistration.register();
        verifyFeatureTogglesPopulated();
        CCConfigs.register();
    }

    private static void verifyFeatureTogglesPopulated() {
        if (FeatureToggle.TOGGLEABLE_FEATURES.isEmpty() || FeatureToggle.DEPENDENT_FEATURES.isEmpty())
            throw new IllegalStateException(
                    "Create: Connected feature toggles must be registered before the config is built (toggleable="
                            + FeatureToggle.TOGGLEABLE_FEATURES.size() + ", dependent="
                            + FeatureToggle.DEPENDENT_FEATURES.size() + ")");
    }

    public static Identifier asResource(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
