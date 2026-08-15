package com.hlysine.create_connected.content.parallelgearbox;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.hlysine.create_connected.registries.CCItems;
import com.zurrtum.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

public class ParallelGearboxBlock extends RotatedPillarKineticBlock implements IBE<ParallelGearboxBlockEntity> {

    public ParallelGearboxBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (state.getValue(AXIS).isVertical())
            return super.getDrops(state, builder);
        return List.of(new ItemStack(CCItems.VERTICAL_PARALLEL_GEARBOX));
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        if (state.getValue(AXIS).isVertical())
            return super.getCloneItemStack(world, pos, state, includeData);
        return new ItemStack(CCItems.VERTICAL_PARALLEL_GEARBOX);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(AXIS, Axis.Y);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() != state.getValue(AXIS);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public Class<ParallelGearboxBlockEntity> getBlockEntityClass() {
        return ParallelGearboxBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ParallelGearboxBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.PARALLEL_GEARBOX;
    }
}
