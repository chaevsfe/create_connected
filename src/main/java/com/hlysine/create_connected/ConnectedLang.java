package com.hlysine.create_connected;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;

public class ConnectedLang {

    public static String asId(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public static String translationKey(String key) {
        return CreateConnected.MODID + "." + key;
    }

    public static MutableComponent translateDirect(String key, Object... args) {
        return Component.translatable(translationKey(key), args);
    }
}
