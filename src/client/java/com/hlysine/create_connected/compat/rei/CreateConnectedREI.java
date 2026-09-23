package com.hlysine.create_connected.compat.rei;

import com.hlysine.create_connected.config.FeatureToggle;
import com.hlysine.create_connected.registries.CCCreativeTabs;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CreateConnectedREI implements REIClientPlugin {

    private static BasicFilteringRule.MarkDirty hiddenItems;

    static {
        FeatureToggle.addVisibilityListener(CreateConnectedREI::refreshItemList);
    }

    @Override
    public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
        hiddenItems = rule.hide(CreateConnectedREI::collectDisabledStacks);
    }

    public static void refreshItemList() {
        BasicFilteringRule.MarkDirty handle = hiddenItems;
        if (handle != null)
            handle.markDirty();
    }

    private static Collection<EntryStack<?>> collectDisabledStacks() {
        List<EntryStack<?>> stacks = new ArrayList<>();
        for (ItemLike entry : CCCreativeTabs.ITEMS) {
            if (!FeatureToggle.isEnabled(CCCreativeTabs.getId(entry)))
                stacks.add(EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(entry)));
        }
        return stacks;
    }
}
