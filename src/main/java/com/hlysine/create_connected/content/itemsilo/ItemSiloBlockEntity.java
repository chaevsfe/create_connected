package com.hlysine.create_connected.content.itemsilo;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.api.packager.InventoryIdentifier;
import com.zurrtum.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.inventory.VersionedInventory;
import com.zurrtum.create.infrastructure.config.AllConfigs;
import com.zurrtum.create.infrastructure.items.ItemInventory;
import com.zurrtum.create.infrastructure.items.ItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.List;
import java.util.function.Supplier;

public class ItemSiloBlockEntity extends SmartBlockEntity implements IMultiBlockEntityContainer.Inventory, Clearable {

    protected @Nullable Supplier<ItemInventory> itemCapability;
    protected InventoryIdentifier invId;

    protected ItemSiloHandler inventory;
    protected @Nullable BlockPos controller;
    protected @Nullable BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected int radius;
    protected int length;
    protected Axis axis;

    public ItemSiloBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        inventory = new ItemSiloHandler();

        radius = 1;
        length = 1;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        super.preRemoveSideEffects(pos, oldState);
        Containers.dropContents(level, pos, inventory);
        level.removeBlockEntity(pos);
        ConnectivityHandler.splitMulti(this);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
    }

    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide())
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
    }

    protected void updateComparators() {
        ItemSiloBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return;

        level.blockEntityChanged(controllerBE.worldPosition);

        BlockPos pos = controllerBE.getBlockPos();
        Block block = getBlockState().getBlock();
        for (int y = 0; y < controllerBE.length; y++) {
            for (int z = 0; z < controllerBE.radius; z++) {
                for (int x = 0; x < controllerBE.radius; x++) {
                    level.updateNeighbourForOutputSignal(pos.offset(x, y, z), block);
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }

        if (updateConnectivity)
            updateConnectivity();
    }

    @Override
    public @Nullable BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.getX() == controller.getX()
                && worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable ItemSiloBlockEntity getControllerBE() {
        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof ItemSiloBlockEntity)
            return (ItemSiloBlockEntity) blockEntity;
        return null;
    }

    @Override
    public void removeController(boolean keepContents) {
        if (level.isClientSide())
            return;
        updateConnectivity = true;
        controller = null;
        radius = 1;
        length = 1;

        BlockState state = getBlockState();
        if (ItemSiloBlock.isVault(state)) {
            state = state.setValue(ItemSiloBlock.LARGE, false);
            getLevel().setBlock(worldPosition, state,
                    Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
        }

        itemCapability = null;
        setChanged();
        sendData();
    }

    @Override
    public void setController(BlockPos controller) {
        if (level.isClientSide() && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        itemCapability = null;
        setChanged();
        sendData();
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    protected void read(ValueInput view, boolean clientPacket) {
        super.read(view, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = radius;
        int prevLength = length;

        updateConnectivity = view.getBooleanOr("Uninitialized", false);

        lastKnownPos = view.read("LastKnownPos", BlockPos.CODEC).orElse(null);

        controller = view.read("Controller", BlockPos.CODEC).orElse(null);

        if (isController()) {
            radius = view.getIntOr("Size", 0);
            length = view.getIntOr("Length", 0);
        }

        if (!clientPacket) {
            inventory.read(view);
            return;
        }

        boolean changeOfController =
                controllerBefore == null ? controller != null : !controllerBefore.equals(controller);
        if (hasLevel() && (changeOfController || prevSize != radius || prevLength != length))
            level.setBlocksDirty(getBlockPos(), Blocks.AIR.defaultBlockState(), getBlockState());
    }

    @Override
    protected void write(ValueOutput view, boolean clientPacket) {
        if (updateConnectivity)
            view.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            view.store("LastKnownPos", BlockPos.CODEC, lastKnownPos);
        if (!isController())
            view.store("Controller", BlockPos.CODEC, controller);
        if (isController()) {
            view.putInt("Size", radius);
            view.putInt("Length", length);
        }

        super.write(view, clientPacket);

        if (!clientPacket) {
            view.putString("StorageType", "CombinedInv");
            inventory.write(view);
        }
    }

    public ItemSiloHandler getInventoryOfBlock() {
        return inventory;
    }

    public InventoryIdentifier getInvId() {
        initCapability();
        return invId;
    }

    public void applyInventoryToBlock(ItemStackHandler handler) {
        int size = handler.getContainerSize();
        for (int i = 0; i < size; i++)
            inventory.setItem(i, handler.getItem(i));
        for (int i = size, max = inventory.getContainerSize(); i < max; i++)
            inventory.setItem(i, ItemStack.EMPTY);
    }

    public void initCapability() {
        if (!isController()) {
            ItemSiloBlockEntity controllerBE = getControllerBE();
            if (controllerBE == null)
                return;
            if (controllerBE.itemCapability == null || controllerBE.itemCapability.get() == null)
                controllerBE.initCapability();
            itemCapability = () -> {
                if (controllerBE.isRemoved())
                    return null;
                if (controllerBE.itemCapability == null)
                    return null;
                return controllerBE.itemCapability.get();
            };
            invId = controllerBE.invId;
            return;
        }

        ItemSiloHandler[] invs = new ItemSiloHandler[length * radius * radius];
        Find:
        for (int yOffset = 0; yOffset < length; yOffset++) {
            for (int xOffset = 0; xOffset < radius; xOffset++) {
                for (int zOffset = 0; zOffset < radius; zOffset++) {
                    BlockPos vaultPos = worldPosition.offset(xOffset, yOffset, zOffset);
                    ItemSiloBlockEntity vaultAt =
                            ConnectivityHandler.partAt(CCBlockEntityTypes.ITEM_SILO, level, vaultPos);
                    if (vaultAt == null) {
                        invs = null;
                        break Find;
                    }
                    invs[yOffset * radius * radius + xOffset * radius + zOffset] = vaultAt.inventory;
                }
            }
        }

        if (invs == null) {
            itemCapability = null;
        } else {
            ConnectedItemSiloHandler capability = new ConnectedItemSiloHandler(invs);
            itemCapability = () -> capability;
        }

        BlockPos farCorner = worldPosition.offset(radius, length, radius);
        BoundingBox bounds = BoundingBox.fromCorners(worldPosition, farCorner);
        invId = new InventoryIdentifier.Bounds(bounds);
    }

    public static int getMaxLength(int radius) {
        return radius * 3;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = getBlockState();
        if (ItemSiloBlock.isVault(state)) {
            level.setBlock(getBlockPos(), state.setValue(ItemSiloBlock.LARGE, radius > 2),
                    Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE);
        }
        itemCapability = null;
        setChanged();
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return getMainAxisOf(this);
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y)
            return getMaxLength(width);
        return getMaxWidth();
    }

    @Override
    public int getMaxWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return length;
    }

    @Override
    public int getWidth() {
        return radius;
    }

    @Override
    public void setHeight(int height) {
        length = height;
    }

    @Override
    public void setWidth(int width) {
        radius = width;
    }

    @Override
    public boolean hasInventory() {
        return true;
    }

    @Override
    public void clearContent() {
        inventory.clearContent();
    }

    public class ItemSiloHandler implements ItemInventory {
        private final int size;
        protected final NonNullList<ItemStack> stacks;

        public ItemSiloHandler() {
            size = AllConfigs.server().logistics.vaultCapacity.get();
            stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        }

        @Override
        public int getContainerSize() {
            return size;
        }

        @Override
        public ItemStack getItem(int slot) {
            if (slot >= size)
                return ItemStack.EMPTY;
            return stacks.get(slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot >= size)
                return;
            stacks.set(slot, stack);
        }

        @Override
        public void setChanged() {
            level.blockEntityChanged(worldPosition);
        }

        public void write(ValueOutput view) {
            ValueOutput.TypedOutputList<ItemStack> list = view.list("Inventory", ItemStack.CODEC);
            for (ItemStack stack : stacks) {
                if (stack.isEmpty())
                    continue;
                list.add(stack);
            }
        }

        public void read(ValueInput view) {
            ValueInput.TypedInputList<ItemStack> list = view.listOrEmpty("Inventory", ItemStack.CODEC);
            int i = 0;
            for (ItemStack itemStack : list) {
                if (i >= size)
                    break;
                stacks.set(i++, itemStack);
            }
            for (int size = stacks.size(); i < size; i++) {
                stacks.set(i, ItemStack.EMPTY);
            }
        }
    }

    public class ConnectedItemSiloHandler implements ItemInventory, VersionedInventory {
        private final ItemSiloHandler[] itemHandler;
        private final int vaultCapacity;
        private final int size;
        private final int id;
        private final BitSet accessed;
        private int version;

        public ConnectedItemSiloHandler(ItemSiloHandler[] invs) {
            vaultCapacity = AllConfigs.server().logistics.vaultCapacity.get();
            itemHandler = invs;
            size = vaultCapacity * invs.length;
            id = idGenerator.getAndIncrement();
            accessed = new BitSet(invs.length);
            version = 0;
        }

        @Override
        public int getContainerSize() {
            return size;
        }

        @Override
        public ItemStack getItem(int slot) {
            if (slot >= size)
                return ItemStack.EMPTY;
            int i = slot / vaultCapacity;
            accessed.set(i);
            return itemHandler[i].getItem(slot % vaultCapacity);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot >= size)
                return;
            int i = slot / vaultCapacity;
            ItemSiloHandler handler = itemHandler[i];
            handler.setItem(slot % vaultCapacity, stack);
            handler.setChanged();
        }

        @Override
        public int getVersion() {
            return version;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public void setChanged() {
            for (ItemSiloHandler inventory : itemHandler) {
                inventory.setChanged();
            }
            updateComparators();
            version++;
        }
    }
}
