package com.hlysine.create_connected.content.itemsilo;

import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.client.AllSpriteShifts;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ItemSiloCTBehaviour extends ConnectedTextureBehaviour.Base {

    @Override
    @Nullable
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        Axis vaultBlockAxis = ItemSiloBlock.getVaultBlockAxis(state);
        if (vaultBlockAxis == null)
            return null;

        boolean large = ItemSiloBlock.isLarge(state);

        if (direction == Direction.DOWN)
            return large ? AllSpriteShifts.VAULT_BOTTOM_LARGE : AllSpriteShifts.VAULT_BOTTOM_MEDIUM;
        if (direction.getAxis() == vaultBlockAxis)
            return large ? AllSpriteShifts.VAULT_FRONT_LARGE : AllSpriteShifts.VAULT_FRONT_MEDIUM;

        return large ? AllSpriteShifts.VAULT_TOP_LARGE : AllSpriteShifts.VAULT_TOP_MEDIUM;
    }

    @Override
    public boolean buildContextForOccludedDirections() {
        return super.buildContextForOccludedDirections();
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos,
                              BlockPos otherPos, Direction face) {
        return state == other && ConnectivityHandler.isConnected(reader, pos, otherPos);
    }

}
