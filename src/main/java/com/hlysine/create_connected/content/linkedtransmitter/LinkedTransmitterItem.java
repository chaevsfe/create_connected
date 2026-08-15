package com.hlysine.create_connected.content.linkedtransmitter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class LinkedTransmitterItem extends Item {
    public static final List<LinkedTransmitterBlock> MODULE_BLOCKS = new LinkedList<>();

    public LinkedTransmitterItem(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    public static InteractionResult onItemUseFirst(
            Level world,
            @Nullable Player player,
            ItemStack stack,
            InteractionHand hand,
            BlockHitResult ray,
            BlockPos pos
    ) {
        if (!(stack.getItem() instanceof LinkedTransmitterItem))
            return null;
        if (player == null)
            return null;

        BlockState hitState = world.getBlockState(pos);

        if (player.mayBuild()) {
            for (LinkedTransmitterBlock moduleBlock : MODULE_BLOCKS) {
                if (hitState.is(moduleBlock.getBase())) {
                    if (!world.isClientSide()) {
                        if (!player.isCreative())
                            stack.shrink(1);
                        moduleBlock.replaceBase(hitState, world, pos);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        for (LinkedTransmitterBlock moduleBlock : MODULE_BLOCKS) {
            if (hitState.is(moduleBlock.getBlock())) {
                return InteractionResult.PASS;
            }
        }

        return null;
    }
}
