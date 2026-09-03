package com.hlysine.create_connected;

import com.hlysine.create_connected.config.CCConfigs;
import com.hlysine.create_connected.content.brasschute.BrassChuteBlockEntity;
import com.hlysine.create_connected.content.redstonelinkwildcard.LinkWildcardNetworkHandler;
import com.hlysine.create_connected.foundation.advancement.CCAdvancements;
import com.hlysine.create_connected.foundation.advancement.CCTriggers;
import com.hlysine.create_connected.foundation.condition.CCCraftingConditions;
import com.hlysine.create_connected.network.CCNetwork;
import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.hlysine.create_connected.registries.CCCreativeTabs;
import com.hlysine.create_connected.registries.CCInventoryIdentifiers;
import com.hlysine.create_connected.registries.CCItems;
import com.hlysine.create_connected.registries.CCRegistration;
import com.hlysine.create_connected.registries.CCSequencerInstructions;
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
        CCInventoryIdentifiers.register();
        CCSequencerInstructions.register();
        CCCraftingConditions.register();
        CCConfigs.register();
        CCNetwork.register();
        LinkWildcardNetworkHandler.register();
        BrassChuteBlockEntity.registerTransfer();
    }

    public static Identifier asResource(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
