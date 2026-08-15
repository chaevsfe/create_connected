package com.hlysine.create_connected;

import com.zurrtum.create.client.catnip.lang.LangBuilder;
import com.zurrtum.create.client.catnip.lang.LangNumberFormat;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ConnectedClientLang {

    public static LangBuilder builder() {
        return new LangBuilder(CreateConnected.MODID);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

    public static LangBuilder text(String text) {
        return builder().text(text);
    }

    public static LangBuilder number(double d) {
        return builder().text(LangNumberFormat.format(d));
    }

    public static List<Component> translatedOptions(String prefix, String... keys) {
        List<Component> result = new ArrayList<>(keys.length);
        for (String key : keys)
            result.add(ConnectedLang.translateDirect((prefix != null ? prefix + "." : "") + key));
        return result;
    }
}
