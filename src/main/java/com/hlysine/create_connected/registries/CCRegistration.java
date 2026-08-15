package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.config.CCConfigs;
import com.hlysine.create_connected.config.CServer;
import com.hlysine.create_connected.content.attributefilter.ItemDamageAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemIdAttribute;
import com.hlysine.create_connected.content.attributefilter.ItemStackCountAttribute;
import com.hlysine.create_connected.content.contraption.jukebox.JukeboxInteractionBehaviour;
import com.hlysine.create_connected.content.contraption.jukebox.JukeboxMovementBehaviour;
import com.hlysine.create_connected.content.contraption.menu.MenuBlockInteractionBehaviour;
import com.hlysine.create_connected.content.contraption.noteblock.NoteBlockInteractionBehaviour;
import com.hlysine.create_connected.content.contraption.noteblock.NoteBlockMovementBehaviour;
import com.hlysine.create_connected.content.copycat.fence.CopycatFenceBlock;
import com.hlysine.create_connected.content.copycat.fencegate.CopycatFenceGateBlock;
import com.hlysine.create_connected.content.copycat.stairs.CopycatStairsBlock;
import com.hlysine.create_connected.content.copycat.wall.CopycatWallBlock;
import com.hlysine.create_connected.content.dashboard.DashboardDisplayTarget;
import com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock;
import com.hlysine.create_connected.content.fluidvessel.FluidVesselMountedStorageType;
import com.hlysine.create_connected.content.itemsilo.ItemSiloBlock;
import com.hlysine.create_connected.content.itemsilo.ItemSiloMountedStorageType;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryDisplaySource;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryInteractionPoint;
import com.hlysine.create_connected.content.kineticbridge.KineticBridgeBlock;
import com.hlysine.create_connected.content.kineticbridge.KineticBridgeDestinationBlock;
import com.zurrtum.create.api.behaviour.display.DisplaySource;
import com.zurrtum.create.api.behaviour.display.DisplayTarget;
import com.zurrtum.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.zurrtum.create.api.behaviour.movement.MovementBehaviour;
import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.api.contraption.BlockMovementChecks;
import com.zurrtum.create.api.contraption.BlockMovementChecks.CheckResult;
import com.zurrtum.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.zurrtum.create.api.contraption.storage.item.MountedItemStorageType;
import com.zurrtum.create.api.registry.CreateRegistries;
import com.zurrtum.create.api.stress.BlockStressValues;
import com.zurrtum.create.content.decoration.encasing.EncasingRegistry;
import com.zurrtum.create.content.fluids.tank.FluidTankMovementBehavior;
import com.zurrtum.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.zurrtum.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.zurrtum.create.content.redstone.displayLink.source.BoilerDisplaySource;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Blocks;

public class CCRegistration {

    public static BoilerDisplaySource BOILER_STATUS;
    public static KineticBatteryDisplaySource KINETIC_BATTERY_SOURCE;

    public static DashboardDisplayTarget DASHBOARD;

    public static ItemSiloMountedStorageType SILO;
    public static FluidVesselMountedStorageType FLUID_VESSEL;

    public static ArmInteractionPointType KINETIC_BATTERY_POINT;

    public static ItemAttributeType MAX_DAMAGE;
    public static ItemAttributeType ID_CONTAINS;
    public static ItemAttributeType STACK_SIZE;

    public static void register() {
        registerStressValues();
        registerMovementBehaviours();
        registerInteractionBehaviours();
        registerDisplaySources();
        registerDisplayTargets();
        registerMountedStorageTypes();
        registerArmInteractionPointTypes();
        registerItemAttributeTypes();
        registerEncasedVariants();
        registerMovementChecks();
        registerWrappedCopycats();
    }

    private static void registerStressValues() {
        BlockStressValues.IMPACTS.registerProvider(block -> {
            CServer server = CCConfigs.server();
            return server == null ? null : server.stressValues.getImpact(block);
        });
        BlockStressValues.CAPACITIES.registerProvider(block -> {
            CServer server = CCConfigs.server();
            return server == null ? null : server.stressValues.getCapacity(block);
        });
        BlockStressValues.setGeneratorSpeed(CCBlocks.CRANK_WHEEL, 32);
        BlockStressValues.setGeneratorSpeed(CCBlocks.LARGE_CRANK_WHEEL, 32);
    }

    private static void registerMovementBehaviours() {
        MovementBehaviour.REGISTRY.register(Blocks.NOTE_BLOCK, new NoteBlockMovementBehaviour());
        MovementBehaviour.REGISTRY.register(Blocks.JUKEBOX, new JukeboxMovementBehaviour());
        MovementBehaviour.REGISTRY.register(CCBlocks.FLUID_VESSEL, new FluidTankMovementBehavior());
    }

    private static void registerInteractionBehaviours() {
        MovingInteractionBehaviour.REGISTRY.register(Blocks.NOTE_BLOCK, new NoteBlockInteractionBehaviour());
        MovingInteractionBehaviour.REGISTRY.register(Blocks.JUKEBOX, new JukeboxInteractionBehaviour());
        if (!Mods.STEAM_N_RAILS.isLoaded()) {
            MovingInteractionBehaviour.REGISTRY.register(Blocks.CRAFTING_TABLE, new MenuBlockInteractionBehaviour());
            MovingInteractionBehaviour.REGISTRY.register(Blocks.STONECUTTER, new MenuBlockInteractionBehaviour());
            MovingInteractionBehaviour.REGISTRY.register(Blocks.GRINDSTONE, new MenuBlockInteractionBehaviour());
            MovingInteractionBehaviour.REGISTRY.register(Blocks.SMITHING_TABLE, new MenuBlockInteractionBehaviour());
            MovingInteractionBehaviour.REGISTRY.register(Blocks.LOOM, new MenuBlockInteractionBehaviour());
            MovingInteractionBehaviour.REGISTRY.register(Blocks.CARTOGRAPHY_TABLE, new MenuBlockInteractionBehaviour());
        }
    }

    private static void registerDisplaySources() {
        BOILER_STATUS = Registry.register(
                CreateRegistries.DISPLAY_SOURCE,
                CreateConnected.asResource("boiler_status"),
                new BoilerDisplaySource());
        DisplaySource.BY_BLOCK.add(CCBlocks.FLUID_VESSEL, BOILER_STATUS);

        KINETIC_BATTERY_SOURCE = Registry.register(
                CreateRegistries.DISPLAY_SOURCE,
                CreateConnected.asResource("kinetic_battery"),
                new KineticBatteryDisplaySource());
        DisplaySource.BY_BLOCK.add(CCBlocks.KINETIC_BATTERY, KINETIC_BATTERY_SOURCE);
    }

    private static void registerDisplayTargets() {
        DASHBOARD = Registry.register(
                CreateRegistries.DISPLAY_TARGET,
                CreateConnected.asResource("dashboard"),
                new DashboardDisplayTarget());
        DisplayTarget.BY_BLOCK.register(CCBlocks.DASHBOARD, DASHBOARD);
    }

    private static void registerMountedStorageTypes() {
        SILO = Registry.register(
                CreateRegistries.MOUNTED_ITEM_STORAGE_TYPE,
                CreateConnected.asResource("silo"),
                new ItemSiloMountedStorageType());
        MountedItemStorageType.REGISTRY.register(CCBlocks.ITEM_SILO, SILO);

        FLUID_VESSEL = Registry.register(
                CreateRegistries.MOUNTED_FLUID_STORAGE_TYPE,
                CreateConnected.asResource("fluid_vessel"),
                new FluidVesselMountedStorageType());
        MountedFluidStorageType.REGISTRY.register(CCBlocks.FLUID_VESSEL, FLUID_VESSEL);
    }

    private static void registerArmInteractionPointTypes() {
        KINETIC_BATTERY_POINT = Registry.register(
                CreateRegistries.ARM_INTERACTION_POINT_TYPE,
                CreateConnected.asResource("kinetic_battery"),
                new KineticBatteryInteractionPoint.Type());
    }

    private static void registerItemAttributeTypes() {
        MAX_DAMAGE = Registry.register(
                CreateRegistries.ITEM_ATTRIBUTE_TYPE,
                CreateConnected.asResource("max_damage"),
                new ItemDamageAttribute.Type());
        ID_CONTAINS = Registry.register(
                CreateRegistries.ITEM_ATTRIBUTE_TYPE,
                CreateConnected.asResource("id_contains"),
                new ItemIdAttribute.Type());
        STACK_SIZE = Registry.register(
                CreateRegistries.ITEM_ATTRIBUTE_TYPE,
                CreateConnected.asResource("stack_size"),
                new ItemStackCountAttribute.Type());
    }

    private static void registerEncasedVariants() {
        EncasingRegistry.addVariant(CCBlocks.CROSS_CONNECTOR, CCBlocks.ANDESITE_ENCASED_CROSS_CONNECTOR);
        EncasingRegistry.addVariant(CCBlocks.CROSS_CONNECTOR, CCBlocks.BRASS_ENCASED_CROSS_CONNECTOR);
    }

    private static void registerMovementChecks() {
        BlockMovementChecks.registerAttachedCheck((state, world, pos, direction) -> {
            if (!(state.getBlock() instanceof KineticBridgeBlock))
                return CheckResult.PASS;
            if (state.getValue(KineticBridgeBlock.FACING) != direction)
                return CheckResult.PASS;
            return CheckResult.SUCCESS;
        });
        BlockMovementChecks.registerBrittleCheck(state -> {
            if (!(state.getBlock() instanceof KineticBridgeBlock))
                return CheckResult.PASS;
            return CheckResult.SUCCESS;
        });
        BlockMovementChecks.registerAttachedCheck((state, world, pos, direction) -> {
            if (!(state.getBlock() instanceof KineticBridgeDestinationBlock))
                return CheckResult.PASS;
            if (state.getValue(KineticBridgeDestinationBlock.FACING).getOpposite() != direction)
                return CheckResult.PASS;
            return CheckResult.SUCCESS;
        });
        BlockMovementChecks.registerAttachedCheck((state, world, pos, direction) -> {
            if (state.getBlock() instanceof ItemSiloBlock)
                return CheckResult.of(ConnectivityHandler.isConnected(world, pos, pos.relative(direction)));
            return CheckResult.PASS;
        });
        BlockMovementChecks.registerAttachedCheck((state, world, pos, direction) -> {
            if (state.getBlock() instanceof FluidVesselBlock)
                return CheckResult.of(ConnectivityHandler.isConnected(world, pos, pos.relative(direction)));
            return CheckResult.PASS;
        });
    }

    private static void registerWrappedCopycats() {
        CopycatStairsBlock.stairs = CCBlocks.WRAPPED_COPYCAT_STAIRS;
        CopycatFenceBlock.fence = CCBlocks.WRAPPED_COPYCAT_FENCE;
        CopycatWallBlock.wall = CCBlocks.WRAPPED_COPYCAT_WALL;
        CopycatFenceGateBlock.fenceGate = CCBlocks.WRAPPED_COPYCAT_FENCE_GATE;
    }
}
