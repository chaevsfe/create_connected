package com.hlysine.create_connected.mixin.nestedschematics;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.CServer;
import com.zurrtum.create.client.content.schematics.client.ClientSchematicLoader;
import com.zurrtum.create.foundation.utility.CreatePaths;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Mixin(value = ClientSchematicLoader.class, remap = false)
public class ClientSchematicLoaderMixin {

    @Shadow
    @Final
    private List<Component> availableSchematics;

    @Inject(
            at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"),
            method = "refresh()V"
    )
    private void create_connected$refresh(CallbackInfo ci) {
        create_connected$searchInSubfolder(CreatePaths.SCHEMATICS_DIR, 0);
    }

    @Unique
    private void create_connected$searchInSubfolder(Path folder, int depth) {
        boolean canRecurse = depth < CServer.SchematicsNestingDepth.get();
        Path base = CreatePaths.SCHEMATICS_DIR;
        try (Stream<Path> entries = Files.list(folder)) {
            entries.forEach(path -> {
                if (Files.isDirectory(path)) {
                    if (canRecurse && (depth != 0 || !path.getFileName().toString().equals("uploaded")))
                        create_connected$searchInSubfolder(path, depth + 1);
                } else if (depth != 0 && path.getFileName().toString().endsWith(".nbt")) {
                    availableSchematics.add(Component.literal(base.relativize(path).toString().replace('\\', '/')));
                }
            });
        } catch (NoSuchFileException ignored) {
        } catch (IOException e) {
            CreateConnected.LOGGER.error("Failed to scan nested schematic folders", e);
        }
    }
}
