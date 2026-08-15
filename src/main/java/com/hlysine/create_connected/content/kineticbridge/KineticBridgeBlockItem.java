package com.hlysine.create_connected.content.kineticbridge;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class KineticBridgeBlockItem extends BlockItem {

    private static Consumer<BlockPlaceContext> placementFailedHandler = ctx -> {
    };

    public static void setPlacementFailedHandler(Consumer<BlockPlaceContext> handler) {
        placementFailedHandler = handler;
    }

    public KineticBridgeBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        InteractionResult result = super.place(ctx);
        if (result == InteractionResult.FAIL && ctx.getLevel().isClientSide())
            placementFailedHandler.accept(ctx);
        return result;
    }
}
