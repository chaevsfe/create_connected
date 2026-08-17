package com.hlysine.create_connected.content.crossconnector;

import com.hlysine.create_connected.foundation.ClientHitResult;
import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.zurrtum.create.content.decoration.encasing.EncasedBlock;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class EncasedCrossConnectorBlock extends CrossConnectorBlock implements SpecialBlockItemRequirement, EncasedBlock {
    private final Supplier<Block> casing;

    public EncasedCrossConnectorBlock(Properties properties, Supplier<Block> casing) {
        super(properties);
        this.casing = casing;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide())
            return InteractionResult.SUCCESS;
        context.getLevel().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, context.getClickedPos(), Block.getId(state));
        KineticBlockEntity.switchToBlockState(context.getLevel(), context.getClickedPos(),
                CCBlocks.CROSS_CONNECTOR.defaultBlockState().setValue(AXIS, state.getValue(AXIS)));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        BlockHitResult hit = ClientHitResult.at(pos);
        if (hit != null && hit.getDirection().getAxis() == getRotationAxis(state))
            return CCBlocks.CROSS_CONNECTOR.asItem().getDefaultInstance();
        return getCasing().asItem().getDefaultInstance();
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity be) {
        return ItemRequirement.of(CCBlocks.CROSS_CONNECTOR.defaultBlockState(), be);
    }

    @Override
    public Block getCasing() {
        return casing.get();
    }

    @Override
    public void handleEncasing(
            BlockState state,
            Level level,
            BlockPos pos,
            ItemStack heldItem,
            Player player,
            InteractionHand hand,
            BlockHitResult ray
    ) {
        KineticBlockEntity.switchToBlockState(level, pos, defaultBlockState()
                .setValue(CrossConnectorBlock.AXIS, state.getValue(CrossConnectorBlock.AXIS)));
    }
}
