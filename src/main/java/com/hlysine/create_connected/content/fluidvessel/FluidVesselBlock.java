package com.hlysine.create_connected.content.fluidvessel;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.mojang.serialization.Codec;
import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.content.fluids.tank.CreativeFluidTankBlockEntity.CreativeFluidTankInventory;
import com.zurrtum.create.content.fluids.transfer.GenericItemEmptying;
import com.zurrtum.create.content.fluids.transfer.GenericItemFilling;
import com.zurrtum.create.foundation.advancement.AdvancementBehaviour;
import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.foundation.blockEntity.ComparatorUtil;
import com.zurrtum.create.foundation.fluid.FluidHelper;
import com.zurrtum.create.foundation.fluid.FluidHelper.FluidExchange;
import com.zurrtum.create.foundation.utility.BlockHelper;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;
import com.zurrtum.create.infrastructure.fluids.FluidInventoryProvider;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public class FluidVesselBlock extends Block implements IWrenchable, IBE<FluidVesselBlockEntity>, FluidInventoryProvider<FluidVesselBlockEntity> {

    public static final BooleanProperty POSITIVE = BooleanProperty.create("positive");
    public static final BooleanProperty NEGATIVE = BooleanProperty.create("negative");
    public static final EnumProperty<Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);
    public static final IntegerProperty LIGHT_LEVEL = BlockStateProperties.LEVEL;

    private final boolean creative;

    public static FluidVesselBlock regular(Properties p_i48440_1_) {
        return new FluidVesselBlock(p_i48440_1_, false);
    }

    public static FluidVesselBlock creative(Properties p_i48440_1_) {
        return new FluidVesselBlock(p_i48440_1_, true);
    }

    protected FluidVesselBlock(Properties p_i48440_1_, boolean creative) {
        super(p_i48440_1_);
        this.creative = creative;
        registerDefaultState(defaultBlockState().setValue(POSITIVE, true)
                .setValue(NEGATIVE, true)
                .setValue(AXIS, Axis.X)
                .setValue(SHAPE, Shape.WINDOW)
                .setValue(LIGHT_LEVEL, 0));
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    public static boolean isVessel(BlockState state) {
        return state.getBlock() instanceof FluidVesselBlock;
    }

    public static int getLight(BlockState state) {
        return state.getValue(LIGHT_LEVEL);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;
        withBlockEntityDo(world, pos, FluidVesselBlockEntity::updateConnectivity);

        BlockState newState = world.getBlockState(pos);
        if (state != newState && newState.getBlock() == this)
            BlockHelper.markAndNotifyBlock(world, pos, world.getChunkAt(pos), oldState, newState, UPDATE_ALL_IMMEDIATE);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(POSITIVE, NEGATIVE, AXIS, SHAPE, LIGHT_LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        if (pContext.getPlayer() == null || !pContext.getPlayer()
                .isShiftKeyDown()) {
            BlockState placedOn = pContext.getLevel()
                    .getBlockState(pContext.getClickedPos()
                            .relative(pContext.getClickedFace()
                                    .getOpposite()));
            Axis preferredAxis = placedOn.getOptionalValue(AXIS).orElse(null);
            if (preferredAxis != null)
                return this.defaultBlockState()
                        .setValue(AXIS, preferredAxis);
        }
        return this.defaultBlockState()
                .setValue(AXIS, pContext.getHorizontalDirection()
                        .getAxis());
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), FluidVesselBlockEntity::toggleWindows);
        return InteractionResult.SUCCESS;
    }

    static final VoxelShape CAMPFIRE_SMOKE_CLIP = box(0, 4, 0, 16, 16, 16);

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
                                        CollisionContext pContext) {
        if (pContext == CollisionContext.empty())
            return CAMPFIRE_SMOKE_CLIP;
        return pState.getShape(pLevel, pPos);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return Shapes.block();
    }

    @Override
    public BlockState updateShape(BlockState pState, LevelReader pLevel, ScheduledTickAccess tickView,
                                  BlockPos pCurrentPos, Direction pDirection, BlockPos pNeighborPos,
                                  BlockState pNeighborState, RandomSource random) {
        if (pDirection == Direction.DOWN && pNeighborState.getBlock() != this)
            withBlockEntityDo(pLevel, pCurrentPos, FluidVesselBlockEntity::updateBoilerTemperature);
        return pState;
    }

    @Override
    public FluidInventory getFluidInventory(LevelAccessor world, BlockPos pos, BlockState state,
                                            FluidVesselBlockEntity blockEntity, @Nullable Direction context) {
        if (blockEntity.fluidCapability == null)
            blockEntity.refreshCapability();
        return blockEntity.fluidCapability;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hitResult) {
        boolean onClient = level.isClientSide();

        if (stack.isEmpty())
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (!player.isCreative() && !creative)
            return InteractionResult.TRY_WITH_EMPTY_HAND;

        FluidExchange exchange = null;
        FluidVesselBlockEntity be = ConnectivityHandler.partAt(getBlockEntityType(), level, pos);
        if (be == null)
            return InteractionResult.FAIL;

        FluidInventory vesselCapability = getFluidInventory(state, level, pos, be, null);
        if (vesselCapability == null)
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        FluidStack prevFluidInVessel = vesselCapability.getStack(0)
                .copy();

        if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be))
            exchange = FluidExchange.ITEM_TO_TANK;
        else if (FluidHelper.tryFillItemFromBE(level, player, hand, stack, be))
            exchange = FluidExchange.TANK_TO_ITEM;

        if (exchange == null) {
            if (GenericItemEmptying.canItemBeEmptied(level, stack)
                    || GenericItemFilling.canItemBeFilled(level, stack))
                return InteractionResult.SUCCESS;
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        SoundEvent soundevent = null;
        BlockState fluidState = null;
        FluidStack fluidInVessel = vesselCapability.getStack(0);

        if (exchange == FluidExchange.ITEM_TO_TANK) {
            if (creative && !onClient) {
                FluidStack fluidInItem = GenericItemEmptying.emptyItem(level, stack, true)
                        .getFirst();
                if (!fluidInItem.isEmpty() && vesselCapability instanceof CreativeFluidTankInventory) {
                    vesselCapability.setStack(0, fluidInItem);
                    vesselCapability.markDirty();
                }
            }

            Fluid fluid = fluidInVessel.getFluid();
            fluidState = fluid.defaultFluidState()
                    .createLegacyBlock();
            soundevent = FluidHelper.getEmptySound(fluidInVessel);
        }

        if (exchange == FluidExchange.TANK_TO_ITEM) {
            if (creative && !onClient)
                if (vesselCapability instanceof CreativeFluidTankInventory)
                    vesselCapability.setStack(0, FluidStack.EMPTY);

            Fluid fluid = prevFluidInVessel.getFluid();
            fluidState = fluid.defaultFluidState()
                    .createLegacyBlock();
            soundevent = FluidHelper.getFillSound(prevFluidInVessel);
        }

        if (soundevent != null && !onClient) {
            float pitch = Mth
                    .clamp(1 - (1f * fluidInVessel.getAmount() / (FluidVesselBlockEntity.getCapacityMultiplier() * 16)), 0, 1);
            pitch /= 1.5f;
            pitch += .5f;
            pitch += (level.getRandom().nextFloat() - .5f) / 4f;
            level.playSound(null, pos, soundevent, SoundSource.BLOCKS, .5f, pitch);
        }

        if (!FluidStack.areFluidsAndComponentsEqual(fluidInVessel, prevFluidInVessel)) {
            FluidVesselBlockEntity controllerBE = be.getControllerBE();
            if (controllerBE != null) {
                if (fluidState != null && onClient) {
                    BlockParticleOption blockParticleData =
                            new BlockParticleOption(ParticleTypes.BLOCK, fluidState);
                    float fluidLevel = (float) fluidInVessel.getAmount() / vesselCapability.getMaxAmountPerStack();
                    if (FluidVesselBlockEntity.isLighterThanAir(fluidInVessel))
                        fluidLevel = 1 - fluidLevel;

                    Vec3 vec = hitResult.getLocation();
                    vec = new Vec3(vec.x, controllerBE.getBlockPos()
                            .getY() + fluidLevel * (controllerBE.getHeight() - .5f) + .25f, vec.z);
                    Vec3 motion = player.position()
                            .subtract(vec)
                            .scale(1 / 20f);
                    vec = vec.add(motion);
                    level.addParticle(blockParticleData, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z);
                    return InteractionResult.SUCCESS;
                }

                controllerBE.sendDataImmediately();
                controllerBE.setChanged();
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public Class<FluidVesselBlockEntity> getBlockEntityClass() {
        return FluidVesselBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FluidVesselBlockEntity> getBlockEntityType() {
        return creative ? CCBlockEntityTypes.CREATIVE_FLUID_VESSEL : CCBlockEntityTypes.FLUID_VESSEL;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE)
            return state;
        Axis mirrorAxis = mirror == Mirror.FRONT_BACK ? Axis.X : Axis.Z;
        Axis axis = state.getValue(AXIS);
        if (axis == mirrorAxis) {
            return state.setValue(POSITIVE, state.getValue(NEGATIVE))
                    .setValue(NEGATIVE, state.getValue(POSITIVE));
        } else {
            return state;
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        for (int i = 0; i < rotation.ordinal(); i++)
            state = rotateOnce(state);
        return state;
    }

    private BlockState rotateOnce(BlockState state) {
        Axis axis = state.getValue(AXIS);
        if (axis == Axis.X) {
            return state.setValue(AXIS, Axis.Z);
        } else if (axis == Axis.Z) {
            return state.setValue(AXIS, Axis.X)
                    .setValue(POSITIVE, state.getValue(NEGATIVE))
                    .setValue(NEGATIVE, state.getValue(POSITIVE));
        }
        return state;
    }

    public enum Shape implements StringRepresentable {
        PLAIN, WINDOW, WINDOW_TOP, WINDOW_MIDDLE, WINDOW_BOTTOM, WINDOW_SINGLE, WINDOW_TOP_SINGLE, WINDOW_MIDDLE_SINGLE, WINDOW_BOTTOM_SINGLE;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public Shape nonSingleVariant() {
            return switch (this) {
                case WINDOW_SINGLE -> WINDOW;
                case WINDOW_TOP_SINGLE -> WINDOW_TOP;
                case WINDOW_MIDDLE_SINGLE -> WINDOW_MIDDLE;
                case WINDOW_BOTTOM_SINGLE -> WINDOW_BOTTOM;
                default -> this;
            };
        }
    }

    public enum WindowType implements StringRepresentable {
        SIDE_WIDE, SIDE_NARROW_ENDS, SIDE_NARROW_THIRDS, SIDE_HORIZONTAL;

        public static final Codec<WindowType> CODEC = Codec.STRING.xmap(WindowType::byName, Enum::name);

        private static WindowType byName(String name) {
            for (WindowType type : values())
                if (type.name().equals(name))
                    return type;
            return SIDE_WIDE;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos, Direction direction) {
        return getBlockEntityOptional(worldIn, pos).map(FluidVesselBlockEntity::getControllerBE)
                .map(be -> ComparatorUtil.fractionToRedstoneLevel(be.getFillState()))
                .orElse(0);
    }

    public static void updateBoilerState(BlockState pState, Level pLevel, BlockPos vesselPos) {
        BlockState vesselState = pLevel.getBlockState(vesselPos);
        if (!(vesselState.getBlock() instanceof FluidVesselBlock vessel))
            return;
        FluidVesselBlockEntity vesselBE = vessel.getBlockEntity(pLevel, vesselPos);
        if (vesselBE == null)
            return;
        FluidVesselBlockEntity controllerBE = vesselBE.getControllerBE();
        if (controllerBE == null)
            return;
        controllerBE.updateBoilerState();
    }

}
