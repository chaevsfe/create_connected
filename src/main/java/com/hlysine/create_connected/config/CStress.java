package com.hlysine.create_connected.config;

import com.hlysine.create_connected.CreateConnected;
import com.zurrtum.create.catnip.config.Builder;
import com.zurrtum.create.catnip.config.ConfigBase;
import com.zurrtum.create.catnip.config.DoubleRawValue;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

public class CStress extends ConfigBase {
    private static final int VERSION = 1;

    private static final Object2DoubleMap<Identifier> DEFAULT_IMPACTS = new Object2DoubleOpenHashMap<>();
    private static final Object2DoubleMap<Identifier> DEFAULT_CAPACITIES = new Object2DoubleOpenHashMap<>();

    protected final Map<Identifier, DoubleRawValue> capacities = new HashMap<>();
    protected final Map<Identifier, DoubleRawValue> impacts = new HashMap<>();

    public static void registerDefaults() {
        setNoImpact(CreateConnected.asResource("encased_chain_cogwheel"));
        setCapacity(CreateConnected.asResource("crank_wheel"), 8.0);
        setCapacity(CreateConnected.asResource("large_crank_wheel"), 8.0);
        setNoImpact(CreateConnected.asResource("parallel_gearbox"));
        setNoImpact(CreateConnected.asResource("six_way_gearbox"));
        setNoImpact(CreateConnected.asResource("overstress_clutch"));
        setNoImpact(CreateConnected.asResource("shear_pin"));
        setNoImpact(CreateConnected.asResource("inverted_clutch"));
        setNoImpact(CreateConnected.asResource("inverted_gearshift"));
        setNoImpact(CreateConnected.asResource("centrifugal_clutch"));
        setNoImpact(CreateConnected.asResource("freewheel_clutch"));
        setNoImpact(CreateConnected.asResource("brass_gearbox"));
        setNoImpact(CreateConnected.asResource("brake"));
        setCapacity(CreateConnected.asResource("kinetic_battery"), 32.0);
        setImpact(CreateConnected.asResource("kinetic_battery"), 64.0);
    }

    @Override
    public void registerAll(Builder builder) {
        builder.comment(Comments.su, Comments.impact).push("impact");
        DEFAULT_IMPACTS.forEach((id, value) -> this.impacts.put(id, builder.define(id.getPath(), value)));
        builder.pop();

        builder.comment(Comments.su, Comments.capacity).push("capacity");
        DEFAULT_CAPACITIES.forEach((id, value) -> this.capacities.put(id, builder.define(id.getPath(), value)));
        builder.pop();
    }

    @Override
    public String getName() {
        return "stressValues.v" + VERSION;
    }

    public DoubleSupplier getImpact(Block block) {
        DoubleRawValue value = this.impacts.get(BuiltInRegistries.BLOCK.getKey(block));
        return value == null ? null : value::get;
    }

    public DoubleSupplier getCapacity(Block block) {
        DoubleRawValue value = this.capacities.get(BuiltInRegistries.BLOCK.getKey(block));
        return value == null ? null : value::get;
    }

    public static void setNoImpact(Identifier id) {
        setImpact(id, 0);
    }

    public static void setImpact(Identifier id, double value) {
        assertFromCC(id);
        DEFAULT_IMPACTS.put(id, value);
    }

    public static void setCapacity(Identifier id, double value) {
        assertFromCC(id);
        DEFAULT_CAPACITIES.put(id, value);
    }

    public static void setNoImpact(Block block) {
        setImpact(block, 0);
    }

    public static void setImpact(Block block, double value) {
        setImpact(BuiltInRegistries.BLOCK.getKey(block), value);
    }

    public static void setCapacity(Block block, double value) {
        setCapacity(BuiltInRegistries.BLOCK.getKey(block), value);
    }

    private static void assertFromCC(Identifier id) {
        if (!id.getNamespace().equals(CreateConnected.MODID)) {
            throw new IllegalStateException("Unrelated blocks cannot be added to the config of Create: Connected.");
        }
    }

    private static class Comments {
        static String su = "[in Stress Units]";
        static String impact =
                "Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives.";
        static String capacity = "Configure how much stress a source can accommodate for.";
    }
}
