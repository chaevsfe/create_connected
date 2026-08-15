package com.hlysine.create_connected.config;

import com.zurrtum.create.catnip.config.Builder;
import com.zurrtum.create.catnip.config.ConfigValue;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

/**
 * Configures all feature categories.
 * Values in this class should NOT be accessed directly. Please access via {@link FeatureToggle} instead.
 */
public class CFeatureCategories extends SyncConfigBase {

    @Override
    public String getName() {
        return "feature_categories";
    }

    final Map<FeatureCategory, ConfigValue<Boolean>> toggles = new HashMap<>();

    Map<FeatureCategory, Boolean> synchronizedToggles;

    @Override
    public void registerAll(Builder builder) {
        for (FeatureCategory r : FeatureCategory.values()) {
            builder.comment(r.getDescription());
            toggles.put(r, builder.define(r.getSerializedName(), true));
        }
    }

    public boolean isEnabled(FeatureCategory category) {
        if (this.synchronizedToggles != null) {
            Boolean synced = synchronizedToggles.get(category);
            if (synced != null) return synced;
        }
        ConfigValue<Boolean> value = toggles.get(category);
        if (value != null)
            return value.get();
        return true;
    }

    @Override
    protected void readSyncConfig(CompoundTag nbt) {
        synchronizedToggles = new HashMap<>();
        for (String key : nbt.keySet()) {
            FeatureCategory category = FeatureCategory.byName(key);
            if (category == null) continue;
            synchronizedToggles.put(category, nbt.getBooleanOr(key, false));
        }
        FeatureToggle.refreshItemVisibility();
    }

    @Override
    protected void writeSyncConfig(CompoundTag nbt) {
        toggles.forEach((key, value) -> nbt.putBoolean(key.toString(), value.get()));
    }
}
