package com.hlysine.create_connected.content.sequencedpulsegenerator;

import com.hlysine.create_connected.foundation.advancement.AdvancementBehaviour;
import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.mojang.serialization.MapCodec;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.content.redstone.diodes.AbstractDiodeBlock;
import com.zurrtum.create.content.redstone.diodes.BrassDiodeBlock;
import com.zurrtum.create.content.redstone.diodes.PoweredLatchBlock;
import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.foundation.block.RedStoneConnectBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Consumer;

public class SequencedPulseGeneratorBlock extends AbstractDiodeBlock
        implements IBE<SequencedPulseGeneratorBlockEntity>, RedStoneConnectBlock {

    public static final BooleanProperty POWERING = BrassDiodeBlock.POWERING;
    public static final BooleanProperty POWERED_SIDE = PoweredLatchBlock.POWERED_SIDE;

    public static final MapCodec<SequencedPulseGeneratorBlock> CODEC = simpleCodec(SequencedPulseGeneratorBlock::new);

    private static Consumer<SequencedPulseGeneratorBlockEntity> screenOpener = be -> {
    };

    public static void setScreenOpener(Consumer<SequencedPulseGeneratorBlockEntity> opener) {
        screenOpener = opener;
    }

    public SequencedPulseGeneratorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(POWERED, false)
                .setValue(POWERING, false)
                .setValue(POWERED_SIDE, false)
        );
    }

    @Override
    protected MapCodec<? extends DiodeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, POWERING, POWERED_SIDE, FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        AdvancementBehaviour.trackOwner(worldIn, pos, placer);
    }

    @Override
    protected void checkTickOnNeighbor(Level level, BlockPos pos, BlockState state) {
        super.checkTickOnNeighbor(level, pos, state);
        if (!this.isLocked(level, pos, state)) {
            int input = getInputSignal(level, pos, state);
            boolean prevPower = state.getValue(POWERED);
            boolean currPower = input > 0;
            boolean prevSide = state.getValue(POWERED_SIDE);
            boolean currSide = getAlternateSignal(level, pos, state) > 0;
            BlockState oldState = state;

            if (prevPower != currPower)
                state = state.cycle(POWERED);
            if (prevSide != currSide)
                state = state.cycle(POWERED_SIDE);

            if (oldState != state)
                level.setBlock(pos, state, 2);

            if (currSide) {
                if (!prevSide)
                    withBlockEntityDo(level, pos, SequencedPulseGeneratorBlockEntity::reset);
                return;
            }
            withBlockEntityDo(level, pos, spg -> spg.onRedstoneUpdate(input));
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource r) {
    }

    @Override
    protected int getOutputSignal(BlockGetter worldIn, BlockPos pos, BlockState state) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (!(be instanceof SequencedPulseGeneratorBlockEntity spg))
            return state.getValue(POWERING) ? 15 : 0;
        return spg.getCurrentSignal();
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(FACING) == side ? this.getOutputSignal(blockAccess, pos, blockState) : 0;
    }

    @Override
    protected int getDelay(BlockState state) {
        return 2;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, Direction side) {
        if (side == null)
            return false;
        return side.getAxis().isHorizontal();
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return getBlockEntityOptional(world, pos).map(be -> be.currentInstruction + 1).orElse(0);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack,
                                          BlockState state,
                                          Level level,
                                          BlockPos pos,
                                          Player player,
                                          InteractionHand hand,
                                          BlockHitResult hitResult) {
        if (stack.is(AllItems.WRENCH))
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (stack.getItem() instanceof BlockItem)
            return InteractionResult.TRY_WITH_EMPTY_HAND;

        displayScreen(level, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state,
                                               Level worldIn,
                                               BlockPos pos,
                                               Player player,
                                               BlockHitResult hit) {
        displayScreen(worldIn, pos);
        return InteractionResult.SUCCESS;
    }

    private void displayScreen(Level level, BlockPos pos) {
        if (level.isClientSide())
            withBlockEntityDo(level, pos, screenOpener);
    }

    @Override
    public Class<SequencedPulseGeneratorBlockEntity> getBlockEntityClass() {
        return SequencedPulseGeneratorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SequencedPulseGeneratorBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.SEQUENCED_PULSE_GENERATOR;
    }
}
