package com.hlysine.create_connected.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.Map;
import java.util.function.Supplier;

public class V4536 extends NamespacedSchema {
    public V4536(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    public static void register(DataFixerBuilder builder) {
        Schema schema = builder.addSchema(4536, V4536::new);
        builder.addFixer(new AddNewChoices(schema, "Added Create Connected block entities", References.BLOCK_ENTITY));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
        schema.register(map, "create_connected:copycat", () -> DSL.optionalFields("Material", References.BLOCK_STATE.in(schema)));
        schema.register(map, "create_connected:shear_pin", () -> DSL.optionalFields("Bracket", References.BLOCK_STATE.in(schema)));
        return map;
    }
}
