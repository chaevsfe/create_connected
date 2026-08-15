package com.hlysine.create_connected.content.itemsilo;

import com.hlysine.create_connected.registries.CCRegistration;
import com.mojang.serialization.MapCodec;
import com.zurrtum.create.api.contraption.storage.item.MountedItemStorageType;
import com.zurrtum.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.zurrtum.create.content.contraptions.Contraption;
import com.zurrtum.create.foundation.codec.CreateCodecs;
import com.zurrtum.create.infrastructure.items.ItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class ItemSiloMountedStorage extends WrapperMountedItemStorage<ItemStackHandler> {
    public static final MapCodec<ItemSiloMountedStorage> CODEC = CreateCodecs.ITEM_STACK_HANDLER.xmap(
            ItemSiloMountedStorage::new, storage -> storage.wrapped
    ).fieldOf("value");

    protected ItemSiloMountedStorage(MountedItemStorageType<?> type, ItemStackHandler handler) {
        super(type, handler);
    }

    protected ItemSiloMountedStorage(ItemStackHandler handler) {
        this(CCRegistration.SILO, handler);
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof ItemSiloBlockEntity vault) {
            vault.applyInventoryToBlock(this.wrapped);
        }
    }

    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureTemplate.StructureBlockInfo info) {
        return false;
    }

    public static ItemSiloMountedStorage fromVault(ItemSiloBlockEntity vault) {
        return new ItemSiloMountedStorage(copyToItemStackHandler(vault.getInventoryOfBlock()));
    }
}
