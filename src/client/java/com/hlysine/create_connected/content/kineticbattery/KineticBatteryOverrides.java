package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.CreateConnected;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class KineticBatteryOverrides {

    public static final Identifier ID = CreateConnected.asResource("kinetic_battery_level");

    public static void register() {
        RangeSelectItemModelProperties.ID_MAPPER.put(ID, Level.MAP_CODEC);
    }

    public record Level() implements RangeSelectItemModelProperty {
        public static final MapCodec<Level> MAP_CODEC = MapCodec.unit(new Level());

        @Override
        public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
            return KineticBatteryBlockEntity.getCrudeBatteryLevel(KineticBatteryBlockItem.getBatteryLevel(stack), 5);
        }

        @Override
        public MapCodec<Level> type() {
            return MAP_CODEC;
        }
    }
}
