package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.content.brake.BrakeBlockEntity;
import com.hlysine.create_connected.content.brasschute.BrassChuteBlockEntity;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxBlockEntity;
import com.hlysine.create_connected.content.centrifugalclutch.CentrifugalClutchBlockEntity;
import com.hlysine.create_connected.content.crankwheel.CrankWheelBlockEntity;
import com.hlysine.create_connected.content.dashboard.DashboardBlockEntity;
import com.hlysine.create_connected.content.fancatalyst.FanCatalystRotatingHeadBlockEntity;
import com.hlysine.create_connected.content.fluidvessel.CreativeFluidVesselBlockEntity;
import com.hlysine.create_connected.content.fluidvessel.FluidVesselBlockEntity;
import com.hlysine.create_connected.content.freewheelclutch.FreewheelClutchBlockEntity;
import com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlockEntity;
import com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlockEntity;
import com.hlysine.create_connected.content.invertedclutch.InvertedClutchBlockEntity;
import com.hlysine.create_connected.content.invertedgearshift.InvertedGearshiftBlockEntity;
import com.hlysine.create_connected.content.itemsilo.ItemSiloBlockEntity;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockEntity;
import com.hlysine.create_connected.content.kineticbridge.KineticBridgeBlockEntity;
import com.hlysine.create_connected.content.kineticbridge.KineticBridgeDestinationBlockEntity;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedAnalogLeverBlockEntity;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterBlockEntity;
import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlockEntity;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxBlockEntity;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import com.hlysine.create_connected.content.shearpin.ShearPinBlockEntity;
import com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxBlockEntity;
import com.zurrtum.create.content.decoration.copycat.CopycatBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class CCBlockEntityTypes {

    public static final BlockEntityType<SimpleKineticBlockEntity> ENCASED_CHAIN_COGWHEEL = register(
            "encased_chain_cogwheel",
            (pos, state) -> new SimpleKineticBlockEntity(CCBlockEntityTypes.ENCASED_CHAIN_COGWHEEL, pos, state),
            CCBlocks.ENCASED_CHAIN_COGWHEEL);

    public static final BlockEntityType<CrankWheelBlockEntity> CRANK_WHEEL = register(
            "crank_wheel",
            (pos, state) -> new CrankWheelBlockEntity(CCBlockEntityTypes.CRANK_WHEEL, pos, state),
            CCBlocks.CRANK_WHEEL, CCBlocks.LARGE_CRANK_WHEEL);

    public static final BlockEntityType<ParallelGearboxBlockEntity> PARALLEL_GEARBOX = register(
            "parallel_gearbox",
            (pos, state) -> new ParallelGearboxBlockEntity(CCBlockEntityTypes.PARALLEL_GEARBOX, pos, state),
            CCBlocks.PARALLEL_GEARBOX);

    public static final BlockEntityType<SixWayGearboxBlockEntity> SIX_WAY_GEARBOX = register(
            "six_way_gearbox",
            (pos, state) -> new SixWayGearboxBlockEntity(CCBlockEntityTypes.SIX_WAY_GEARBOX, pos, state),
            CCBlocks.SIX_WAY_GEARBOX);

    public static final BlockEntityType<OverstressClutchBlockEntity> OVERSTRESS_CLUTCH = register(
            "overstress_clutch",
            (pos, state) -> new OverstressClutchBlockEntity(CCBlockEntityTypes.OVERSTRESS_CLUTCH, pos, state),
            CCBlocks.OVERSTRESS_CLUTCH);

    public static final BlockEntityType<ShearPinBlockEntity> SHEAR_PIN = register(
            "shear_pin",
            (pos, state) -> new ShearPinBlockEntity(CCBlockEntityTypes.SHEAR_PIN, pos, state),
            CCBlocks.SHEAR_PIN);

    public static final BlockEntityType<InvertedClutchBlockEntity> INVERTED_CLUTCH = register(
            "inverted_clutch",
            (pos, state) -> new InvertedClutchBlockEntity(CCBlockEntityTypes.INVERTED_CLUTCH, pos, state),
            CCBlocks.INVERTED_CLUTCH);

    public static final BlockEntityType<InvertedGearshiftBlockEntity> INVERTED_GEARSHIFT = register(
            "inverted_gearshift",
            (pos, state) -> new InvertedGearshiftBlockEntity(CCBlockEntityTypes.INVERTED_GEARSHIFT, pos, state),
            CCBlocks.INVERTED_GEARSHIFT);

    public static final BlockEntityType<CentrifugalClutchBlockEntity> CENTRIFUGAL_CLUTCH = register(
            "centrifugal_clutch",
            (pos, state) -> new CentrifugalClutchBlockEntity(CCBlockEntityTypes.CENTRIFUGAL_CLUTCH, pos, state),
            CCBlocks.CENTRIFUGAL_CLUTCH);

    public static final BlockEntityType<FreewheelClutchBlockEntity> FREEWHEEL_CLUTCH = register(
            "freewheel_clutch",
            (pos, state) -> new FreewheelClutchBlockEntity(CCBlockEntityTypes.FREEWHEEL_CLUTCH, pos, state),
            CCBlocks.FREEWHEEL_CLUTCH);

    public static final BlockEntityType<KineticBridgeBlockEntity> KINETIC_BRIDGE = register(
            "kinetic_bridge",
            (pos, state) -> new KineticBridgeBlockEntity(CCBlockEntityTypes.KINETIC_BRIDGE, pos, state),
            CCBlocks.KINETIC_BRIDGE);

    public static final BlockEntityType<KineticBridgeDestinationBlockEntity> KINETIC_BRIDGE_DESTINATION = register(
            "kinetic_bridge_destination",
            (pos, state) -> new KineticBridgeDestinationBlockEntity(CCBlockEntityTypes.KINETIC_BRIDGE_DESTINATION, pos, state),
            CCBlocks.KINETIC_BRIDGE_DESTINATION);

    public static final BlockEntityType<BrassGearboxBlockEntity> BRASS_GEARBOX = register(
            "brass_gearbox",
            (pos, state) -> new BrassGearboxBlockEntity(CCBlockEntityTypes.BRASS_GEARBOX, pos, state),
            CCBlocks.BRASS_GEARBOX);

    public static final BlockEntityType<BrakeBlockEntity> BRAKE = register(
            "brake",
            (pos, state) -> new BrakeBlockEntity(CCBlockEntityTypes.BRAKE, pos, state),
            CCBlocks.BRAKE);

    public static final BlockEntityType<KineticBatteryBlockEntity> KINETIC_BATTERY = register(
            "kinetic_battery",
            (pos, state) -> new KineticBatteryBlockEntity(CCBlockEntityTypes.KINETIC_BATTERY, pos, state),
            CCBlocks.KINETIC_BATTERY);

    public static final BlockEntityType<ItemSiloBlockEntity> ITEM_SILO = register(
            "item_silo",
            (pos, state) -> new ItemSiloBlockEntity(CCBlockEntityTypes.ITEM_SILO, pos, state),
            CCBlocks.ITEM_SILO);

    public static final BlockEntityType<FluidVesselBlockEntity> FLUID_VESSEL = register(
            "fluid_vessel",
            (pos, state) -> new FluidVesselBlockEntity(CCBlockEntityTypes.FLUID_VESSEL, pos, state),
            CCBlocks.FLUID_VESSEL);

    public static final BlockEntityType<CreativeFluidVesselBlockEntity> CREATIVE_FLUID_VESSEL = register(
            "creative_fluid_vessel",
            (pos, state) -> new CreativeFluidVesselBlockEntity(CCBlockEntityTypes.CREATIVE_FLUID_VESSEL, pos, state),
            CCBlocks.CREATIVE_FLUID_VESSEL);

    public static final BlockEntityType<InventoryAccessPortBlockEntity> INVENTORY_ACCESS_PORT = register(
            "inventory_access_port",
            (pos, state) -> new InventoryAccessPortBlockEntity(CCBlockEntityTypes.INVENTORY_ACCESS_PORT, pos, state),
            CCBlocks.INVENTORY_ACCESS_PORT);

    public static final BlockEntityType<InventoryBridgeBlockEntity> INVENTORY_BRIDGE = register(
            "inventory_bridge",
            (pos, state) -> new InventoryBridgeBlockEntity(CCBlockEntityTypes.INVENTORY_BRIDGE, pos, state),
            CCBlocks.INVENTORY_BRIDGE);

    public static final BlockEntityType<SequencedPulseGeneratorBlockEntity> SEQUENCED_PULSE_GENERATOR = register(
            "sequenced_pulse_generator",
            (pos, state) -> new SequencedPulseGeneratorBlockEntity(CCBlockEntityTypes.SEQUENCED_PULSE_GENERATOR, pos, state),
            CCBlocks.SEQUENCED_PULSE_GENERATOR);

    public static final BlockEntityType<LinkedTransmitterBlockEntity> LINKED_TRANSMITTER = register(
            "linked_transmitter",
            (pos, state) -> new LinkedTransmitterBlockEntity(CCBlockEntityTypes.LINKED_TRANSMITTER, pos, state),
            linkedTransmitterBlocks());

    public static final BlockEntityType<LinkedAnalogLeverBlockEntity> LINKED_ANALOG_LEVER = register(
            "linked_analog_lever",
            (pos, state) -> new LinkedAnalogLeverBlockEntity(CCBlockEntityTypes.LINKED_ANALOG_LEVER, pos, state),
            CCBlocks.LINKED_ANALOG_LEVER);

    public static final BlockEntityType<BrassChuteBlockEntity> BRASS_CHUTE = register(
            "brass_chute",
            (pos, state) -> new BrassChuteBlockEntity(CCBlockEntityTypes.BRASS_CHUTE, pos, state),
            CCBlocks.BRASS_CHUTE);

    public static final BlockEntityType<DashboardBlockEntity> DASHBOARD = register(
            "dashboard",
            (pos, state) -> new DashboardBlockEntity(CCBlockEntityTypes.DASHBOARD, pos, state),
            CCBlocks.DASHBOARD);

    public static final BlockEntityType<CopycatBlockEntity> COPYCAT = register(
            "copycat",
            CCCopycatBlockEntity::new,
            CCBlocks.COPYCAT_BLOCK,
            CCBlocks.COPYCAT_SLAB,
            CCBlocks.COPYCAT_BEAM,
            CCBlocks.COPYCAT_VERTICAL_STEP,
            CCBlocks.COPYCAT_STAIRS,
            CCBlocks.COPYCAT_FENCE,
            CCBlocks.COPYCAT_FENCE_GATE,
            CCBlocks.COPYCAT_WALL,
            CCBlocks.COPYCAT_BOARD);

    public static final BlockEntityType<FanCatalystRotatingHeadBlockEntity> FAN_ENDING_CATALYST_DRAGON_HEAD = register(
            "fan_ending_catalyst_dragon_head",
            (pos, state) -> new FanCatalystRotatingHeadBlockEntity(CCBlockEntityTypes.FAN_ENDING_CATALYST_DRAGON_HEAD, pos, state),
            CCBlocks.FAN_ENDING_CATALYST_DRAGON_HEAD);

    public static final BlockEntityType<FanCatalystRotatingHeadBlockEntity> FAN_EXPLODING_CATALYST = register(
            "fan_exploding_catalyst",
            (pos, state) -> new FanCatalystRotatingHeadBlockEntity(CCBlockEntityTypes.FAN_EXPLODING_CATALYST, pos, state),
            CCBlocks.FAN_EXPLODING_CATALYST);

    private static Set<Block> linkedTransmitterBlocks() {
        Set<Block> blocks = new HashSet<>(CCBlocks.LINKED_BUTTONS.values());
        blocks.add(CCBlocks.LINKED_LEVER);
        return blocks;
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Block... blocks
    ) {
        return register(name, factory, Set.of(blocks));
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Set<Block> blocks
    ) {
        return Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                CreateConnected.asResource(name),
                new BlockEntityType<>(factory, blocks)
        );
    }

    public static class CCCopycatBlockEntity extends CopycatBlockEntity {
        public CCCopycatBlockEntity(BlockPos pos, BlockState state) {
            super(pos, state);
        }

        @Override
        public BlockEntityType<?> getType() {
            return COPYCAT;
        }

        @SuppressWarnings("deprecation")
        @Override
        public Holder<BlockEntityType<?>> typeHolder() {
            return COPYCAT.builtInRegistryHolder();
        }

        @Override
        public boolean isValidBlockState(BlockState state) {
            return COPYCAT.isValid(state);
        }
    }

    public static void register() {
    }
}
