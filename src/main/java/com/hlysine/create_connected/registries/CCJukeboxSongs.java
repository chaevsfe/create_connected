package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

public class CCJukeboxSongs {

    public static final ResourceKey<JukeboxSong> INTERLUDE = key("interlude");

    public static final ResourceKey<JukeboxSong> ELEVATOR = key("elevator");

    private static ResourceKey<JukeboxSong> key(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, CreateConnected.asResource(name));
    }
}
