package com.hlysine.create_connected;

import com.hlysine.create_connected.config.FeatureToggle;
import com.hlysine.create_connected.content.ClutchValueBox;
import com.hlysine.create_connected.content.RotationScrollValueBehaviour;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxRenderer;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxVisual;
import com.hlysine.create_connected.content.crankwheel.CrankWheelVisual;
import com.hlysine.create_connected.content.dashboard.DashboardRenderer;
import com.hlysine.create_connected.content.fancatalyst.FanCatalystRotatingHeadRenderer;
import com.hlysine.create_connected.content.fluidvessel.FluidVesselRenderer;
import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeFilterSlot;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryOverrides;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryRenderer;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryTooltipBehaviour;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryValueBox;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryVisual;
import com.hlysine.create_connected.content.kineticbridge.KineticBridgePlacementPreview;
import com.hlysine.create_connected.content.kineticbridge.KineticBridgeRenderer;
import com.hlysine.create_connected.content.kineticbridge.KineticBridgeStressScrollBehaviour;
import com.hlysine.create_connected.content.kineticbridge.KineticBridgeVisual;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedAnalogLeverRenderer;
import com.hlysine.create_connected.content.overstressclutch.OverstressClutchScrollValueBehaviour;
import com.hlysine.create_connected.content.overstressclutch.OverstressClutchTooltipBehaviour;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxRenderer;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxVisual;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlock;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorScreen;
import com.hlysine.create_connected.content.shearpin.ShearPinVisual;
import com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxRenderer;
import com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxVisual;
import com.hlysine.create_connected.mixin.featuretoggle.CreativeModeTabsAccessor;
import com.hlysine.create_connected.network.CCClientNetwork;
import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.hlysine.create_connected.registries.CCColorHandlers;
import com.hlysine.create_connected.registries.CCModels;
import com.hlysine.create_connected.registries.CCPartialModels;
import com.zurrtum.create.client.AllBlockEntityBehaviours;
import com.zurrtum.create.client.AllBlockEntityRenders;
import com.zurrtum.create.client.AllItemTooltips;
import com.zurrtum.create.client.catnip.gui.ScreenOpener;
import com.zurrtum.create.client.content.kinetics.base.ShaftRenderer;
import com.zurrtum.create.client.content.kinetics.crank.HandCrankRenderer;
import com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedCogVisual;
import com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedSmallCogRenderer;
import com.zurrtum.create.client.content.kinetics.transmission.SplitShaftRenderer;
import com.zurrtum.create.client.content.kinetics.transmission.SplitShaftVisual;
import com.zurrtum.create.client.content.logistics.chute.ChuteRenderer;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.audio.HandCrankAudioBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.audio.KineticAudioBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.filtering.SidedFilteringBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.RotationDirectionScrollBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.ChuteTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.FluidTankTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.GeneratingKineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.KineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.Map;

public class CreateConnectedClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CCPartialModels.register();
        CCModels.register();
        registerBlockEntityRenders();
        registerBlockEntityBehaviours();
        registerItemTooltips();
        CCColorHandlers.register();
        KineticBatteryOverrides.register();
        KineticBridgePlacementPreview.register();
        CCClientNetwork.register();
        SequencedPulseGeneratorBlock.setScreenOpener(be -> ScreenOpener.open(new SequencedPulseGeneratorScreen(be)));
        FeatureToggle.addVisibilityListener(CreateConnectedClient::rebuildCreativeTabs);
    }

    private static void registerBlockEntityRenders() {
        AllBlockEntityRenders.visual(
                CCBlockEntityTypes.ENCASED_CHAIN_COGWHEEL,
                EncasedSmallCogRenderer::new,
                EncasedCogVisual::small
        );
        AllBlockEntityRenders.visual(CCBlockEntityTypes.CRANK_WHEEL, HandCrankRenderer::new, CrankWheelVisual::new);
        AllBlockEntityRenders.visual(
                CCBlockEntityTypes.PARALLEL_GEARBOX,
                ParallelGearboxRenderer::new,
                ParallelGearboxVisual::new
        );
        AllBlockEntityRenders.visual(
                CCBlockEntityTypes.SIX_WAY_GEARBOX,
                SixWayGearboxRenderer::new,
                SixWayGearboxVisual::new
        );
        AllBlockEntityRenders.visual(CCBlockEntityTypes.BRASS_GEARBOX, BrassGearboxRenderer::new, BrassGearboxVisual::new);
        AllBlockEntityRenders.visual(CCBlockEntityTypes.OVERSTRESS_CLUTCH, SplitShaftRenderer::new, SplitShaftVisual::new);
        AllBlockEntityRenders.visual(CCBlockEntityTypes.INVERTED_CLUTCH, SplitShaftRenderer::new, SplitShaftVisual::new);
        AllBlockEntityRenders.visual(CCBlockEntityTypes.INVERTED_GEARSHIFT, SplitShaftRenderer::new, SplitShaftVisual::new);
        AllBlockEntityRenders.visual(CCBlockEntityTypes.CENTRIFUGAL_CLUTCH, SplitShaftRenderer::new, SplitShaftVisual::new);
        AllBlockEntityRenders.visual(CCBlockEntityTypes.FREEWHEEL_CLUTCH, SplitShaftRenderer::new, SplitShaftVisual::new);
        AllBlockEntityRenders.visual(CCBlockEntityTypes.BRAKE, SplitShaftRenderer::new, SplitShaftVisual::new);
        AllBlockEntityRenders.visual(CCBlockEntityTypes.SHEAR_PIN, ShaftRenderer::new, ShearPinVisual::new);
        AllBlockEntityRenders.visual(
                CCBlockEntityTypes.KINETIC_BRIDGE,
                KineticBridgeRenderer::source,
                (ctx, blockEntity, partialTick) -> new KineticBridgeVisual(ctx, blockEntity, partialTick, false)
        );
        AllBlockEntityRenders.visual(
                CCBlockEntityTypes.KINETIC_BRIDGE_DESTINATION,
                KineticBridgeRenderer::destination,
                (ctx, blockEntity, partialTick) -> new KineticBridgeVisual(ctx, blockEntity, partialTick, true)
        );
        AllBlockEntityRenders.visual(
                CCBlockEntityTypes.KINETIC_BATTERY,
                KineticBatteryRenderer::new,
                KineticBatteryVisual::new
        );
        AllBlockEntityRenders.render(CCBlockEntityTypes.LINKED_ANALOG_LEVER, LinkedAnalogLeverRenderer::new);
        AllBlockEntityRenders.render(CCBlockEntityTypes.FLUID_VESSEL, FluidVesselRenderer::new);
        AllBlockEntityRenders.render(CCBlockEntityTypes.CREATIVE_FLUID_VESSEL, FluidVesselRenderer::new);
        AllBlockEntityRenders.render(CCBlockEntityTypes.INVENTORY_BRIDGE, SmartBlockEntityRenderer::new);
        AllBlockEntityRenders.render(CCBlockEntityTypes.LINKED_TRANSMITTER, SmartBlockEntityRenderer::new);
        AllBlockEntityRenders.render(CCBlockEntityTypes.BRASS_CHUTE, ChuteRenderer::new);
        AllBlockEntityRenders.render(CCBlockEntityTypes.DASHBOARD, DashboardRenderer::new);
        AllBlockEntityRenders.render(
                CCBlockEntityTypes.FAN_ENDING_CATALYST_DRAGON_HEAD,
                FanCatalystRotatingHeadRenderer::dragon
        );
        AllBlockEntityRenders.render(CCBlockEntityTypes.FAN_EXPLODING_CATALYST, FanCatalystRotatingHeadRenderer::creeper);
    }

    private static void registerBlockEntityBehaviours() {
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.ENCASED_CHAIN_COGWHEEL,
                KineticAudioBehaviour::new,
                KineticTooltipBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.CRANK_WHEEL,
                HandCrankAudioBehaviour::new,
                GeneratingKineticTooltipBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.PARALLEL_GEARBOX,
                KineticAudioBehaviour::new,
                KineticTooltipBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.SIX_WAY_GEARBOX,
                KineticAudioBehaviour::new,
                KineticTooltipBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.BRASS_GEARBOX,
                KineticAudioBehaviour::new,
                KineticTooltipBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.OVERSTRESS_CLUTCH,
                KineticAudioBehaviour::new,
                OverstressClutchTooltipBehaviour::new,
                OverstressClutchScrollValueBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.SHEAR_PIN,
                KineticAudioBehaviour::new,
                KineticTooltipBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.INVERTED_CLUTCH,
                KineticAudioBehaviour::new,
                KineticTooltipBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.INVERTED_GEARSHIFT,
                KineticAudioBehaviour::new,
                KineticTooltipBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.CENTRIFUGAL_CLUTCH,
                KineticAudioBehaviour::new,
                KineticTooltipBehaviour::new,
                RotationScrollValueBehaviour::centrifugalClutch
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.FREEWHEEL_CLUTCH,
                KineticAudioBehaviour::new,
                KineticTooltipBehaviour::new,
                be -> new RotationDirectionScrollBehaviour(
                        be,
                        CreateLang.translateDirect("contraptions.windmill.rotation_direction"),
                        new ClutchValueBox()
                )
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.BRAKE,
                KineticAudioBehaviour::new,
                KineticTooltipBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.KINETIC_BRIDGE,
                KineticAudioBehaviour::new,
                KineticTooltipBehaviour::new,
                KineticBridgeStressScrollBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.KINETIC_BRIDGE_DESTINATION,
                KineticAudioBehaviour::new,
                GeneratingKineticTooltipBehaviour::new
        );
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.KINETIC_BATTERY,
                KineticAudioBehaviour::new,
                KineticBatteryTooltipBehaviour::new,
                be -> new RotationDirectionScrollBehaviour(
                        be,
                        ConnectedLang.translateDirect("battery.rotation_direction"),
                        new KineticBatteryValueBox(3)
                )
        );
        AllBlockEntityBehaviours.add(CCBlockEntityTypes.FLUID_VESSEL, FluidTankTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(CCBlockEntityTypes.BRASS_CHUTE, ChuteTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(
                CCBlockEntityTypes.INVENTORY_BRIDGE,
                be -> new SidedFilteringBehaviour(be, new InventoryBridgeFilterSlot())
        );
    }

    private static void registerItemTooltips() {
        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            if (entry.getKey().identifier().getNamespace().equals(CreateConnected.MODID))
                AllItemTooltips.register(entry.getValue());
        }
    }

    private static void rebuildCreativeTabs() {
        CreativeModeTab.ItemDisplayParameters parameters = CreativeModeTabsAccessor.getCACHED_PARAMETERS();
        if (parameters != null)
            CreativeModeTabsAccessor.callBuildAllTabContents(parameters);
    }
}
