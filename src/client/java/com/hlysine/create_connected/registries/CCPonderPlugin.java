package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.ponder.*;
import com.zurrtum.create.client.infrastructure.ponder.AllCreatePonderTags;
import com.zurrtum.create.client.infrastructure.ponder.scenes.ChuteScenes;
import com.zurrtum.create.client.ponder.api.registration.PonderPlugin;
import com.zurrtum.create.client.ponder.api.registration.PonderSceneRegistrationHelper;
import com.zurrtum.create.client.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class CCPonderPlugin implements PonderPlugin {
    private static final Identifier CHUTE_DOWNWARD = Identifier.fromNamespaceAndPath("create", "chute/downward");
    private static final Identifier CHUTE_UPWARD = Identifier.fromNamespaceAndPath("create", "chute/upward");

    @Override
    public @NotNull String getModId() {
        return CreateConnected.MODID;
    }

    @SuppressWarnings("removal")
    @Override
    public void registerScenes(PonderSceneRegistrationHelper<Identifier> helper) {
        PonderSceneRegistrationHelper<Item> SCENE_HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);

        SCENE_HELPER.forComponents(CCBlocks.ENCASED_CHAIN_COGWHEEL.asItem())
                .addStoryBoard("chain_cogwheel", ChainCogwheelScenes::chainCogwheelAsRelay, AllCreatePonderTags.KINETIC_RELAYS);
        SCENE_HELPER.forComponents(CCBlocks.CRANK_WHEEL.asItem(), CCBlocks.LARGE_CRANK_WHEEL.asItem())
                .addStoryBoard("crank_wheel", CrankWheelScenes::crankWheel, AllCreatePonderTags.KINETIC_SOURCES);
        SCENE_HELPER.forComponents(CCBlocks.INVERTED_CLUTCH.asItem())
                .addStoryBoard("inverted_clutch", InvertedClutchScenes::invertedClutch, AllCreatePonderTags.KINETIC_RELAYS);
        SCENE_HELPER.forComponents(CCBlocks.INVERTED_GEARSHIFT.asItem())
                .addStoryBoard("inverted_gearshift", InvertedGearshiftScenes::invertedGearshift, AllCreatePonderTags.KINETIC_RELAYS);
        SCENE_HELPER.forComponents(CCBlocks.PARALLEL_GEARBOX.asItem(), CCItems.VERTICAL_PARALLEL_GEARBOX)
                .addStoryBoard("parallel_gearbox", ParallelGearboxScenes::parallelGearbox, AllCreatePonderTags.KINETIC_RELAYS);
        SCENE_HELPER.forComponents(CCBlocks.KINETIC_BRIDGE.asItem())
                .addStoryBoard("kinetic_bridge", KineticBridgeScene::kineticBridge, AllCreatePonderTags.KINETIC_RELAYS);
        SCENE_HELPER.forComponents(CCBlocks.KINETIC_BATTERY.asItem(), CCItems.CHARGED_KINETIC_BATTERY)
                .addStoryBoard("kinetic_battery", KineticBatteryScene::kineticBattery, AllCreatePonderTags.KINETIC_SOURCES, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("kinetic_battery_chaining", KineticBatteryScene::kineticBatteryChaining, AllCreatePonderTags.KINETIC_SOURCES, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("kinetic_battery_automation", KineticBatteryScene::kineticBatteryAutomation, AllCreatePonderTags.KINETIC_SOURCES, AllCreatePonderTags.KINETIC_APPLIANCES);
        SCENE_HELPER.forComponents(CCBlocks.SEQUENCED_PULSE_GENERATOR.asItem())
                .addStoryBoard("sequenced_pulse_generator", SequencedPulseGeneratorScenes::pulseGenerator, AllCreatePonderTags.REDSTONE);
        SCENE_HELPER.forComponents(CCItems.LINKED_TRANSMITTER)
                .addStoryBoard("linked_transmitter", LinkedTransmitterScenes::linkedTransmitter, AllCreatePonderTags.REDSTONE);
        SCENE_HELPER.forComponents(CCBlocks.INVENTORY_ACCESS_PORT.asItem())
                .addStoryBoard("inventory_access_port", InventoryAccessPortScenes::inventoryAccessPort, AllCreatePonderTags.LOGISTICS);
        SCENE_HELPER.forComponents(CCBlocks.INVENTORY_BRIDGE.asItem())
                .addStoryBoard("inventory_bridge", InventoryBridgeScenes::inventoryBridge, AllCreatePonderTags.LOGISTICS)
                .addStoryBoard("inventory_bridge_filter", InventoryBridgeScenes::filtering, AllCreatePonderTags.LOGISTICS);
        SCENE_HELPER.forComponents(CCBlocks.BRASS_CHUTE.asItem())
                .addStoryBoard(CHUTE_DOWNWARD, ChuteScenes::downward, AllCreatePonderTags.LOGISTICS)
                .addStoryBoard(CHUTE_UPWARD, ChuteScenes::upward);
        SCENE_HELPER.forComponents(CCBlocks.DASHBOARD.asItem())
                .addStoryBoard("dashboard", DashboardScenes::dashboard, AllCreatePonderTags.DISPLAY_TARGETS);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<Identifier> helper) {
        PonderTagRegistrationHelper<Item> TAG_HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);

        TAG_HELPER.addToTag(AllCreatePonderTags.KINETIC_SOURCES)
                .add(CCBlocks.CRANK_WHEEL.asItem())
                .add(CCBlocks.KINETIC_BATTERY.asItem());
        TAG_HELPER.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES)
                .add(CCBlocks.KINETIC_BATTERY.asItem());
        TAG_HELPER.addToTag(AllCreatePonderTags.KINETIC_RELAYS)
                .add(CCBlocks.ENCASED_CHAIN_COGWHEEL.asItem())
                .add(CCBlocks.INVERTED_CLUTCH.asItem())
                .add(CCBlocks.INVERTED_GEARSHIFT.asItem())
                .add(CCBlocks.PARALLEL_GEARBOX.asItem())
                .add(CCBlocks.KINETIC_BRIDGE.asItem());
        TAG_HELPER.addToTag(AllCreatePonderTags.REDSTONE)
                .add(CCBlocks.SEQUENCED_PULSE_GENERATOR.asItem())
                .add(CCItems.LINKED_TRANSMITTER);
        TAG_HELPER.addToTag(AllCreatePonderTags.LOGISTICS)
                .add(CCBlocks.INVENTORY_ACCESS_PORT.asItem())
                .add(CCBlocks.INVENTORY_BRIDGE.asItem());
        TAG_HELPER.addToTag(AllCreatePonderTags.DISPLAY_TARGETS)
                .add(CCBlocks.DASHBOARD.asItem());
    }
}
