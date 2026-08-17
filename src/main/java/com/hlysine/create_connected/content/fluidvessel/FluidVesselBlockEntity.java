package com.hlysine.create_connected.content.fluidvessel;

import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.catnip.animation.LerpedFloat;
import com.zurrtum.create.content.fluids.tank.FluidTankBlockEntity;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import static com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock.AXIS;
import static com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock.LIGHT_LEVEL;
import static com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock.NEGATIVE;
import static com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock.POSITIVE;
import static com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock.SHAPE;
import static com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock.Shape;
import static com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock.WindowType;
import static com.hlysine.create_connected.content.fluidvessel.FluidVesselBlock.isVessel;

public class FluidVesselBlockEntity extends FluidTankBlockEntity {

    private static final int MAX_SIZE = 3;

    protected WindowType windowType;

    public FluidVesselBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        windowType = WindowType.SIDE_WIDE;
        boiler = new BoilerData();
        refreshCapability();
    }

    @Override
    public void updateConnectivity() {
        super.updateConnectivity();
    }

    public Axis getAxis() {
        return getBlockState().getValue(AXIS);
    }

    public static boolean isLighterThanAir(FluidStack fluidStack) {
        return FluidVariantAttributes.isLighterThanAir(
                FluidVariant.of(fluidStack.getFluid(), fluidStack.getComponentChanges())
        );
    }

    @Override
    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasLevel())
            return;

        int luminosity = (int) (newFluidStack.getFluid()
                .defaultFluidState()
                .createLegacyBlock()
                .getLightEmission() / 1.2f);
        int maxY = (int) ((getFillState() * width) + 1);
        Axis axis = getAxis();
        boolean reversed = isLighterThanAir(newFluidStack);

        for (int yOffset = 0; yOffset < width; yOffset++) {
            boolean isBright = reversed ? (width - yOffset <= maxY) : (yOffset < maxY);
            int actualLuminosity = isBright ? luminosity : luminosity > 0 ? 1 : 0;

            for (int lengthOffset = 0; lengthOffset < height; lengthOffset++) {
                for (int widthOffset = 0; widthOffset < width; widthOffset++) {
                    BlockPos pos = this.worldPosition.offset(
                            axis == Axis.X ? lengthOffset : widthOffset,
                            yOffset,
                            axis == Axis.Z ? lengthOffset : widthOffset
                    );
                    FluidVesselBlockEntity vesselAt = ConnectivityHandler.partAt(getType(), level, pos);
                    if (vesselAt == null)
                        continue;
                    level.updateNeighbourForOutputSignal(pos, vesselAt.getBlockState()
                            .getBlock());
                    if (vesselAt.luminosity == actualLuminosity)
                        continue;
                    vesselAt.setLuminosity(actualLuminosity);
                }
            }
        }

        if (!level.isClientSide()) {
            setChanged();
            sendData();
        }

        if (isVirtual()) {
            if (getFluidLevel() == null)
                setFluidLevel(LerpedFloat.linear()
                        .startWithValue(getFillState()));
            getFluidLevel().chase(getFillState(), .5f, LerpedFloat.Chaser.EXP);
        }
    }

    @Override
    protected void updateStateLuminosity() {
        if (level.isClientSide())
            return;
        int actualLuminosity = luminosity;
        FluidVesselBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null || !controllerBE.window)
            actualLuminosity = 0;
        refreshBlockState();
        BlockState state = getBlockState();
        if (!isVessel(state))
            return;
        if (state.getValue(LIGHT_LEVEL) != actualLuminosity)
            level.setBlock(worldPosition, state.setValue(LIGHT_LEVEL, actualLuminosity),
                    Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
    }

    @Override
    @Nullable
    public FluidVesselBlockEntity getControllerBE() {
        if (isController() || !hasLevel())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof FluidVesselBlockEntity)
            return (FluidVesselBlockEntity) blockEntity;
        return null;
    }

    @Override
    public void removeController(boolean keepFluids) {
        if (level.isClientSide())
            return;
        updateConnectivity = true;
        if (!keepFluids)
            applyFluidTankSize(1);
        controller = null;
        width = 1;
        height = 1;
        boiler.clear();
        onFluidStackChanged(tankInventory.getFluid());

        BlockState state = getBlockState();
        if (isVessel(state)) {
            state = state.setValue(POSITIVE, true);
            state = state.setValue(NEGATIVE, true);
            state = state.setValue(SHAPE, window ? Shape.WINDOW : Shape.PLAIN);
            getLevel().setBlock(worldPosition, state,
                    Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
        }

        refreshCapability();
        setChanged();
        sendData();
    }

    public boolean isWindowTypeAllowed(WindowType type) {
        return switch (type) {
            case SIDE_WIDE -> true;
            case SIDE_NARROW_ENDS -> height >= 2;
            case SIDE_NARROW_THIRDS -> height >= 3;
            case SIDE_HORIZONTAL -> width > 2 && width % 2 == 1;
        };
    }

    @Override
    public void toggleWindows() {
        FluidVesselBlockEntity be = getControllerBE();
        if (be == null)
            return;
        if (be.boiler.isActive())
            return;
        if (!be.window) {
            be.setWindowType(WindowType.SIDE_WIDE);
            be.setWindows(true);
        } else {
            WindowType[] types = WindowType.values();
            if (be.windowType.ordinal() >= types.length - 1) {
                be.setWindows(false);
                return;
            }
            WindowType nextType = types[be.windowType.ordinal() + 1];
            while (!be.isWindowTypeAllowed(nextType)) {
                if (nextType.ordinal() >= types.length - 1) {
                    be.setWindows(false);
                    return;
                }
                nextType = types[nextType.ordinal() + 1];
            }
            be.setWindowType(nextType);
            be.setWindows(true);
        }
    }

    public WindowType getWindowType() {
        return windowType;
    }

    public void setWindowType(WindowType windowType) {
        this.windowType = windowType;
    }

    @Override
    public void setWindows(boolean window) {
        this.window = window;
        Axis axis = getAxis();
        for (int yOffset = 0; yOffset < width; yOffset++) {
            for (int lengthOffset = 0; lengthOffset < height; lengthOffset++) {
                for (int widthOffset = 0; widthOffset < width; widthOffset++) {

                    BlockPos pos = this.worldPosition.offset(
                            axis == Axis.X ? lengthOffset : widthOffset,
                            yOffset,
                            axis == Axis.Z ? lengthOffset : widthOffset
                    );
                    BlockState blockState = level.getBlockState(pos);
                    if (!isVessel(blockState))
                        continue;

                    Shape shape = Shape.PLAIN;
                    if (window)
                        if (windowType == WindowType.SIDE_HORIZONTAL) {
                            if (yOffset == width / 2) {
                                shape = Shape.WINDOW;
                            }
                        } else if (windowType == WindowType.SIDE_WIDE || height <= 1) {
                            if ((widthOffset == 0 || widthOffset == width - 1)) {
                                if (width == 1)
                                    shape = Shape.WINDOW;
                                else if (yOffset == 0)
                                    shape = Shape.WINDOW_TOP;
                                else if (yOffset == width - 1)
                                    shape = Shape.WINDOW_BOTTOM;
                                else
                                    shape = Shape.WINDOW_MIDDLE;
                            }
                        } else if (windowType == WindowType.SIDE_NARROW_ENDS || windowType == WindowType.SIDE_NARROW_THIRDS) {
                            int windowOffset = windowType == WindowType.SIDE_NARROW_ENDS ? 0 : Math.max(1, height / 3 - 1);
                            if ((lengthOffset == windowOffset || lengthOffset == height - 1 - windowOffset) && (widthOffset == 0 || widthOffset == width - 1)) {
                                if (width == 1)
                                    shape = Shape.WINDOW_SINGLE;
                                else if (yOffset == 0)
                                    shape = Shape.WINDOW_TOP_SINGLE;
                                else if (yOffset == width - 1)
                                    shape = Shape.WINDOW_BOTTOM_SINGLE;
                                else
                                    shape = Shape.WINDOW_MIDDLE_SINGLE;
                            }
                        }

                    level.setBlock(pos, blockState.setValue(SHAPE, shape),
                            Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
                    if (level.getBlockEntity(pos) instanceof FluidVesselBlockEntity vesselAt)
                        vesselAt.updateStateLuminosity();
                    level.getChunkSource()
                            .getLightEngine()
                            .checkBlock(pos);
                }
            }
        }
    }

    @Override
    public void updateBoilerState() {
        if (!isController())
            return;

        boolean wasBoiler = boiler.isActive();
        boolean changed = boiler.evaluate(this);

        if (wasBoiler != boiler.isActive()) {
            if (boiler.isActive())
                setWindows(false);

            Axis axis = getAxis();
            for (int yOffset = 0; yOffset < width; yOffset++)
                for (int lengthOffset = 0; lengthOffset < height; lengthOffset++)
                    for (int widthOffset = 0; widthOffset < width; widthOffset++)
                        if (level.getBlockEntity(
                                worldPosition.offset(
                                        axis == Axis.X ? lengthOffset : widthOffset,
                                        yOffset,
                                        axis == Axis.Z ? lengthOffset : widthOffset
                                )) instanceof FluidVesselBlockEntity fbe)
                            fbe.refreshCapability();
        }

        if (changed) {
            notifyUpdate();
            boiler.checkPipeOrganAdvancement(this);
        }
    }

    @Override
    protected AABB createRenderBoundingBox() {
        AABB base = new AABB(getBlockPos());
        if (!isController())
            return base;
        Axis axis = getAxis();
        return base.expandTowards(
                axis == Axis.X ? (height - 1) : (width - 1),
                width - 1,
                axis == Axis.Z ? (height - 1) : (width - 1)
        );
    }

    @Override
    @Nullable
    public FluidVesselBlockEntity getOtherFluidTankBlockEntity(Direction direction) {
        BlockEntity otherBE = level.getBlockEntity(worldPosition.relative(direction));
        if (otherBE instanceof FluidVesselBlockEntity)
            return (FluidVesselBlockEntity) otherBE;
        return null;
    }

    @Override
    protected void read(ValueInput view, boolean clientPacket) {
        super.read(view, clientPacket);
        if (isController())
            windowType = view.read("WindowType", WindowType.CODEC)
                    .orElse(WindowType.SIDE_WIDE);
    }

    @Override
    public void write(ValueOutput view, boolean clientPacket) {
        super.write(view, clientPacket);
        if (isController())
            view.store("WindowType", WindowType.CODEC, windowType);
    }

    @Override
    public void writeSafe(ValueOutput view) {
        if (isController()) {
            view.putBoolean("Window", window);
            view.putInt("Size", width);
            view.putInt("Height", height);
        }
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (isVessel(state)) {
            Axis axis = getAxis();
            state = state.setValue(NEGATIVE, axis == Axis.X
                    ? getController().getX() == getBlockPos().getX()
                    : getController().getZ() == getBlockPos().getZ());
            state = state.setValue(POSITIVE, axis == Axis.X
                    ? getController().getX() + height - 1 == getBlockPos().getX()
                    : getController().getZ() + height - 1 == getBlockPos().getZ());
            level.setBlock(getBlockPos(), state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE);
        }
        if (isController())
            setWindows(window);
        onFluidStackChanged(tankInventory.getFluid());
        updateBoilerState();
        setChanged();
    }

    @Override
    public void setExtraData(@Nullable Object data) {
        if (data == null) {
            window = false;
            windowType = WindowType.SIDE_WIDE;
        } else if (data instanceof WindowType type) {
            window = true;
            windowType = type;
        }
    }

    @Override
    @Nullable
    public Object getExtraData() {
        return window ? windowType : null;
    }

    @Override
    @Nullable
    public Object modifyExtraData(@Nullable Object data) {
        if (data == null || (data instanceof WindowType)) {
            if (data != null && !window)
                return data;
            if (window)
                return windowType;
            return null;
        }
        return data;
    }

    @Override
    public Axis getMainConnectionAxis() {
        return getAxis();
    }

    @Override
    public int getMaxLength(Axis longAxis, int width) {
        if (longAxis == Axis.Y)
            return getMaxWidth();
        return getMaxHeight();
    }

    @Override
    public int getMaxWidth() {
        return MAX_SIZE;
    }

    public boolean hasWindow() {
        return window;
    }

    public int getLuminosity() {
        return luminosity;
    }

}
