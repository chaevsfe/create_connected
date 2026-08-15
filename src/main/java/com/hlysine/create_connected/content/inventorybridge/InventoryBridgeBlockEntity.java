package com.hlysine.create_connected.content.inventorybridge;

import com.hlysine.create_connected.content.inventoryaccessport.WrappedItemHandler;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.catnip.math.BlockFace;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerSidedFilteringBehaviour;
import com.zurrtum.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.zurrtum.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlock.ATTACHED_NEGATIVE;
import static com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlock.ATTACHED_POSITIVE;

public class InventoryBridgeBlockEntity extends SmartBlockEntity {
    protected final Container itemCapability = new InventoryBridgeHandler();
    private InvManipulationBehaviour negativeInventory;
    private InvManipulationBehaviour positiveInventory;

    ServerSidedFilteringBehaviour filters;
    public ServerFilteringBehaviour negativeFilter;
    public ServerFilteringBehaviour positiveFilter;

    private boolean powered;

    public InventoryBridgeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        powered = false;
    }

    @Override
    public void initialize() {
        super.initialize();
        updateConnectedInventory();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing1 =
                (w, p, s) -> new BlockFace(p, InventoryBridgeBlock.getNegativeTarget(s));
        CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing2 =
                (w, p, s) -> new BlockFace(p, InventoryBridgeBlock.getPositiveTarget(s));
        behaviours.add(negativeInventory = new InvManipulationBehaviour(this, towardBlockFacing1));
        behaviours.add(positiveInventory = new InvManipulationBehaviour(this, towardBlockFacing2));
        behaviours.add(filters = new ServerSidedFilteringBehaviour(
                this,
                (facing, filter) -> {
                    if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
                        negativeFilter = filter;
                    } else {
                        positiveFilter = filter;
                    }
                    return filter;
                },
                facing -> facing.getAxis() == getBlockState().getValue(InventoryBridgeBlock.AXIS)
        ));
    }

    public Container getItemCapability() {
        return itemCapability;
    }

    public boolean isAttachedNegative() {
        return !powered && negativeInventory.hasInventory()
                && !(negativeInventory.getInventory() instanceof WrappedItemHandler);
    }

    public boolean isAttachedPositive() {
        return !powered && positiveInventory.hasInventory()
                && !(positiveInventory.getInventory() instanceof WrappedItemHandler);
    }

    @Nullable
    public BlockState getNegativeAttachedBlock() {
        if (!isAttachedNegative())
            return null;
        return level.getBlockState(negativeInventory.getTarget().getConnectedPos());
    }

    @Nullable
    public BlockState getPositiveAttachedBlock() {
        if (!isAttachedPositive())
            return null;
        return level.getBlockState(positiveInventory.getTarget().getConnectedPos());
    }

    public void updateConnectedInventory() {
        negativeInventory.findNewCapability();
        positiveInventory.findNewCapability();
        boolean previouslyPowered = powered;
        powered = level.hasNeighborSignal(worldPosition);
        if (powered != previouslyPowered) {
            notifyUpdate();
        }
        boolean attachedNegative = isAttachedNegative();
        boolean attachedPositive = isAttachedPositive();
        if (attachedNegative != getBlockState().getValue(ATTACHED_NEGATIVE)
                || attachedPositive != getBlockState().getValue(ATTACHED_POSITIVE)) {
            BlockState state = getBlockState()
                    .setValue(ATTACHED_NEGATIVE, attachedNegative)
                    .setValue(ATTACHED_POSITIVE, attachedPositive);
            level.setBlockAndUpdate(worldPosition, state);
        }
    }

    @Override
    protected void read(ValueInput view, boolean clientPacket) {
        super.read(view, clientPacket);
        powered = view.getBooleanOr("Powered", false);
    }

    @Override
    protected void write(ValueOutput view, boolean clientPacket) {
        super.write(view, clientPacket);
        view.putBoolean("Powered", powered);
    }

    @Nullable
    private Container getNegativeHandler() {
        if (powered)
            return null;
        Container handler = negativeInventory.getInventory();
        if (handler instanceof WrappedItemHandler)
            return null;
        return handler;
    }

    @Nullable
    private Container getPositiveHandler() {
        if (powered)
            return null;
        Container handler = positiveInventory.getInventory();
        if (handler instanceof WrappedItemHandler)
            return null;
        return handler;
    }

    private boolean allowsNegativeOnly(ItemStack stack) {
        boolean negative = negativeFilter.test(stack);
        boolean positive = positiveFilter.test(stack);
        if (!negative)
            return false;
        return !positive || positiveFilter.getFilter().isEmpty() || !negativeFilter.getFilter().isEmpty();
    }

    private boolean allowsPositiveOnly(ItemStack stack) {
        boolean negative = negativeFilter.test(stack);
        boolean positive = positiveFilter.test(stack);
        if (!positive)
            return false;
        return !negative || negativeFilter.getFilter().isEmpty() || !positiveFilter.getFilter().isEmpty();
    }

    private boolean allowsCombined(ItemStack stack, int slot, int negativeSize) {
        boolean negative = negativeFilter.test(stack);
        boolean positive = positiveFilter.test(stack);
        if (!negative && !positive)
            return false;
        if (negative && !positive && slot >= negativeSize)
            return false;
        if (positive && !negative && slot < negativeSize)
            return false;
        boolean negativeFilterEmpty = negativeFilter.getFilter().isEmpty();
        boolean positiveFilterEmpty = positiveFilter.getFilter().isEmpty();
        if (!negativeFilterEmpty || !positiveFilterEmpty) {
            if (slot >= negativeSize && negative && positiveFilterEmpty)
                return false;
            if (slot < negativeSize && positive && negativeFilterEmpty)
                return false;
        }
        return true;
    }

    private class InventoryBridgeHandler implements WrappedItemHandler {

        private final ThreadLocal<Boolean> recursionGuard = ThreadLocal.withInitial(() -> false);

        private <T> T preventRecursion(Supplier<T> value, T defaultValue) {
            if (recursionGuard.get())
                return defaultValue;
            recursionGuard.set(true);
            T result = value.get();
            recursionGuard.set(false);
            return result;
        }

        @Override
        public int getContainerSize() {
            return preventRecursion(() -> {
                Container handler1 = getNegativeHandler();
                Container handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null)
                    return 0;
                if (handler1 == null)
                    return handler2.getContainerSize();
                if (handler2 == null)
                    return handler1.getContainerSize();
                return handler1.getContainerSize() + handler2.getContainerSize();
            }, 0);
        }

        @Override
        public boolean isEmpty() {
            for (int i = 0, size = getContainerSize(); i < size; i++) {
                if (!getItem(i).isEmpty())
                    return false;
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return preventRecursion(() -> {
                Container handler1 = getNegativeHandler();
                Container handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null)
                    return ItemStack.EMPTY;
                if (handler1 == null) {
                    ItemStack stack = handler2.getItem(slot);
                    return allowsPositiveOnly(stack) ? stack : ItemStack.EMPTY;
                }
                if (handler2 == null) {
                    ItemStack stack = handler1.getItem(slot);
                    return allowsNegativeOnly(stack) ? stack : ItemStack.EMPTY;
                }
                int size1 = handler1.getContainerSize();
                ItemStack stack = slot < size1 ? handler1.getItem(slot) : handler2.getItem(slot - size1);
                return allowsCombined(stack, slot, size1) ? stack : ItemStack.EMPTY;
            }, ItemStack.EMPTY);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return preventRecursion(() -> {
                Container handler1 = getNegativeHandler();
                Container handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null)
                    return ItemStack.EMPTY;
                if (handler1 == null) {
                    return allowsPositiveOnly(handler2.getItem(slot)) ? handler2.removeItem(slot, amount)
                            : ItemStack.EMPTY;
                }
                if (handler2 == null) {
                    return allowsNegativeOnly(handler1.getItem(slot)) ? handler1.removeItem(slot, amount)
                            : ItemStack.EMPTY;
                }
                int size1 = handler1.getContainerSize();
                ItemStack stack = slot < size1 ? handler1.getItem(slot) : handler2.getItem(slot - size1);
                if (!allowsCombined(stack, slot, size1))
                    return ItemStack.EMPTY;
                return slot < size1 ? handler1.removeItem(slot, amount) : handler2.removeItem(slot - size1, amount);
            }, ItemStack.EMPTY);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return preventRecursion(() -> {
                Container handler1 = getNegativeHandler();
                Container handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null)
                    return ItemStack.EMPTY;
                if (handler1 == null)
                    return handler2.removeItemNoUpdate(slot);
                if (handler2 == null)
                    return handler1.removeItemNoUpdate(slot);
                int size1 = handler1.getContainerSize();
                return slot < size1 ? handler1.removeItemNoUpdate(slot) : handler2.removeItemNoUpdate(slot - size1);
            }, ItemStack.EMPTY);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            preventRecursion(() -> {
                Container handler1 = getNegativeHandler();
                Container handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null)
                    return null;
                if (handler1 == null) {
                    handler2.setItem(slot, stack);
                    return null;
                }
                if (handler2 == null) {
                    handler1.setItem(slot, stack);
                    return null;
                }
                int size1 = handler1.getContainerSize();
                if (slot < size1)
                    handler1.setItem(slot, stack);
                else
                    handler2.setItem(slot - size1, stack);
                return null;
            }, null);
        }

        @Override
        public int getMaxStackSize() {
            return preventRecursion(() -> {
                Container handler1 = getNegativeHandler();
                Container handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null)
                    return 0;
                if (handler1 == null)
                    return handler2.getMaxStackSize();
                return handler1.getMaxStackSize();
            }, 0);
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return preventRecursion(() -> {
                Container handler1 = getNegativeHandler();
                Container handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null)
                    return 0;
                if (handler1 == null)
                    return handler2.getMaxStackSize(stack);
                return handler1.getMaxStackSize(stack);
            }, 0);
        }

        @Override
        public void setChanged() {
            preventRecursion(() -> {
                Container handler1 = getNegativeHandler();
                Container handler2 = getPositiveHandler();
                if (handler1 != null)
                    handler1.setChanged();
                if (handler2 != null)
                    handler2.setChanged();
                return null;
            }, null);
        }

        @Override
        public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(InventoryBridgeBlockEntity.this, player);
        }

        @Override
        public void clearContent() {
            preventRecursion(() -> {
                Container handler1 = getNegativeHandler();
                Container handler2 = getPositiveHandler();
                if (handler1 != null)
                    handler1.clearContent();
                if (handler2 != null)
                    handler2.clearContent();
                return null;
            }, null);
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return preventRecursion(() -> {
                Container handler1 = getNegativeHandler();
                Container handler2 = getPositiveHandler();
                if (handler1 == null && handler2 == null)
                    return false;
                if (handler1 == null)
                    return allowsPositiveOnly(stack) && handler2.canPlaceItem(slot, stack);
                if (handler2 == null)
                    return allowsNegativeOnly(stack) && handler1.canPlaceItem(slot, stack);
                int size1 = handler1.getContainerSize();
                if (!allowsCombined(stack, slot, size1))
                    return false;
                return slot < size1 ? handler1.canPlaceItem(slot, stack)
                        : handler2.canPlaceItem(slot - size1, stack);
            }, false);
        }
    }
}
