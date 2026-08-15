package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class CCSoundEvents {

    public static final SoundEvent ELEVATOR_MUSIC = registerSoundEvent("elevator_music");

    public static final SoundEvent INTERLUDE_MUSIC = registerSoundEvent("interlude_music");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = CreateConnected.asResource(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void register() {
    }
}
