package com.hlysine.create_connected.content.inventorybridge;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllUnpackingHandlers;
import com.zurrtum.create.api.packager.unpacking.UnpackingHandler;
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import com.zurrtum.create.infrastructure.component.PackageOrderWithCrafts;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum InventoryBridgeUnpackingHandler implements UnpackingHandler {
    INSTANCE;

    @Override
    public boolean unpack(
            Level level,
            BlockPos pos,
            BlockState state,
            Direction side,
            List<ItemStack> items,
            @Nullable PackageOrderWithCrafts orderContext,
            boolean simulate
    ) {
        if (state.getBlock() instanceof InventoryBridgeBlock
                && level.getBlockEntity(pos) instanceof InventoryBridgeBlockEntity bridge) {
            if (bridge.isAttachedNegative()) {
                Direction negativeTarget = InventoryBridgeBlock.getNegativeTarget(state);
                if (unpacksIntoCrafter(level, pos.relative(negativeTarget), negativeTarget, bridge.negativeFilter, items, orderContext))
                    return unpackIntoCrafter(level, pos.relative(negativeTarget), negativeTarget, items, orderContext, simulate);
            }
            if (bridge.isAttachedPositive()) {
                Direction positiveTarget = InventoryBridgeBlock.getPositiveTarget(state);
                if (unpacksIntoCrafter(level, pos.relative(positiveTarget), positiveTarget, bridge.positiveFilter, items, orderContext))
                    return unpackIntoCrafter(level, pos.relative(positiveTarget), positiveTarget, items, orderContext, simulate);
            }
        }
        return AllUnpackingHandlers.DEFAULT.unpack(level, pos, state, side, items, orderContext, simulate);
    }

    private static boolean unpacksIntoCrafter(
            Level level,
            BlockPos targetPos,
            Direction targetDirection,
            ServerFilteringBehaviour filter,
            List<ItemStack> items,
            @Nullable PackageOrderWithCrafts orderContext
    ) {
        if (!level.getBlockState(targetPos).is(AllBlocks.MECHANICAL_CRAFTER))
            return false;
        for (ItemStack item : items) {
            if (!filter.test(item))
                return false;
        }
        return unpackIntoCrafter(level, targetPos, targetDirection, copyItems(items), orderContext, true);
    }

    private static boolean unpackIntoCrafter(
            Level level,
            BlockPos targetPos,
            Direction targetDirection,
            List<ItemStack> items,
            @Nullable PackageOrderWithCrafts orderContext,
            boolean simulate
    ) {
        return AllUnpackingHandlers.MECHANICAL_CRAFTER.unpack(
                level, targetPos, level.getBlockState(targetPos), targetDirection, items, orderContext, simulate);
    }

    private static List<ItemStack> copyItems(List<ItemStack> items) {
        List<ItemStack> copy = new ArrayList<>(items.size());
        for (ItemStack item : items) {
            copy.add(item.copy());
        }
        return copy;
    }
}
