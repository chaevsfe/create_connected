package com.hlysine.create_connected.foundation.advancement;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.LinkedList;
import java.util.List;

public class CCTriggers {

    private static final List<SimpleCCTrigger> triggers = new LinkedList<>();

    public static SimpleCCTrigger addSimple(String id) {
        SimpleCCTrigger trigger = new SimpleCCTrigger(id);
        triggers.add(trigger);
        return trigger;
    }

    public static void register() {
        triggers.forEach(trigger -> Registry.register(BuiltInRegistries.TRIGGER_TYPES, trigger.getId(), trigger));
    }
}
