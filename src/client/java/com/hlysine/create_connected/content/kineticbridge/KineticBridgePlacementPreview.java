package com.hlysine.create_connected.content.kineticbridge;

import com.hlysine.create_connected.ConnectedClientLang;
import com.zurrtum.create.catnip.data.Pair;
import com.zurrtum.create.client.catnip.outliner.Outliner;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.AABB;

public class KineticBridgePlacementPreview {

    public static void register() {
        KineticBridgeBlockItem.setPlacementFailedHandler(KineticBridgePlacementPreview::showBounds);
    }

    private static void showBounds(BlockPlaceContext context) {
        if (!(context.getPlayer() instanceof LocalPlayer localPlayer))
            return;
        if (!(context.getItemInHand().getItem() instanceof KineticBridgeBlockItem item))
            return;
        if (!(item.getBlock() instanceof KineticBridgeBlock bridge))
            return;

        BlockPos pos = context.getClickedPos();
        Direction facing = bridge.getDirectionForPlacement(context);
        AABB bounds = new AABB(pos).expandTowards(
                facing.getUnitVec3i().getX(),
                facing.getUnitVec3i().getY(),
                facing.getUnitVec3i().getZ()
        );

        Outliner.getInstance().showAABB(Pair.of("kinetic_bridge", pos), bounds).colored(0xFF_ff5d6c);
        ConnectedClientLang.translate("kinetic_bridge.not_enough_space")
                .color(0xFF_ff5d6c)
                .sendStatus(localPlayer);
    }
}
