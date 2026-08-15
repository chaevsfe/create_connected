package com.hlysine.create_connected.compat.jei;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.FeatureToggle;
import com.hlysine.create_connected.registries.CCCreativeTabs;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class CreateConnectedJEI implements IModPlugin {

    public static final Identifier ID = CreateConnected.asResource("jei_plugin");

    private static IIngredientManager manager;

    static {
        FeatureToggle.addVisibilityListener(CreateConnectedJEI::refreshItemList);
    }

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        manager = jeiRuntime.getIngredientManager();
    }

    @Override
    public void onRuntimeUnavailable() {
        manager = null;
    }

    public static void refreshItemList() {
        IIngredientManager ingredientManager = manager;
        if (ingredientManager == null || Minecraft.getInstance().level == null)
            return;
        ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, collectStacks(false));
        ingredientManager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, collectStacks(true));
    }

    private static List<ItemStack> collectStacks(boolean enabledOnly) {
        List<ItemStack> stacks = new ArrayList<>(CCCreativeTabs.ITEMS.size());
        for (ItemLike entry : CCCreativeTabs.ITEMS) {
            if (enabledOnly && !FeatureToggle.isEnabled(CCCreativeTabs.getId(entry)))
                continue;
            stacks.add(new ItemStack(entry));
        }
        return stacks;
    }
}
