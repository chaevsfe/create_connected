package com.hlysine.create_connected.content.itemsilo;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.hlysine.create_connected.registries.CCBlocks;
import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.content.logistics.vault.ItemVaultBlock;
import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.foundation.item.ItemHelper;
import com.zurrtum.create.infrastructure.items.ItemInventoryProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class ItemSiloBlock extends Block
        implements IWrenchable, IBE<ItemSiloBlockEntity>, ItemInventoryProvider<ItemSiloBlockEntity> {
    public static final BooleanProperty LARGE = ItemVaultBlock.LARGE;

    public ItemSiloBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        registerDefaultState(defaultBlockState().setValue(LARGE, false));
    }

    @Override
    public @Nullable Container getInventory(LevelAccessor world, BlockPos pos, BlockState state,
                                            ItemSiloBlockEntity blockEntity, @Nullable Direction context) {
        if (blockEntity.itemCapability != null) {
            Container inventory = blockEntity.itemCapability.get();
            if (inventory != null)
                return inventory;
        }
        blockEntity.initCapability();
        return blockEntity.itemCapability != null ? blockEntity.itemCapability.get() : null;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LARGE);
        super.createBlockStateDefinition(pBuilder);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (pOldState.getBlock() == pState.getBlock())
            return;
        if (pIsMoving)
            return;
        withBlockEntityDo(pLevel, pPos, ItemSiloBlockEntity::updateConnectivity);
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean pIsMoving) {
        if (state.hasBlockEntity()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof ItemSiloBlockEntity vaultBE))
                return;
            Containers.dropContents(world, pos, vaultBE.inventory);
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(vaultBE);
        }
    }

    public static boolean isVault(BlockState state) {
        return state.is(CCBlocks.ITEM_SILO);
    }

    @Nullable
    public static Direction.Axis getVaultBlockAxis(BlockState state) {
        if (!isVault(state))
            return null;
        return Direction.Axis.Y;
    }

    public static boolean isLarge(BlockState state) {
        if (!isVault(state))
            return false;
        return state.getValue(LARGE);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, Direction direction) {
        return ItemHelper.calcRedstoneFromBlockEntity(this, pLevel, pPos);
    }

    @Override
    public BlockEntityType<? extends ItemSiloBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.ITEM_SILO;
    }

    @Override
    public Class<ItemSiloBlockEntity> getBlockEntityClass() {
        return ItemSiloBlockEntity.class;
    }
}
