package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.DyeDepotCompat;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.config.FeatureCategory;
import com.hlysine.create_connected.config.FeatureToggle;
import com.hlysine.create_connected.content.WrenchableBlock;
import com.hlysine.create_connected.content.brake.BrakeBlock;
import com.hlysine.create_connected.content.brasschute.BrassChuteBlock;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxBlock;
import com.hlysine.create_connected.content.centrifugalclutch.CentrifugalClutchBlock;
import com.hlysine.create_connected.content.chaincogwheel.ChainCogwheelBlock;
import com.hlysine.create_connected.content.copycat.beam.CopycatBeamBlock;
import com.hlysine.create_connected.content.copycat.block.CopycatBlockBlock;
import com.hlysine.create_connected.content.copycat.board.CopycatBoardBlock;
import com.hlysine.create_connected.content.copycat.fence.CopycatFenceBlock;
import com.hlysine.create_connected.content.copycat.fence.WrappedFenceBlock;
import com.hlysine.create_connected.content.copycat.fencegate.CopycatFenceGateBlock;
import com.hlysine.create_connected.content.copycat.fencegate.WrappedFenceGateBlock;
import com.hlysine.create_connected.content.copycat.slab.CopycatSlabBlock;
import com.hlysine.create_connected.content.copycat.stairs.CopycatStairsBlock;
import com.hlysine.create_connected.content.copycat.stairs.WrappedStairsBlock;
import com.hlysine.create_connected.content.copycat.verticalstep.CopycatVerticalStepBlock;
import com.hlysine.create_connected.content.copycat.wall.CopycatWallBlock;
import com.hlysine.create_connected.content.copycat.wall.WrappedWallBlock;
import com.hlysine.create_connected.content.crankwheel.CrankWheelBlock;
import com.hlysine.create_connected.content.crankwheel.CrankWheelItem;
import com.hlysine.create_connected.content.crossconnector.CrossConnectorBlock;
import com.hlysine.create_connected.content.crossconnector.EncasedCrossConnectorBlock;
import com.hlysine.create_connected.content.dashboard.DashboardBlock;
import com.hlysine.create_connected.content.fancatalyst.FanCatalystRotatingHeadBlock;
import com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock;
import com.hlysine.create_connected.content.fluidvessel.FluidVesselItem;
import com.hlysine.create_connected.content.freewheelclutch.FreewheelClutchBlock;
import com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlock;
import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlock;
import com.hlysine.create_connected.content.invertedclutch.InvertedClutchBlock;
import com.hlysine.create_connected.content.invertedgearshift.InvertedGearshiftBlock;
import com.hlysine.create_connected.content.itemsilo.ItemSiloBlock;
import com.hlysine.create_connected.content.itemsilo.ItemSiloItem;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlock;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockItem;
import com.hlysine.create_connected.content.kineticbridge.KineticBridgeBlock;
import com.hlysine.create_connected.content.kineticbridge.KineticBridgeBlockItem;
import com.hlysine.create_connected.content.kineticbridge.KineticBridgeDestinationBlock;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedAnalogLeverBlock;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedButtonBlock;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedLeverBlock;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterBlock;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterItem;
import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxBlock;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlock;
import com.hlysine.create_connected.content.shearpin.ShearPinBlock;
import com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxBlock;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.decoration.copycat.CopycatBlock;
import com.zurrtum.create.content.logistics.chute.ChuteItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class CCBlocks {
    static {
        CCDataComponents.register();
    }

    public static final ChainCogwheelBlock ENCASED_CHAIN_COGWHEEL = toggle(
            registerBlock("encased_chain_cogwheel", ChainCogwheelBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.PODZOL)),
            FeatureCategory.KINETIC);

    public static final CrankWheelBlock.Small CRANK_WHEEL = toggle(
            registerBlock("crank_wheel", CrankWheelBlock.Small::new,
                    Properties.ofFullCopy(Blocks.STRIPPED_SPRUCE_WOOD).mapColor(MapColor.PODZOL),
                    CrankWheelItem::new, new Item.Properties()),
            FeatureCategory.KINETIC);

    public static final CrankWheelBlock.Large LARGE_CRANK_WHEEL = toggle(
            registerBlock("large_crank_wheel", CrankWheelBlock.Large::new,
                    Properties.ofFullCopy(Blocks.STRIPPED_SPRUCE_WOOD).mapColor(MapColor.PODZOL),
                    CrankWheelItem::new, new Item.Properties()),
            FeatureCategory.KINETIC);

    public static final ParallelGearboxBlock PARALLEL_GEARBOX = toggle(
            registerBlock("parallel_gearbox", ParallelGearboxBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.PODZOL)),
            FeatureCategory.KINETIC);

    public static final SixWayGearboxBlock SIX_WAY_GEARBOX = toggle(
            registerBlock("six_way_gearbox", SixWayGearboxBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.PODZOL)),
            FeatureCategory.KINETIC);

    public static final CrossConnectorBlock CROSS_CONNECTOR = toggle(
            registerBlock("cross_connector", CrossConnectorBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.PODZOL)),
            FeatureCategory.KINETIC);

    public static final EncasedCrossConnectorBlock ANDESITE_ENCASED_CROSS_CONNECTOR = dependent(
            registerBlock("andesite_encased_cross_connector",
                    p -> new EncasedCrossConnectorBlock(p, () -> AllBlocks.ANDESITE_CASING),
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.PODZOL)),
            CROSS_CONNECTOR, FeatureCategory.KINETIC);

    public static final EncasedCrossConnectorBlock BRASS_ENCASED_CROSS_CONNECTOR = dependent(
            registerBlock("brass_encased_cross_connector",
                    p -> new EncasedCrossConnectorBlock(p, () -> AllBlocks.BRASS_CASING),
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.TERRACOTTA_BROWN)),
            CROSS_CONNECTOR, FeatureCategory.KINETIC);

    public static final OverstressClutchBlock OVERSTRESS_CLUTCH = toggle(
            registerBlock("overstress_clutch", OverstressClutchBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.PODZOL)),
            FeatureCategory.KINETIC);

    public static final ShearPinBlock SHEAR_PIN = toggle(
            registerBlock("shear_pin", ShearPinBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).mapColor(MapColor.METAL).forceSolidOn()),
            FeatureCategory.KINETIC);

    public static final InvertedClutchBlock INVERTED_CLUTCH = toggle(
            registerBlock("inverted_clutch", InvertedClutchBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.PODZOL)),
            FeatureCategory.KINETIC);

    public static final InvertedGearshiftBlock INVERTED_GEARSHIFT = toggle(
            registerBlock("inverted_gearshift", InvertedGearshiftBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.PODZOL)),
            FeatureCategory.KINETIC);

    public static final CentrifugalClutchBlock CENTRIFUGAL_CLUTCH = toggle(
            registerBlock("centrifugal_clutch", CentrifugalClutchBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.PODZOL)),
            FeatureCategory.KINETIC);

    public static final FreewheelClutchBlock FREEWHEEL_CLUTCH = toggle(
            registerBlock("freewheel_clutch", FreewheelClutchBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.PODZOL)),
            FeatureCategory.KINETIC);

    public static final KineticBridgeBlock KINETIC_BRIDGE = toggle(
            registerBlock("kinetic_bridge", KineticBridgeBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.TERRACOTTA_BROWN),
                    KineticBridgeBlockItem::new, new Item.Properties()),
            FeatureCategory.KINETIC);

    public static final KineticBridgeDestinationBlock KINETIC_BRIDGE_DESTINATION = dependent(
            registerBlockNoItem("kinetic_bridge_destination", KineticBridgeDestinationBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.TERRACOTTA_BROWN)),
            KINETIC_BRIDGE, FeatureCategory.KINETIC);

    public static final BrassGearboxBlock BRASS_GEARBOX = toggle(
            registerBlock("brass_gearbox", BrassGearboxBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.TERRACOTTA_BROWN)),
            FeatureCategory.KINETIC);

    public static final BrakeBlock BRAKE = toggle(
            registerBlock("brake", BrakeBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.PODZOL)),
            FeatureCategory.KINETIC);

    public static final KineticBatteryBlock KINETIC_BATTERY = toggle(
            registerBlock("kinetic_battery", KineticBatteryBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion().mapColor(MapColor.TERRACOTTA_BROWN),
                    KineticBatteryBlockItem::new,
                    new Item.Properties().component(CCDataComponents.KINETIC_BATTERY_CHARGE, 0.0)),
            FeatureCategory.KINETIC);

    public static final SequencedPulseGeneratorBlock SEQUENCED_PULSE_GENERATOR = toggle(
            registerBlock("sequenced_pulse_generator", SequencedPulseGeneratorBlock::new,
                    Properties.ofFullCopy(Blocks.REPEATER)),
            FeatureCategory.REDSTONE);

    public static final Map<BlockSetType, LinkedButtonBlock> LINKED_BUTTONS = new HashMap<>();

    static {
        BlockSetType.values().forEach(type -> {
            Block button = BuiltInRegistries.BLOCK.getValue(Identifier.parse(type.name() + "_button"));
            if (!(button instanceof ButtonBlock buttonBlock))
                return;
            String namePath = type.name().contains(":") ? type.name().replace(':', '_') : type.name();
            LinkedButtonBlock block = linkedTransmitter(registerBlockNoItem("linked_" + namePath + "_button",
                    properties -> new LinkedButtonBlock(properties, buttonBlock),
                    Properties.ofFullCopy(buttonBlock)));
            LINKED_BUTTONS.put(type, block);
        });
    }

    public static final LinkedLeverBlock LINKED_LEVER = linkedTransmitter(
            registerBlockNoItem("linked_lever",
                    properties -> new LinkedLeverBlock(properties, (LeverBlock) Blocks.LEVER),
                    Properties.ofFullCopy(Blocks.LEVER)));

    public static final LinkedAnalogLeverBlock LINKED_ANALOG_LEVER = linkedTransmitter(
            registerBlockNoItem("linked_analog_lever",
                    properties -> new LinkedAnalogLeverBlock(properties, () -> AllBlocks.ANALOG_LEVER),
                    Properties.ofFullCopy(Blocks.LEVER)));

    public static final WrenchableBlock EMPTY_FAN_CATALYST = toggle(
            registerBlock("empty_fan_catalyst", WrenchableBlock::new, fanCatalystProperties(0)),
            FeatureCategory.LOGISTICS);

    public static final WrenchableBlock FAN_BLASTING_CATALYST = fanCatalyst("fan_blasting_catalyst", 10);

    public static final WrenchableBlock FAN_SMOKING_CATALYST = fanCatalyst("fan_smoking_catalyst", 10);

    public static final WrenchableBlock FAN_SPLASHING_CATALYST = fanCatalyst("fan_splashing_catalyst", 0);

    public static final WrenchableBlock FAN_HAUNTING_CATALYST = fanCatalyst("fan_haunting_catalyst", 5);

    public static final WrenchableBlock FAN_FREEZING_CATALYST = fanCatalyst("fan_freezing_catalyst", 0,
            () -> Mods.GARNISHED.isLoaded() || Mods.DREAMS_DESIRES.isLoaded() || Mods.DRAGONS_PLUS.isLoaded());

    public static final WrenchableBlock FAN_SEETHING_CATALYST = fanCatalyst("fan_seething_catalyst", 12,
            () -> Mods.DREAMS_DESIRES.isLoaded());

    public static final WrenchableBlock FAN_SANDING_CATALYST = fanCatalyst("fan_sanding_catalyst", 0,
            () -> Mods.DREAMS_DESIRES.isLoaded() || Mods.DRAGONS_PLUS.isLoaded());

    public static final WrenchableBlock FAN_ENRICHED_CATALYST = fanCatalyst("fan_enriched_catalyst", 13,
            () -> Mods.NUCLEAR.isLoaded());

    public static final WrenchableBlock FAN_ENDING_CATALYST_DRAGONS_BREATH =
            fanCatalyst("fan_ending_catalyst_dragons_breath", 15, () -> Mods.DRAGONS_PLUS.isLoaded());

    public static final FanCatalystRotatingHeadBlock FAN_ENDING_CATALYST_DRAGON_HEAD = condition(dependent(
            registerBlock("fan_ending_catalyst_dragon_head",
                    properties -> new FanCatalystRotatingHeadBlock(properties,
                            () -> CCBlockEntityTypes.FAN_ENDING_CATALYST_DRAGON_HEAD),
                    fanCatalystProperties(0)),
            EMPTY_FAN_CATALYST), () -> Mods.DRAGONS_PLUS.isLoaded());

    public static final WrenchableBlock FAN_WITHERING_CATALYST = fanCatalyst("fan_withering_catalyst", 0,
            () -> false);

    public static final WrenchableBlock FAN_CHOCOLATE_COATING_CATALYST =
            fanCatalyst("fan_chocolate_coating_catalyst", 0, () -> Mods.MORE_CATALYSTS.isLoaded());

    public static final WrenchableBlock FAN_HONEY_COATING_CATALYST =
            fanCatalyst("fan_honey_coating_catalyst", 0, () -> Mods.MORE_CATALYSTS.isLoaded());

    public static final FanCatalystRotatingHeadBlock FAN_EXPLODING_CATALYST = condition(dependent(
            registerBlock("fan_exploding_catalyst",
                    properties -> new FanCatalystRotatingHeadBlock(properties,
                            () -> CCBlockEntityTypes.FAN_EXPLODING_CATALYST),
                    fanCatalystProperties(0)),
            EMPTY_FAN_CATALYST), () -> Mods.MORE_CATALYSTS.isLoaded());

    public static final WrenchableBlock FAN_RESONANCE_CATALYST = fanCatalyst("fan_resonance_catalyst", 3,
            () -> Mods.MORE_CATALYSTS.isLoaded());

    public static final WrenchableBlock FAN_SCULKING_CATALYST = fanCatalyst("fan_sculking_catalyst", 4,
            () -> Mods.MORE_CATALYSTS.isLoaded());

    public static final WrenchableBlock FAN_PURIFYING_CATALYST = fanCatalyst("fan_purifying_catalyst", 14,
            () -> Mods.MORE_CATALYSTS.isLoaded());

    public static final WrenchableBlock FAN_TRANSMUTATION_CATALYST = fanCatalyst("fan_transmutation_catalyst", 10,
            () -> Mods.SHIMMER.isLoaded());

    public static final WrenchableBlock FAN_GLOOMING_CATALYST = fanCatalyst("fan_glooming_catalyst", 10,
            () -> Mods.SHIMMER.isLoaded());

    public static final WrenchableBlock FAN_SOUL_STRIPPING_CATALYST = fanCatalyst("fan_soul_stripping_catalyst", 0,
            () -> Mods.NETHER_INDUSTRY.isLoaded());

    public static final Map<DyeColor, WrenchableBlock> FAN_DYEING_CATALYSTS = new TreeMap<>();

    static {
        for (DyeColor color : DyeColor.values()) {
            String namespace = DyeDepotCompat.getColorNamespace(color);
            boolean isVanilla = namespace.equals(Identifier.DEFAULT_NAMESPACE);
            WrenchableBlock block = registerBlock(
                    (isVanilla ? "" : (namespace + "_")) + color.getName() + "_fan_dyeing_catalyst",
                    WrenchableBlock::new, fanCatalystProperties(0));
            dependent(block, EMPTY_FAN_CATALYST);
            condition(block, () -> (Mods.DRAGONS_PLUS.isLoaded() || Mods.GARNISHED.isLoaded())
                    && (isVanilla || Mods.DYE_DEPOT.isLoaded()));
            FAN_DYEING_CATALYSTS.put(color, block);
        }
    }

    public static final ItemSiloBlock ITEM_SILO = toggle(
            registerBlock("item_silo", ItemSiloBlock::new,
                    Properties.ofFullCopy(Blocks.GOLD_BLOCK).mapColor(MapColor.TERRACOTTA_BLUE)
                            .sound(SoundType.NETHERITE_BLOCK).explosionResistance(1200),
                    ItemSiloItem::new, new Item.Properties()),
            FeatureCategory.LOGISTICS);

    public static final FluidVesselBlock FLUID_VESSEL = toggle(
            registerBlock("fluid_vessel", FluidVesselBlock::regular,
                    Properties.ofFullCopy(Blocks.COPPER_BLOCK.weathering().unaffected()).noOcclusion()
                            .isRedstoneConductor((state, level, pos) -> true),
                    FluidVesselItem::new, new Item.Properties()),
            FeatureCategory.LOGISTICS);

    public static final FluidVesselBlock CREATIVE_FLUID_VESSEL = dependent(
            registerBlock("creative_fluid_vessel", FluidVesselBlock::creative,
                    Properties.ofFullCopy(Blocks.COPPER_BLOCK.weathering().unaffected()).noOcclusion()
                            .mapColor(MapColor.COLOR_PURPLE),
                    FluidVesselItem::new, new Item.Properties().rarity(Rarity.EPIC)),
            FLUID_VESSEL);

    public static final InventoryAccessPortBlock INVENTORY_ACCESS_PORT = toggle(
            registerBlock("inventory_access_port", InventoryAccessPortBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).mapColor(MapColor.TERRACOTTA_BROWN).noOcclusion()),
            FeatureCategory.LOGISTICS);

    public static final InventoryBridgeBlock INVENTORY_BRIDGE = toggle(
            registerBlock("inventory_bridge", InventoryBridgeBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).mapColor(MapColor.TERRACOTTA_BROWN).noOcclusion()),
            FeatureCategory.LOGISTICS);

    public static final BrassChuteBlock BRASS_CHUTE = toggle(
            registerBlock("brass_chute", BrassChuteBlock::new,
                    Properties.ofFullCopy(Blocks.GOLD_BLOCK).mapColor(MapColor.TERRACOTTA_YELLOW)
                            .sound(SoundType.NETHERITE_BLOCK).noOcclusion()
                            .isSuffocating((state, level, pos) -> false),
                    ChuteItem::new, new Item.Properties()),
            FeatureCategory.LOGISTICS);

    public static final DashboardBlock DASHBOARD = toggle(
            registerBlock("dashboard", DashboardBlock::new,
                    Properties.ofFullCopy(Blocks.ANDESITE).mapColor(MapColor.PODZOL)),
            FeatureCategory.KINETIC);

    public static final CopycatSlabBlock COPYCAT_SLAB = toggle(
            registerBlock("copycat_slab", CopycatSlabBlock::new, copycatProperties()),
            FeatureCategory.COPYCATS);

    public static final CopycatBlockBlock COPYCAT_BLOCK = toggle(
            registerBlock("copycat_block", CopycatBlockBlock::new, copycatProperties()),
            FeatureCategory.COPYCATS);

    public static final CopycatBeamBlock COPYCAT_BEAM = toggle(
            registerBlock("copycat_beam", CopycatBeamBlock::new, copycatProperties()),
            FeatureCategory.COPYCATS);

    public static final CopycatVerticalStepBlock COPYCAT_VERTICAL_STEP = toggle(
            registerBlock("copycat_vertical_step", CopycatVerticalStepBlock::new, copycatProperties()),
            FeatureCategory.COPYCATS);

    public static final CopycatStairsBlock COPYCAT_STAIRS = toggle(
            registerBlock("copycat_stairs", CopycatStairsBlock::new, copycatProperties()),
            FeatureCategory.COPYCATS);

    public static final WrappedStairsBlock WRAPPED_COPYCAT_STAIRS = registerBlockNoItem("wrapped_copycat_stairs",
            p -> new WrappedStairsBlock(Blocks.STONE.defaultBlockState(), p),
            Properties.ofFullCopy(Blocks.STONE_STAIRS));

    static {
        CopycatStairsBlock.stairs = WRAPPED_COPYCAT_STAIRS;
    }

    public static final CopycatFenceBlock COPYCAT_FENCE = toggle(
            registerBlock("copycat_fence", CopycatFenceBlock::new, copycatProperties()),
            FeatureCategory.COPYCATS);

    public static final WrappedFenceBlock WRAPPED_COPYCAT_FENCE = registerBlockNoItem("wrapped_copycat_fence",
            WrappedFenceBlock::new, Properties.ofFullCopy(Blocks.OAK_FENCE));

    static {
        CopycatFenceBlock.fence = WRAPPED_COPYCAT_FENCE;
    }

    public static final CopycatWallBlock COPYCAT_WALL = toggle(
            registerBlock("copycat_wall", CopycatWallBlock::new, copycatProperties().forceSolidOn()),
            FeatureCategory.COPYCATS);

    public static final WrappedWallBlock WRAPPED_COPYCAT_WALL = registerBlockNoItem("wrapped_copycat_wall",
            WrappedWallBlock::new, Properties.ofFullCopy(Blocks.COBBLESTONE_WALL));

    static {
        CopycatWallBlock.wall = WRAPPED_COPYCAT_WALL;
    }

    public static final CopycatFenceGateBlock COPYCAT_FENCE_GATE = toggle(
            registerBlock("copycat_fence_gate", CopycatFenceGateBlock::new, copycatProperties().forceSolidOn()),
            FeatureCategory.COPYCATS);

    public static final WrappedFenceGateBlock WRAPPED_COPYCAT_FENCE_GATE = registerBlockNoItem(
            "wrapped_copycat_fence_gate", p -> new WrappedFenceGateBlock(WoodType.OAK, p),
            Properties.ofFullCopy(Blocks.OAK_FENCE_GATE));

    static {
        CopycatFenceGateBlock.fenceGate = WRAPPED_COPYCAT_FENCE_GATE;
    }

    public static final CopycatBoardBlock COPYCAT_BOARD = toggle(
            registerBlock("copycat_board", CopycatBoardBlock::new, copycatProperties()),
            FeatureCategory.COPYCATS);

    public static void register() {
    }

    private static Properties fanCatalystProperties(int lightLevel) {
        return Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .mapColor(MapColor.TERRACOTTA_YELLOW)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .lightLevel(state -> lightLevel)
                .isRedstoneConductor((state, level, pos) -> false);
    }

    private static Properties copycatProperties() {
        return Properties.ofFullCopy(Blocks.GOLD_BLOCK)
                .noOcclusion()
                .mapColor(MapColor.NONE)
                .isValidSpawn((state, level, pos, type) -> false)
                .emissiveRendering(CopycatBlock::hasEmissiveLighting);
    }

    private static WrenchableBlock fanCatalyst(String name, int lightLevel) {
        return dependent(registerBlock(name, WrenchableBlock::new, fanCatalystProperties(lightLevel)),
                EMPTY_FAN_CATALYST);
    }

    private static WrenchableBlock fanCatalyst(String name, int lightLevel, Supplier<Boolean> condition) {
        return condition(fanCatalyst(name, lightLevel), condition);
    }

    private static <T extends Block> T toggle(T block, FeatureCategory... categories) {
        FeatureToggle.register(BuiltInRegistries.BLOCK.getKey(block), categories);
        return block;
    }

    private static <T extends Block> T dependent(T block, Block dependency, FeatureCategory... categories) {
        Identifier key = BuiltInRegistries.BLOCK.getKey(block);
        Identifier dependencyKey = BuiltInRegistries.BLOCK.getKey(dependency);
        if (categories.length == 0) {
            FeatureToggle.registerDependent(key, dependencyKey);
        } else {
            FeatureToggle.registerDependent(key, dependencyKey, categories);
        }
        return block;
    }

    private static <T extends Block> T condition(T block, Supplier<Boolean> condition) {
        FeatureToggle.addCondition(BuiltInRegistries.BLOCK.getKey(block), condition);
        return block;
    }

    private static <T extends Block & LinkedTransmitterBlock> T linkedTransmitter(T block) {
        LinkedTransmitterItem.MODULE_BLOCKS.add(block);
        PreciseItemUseOverrides.addBlock(block);
        return block;
    }

    private static <T extends Block> T registerBlock(String name, Function<Properties, T> factory, Properties properties) {
        return registerBlock(name, factory, properties, BlockItem::new, new Item.Properties());
    }

    private static <T extends Block> T registerBlock(String name, Function<Properties, T> factory, Properties properties,
                                                     BiFunction<? super T, Item.Properties, ? extends BlockItem> itemFactory,
                                                     Item.Properties itemProperties) {
        T block = registerBlockNoItem(name, factory, properties);
        Identifier id = asResource(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        BlockItem item = itemFactory.apply(block, itemProperties.setId(key).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, id, item);
        item.registerBlocks(Item.BY_BLOCK, item);
        return block;
    }

    private static <T extends Block> T registerBlockNoItem(String name, Function<Properties, T> factory, Properties properties) {
        Identifier id = asResource(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        T block = factory.apply(properties.setId(key));
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        return block;
    }

    private static Identifier asResource(String path) {
        return Identifier.fromNamespaceAndPath(CreateConnected.MODID, path);
    }
}
