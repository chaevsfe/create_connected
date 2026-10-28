package com.hlysine.create_connected.compat;

import com.hlysine.create_connected.CreateConnected;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.network.chat.Component;

public class CreateFlyPacks {

    public static final String CHAIN_COGWHEEL_PACK = "fly_6092_chain_cogwheel";

    public static void register() {
        if (!createAtLeast("6.0.9-2"))
            return;
        FabricLoader.getInstance().getModContainer(CreateConnected.MODID).ifPresent(container ->
                ResourceLoader.registerBuiltinPack(
                        CreateConnected.asResource(CHAIN_COGWHEEL_PACK),
                        container,
                        Component.literal("Create Fly: Connected chain cogwheel for Create Fly 6.0.9-2"),
                        PackActivationType.ALWAYS_ENABLED
                ));
    }

    public static boolean createAtLeast(String version) {
        try {
            VersionPredicate predicate = VersionPredicate.parse(">=" + version);
            return FabricLoader.getInstance().getModContainer("create")
                    .map(ModContainer::getMetadata)
                    .map(metadata -> predicate.test(metadata.getVersion()))
                    .orElse(false);
        } catch (VersionParsingException e) {
            return false;
        }
    }
}
