package com.hlysine.create_connected.config;

import com.hlysine.create_connected.CreateConnected;
import com.zurrtum.create.catnip.config.Builder;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class CCConfigs {

    private static CCommon common;
    private static CServer server;

    public static CCommon common() {
        return common;
    }

    public static CServer server() {
        return server;
    }

    public static CStress stress() {
        return server().stressValues;
    }

    public static <T> Supplier<T> safeGetter(Supplier<T> getter, T defaultValue) {
        return () -> {
            try {
                return getter.get();
            } catch (AssertionError | IllegalStateException | NullPointerException ex) {
                return defaultValue;
            }
        };
    }

    public static void register() {
        FeatureToggle.assertAllRegistered();
        CStress.registerDefaults();

        common = Builder.create(CCommon::new, CreateConnected.MODID, "common", true);
        server = Builder.create(CServer::new, CreateConnected.MODID, "server", true);
    }
}
