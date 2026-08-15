package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

import java.util.function.UnaryOperator;

public class CCDataComponents {
    public static final DataComponentType<Double> KINETIC_BATTERY_CHARGE = register(
            "kinetic_battery_charge",
            builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE)
    );

    public static void register() {
    }

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(CreateConnected.MODID, name),
                builder.apply(DataComponentType.builder()).build()
        );
    }
}
