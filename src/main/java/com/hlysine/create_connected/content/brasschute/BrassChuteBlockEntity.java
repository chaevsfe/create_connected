package com.hlysine.create_connected.content.brasschute;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.logistics.chute.ChuteBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.CachedInventoryBehaviour;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BrassChuteBlockEntity extends ChuteBlockEntity {
    public BrassChuteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected int getExtractionAmount() {
        return 64;
    }

    public static void registerTransfer() {
        if (!FabricLoader.getInstance().isModLoaded("fabric-transfer-api-v1"))
            return;
        BlockEntityBehaviour.add(CCBlockEntityTypes.BRASS_CHUTE,
                be -> new CachedInventoryBehaviour<>(be, chute -> chute.itemHandler));
        ItemStorage.SIDED.registerForBlockEntity(CachedInventoryBehaviour::get, CCBlockEntityTypes.BRASS_CHUTE);
    }
}
