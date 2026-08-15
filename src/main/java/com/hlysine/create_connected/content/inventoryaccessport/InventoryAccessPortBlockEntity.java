package com.hlysine.create_connected.content.inventoryaccessport;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.catnip.math.BlockFace;
import com.zurrtum.create.content.redstone.DirectedDirectionalBlock;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.zurrtum.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import net.minecraft.core.BlockPos;
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

import static com.hlysine.create_connected.content.inventoryaccessport.InventoryAccessPortBlock.ATTACHED;

public class InventoryAccessPortBlockEntity extends SmartBlockEntity {
    protected final Container itemCapability = new InventoryAccessHandler();
    private InvManipulationBehaviour observedInventory;
    private boolean powered;

    public InventoryAccessPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
        CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing =
                (w, p, s) -> new BlockFace(p, DirectedDirectionalBlock.getTargetDirection(s));
        behaviours.add(observedInventory = new InvManipulationBehaviour(this, towardBlockFacing));
    }

    public Container getItemCapability() {
        return itemCapability;
    }

    public boolean isAttached() {
        return !powered && observedInventory.hasInventory()
                && !(observedInventory.getInventory() instanceof WrappedItemHandler);
    }

    @Nullable
    public BlockState getAttachedBlock() {
        if (!isAttached())
            return null;
        return level.getBlockState(observedInventory.getTarget().getConnectedPos());
    }

    public void updateConnectedInventory() {
        observedInventory.findNewCapability();
        boolean previouslyPowered = powered;
        powered = level.hasNeighborSignal(worldPosition);
        if (powered != previouslyPowered) {
            notifyUpdate();
        }
        if (isAttached() != getBlockState().getValue(ATTACHED)) {
            BlockState state = getBlockState().cycle(ATTACHED);
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
    private Container getConnectedContainer() {
        if (powered)
            return null;
        Container handler = observedInventory.getInventory();
        if (handler instanceof WrappedItemHandler)
            return null;
        return handler;
    }

    private class InventoryAccessHandler implements WrappedItemHandler {

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
                Container handler = getConnectedContainer();
                return handler == null ? 0 : handler.getContainerSize();
            }, 0);
        }

        @Override
        public boolean isEmpty() {
            return preventRecursion(() -> {
                Container handler = getConnectedContainer();
                return handler == null || handler.isEmpty();
            }, true);
        }

        @Override
        public ItemStack getItem(int slot) {
            return preventRecursion(() -> {
                Container handler = getConnectedContainer();
                return handler == null ? ItemStack.EMPTY : handler.getItem(slot);
            }, ItemStack.EMPTY);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return preventRecursion(() -> {
                Container handler = getConnectedContainer();
                return handler == null ? ItemStack.EMPTY : handler.removeItem(slot, amount);
            }, ItemStack.EMPTY);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return preventRecursion(() -> {
                Container handler = getConnectedContainer();
                return handler == null ? ItemStack.EMPTY : handler.removeItemNoUpdate(slot);
            }, ItemStack.EMPTY);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            preventRecursion(() -> {
                Container handler = getConnectedContainer();
                if (handler != null)
                    handler.setItem(slot, stack);
                return null;
            }, null);
        }

        @Override
        public int getMaxStackSize() {
            return preventRecursion(() -> {
                Container handler = getConnectedContainer();
                return handler == null ? 0 : handler.getMaxStackSize();
            }, 0);
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return preventRecursion(() -> {
                Container handler = getConnectedContainer();
                return handler == null ? 0 : handler.getMaxStackSize(stack);
            }, 0);
        }

        @Override
        public void setChanged() {
            preventRecursion(() -> {
                Container handler = getConnectedContainer();
                if (handler != null)
                    handler.setChanged();
                return null;
            }, null);
        }

        @Override
        public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(InventoryAccessPortBlockEntity.this, player);
        }

        @Override
        public void clearContent() {
            preventRecursion(() -> {
                Container handler = getConnectedContainer();
                if (handler != null)
                    handler.clearContent();
                return null;
            }, null);
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return preventRecursion(() -> {
                Container handler = getConnectedContainer();
                return handler != null && handler.canPlaceItem(slot, stack);
            }, false);
        }
    }
}
