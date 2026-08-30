package com.hlysine.create_connected.content.linkedtransmitter;

import com.hlysine.create_connected.foundation.ClientHitResult;
import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.hlysine.create_connected.registries.CCItems;
import com.mojang.serialization.MapCodec;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllSoundEvents;
import com.zurrtum.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.content.redstone.analogLever.AnalogLeverBlock;
import com.zurrtum.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.zurrtum.create.content.schematics.requirement.ItemRequirement;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class LinkedAnalogLeverBlock extends FaceAttachedHorizontalDirectionalBlock
        implements IBE<LinkedAnalogLeverBlockEntity>, SpecialBlockItemRequirement, IWrenchable, LinkedTransmitterBlock {
    public static BooleanProperty LOCKED = BlockStateProperties.LOCKED;

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static final MapCodec<LinkedAnalogLeverBlock> CODEC =
            simpleCodec(p -> new LinkedAnalogLeverBlock(p, () -> AllBlocks.ANALOG_LEVER));

    private final Supplier<AnalogLeverBlock> baseSupplier;
    private final Function<BlockState, VoxelShape> shapeFunction;

    public LinkedAnalogLeverBlock(Properties pProperties, Supplier<AnalogLeverBlock> baseSupplier) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(LOCKED, false));
        this.baseSupplier = baseSupplier;
        this.shapeFunction = createShapeFunction();
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        Map<AttachFace, Map<Direction, VoxelShape>> map = Shapes.rotateAttachFace(boxZ(6.0, 8.0, 10.0, 16.0));
        return getShapeForEachState(state -> map.get(state.getValue(FACE)).get(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING, FACE, POWERED, LOCKED));
    }

    @Override
    public Block getBlock() {
        return this;
    }

    @Override
    public Block getBase() {
        return baseSupplier.get();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.or(getTransmitterShape(state), shapeFunction.apply(state));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return getBase().defaultBlockState().getDrops(builder);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (player.isSpectator())
            return InteractionResult.PASS;

        if (isHittingBase(state, level, pos, hitResult)) {
            if (level.isClientSide()) {
                addParticles(state, level, pos, 1.0F);
                return InteractionResult.SUCCESS;
            }
            return onBlockEntityUse(level, pos, be -> {
                boolean sneak = player.isShiftKeyDown();
                be.changeState(sneak);
                float f = 0.25f + (be.getState() + 5) / 15.0f * 0.5f;
                level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.2F, f);
                return InteractionResult.SUCCESS;
            });
        }
        return LinkedTransmitterBlock.super.useTransmitter(state, level, pos, player);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        return LinkedTransmitterBlock.super.useWax(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return getBlockEntityOptional(blockAccess, pos).map(LinkedAnalogLeverBlockEntity::getState).orElse(0);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return getConnectedDirection(blockState) == side ? getSignal(blockState, blockAccess, pos, side) : 0;
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        withBlockEntityDo(worldIn, pos, be -> {
            if (be.getState() != 0 && rand.nextFloat() < 0.25F) {
                addParticles(stateIn, worldIn, pos, 0.5F);
            }
        });
    }

    private static void addParticles(BlockState state, LevelAccessor worldIn, BlockPos pos, float alpha) {
        Direction direction = state.getValue(FACING).getOpposite();
        Direction direction1 = getConnectedDirection(state).getOpposite();
        double d0 = pos.getX() + 0.5D + 0.1D * direction.getStepX() + 0.2D * direction1.getStepX();
        double d1 = pos.getY() + 0.5D + 0.1D * direction.getStepY() + 0.2D * direction1.getStepY();
        double d2 = pos.getZ() + 0.5D + 0.1D * direction.getStepZ() + 0.2D * direction1.getStepZ();
        worldIn.addParticle(new DustParticleOptions(0xFF0000, alpha), d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    static void updateNeighbors(BlockState state, Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, state.getBlock(), null);
        world.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), state.getBlock(), null);
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        onWrenched(state, context);
        return IWrenchable.super.onSneakWrenched(state, context);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Player player = context.getPlayer();
        if (!player.isCreative()) {
            player.getInventory().placeItemBackInInventory(new ItemStack(CCItems.LINKED_TRANSMITTER));
        }
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), be -> be.containsBase = false);
        replaceWithBase(state, context.getLevel(), context.getClickedPos());
        return InteractionResult.SUCCESS;
    }

    @Override
    public void replaceBase(BlockState baseState, Level world, BlockPos pos) {
        int carried = 0;
        BlockEntity previous = world.getBlockEntity(pos);
        if (previous instanceof AnalogLeverBlockEntity analogLever)
            carried = analogLever.getState();

        world.setBlockAndUpdate(pos, defaultBlockState()
                .setValue(FACING, baseState.getValue(FACING))
                .setValue(FACE, baseState.getValue(FACE))
                .setValue(POWERED, carried > 0)
        );
        int finalCarried = carried;
        withBlockEntityDo(world, pos, be -> be.setState(finalCarried));
        AllSoundEvents.CONTROLLER_PUT.playOnServer(world, pos);
    }

    public void replaceWithBase(BlockState state, Level world, BlockPos pos) {
        int carried = getBlockEntityOptional(world, pos).map(LinkedAnalogLeverBlockEntity::getState).orElse(0);
        AllSoundEvents.CONTROLLER_TAKE.playOnServer(world, pos);
        world.setBlockAndUpdate(pos, getBase().defaultBlockState()
                .setValue(FACING, state.getValue(FACING))
                .setValue(FACE, state.getValue(FACE)));
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof AnalogLeverBlockEntity analogLever) {
            for (int i = 0; i < carried; i++)
                analogLever.changeState(false);
        }
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        BlockHitResult hit = ClientHitResult.at(pos);
        if (hit != null && isHittingBase(state, level, pos, hit))
            return new ItemStack(baseSupplier.get());
        return new ItemStack(CCItems.LINKED_TRANSMITTER);
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity be) {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();
        requiredItems.add(new ItemStack(getBase()));
        requiredItems.add(new ItemStack(CCItems.LINKED_TRANSMITTER));
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, requiredItems);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public Class<LinkedAnalogLeverBlockEntity> getBlockEntityClass() {
        return LinkedAnalogLeverBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LinkedAnalogLeverBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.LINKED_ANALOG_LEVER;
    }
}
